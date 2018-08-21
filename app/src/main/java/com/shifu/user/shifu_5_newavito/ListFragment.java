package com.shifu.user.shifu_5_newavito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.Product;
import com.shifu.user.shifu_5_newavito.ui.CustomFocusChangeListener;
import com.shifu.user.shifu_5_newavito.ui.CustomTextField;
import com.shifu.user.shifu_5_newavito.ui.CustomTextWatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

import static com.shifu.user.shifu_5_newavito.ui.CustomDialogCall.showRadioButtonDialog;


public class ListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();


    private View rootView;
    private ImageButton imageMenuBackButton;
    private InputMethodManager imm;
    private FloatingActionButton fab;
    RecyclerView recyclerView;

    public CustomTextField editText;
    public String chosenCategory = "-1";
    public Long lastArticleId = null;

    private ProgressBar progress;
    private NestedScrollView scrollView;


    private ApiInterface api = ApiClient.getInstance().getApi();
    private static RealmController rc = RealmController.getInstance();
    private static RealmRVAdapter ra = RealmRVAdapter.getInstance();

    private CompositeDisposable uiDisposables = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.list_fragment, container, false);

        setUIActions(rootView);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        progress = rootView.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        scrollView = rootView.findViewById(R.id.nested_scroll_view);
        progress.setVisibility(View.GONE);

        lastArticleId = (Long) Realm.getDefaultInstance().where(Product.class).max(Product.getNetIdField());
        Log.d("lastArticleId", (lastArticleId == null)?"null":Long.toString(lastArticleId));

        getDataWithLoad();

        return rootView;


    }

    private void setUIActions(View rootView) {

        editText = rootView.findViewById(R.id.textview_search);
        editText.clearState();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setImm(imm);

        imageMenuBackButton = rootView.findViewById(R.id.menuback);
        editText.setContectUIToChange(imageMenuBackButton, this);

        /* Cross button appearance to clear editText */
        editText.addTextChangedListener(new CustomTextWatcher(editText));

        /* Finish typing text action (by virtual keyboard button) */
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                Disposable d = getProductsResult()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showProducts);
                if (editText.getText() != null && editText.getText().length() == 0) clearTextFieldState();
            }
            return false;
        });

        /* MenuButton actions */
        imageMenuBackButton.setOnClickListener(view -> {
            if (editText.isFocused()) {
                clearTextFieldState();
                showProducts(true);
//                Disposable d = getProductsResult()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(this::showProducts);
            } else {
                Log.d("MenuAction", "request");
                ((ActivityMain) getActivity()).openDrawer();
            }
        });

        /* RequestFocus action */
        CustomFocusChangeListener focusChangeListener = new CustomFocusChangeListener(editText, imageMenuBackButton, imm, this);
        Log.d("uiDisposable", "restore");
        uiDisposables.add(focusChangeListener.getPublishSubject().subscribe(i -> {
            Log.d("Filter", "work starts properly");
            uiDisposables.add(showRadioButtonDialog(getContext())
                    .subscribe(msg -> {
                        Log.d("Dialog", "Selected: " + msg);
                        if (!msg.equals(chosenCategory)) {
                            chosenCategory = msg;
                            Disposable d = getProductsResult()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(this::showProducts);
                        }
                    }));
        }));
        editText.setOnFocusChangeListener(focusChangeListener);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> getActivity().startActivity(new Intent(getActivity(), ActivityAddProduct.class)));

    }

    public void setFBState(Boolean state){
        if (state != null) {
            if (state) {
                fab.show();
            } else {
                fab.hide();
            }
        }
    }

    public void clearTextFieldState(){
        editText.clearState();
        editText.clearFocus();
        editText.setText("");
        editText.setCrossWork(false);
        imageMenuBackButton.setImageDrawable(editText.stylish(R.drawable.icons8_menu_24));
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /*
     * REST GET and load to Realm /category
     */
    public Flowable<Boolean> getCategories(){
        return api.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(categories -> {
                    Realm realm = Realm.getDefaultInstance();
                    if (categories.body() == null || categories.body().size() <= realm.where(Category.class).count())
                        return false;

                    realm.executeTransaction(trRealm -> {
                            trRealm.where(Category.class).findAll().deleteAllFromRealm();
                            trRealm.copyToRealm(categories.body());
                    });
                    return true;
                });
    }

    /*
     *  REST GET and load to Realms /article
     */

    public Flowable<Boolean> getProductsResult() {

        Realm realm = Realm.getDefaultInstance();
        Author user = realm.where(Author.class).findFirst();
        String username = (user == null)?"":user.getUsername();

        Map<String, String> headers = new HashMap <>();
        if (!username.equals("")) headers.put("username", username);


        Map<String, String> options = new HashMap <>();
        options.put("format", ApiInterface.format);
        if (chosenCategory != null && !chosenCategory.equals("-1")) {
            options.put("category", chosenCategory);
        }
        if (editText.getText() != null && editText.getText().length() > 0) {
            options.put("keyword", editText.getText().toString());
        }
        if (lastArticleId != null){
            options.put("id_article", Long.toString(lastArticleId));
        }


        return api.getProducts(options, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(products -> {
                    Realm realmInner = Realm.getDefaultInstance();
                    if (products.body() != null && products.body().size() > 0) {
                        realmInner.executeTransaction(trRealm -> {
                            Long newLastId = 0L;
                            Long count = 0L;
                            for (Product obj: products.body()) {
                                if (obj.getUpid() > newLastId) newLastId = obj.getUpid();
//                                Log.d("Product", "id: "+obj.getUpid()+" like: "+obj.getLikes());
                                Product productIn = trRealm.where(Product.class).equalTo(Product.getNetIdField(), obj.getUpid()).findFirst();
                                if (productIn == null) {
                                    count++;
                                    trRealm.copyToRealm(products.body());
                                } else {
                                    productIn.setLikes(obj.getLikes());
                                }
                            }
                            lastArticleId = newLastId;
//                            Log.d("DB Load", "newLastID: "+Long.toString(lastArticleId));
//                            Log.d("DB Load", "End. DB: "+Long.toString(realmInner.where(Product.class).count()));
                        });
                    }
                    return products;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(products -> {
                    Log.d("showProgress", "start");
                    showProgress(false);
                    if (products.code() == 520) {
                        Toast.makeText(getContext(), products.message(), Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        return true;
                    }
                });
    }

    private void getDataWithLoad(){
        Disposable d = Flowable.timer(2, TimeUnit.SECONDS)
                .concatMap(i -> getCategories())
                .concatMap(i -> getProductsResult())
                .subscribe(this::showProducts);
    }

    public void showProgress(final boolean show) {
        View view = getActivity().getCurrentFocus();
        if (view == null) view = new View(getActivity());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showProducts(Boolean success) {
        if (success) {
            Realm realm = rc.realm;
            RealmQuery<Product> query = realm.where(Product.class)
                    .sort(Product.getNetIdField(), Sort.DESCENDING);

            if (chosenCategory != null && !chosenCategory.equals("-1")) {
                Log.d("Query", "category");
                query = query.equalTo("category", chosenCategory);
            }

            String keywordString = (editText.getText() == null)?"":editText.getText().toString();
            keywordString = keywordString.replaceAll("\\s+", " ");
            int count = 0;
            if (keywordString.length() > 1) {
                String[] keywords = keywordString.split(" ");
                Log.d("Query", "keywords: "+ Arrays.asList(keywords));
                for (String keyword : keywords) {
                    keyword = keyword.replaceAll("\\s+", " ");
                    if (keyword.length() > 0) {
                        if (count == 0) {
                            query = query.beginGroup();
                        } else {
                            query = query.or();
                        }
                        count++;
                        query = query
                                .contains("title", keyword)
                                .or()
                                .contains("description", keyword);
                    }
                }
                if (count>0) query.endGroup();
                //clearTextFieldState();
            }
            if (keywordString.length() > 0 && count == 0) clearTextFieldState();

            ra.setData(query.findAll());
        }
    }
//    @Override
//    public void onPause(){
//        super.onPause();
//        Log.d(TAG, "onPause");
//        //if (!uiDisposables.isDisposed()) uiDisposables.dispose();
//    }
//

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        recyclerView.setAdapter(ra);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (!uiDisposables.isDisposed()) uiDisposables.dispose();
    }


}
