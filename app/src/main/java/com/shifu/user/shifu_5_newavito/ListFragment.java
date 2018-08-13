package com.shifu.user.shifu_5_newavito;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Arrays;
import java.util.List;


public class ListFragment extends android.support.v4.app.Fragment {

    CustomTextField editText;
    ImageButton imageMenuBackButton;
    InputMethodManager imm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

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

        /*
         * Cross button to clear editText
         */
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    editText.crossButtonState();
                    editText.setCrossWork(true);
                } else {
                    editText.requestFocusState();
                    editText.setCrossWork(false);
                }
            }
        };

        editText.addTextChangedListener(textWatcher);

        /*
         * Finish typing text
         */
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //TODO обработка поиска
                clearTextFieldState();
            }
            return false;
        });

        /*
         * MenuButton actions
         */
        imageMenuBackButton.setOnClickListener(view -> {
            if (editText.isFocused()) {
                clearTextFieldState();
            } else {
                Log.d("MenuAction", "request");
                //TODO обработка вызова меню
            }
        });

        /*
         * RequestFocus action
         */
        editText.setOnFocusChangeListener((view, hasFocuse) -> {
            if (hasFocuse) {
                if (!editText.isFilter()) {
                    imageMenuBackButton.setImageResource(R.drawable.icons8_left_32);
                    editText.requestFocusState();
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    editText.clearFocus();
                    editText.clearFilter();
                    showRadioButtonDialog();
                }
            }
        });


        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

    }

    public void clearTextFieldState(){
        editText.clearState();
        editText.clearFocus();
        editText.setText("");
        editText.setCrossWork(false);
        imageMenuBackButton.setImageResource(R.drawable.icons8_menu_24);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_NewAvito_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        String[] stringArray = getResources().getStringArray(R.array.categories);
        List<String> stringList= Arrays.asList(stringArray);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            Log.d("Dialog", "Selected: "+stringArray[(((int) rg.getCheckedRadioButtonId())-1)]);
            dialog.cancel();
        });

        dialog.show();

    }

}
