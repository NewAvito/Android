package com.shifu.user.shifu_5_newavito;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shifu.user.shifu_5_newavito.model.Author;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import retrofit2.Response;

import static com.shifu.user.shifu_5_newavito.AppGlobals.*;

class State {
    private int i = 0;

    State(){ }

    public int get() {
        return i;
    }
    public void set(int i) {
        this.i = i;
    }
}

public class ActivityLogin extends AppCompatActivity {



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
    private EditText vLogin;
    private EditText vPass;
    private View vProgress;
    private View vForm;
    private Button bAction;
    private Button bRestore;

    State state = new State();

    // RxJava
    CompositeDisposable cd = new CompositeDisposable();
    Disposable d;
    PublishSubject<Response<Author>> source = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vForm = findViewById(R.id.login_form);
        vProgress = findViewById(R.id.login_progress);
        vLogin = findViewById(R.id.login);
        vPass = findViewById(R.id.password);
        bAction = findViewById(R.id.action_button);

        bRestore = findViewById(R.id.restore_button);
        bRestore.setOnClickListener(view -> {
            if (bRestore.getText().toString().equals(getResources().getString(R.string.action_restore))) {
                setState(RESTORE);
                bRestore.setText(getResources().getString(R.string.action_back));
            } else {
                setState(LOGIN);
                bRestore.setText(getResources().getString(R.string.action_restore));
            }
        });

        Intent intent = getIntent();
        Integer code = intent.getIntExtra("requestCode", 0);
        Log.d("Intent","requestCode: "+code);
        setState(code);

        vPass.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        bAction.setOnClickListener(view -> attemptLogin());

        ImageButton bClose = findViewById(R.id.button_close);
        bClose.setOnClickListener(view -> {
            clearState();
            setResult(RESULT_CANCELED,  getIntent());
            finish();
        });

    }

    private void clearState() {
        vLogin.setText("");
        vLogin.setError(null);
        vPass.setText("");
        vPass.setError(null);
    }

    private void attemptLogin() {
            if (vProgress.getVisibility() == View.VISIBLE) return;

            vLogin.setError(null);
            vPass.setError(null);

            String pass = vPass.getText().toString();
            String name = vLogin.getText().toString();
            //String location = vLocation.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (!isPasswordValid(vPass)) {
                vPass.setError(getString(R.string.error_invalid_password));
                focusView = vPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(name)) {
                vLogin.setError(getString(R.string.error_field_required));
                focusView = vLogin;
                cancel = true;
            } else if (!isLoginValid(vLogin)) {
                vLogin.setError(getString(R.string.error_invalid_login));
                focusView = vLogin;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                showProgress(true);
                if (state.get() == REGISTER || state.get() == LOGIN) {

                    d = Flowable.fromCallable(() -> {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(trRealm -> {
                                    trRealm.where(Author.class).findAll().deleteAllFromRealm();
                                    trRealm.createObject(Author.class, name);
                                });
                                return 0;
                            })
                            .observeOn(Schedulers.computation())
                            .subscribeOn(Schedulers.io())
                            .concatMap(i -> {
                                String requestType = (state.get() == REGISTER)?"register":"login";
                                String answer="secret";
                                return api.login(requestType, new Author(name, pass, answer));
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> RegSuccess(response), this::RegError);
                }
                else if (state.get() == RESTORE){
                    //TODO вызов запроса на восстановление пароля
                }
            }
    }

    private boolean isLoginValid(EditText loginView) {
        //TODO: validate login. М.б. только английскими?
        return true;
    }

    private boolean isPasswordValid(EditText passView) {
        return passView.getText().toString().length() >= 6;
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
        clearState();
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    private void RegSuccess(Response<Author> response){
        if (!d.isDisposed()) d.dispose();
        if (response.isSuccessful()) {
            clearState();
            setResult(RESULT_OK, getIntent());
            finish();
        } else {
            rc.clear(Author.class);
            try {
                String errorBody = response.errorBody().string();
                Log.d("Response", errorBody);
                String init = "Ошибка на сервере";
                StringBuilder error = new StringBuilder(init);
                for (String key : Errors.keySet()) {
                    if (errorBody.contains(key)) {
                        error.append((error.toString().equals(init))?":\n":"\n");
                        error.append(Errors.get(key));
                    }
                }
                error.append("\nПопробуйте ещё раз");
                showProgress(false);
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void RegError(Throwable t) {
        if (!d.isDisposed()) d.dispose();
        rc.clear(Author.class);
        Toast.makeText(this, "Ошибка соединения с сервером: "+t.toString(), Toast.LENGTH_LONG).show();
        t.printStackTrace();
    }

    private void setState(Integer code) {
        state.set(code);
        if (state.get() == REGISTER) {
            bAction.setText(R.string.action_sign_up);
            bRestore.setVisibility(View.GONE);
        }
        else if (state.get() == LOGIN){
            bAction.setText(R.string.action_sign_in);
            bRestore.setVisibility(View.GONE);
        }
        else if (state.get() == RESTORE) {
            //TODO форма восстановления пароля - согласовать с бекендом
            bAction.setText("");
        }
        else {
            setResult(RESULT_CANCELED, getIntent());
            finish();
        }
    }
}
