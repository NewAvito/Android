package com.shifu.user.shifu_5_newavito.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shifu.user.shifu_5_newavito.AppGlobals;
import com.shifu.user.shifu_5_newavito.R;
import com.shifu.user.shifu_5_newavito.RealmController;
import com.shifu.user.shifu_5_newavito.model.Category;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.RealmResults;

public class CustomDialogCall {

    private static RealmController rc = RealmController.getInstance();

    public static Observable<String> showRadioButtonDialog(Context context) {

        PublishSubject<String> publishSubject = PublishSubject.create();

        final Dialog dialog = new Dialog(context, R.style.Theme_NewAvito_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(false);

        RealmResults<Category> categories = rc.getBase(Category.class, null).sort("category");
        Category[] stringArray = categories.toArray(new Category[categories.size()]);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for(int i=0; i<categories.size();i++){
            if (!stringArray[i].getCategory().equals(AppGlobals.emptyCategory)) {
                RadioButton rb = new RadioButton(context);
                rb.setText(stringArray[i].getCategory());
                rb.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                rb.setId(i);
                rg.addView(rb);
            }
        }

        RadioButton rb = new RadioButton(context);
        rb.setText(AppGlobals.emptyCategory);
        rb.setId(categories.size()+1);
        rb.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        rg.addView(rb);

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            if (rg.getCheckedRadioButtonId() != -1) {
                if (rg.getCheckedRadioButtonId() == categories.size()+1) {
                    publishSubject.onNext(AppGlobals.emptyCategory);
                } else {
                    publishSubject.onNext(stringArray[rg.getCheckedRadioButtonId()].getCategory());
                }
                dialog.cancel();
            }
        });

        Button cansel = dialog.findViewById(R.id.button_cansel);
        cansel.setOnClickListener(view -> {
            publishSubject.onNext("-1");
            dialog.cancel();
        });

        return publishSubject
                .hide()
                .doOnSubscribe(disp -> dialog.show());
    }
}
