package com.shifu.user.shifu_5_newavito;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import com.shifu.user.shifu_5_newavito.fake.FakeItemEntry;
import com.shifu.user.shifu_5_newavito.fake.FakeRVAdapter;
import com.shifu.user.shifu_5_newavito.ui.CustomFocusChangeListener;
import com.shifu.user.shifu_5_newavito.ui.CustomTextField;
import com.shifu.user.shifu_5_newavito.ui.CustomTextWatcher;

import io.reactivex.disposables.CompositeDisposable;

import static com.shifu.user.shifu_5_newavito.ui.CustomDialogCall.showRadioButtonDialog;


public class ListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();

    private View rootView;
    private CustomTextField editText;
    private ImageButton imageMenuBackButton;
    private InputMethodManager imm;
    private FloatingActionButton fab;



    private CompositeDisposable uiDisposables = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.list_fragment, container, false);

        setUIActions(rootView);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        FakeRVAdapter ra = new FakeRVAdapter(getContext(), FakeItemEntry.DatedList(getResources()));
        recyclerView.setAdapter(ra);

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
