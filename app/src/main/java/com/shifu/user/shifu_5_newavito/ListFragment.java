package com.shifu.user.shifu_5_newavito;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.shifu.user.shifu_5_newavito.fake.FakeItemEntry;
import com.shifu.user.shifu_5_newavito.fake.FakeRVAdapter;
import com.shifu.user.shifu_5_newavito.ui.CustomFocusChangeListener;
import com.shifu.user.shifu_5_newavito.ui.CustomTextField;
import com.shifu.user.shifu_5_newavito.ui.CustomTextWatcher;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;


public class ListFragment extends android.support.v4.app.Fragment {

    CustomTextField editText;
    ImageButton imageMenuBackButton;
    InputMethodManager imm;
    FloatingActionButton fab;

    Integer RESULT_LOAD_IMAGE;

    CompositeDisposable uiDisposables = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        /* Cross button appearance to clear editText */
        editText.addTextChangedListener(new CustomTextWatcher(editText));

        /* Finish typing text action (by virtual keyboard button) */
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        uiDisposables.add(focusChangeListener.getPublishSubject().subscribe(i -> showRadioButtonDialog()));
        editText.setOnFocusChangeListener(focusChangeListener);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            RESULT_LOAD_IMAGE = new Random().nextInt(65536);
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        });

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

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_NewAvito_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        String[] stringArray = getResources().getStringArray(R.array.categories);
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        for(int i=0; i<stringArray.length;i++){
            RadioButton rb=new RadioButton(getActivity());
            rb.setText(stringArray[i]);
            rb.setId(i);
            rg.addView(rb);
        }

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            if (rg.getCheckedRadioButtonId() != -1) {
                Log.d("Dialog", "Selected: " + stringArray[rg.getCheckedRadioButtonId()]);
                dialog.cancel();
            }
        });

        dialog.show();

    }

    public String getRealPathFromURI(Uri contentUri) {

        String result;
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
//
//            String path = getRealPathFromURI(data.getData());
//            File file = new File(path);
//
//            Log.d("File","Path: "+path);
//            Log.d("File","Length: "+file.length());
//
//
//
//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//
//            MultipartBody.Part filePart = MultipartBody.Part.createFormData("picture",file.getName(),requestFile);
//
//            try {
//                Log.d("File part", "Content-Length: " + filePart.body().contentLength());
//                Log.d("File part", "Content-Type: " + filePart.body().contentType());
//            } catch (IOException e) {
//                Log.d("File part", "Content-Length -> IOException");
//                e.printStackTrace();
//            }
//
//            Call<Response<Empty>> responseBodyCall = ApiClient.getInstance().getApi().pushImageYandex(filePart);
////                    getToken(),
////                    file.length(),
////
////                    filePart);
//            responseBodyCall.enqueue(new Callback<Response<Empty>>() {
//                @Override
//                public void onResponse(Call<Response<Empty>> call, Response<Response<Empty>> response) {
//                    Log.d("Success", "code: "+response.code());
//                    Log.d("Success", "message: "+response.message());
//                    Log.d("Success", "body: "+response.body());
//                }
//
//                @Override
//                public void onFailure(Call<Response<Empty>> call, Throwable t) {
//                    Log.d("failure", "message = " + t.getMessage());
//                    Log.d("failure", "cause = " + t.getCause());
//                }
//            });

//        }
    //}
}
