package com.shifu.user.shifu_5_newavito.ui;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.shifu.user.shifu_5_newavito.ListFragment;
import com.shifu.user.shifu_5_newavito.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class CustomFocusChangeListener implements View.OnFocusChangeListener{

    private CustomTextField editText;
    private ImageButton imageMenuButton;
    private InputMethodManager imm;
    private PublishSubject<Boolean> publishSubject = PublishSubject.create();
    private ListFragment lf;

    public CustomFocusChangeListener(CustomTextField editText, ImageButton imageButton, InputMethodManager imm, ListFragment lf) {
        this.editText = editText;
        this.imageMenuButton = imageButton;
        this.imm = imm;
        this.lf = lf;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocuse) {
        if (hasFocuse) {
            if (!editText.isFilter()) {
                imageMenuButton.setImageDrawable(editText.stylish(R.drawable.icons8_left_32));
                editText.requestFocusState();
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                lf.showProducts(true);
//                Disposable d = lf.getProductsResult()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(lf::showProducts);

            } else {
                editText.clearFocus();
                editText.clearFilter();
                publishSubject.onNext(true);
            }
        }
    }

    public PublishSubject <Boolean> getPublishSubject() {
        return publishSubject;
    }
}
