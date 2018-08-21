package com.shifu.user.shifu_5_newavito.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.shifu.user.shifu_5_newavito.ListFragment;
import com.shifu.user.shifu_5_newavito.R;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class CustomTextField extends TextInputEditText {

    private InputMethodManager imm;
    private ImageButton buttonToChange;
    private ListFragment lf;
    //private int resToChange;

    final int resRight = R.drawable.icons8_filter_24;
    final int resLeft = R.drawable.icons8_search_24;
    final int resRightAlter = R.drawable.icons8_close_24;

    private int rightWidth=0;
    private int leftWidth=0;

    private boolean isCross = false;
    private boolean isFilter = false;


    public CustomTextField(Context context) {
        super(context);
    }

    public CustomTextField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clearState(){
        final CustomTextField viewToTask = this;
        this.post(() -> {
            viewToTask.setCompoundDrawablesWithIntrinsicBounds(resLeft, 0, resRight, 0);
            rightWidth = getCompoundDrawables()[2].getBounds().width();
            leftWidth = getCompoundDrawables()[0].getBounds().width();
            setTextViewDrawableColor();
            Log.d("EditorAction", Integer.toString(rightWidth));
        });
    }

    public void requestFocusState(){
        this.setCompoundDrawablesWithIntrinsicBounds(resLeft, 0, 0, 0);
        rightWidth = 0;
        leftWidth = getCompoundDrawables()[0].getBounds().width();
        setTextViewDrawableColor();
    }

    public void crossButtonState(){
        this.setCompoundDrawablesWithIntrinsicBounds(resLeft, 0, resRightAlter, 0);
        rightWidth = getCompoundDrawables()[2].getBounds().width();
        leftWidth = getCompoundDrawables()[0].getBounds().width();
        setTextViewDrawableColor();

    }

    private void setTextViewDrawableColor() {
        for (Drawable drawable : this.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getRawX() >= (this.getRight() - rightWidth) && !isCross) {
                    isFilter = true;
                }
                return true;

            case MotionEvent.ACTION_UP:
                Log.d("editText", "onTouchUp");
                performClick();
                if (event.getRawX() >= (this.getRight() - rightWidth)) {
                    // your action here
                    if (isCross) {
                        Log.d("EditorAction", "clear typing");
                        this.requestFocusState();
                        this.setText("");
                    }
                }
                else if (event.getRawX() <= (this.getLeft()+leftWidth)) {
                    this.requestFocus();
                    this.requestFocusState();
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {

        super.performClick();
        return true;
    }

    public void setImm(InputMethodManager imm) {
        this.imm = imm;
    }

    public void setCrossWork(boolean alterWork) {
        isCross = alterWork;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void clearFilter() {
        isFilter = false;
    }

    public void setContectUIToChange(ImageButton imageButton, ListFragment lf){
        this.buttonToChange = imageButton;
        this.lf = lf;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.clearState();
            this.clearFocus();
            this.setText("");
            this.setCrossWork(false);
            buttonToChange.setImageDrawable(stylish(R.drawable.icons8_menu_24));
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);

            lf.showProducts(true);
//            Disposable d = lf.getProductsResult()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(lf::showProducts);

        }
        return false;
    }

    public Drawable stylish(int resource) {
        Drawable icon = getResources().getDrawable(resource);
        icon.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP));
        return icon;
    }
}