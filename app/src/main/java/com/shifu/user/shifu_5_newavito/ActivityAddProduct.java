package com.shifu.user.shifu_5_newavito;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.json.JResponsePushProduct;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import retrofit2.Response;

import static com.shifu.user.shifu_5_newavito.ActivityMain.mAuth;
import static com.shifu.user.shifu_5_newavito.ui.CustomDialogCall.showRadioButtonDialog;
import static com.shifu.user.shifu_5_newavito.RealmController.getIdField;

public class ActivityAddProduct extends AppCompatActivity {

    private RealmController rc = RealmController.getInstance();
    private ApiInterface api = ApiClient.getInstance().getApi();

    // UI references.
    private View vProgress;
    private LinearLayout vForm;

    private EditText vTitle, vDescription, vPrice, vMobile, vLocation;
    private TextView vCategory, vPhotoText;
    private String category;
    private ImageView vPhoto;

    private Integer RESULT_LOAD_IMAGE;
    private Uri imgUri;

    private Boolean isExit = false;


    // RxJava
    CompositeDisposable cd = new CompositeDisposable();
    Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageButton bClose = findViewById(R.id.button_close);
        bClose.setOnClickListener(view -> finish());

        vForm = findViewById(R.id.add_form);
        vProgress = findViewById(R.id.login_progress);

        vTitle = findViewById(R.id.title);
        vDescription = findViewById(R.id.description);
        vPrice = findViewById(R.id.price);
        vLocation = findViewById(R.id.location);

        vCategory = findViewById(R.id.category);
        category = AppGlobals.emptyCategory;
        vCategory.setText(getResources().getString(R.string.category, category));
        Button bCategory = findViewById(R.id.button_category);
        bCategory.setOnClickListener(view -> cd.add(showRadioButtonDialog(this)
                .subscribe(msg -> {
                    Log.d("Dialog", "Selected: " + msg);
                    if (!msg.equals("-1")) {
                        vCategory.setText(getResources().getString(R.string.category, msg));
                        category = msg;
                    }
                })));

        vPhoto = findViewById(R.id.photo);
        vPhoto.setOnClickListener(view -> verifyStoragePermissionsAndRequest());

        vPhotoText = findViewById(R.id.photo_text);
        vPhotoText.setOnClickListener(view -> verifyStoragePermissionsAndRequest());

        vMobile = findViewById(R.id.mobile);
        vLocation = findViewById(R.id.location);

