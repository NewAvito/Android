package com.shifu.user.shifu_5_newavito.ui;

import android.text.Editable;
import android.text.TextWatcher;


public class CustomTextWatcher implements TextWatcher{

    private CustomTextField editText;


//    public CustomTextWatcher(){
//        throw new NullPointerException();
//    }

    public CustomTextWatcher(CustomTextField editText) {
        this.editText = editText;
    }

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
}
