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

import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.Product;
import com.shifu.user.shifu_5_newavito.ui.CustomFocusChangeListener;
import com.shifu.user.shifu_5_newavito.ui.CustomTextField;
import com.shifu.user.shifu_5_newavito.ui.CustomTextWatcher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;

import static com.shifu.user.shifu_5_newavito.ui.CustomDialogCall.showRadioButtonDialog;


public class ListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();

    private View rootView;
    private CustomTextField editText;
    private ImageButton imageMenuBackButton;
    private InputMethodManager imm;
    private FloatingActionButton fab;
    RecyclerView recyclerView;

    private ProgressBar progress;
    private NestedScrollView scrollView;


    private ApiInterface api = ApiClient.getInstance().getApi();
    private static RealmController rc = RealmController.getInstance();
    private static RealmRVAdapter ra = RealmRVAdapter.getInstance();

    private CompositeDisposable uiDisposables = new CompositeDisposable();

    PublishSubject<Boolean> publishCategories;

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

        recyclerView.setAdapter(ra);

        //FakeRVAdapter ra = new FakeRVAdapter(getContext(), FakeItemEntry.DatedList(getResources()));
        //recyclerView.setAdapter(ra);

        progress = rootView.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        scrollView = rootView.findViewById(R.id.nested_scroll_view);
        progress.setVisibility(View.GONE);

        getData();

        return rootView;
    }

    private void setUIActions(View rootView) {

        editText = rootView.findViewById(R.id.textview_search);
        editText.clearState();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setImm(imm);

        imageMenuBackButton = rootView.findViewById(R.id.menuback);
        editText.setButtonToChange(imageMenuBackButton, R.drawable.icons8_menu_24);

        /* Cross button appearance to clear editText */
        editText.addTextChangedListener(new CustomTextWatcher(editText));

        /* Finish typing text action (by virtual keyboard button) */
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                //TODO обработка поиска
                clearTextFieldState();
            }
            return false;
        });

        /* MenuButton actions */
        imageMenuBackButton.setOnClickListener(view -> {
            if (editText.isFocused()) {
                clearTextFieldState();
            } else {
                Log.d("MenuAction", "request");
                ((ActivityMain) getActivity()).openDrawer();
            }
        });

        /* RequestFocus action */
        CustomFocusChangeListener focusChangeListener = new CustomFocusChangeListener(editText, imageMenuBackButton, imm);
        Log.d("uiDisposable", "restore");
        uiDisposables.add(focusChangeListener.getPublishSubject().subscribe(i -> {
            Log.d("Filter", "work starts properly");
            uiDisposables.add(showRadioButtonDialog(getContext(), getResources())
                    .subscribe(msg -> Log.d("Dialog", "Selected: " + msg)));
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
        imageMenuBackButton.setImageResource(R.drawable.icons8_menu_24);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void getData() {
        Disposable d = Flowable.interval(1, TimeUnit.SECONDS)
                .filter(i -> (i-1)%600 == 0)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())

                /*
                 * REST GET and load to Realms /category
                 */
                .flatMap(i -> api.getCategories(ApiInterface.format))
                .doOnError(t -> {t.printStackTrace();})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(listResponse -> {
                    Realm realm = Realm.getDefaultInstance();
                    if (listResponse.body() != null && listResponse.body().size() > realm.where(Category.class).findAll().size()) {
                        realm.executeTransactionAsync(trRealm -> {
                            trRealm.where(Category.class).findAll().deleteAllFromRealm();
                            trRealm.copyToRealm(listResponse.body());
                        });
                    }
                    return false;
                })

                /*
                 *  REST GET and load to Realms /article
                 */
                .observeOn(Schedulers.io())
                .concatMap(b -> api.getProducts(ApiInterface.format))
                .observeOn(Schedulers.computation())
                .map(products -> {
                    Realm realm = Realm.getDefaultInstance();
                    if (products.body() != null && products.body().size() > 0) {
                        realm.executeTransactionAsync(trRealm -> {
                            for (Product obj: products.body()) {
                                Product productIn = trRealm.where(Product.class).equalTo(Product.getNetIdField(), obj.getUpid()).findFirst();
                                if (productIn == null) {
                                    trRealm.copyToRealm(products.body());
                                }
                            }
                        });
                    }
                    return products;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(products -> {
                   if (products.code() == 520) {
                       Toast.makeText(getContext(), products.message(), Toast.LENGTH_LONG).show();
                   } else {
                       showProgress(false);
                       ra.notifyDataSetChanged();
                   }
                });


    }

    private void showProgress(final boolean show) {
        View view = getActivity().getCurrentFocus();
        if (view == null) view = new View(getActivity());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public void onPause(){
//        super.onPause();
//        Log.d(TAG, "onPause");
//        //if (!uiDisposables.isDisposed()) uiDisposables.dispose();
//    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        Log.d(TAG, "onResume");
//        //setUIActions(rootView);
//    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (!uiDisposables.isDisposed()) uiDisposables.dispose();
    }


}