        Button bAction = findViewById(R.id.action_button);
        bAction.setOnClickListener(view -> attemptSend());
    }


    private void attemptSend() {
        if (vProgress.getVisibility() == View.VISIBLE) return;

        for (int i = 0; i< vForm.getChildCount(); i++) {
            View child = vForm.getChildAt(i);
            if (child instanceof TextView) ((TextView) child).setError(null);
        }

        View focusView = null;
        if (isMax(vLocation, 200)) focusView = vLocation;
        if (isEmpty(vMobile)) focusView = vMobile;
        if (isEmpty(vPrice)) focusView = vPrice;
        if (isEmpty(vTitle)) focusView = vTitle;
        if (isMax(vTitle, 200)) focusView = vTitle;

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            if (imgUri != null) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    uploadImageToFirebase(imgUri);
                } else {
                    mAuth.signInAnonymously()
                            .addOnSuccessListener(this, authResult -> uploadImageToFirebase(imgUri))
                            .addOnFailureListener(this, e -> {
                                Log.e("Firebase", "signInAnonymously:FAILURE\n", e);
                                loadImageFailure();
                            });
                }
            } else {
                sendProduct();
            }
        }
    }

    private boolean isMax(EditText view, Integer max) {
        if (view.getText().length() > max) {
            view.setError(getString(R.string.error_long, max));
            return true;
        }
        return false;
    }

    private boolean isEmpty(EditText view) {
        if (view.getText().length() == 0) {
            view.setError(getString(R.string.error_field_required));
            return true;
        }
        return false;
    }


    /*
     * Load image from phone
     * verifyStoragePermissionsAndRequest -> onRequestPermissionsResult -> onActivityResult
     */

    public void verifyStoragePermissionsAndRequest() {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };
        int permission = ActivityCompat.checkSelfPermission(this, permissions[0]);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            loadImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode ==0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage();
        }
    }

    private void loadImage() {
        RESULT_LOAD_IMAGE = new Random().nextInt(65536);
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            vPhotoText.setVisibility(View.GONE);

            String path;
            Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);

            if (cursor == null) {
                path = data.getData().getPath();
            } else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(column_index);
                cursor.close();
            }

            File imgFile = new File(path);
            if(imgFile.exists()) {
                vPhoto.setImageBitmap(BitmapFactory.decodeFile(path));
                imgUri = data.getData();
            }
            }
    }

    /*
     * Upload image to Firebase
     */

    private void uploadImageToFirebase(Uri file){

        String pathToFile = "images/"+UUID.randomUUID().toString()+".jpg";
        StorageReference imagesRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child(pathToFile);

        UploadTask uploadTask = imagesRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) loadImageFailure();

            return imagesRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imgUri = task.getResult();
                Log.d("Firebase", "Upload uri: "+imgUri);
                sendProduct();
            } else loadImageFailure();
        });
    }

    private void loadImageFailure(){
        Toast.makeText(this, "Ошибка загрузки картинки", Toast.LENGTH_LONG).show();
    }

    private void sendProduct(){
        final Long currentProdId = rc.newProductId();

        d = Flowable.fromCallable(() -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(trRealm -> {
                Product product = new Product(
                        currentProdId,
                        trRealm.where(Author.class).findFirst().getUsername(),
                        vTitle.getText().toString(),
                        vMobile.getText().toString(),
                        category,
                        vDescription.getText().toString(),
                        Long.parseLong(vPrice.getText().toString()),
                        vLocation.getText().toString(),
                        imgUri.toString()
                );
                trRealm.copyToRealm(product);
            });
            return realm.copyFromRealm(
                    realm
                            .where(Product.class)
                            .equalTo(getIdField(Product.class), currentProdId)
                            .findFirst());
        })
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .concatMap(product -> api.pushProduct(ApiInterface.contentType, product))
                .observeOn(Schedulers.computation())
                .map(response -> {
                    Realm realm = Realm.getDefaultInstance();
                    if (response.body() != null && response.body().getAnswer() != null && response.body().getAnswer().equals(AppGlobals.pushAnswer)){
                        realm.executeTransaction(trRealm -> trRealm.where(Product.class)
                                .equalTo(getIdField(Product.class), currentProdId)
                                .findFirst()
                                .setUpid(response.body().getId_article()));
                    } else {
                        realm.executeTransaction(trRealm -> trRealm.where(Product.class)
                                .equalTo(getIdField(Product.class), currentProdId)
                                .findFirst()
                                .deleteFromRealm());
                    }
                    return response;
                })
                .subscribe(response -> RegSuccess(response), this::RegError);

    }

    private void showProgress(final boolean show) {
        View view = this.getCurrentFocus();
        if (view == null) view = new View(this);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        vForm.setVisibility(show ? View.GONE : View.VISIBLE);
        vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void RegSuccess(Response<JResponsePushProduct> response){
        if (!d.isDisposed()) d.dispose();
        if (response.isSuccessful() && response.body().getAnswer().equals(AppGlobals.pushAnswer)) {
            setResult(RESULT_OK, getIntent());
            finish();
        } else {
            showProgress(false);
            Toast.makeText(this, "Ошибка на сервере, попробуйте ещё раз", Toast.LENGTH_LONG).show();
        }
    }

    private void RegError(Throwable t) {
        if (!d.isDisposed()) d.dispose();
        Toast.makeText(this, "Ошибка соединения с сервером: "+t.toString(), Toast.LENGTH_LONG).show();
        t.printStackTrace();
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Для выхода нажмите ещё раз", Toast.LENGTH_SHORT).show();
            isExit = true;
            Thread t = new Thread (() -> {
                try {
                    Thread.sleep(3000);
                    Log.d("Run", "work");
                    isExit = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }
}
