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

import com.shifu.user.shifu_5_newavito.json.JsonAuthorResponse;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.shifu.user.shifu_5_newavito.ui.CustomDialogCall.showRadioButtonDialog;
import static com.shifu.user.shifu_5_newavito.RealmController.getIdField;

public class ActivityAddProduct extends AppCompatActivity {

    private static final Map<String, String> Errors=new HashMap<>();
    static
    {
        Errors.put("A user with that username already exists.", "Такой пользователь уже существует!");
        Errors.put("Not valid username", "Пользователь не существует!");
        Errors.put("Not valid password", "Пароль некорректен!");
    }

    private RealmController rc = RealmController.getInstance();
    private ApiInterface api = ApiClient.getInstance().getApi();

    // UI references.
    private View vProgress;
    private LinearLayout vForm;

    private EditText vTitle;
    private TextView vCategory;
    private String category;
    private Button bCategory;
    private EditText vDescription;
    private EditText vPrice;
    private ImageView vPhoto;
    private TextView vPhotoText;

    private Switch sMobile;
    private EditText vMobile;
    private String mobile;

    private EditText vLocation;
    private Button bAction;

    private Integer RESULT_LOAD_IMAGE;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final HashMap<Integer, String[]> PERMISSIONS = new HashMap <>();
    static {
        PERMISSIONS.put(REQUEST_EXTERNAL_STORAGE, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    private File imgFile;


    // RxJava
    CompositeDisposable cd = new CompositeDisposable();
    Disposable d;
    PublishSubject<Response<Author>> source = PublishSubject.create();

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
        category = getResources().getString(R.string.category_cansel);
        vCategory.setText(getResources().getString(R.string.category, category));
        bCategory = findViewById(R.id.button_category);
        bCategory.setOnClickListener(view -> cd.add(showRadioButtonDialog(this, getResources())
                .subscribe(msg -> {
                    Log.d("Dialog", "Selected: " + msg);
                    vCategory.setText(getResources().getString(R.string.category, msg));
                    category = msg;
                })));

        vPhoto = findViewById(R.id.photo);
        vPhoto.setOnClickListener(view -> {
            Log.d("Photo", "click");
            verifyStoragePermissionsAndRequest(this, REQUEST_EXTERNAL_STORAGE);
        });
        vPhotoText = findViewById(R.id.photo_text);
        vPhotoText.setOnClickListener(view -> {
            Log.d("PhotoText", "click");
            verifyStoragePermissionsAndRequest(this, REQUEST_EXTERNAL_STORAGE);
        });

        vMobile = findViewById(R.id.mobile);
        vLocation = findViewById(R.id.location);

        bAction = findViewById(R.id.action_button);
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
            final Long currentProdId = rc.newProductId();
            d = Flowable.fromCallable(() -> {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(trRealm -> {
                    Product product = trRealm.createObject(Product.class);
                    product.setUpid(currentProdId);
                    product.setDate(new Date());
                    product.setUsername(trRealm.where(Author.class).findFirst().getUsername());
                    product.setTitle(vTitle.getText().toString());
                    product.setNameCategory(category);
                    product.setDescription(vDescription.getText().toString());
                    product.setCost(Long.parseLong(vPrice.getText().toString()));
                    product.setMobile(vMobile.getText().toString());
                    product.setLocation(vLocation.getText().toString());
                });
                return realm.copyFromRealm(
                        realm
                        .where(Product.class)
                        .equalTo(getIdField(Product.class), currentProdId)
                        .findFirst());
            })
                    .observeOn(Schedulers.computation())
                    .subscribeOn(Schedulers.io())
                    .map(product -> {
//                        api.pushProduct(product);
                        // TODO myTest
                        String JsonStr = "{ \"non_field_errors\":[\"Not valid password\"] }";
                        ResponseBody body = ResponseBody.create(MediaType.parse("application/json;"), JsonStr);
                        okhttp3.Response response = new okhttp3.Response.Builder()
                                .request(new Request.Builder().url("http://localhost/").build())
                                .code(400).message("Нет корректного ответа от сервера")
                                .body(body)
                                .protocol(Protocol.HTTP_1_0)
                                .build();

                        Response <JsonAuthorResponse> responseRetrofit = Response.error(400, response.body());
                        JsonAuthorResponse author = new JsonAuthorResponse();
                        Response <JsonAuthorResponse> responseRetrofit2 = Response.success(author);
                        return responseRetrofit;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::RegSuccess, this::RegError);
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

    private void showProgress(final boolean show) {
        View view = this.getCurrentFocus();
        if (view == null) view = new View(this);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        vForm.setVisibility(show ? View.GONE : View.VISIBLE);
        vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void RegSuccess(Response<JsonAuthorResponse> response){
        if (!d.isDisposed()) d.dispose();
        if (response.isSuccessful()) {
            setResult(RESULT_OK, getIntent());
            finish();
        } else {
//            rc.clear(Author.class);
//            try {
//                JSONObject jObj = new JSONObject(response.errorBody().string());
//                JSONArray jArr = jObj.getJSONArray("non_field_errors");
//                StringBuilder builder = new StringBuilder();
//                if (jArr == null) {
//                    builder.append("Неизвестная ошибка сервера.");
//                } else {
//                    for (int i=0; i<jArr.length(); i++) {
//                        if (builder.length() > 0) {
//                            builder.append("\n");
//                        }
//
//                        // Особый случай - некорректный пароль
//                        if (jArr.get(i).equals("Not valid password")) {
////                            bRestore.setVisibility(View.VISIBLE);
//                        }
//
//                        String next = Errors.get((String) jArr.get(i));
//                        if (next != null) {
//                            builder.append(next);
//                        } else {
//                            builder.append("Неизвестная ошибка: ");
//                            builder.append(jArr.get(i));
//                        }
//                    }
//                }
//                showProgress(false);
//                Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void RegError(Throwable t) {
        if (!d.isDisposed()) d.dispose();
        rc.clear(Author.class);
        Toast.makeText(this, "Ошибка соединения с сервером: "+t.toString(), Toast.LENGTH_LONG).show();
        t.printStackTrace();
    }

    /*
     * Load image
     * verifyStoragePermissionsAndRequest -> onRequestPermissionsResult -> onActivityResult
     */

    public void verifyStoragePermissionsAndRequest(Activity activity, Integer requestType) {

        String[] permissions = PERMISSIONS.get(requestType);
        if (permissions != null) {

            int permission = ActivityCompat.checkSelfPermission(activity, permissions[0]);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        permissions,
                        requestType
                );
            } else {
                if (requestType == REQUEST_EXTERNAL_STORAGE) {
                    loadImage();
                }
            }
        } else {
            Log.d("getPermissions", "unknown request");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_EXTERNAL_STORAGE) {
                loadImage();
            }
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

            String path = getRealPathFromURI(data.getData());
            imgFile = new File(path);

            Log.d("Image","Path: "+path);
            Log.d("Image","Length: "+imgFile.length());

            if(imgFile.exists()) {
                Log.d("Image", "loading");
                vPhoto.setImageBitmap(BitmapFactory.decodeFile(path));
            }

//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            MultipartBody.Part filePart = MultipartBody.Part.createFormData("picture",file.getName(),requestFile);
//            try {
//                Log.d("File part", "Content-Length: " + filePart.body().contentLength());
//                Log.d("File part", "Content-Type: " + filePart.body().contentType());
//            } catch (IOException e) {
//                Log.d("File part", "Content-Length -> IOException");
//                e.printStackTrace();
//            }
//
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        String result;
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
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

}
