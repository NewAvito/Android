package com.shifu.user.shifu_5_newavito.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shifu.user.shifu_5_newavito.AppGlobals;
import com.shifu.user.shifu_5_newavito.R;
import com.shifu.user.shifu_5_newavito.RealmController;
import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.realm.RealmResults;

public class CustomDialogCall {

    private static RealmController rc = RealmController.getInstance();

    public static Observable<String> showRadioButtonDialog(Context context, Resources resources) {

        PublishSubject<String> publishSubject = PublishSubject.create();

        final Dialog dialog = new Dialog(context, R.style.Theme_NewAvito_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(true);


        RealmResults<Category> categories = rc.getBase(Category.class, "category");
        Category[] stringArray = categories.toArray(new Category[categories.size()]);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for(int i=0; i<categories.size();i++){
            if (!stringArray[i].getCategory().equals(AppGlobals.emptyCategory)) {
                RadioButton rb = new RadioButton(context);
                rb.setText(stringArray[i].getCategory());
                rb.setId(i);
                rg.addView(rb);
            }
        }

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            if (rg.getCheckedRadioButtonId() != -1) {
                publishSubject.onNext(stringArray[rg.getCheckedRadioButtonId()].getCategory());
                dialog.cancel();
            }
        });

        return publishSubject
                .hide()
                .doOnSubscribe(disp -> dialog.show());
    }
}
