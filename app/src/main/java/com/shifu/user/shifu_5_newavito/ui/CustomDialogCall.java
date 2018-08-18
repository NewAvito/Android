package com.shifu.user.shifu_5_newavito.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shifu.user.shifu_5_newavito.R;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class CustomDialogCall {

    public static Observable<String> showRadioButtonDialog(Context context, Resources resources) {

        PublishSubject<String> publishSubject = PublishSubject.create();

        final Dialog dialog = new Dialog(context, R.style.Theme_NewAvito_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(true);

        String[] stringArray = resources.getStringArray(R.array.categories);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for(int i=0; i<stringArray.length;i++){
            RadioButton rb=new RadioButton(context);
            rb.setText(stringArray[i]);
            rb.setId(i);
            rg.addView(rb);
        }

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            if (rg.getCheckedRadioButtonId() != -1) {
                publishSubject.onNext(stringArray[rg.getCheckedRadioButtonId()]);
                dialog.cancel();
            }
        });

        return publishSubject
                .hide()
                .doOnSubscribe(disp -> dialog.show());
    }
}
