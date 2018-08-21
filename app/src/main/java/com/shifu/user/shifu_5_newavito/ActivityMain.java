package com.shifu.user.shifu_5_newavito;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Product;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

import static com.shifu.user.shifu_5_newavito.AppGlobals.*;

/**
 * https://guides.codepath.com/android/fragment-navigation-drawer
 */

public class ActivityMain extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;

    private TextView username;

    private ListFragment lf;
    private RealmController rc;
    private RealmRVAdapter ra;

    private Boolean isExit = false;

    public static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        rc = new RealmController(this);
        ra = new RealmRVAdapter(getApplicationContext(), rc.getBase(Product.class, "id_article"), this);

        mDrawer = findViewById(R.id.drawer_layout);

        lf = new ListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, lf, "START")
                .commit();

        nvDrawer = findViewById(R.id.nvView);
        View hView = nvDrawer.getHeaderView(0);
        username = hView.findViewById(R.id.username);

        nvDrawer.setNavigationItemSelectedListener(menuItem -> {
            Intent intent = new Intent(this, ActivityLogin.class);
            switch (menuItem.getItemId()) {
                case R.id.menu_item_register:
                    intent.putExtra("requestCode", REGISTER);
                    startActivityForResult(intent, REGISTER);
                    break;
                case R.id.menu_item_login:
                    Log.d("login", "clicked");
                    if (rc.getSize(Author.class) > 0) {
                        rc.clear(Author.class);
                        username.setText("");
                        Menu menu = nvDrawer.getMenu();
                        menu.findItem(R.id.menu_item_register).setVisible(true);
                        menu.findItem(R.id.menu_item_login).setTitle(R.string.action_login);
//                        menu.findItem(R.id.menu_item_favourites).setVisible(false);
                        lf.setFBState(false);
//                        Disposable d = lf.getProductsResult()
//                                .subscribe(success -> lf.showProducts(success));

                    } else {
                        intent.putExtra("requestCode", LOGIN);
                        startActivityForResult(intent, LOGIN);
                    }
                    break;
//                case R.id.menu_item_update:
//                    rc.clear(Product.class);
//                    lf.chosenCategory = null;
//                    lf.lastArticleId = null;
//                    lf.editText.clearState();
//
//                    lf.getCategories()
//                            .concatMap(b -> lf.getProductsResult())
//                            .subscribe(success -> lf.showProducts(success));
//
//                    break;
//                case R.id.menu_item_favourites:
//                    Menu menu = nvDrawer.getMenu();
//                    MenuItem item = menu.findItem(R.id.menu_item_favourites);
//                    if (item.getTitle().equals(getResources().getString(R.string.favourites))) {
//                        ra.setData(rc.getBaseWithLikes(Product.getNetIdField()));
//                        item.setTitle(getResources().getString(R.string.all));
//                    } else {
//                        ra.setData(rc.getBase(Product.class, Product.getNetIdField()));
//                        item.setTitle(getResources().getString(R.string.favourites));
//                    }
            }
            return true;
        });

    }


    @Override
    protected void onStart(){
        super.onStart();
        Menu menu = nvDrawer.getMenu();
        MenuItem register = menu.findItem(R.id.menu_item_register);
        MenuItem login = menu.findItem(R.id.menu_item_login);
//        MenuItem favourites = menu.findItem(R.id.menu_item_favourites);

        if (rc.getSize(Author.class) > 0) {
            lf.setFBState(true);
            register.setVisible(false);
//            favourites.setVisible(true);
            login.setTitle(R.string.action_logout);
            username.setText(rc.getItem(Author.class, null).getUsername());
        }

        // возможно, излишний код - если RESULT_OK, то вход уже должен быть - автор по-любому должен быть уже добавлен
        else {
            lf.setFBState(false);
            register.setVisible(true);
  //          favourites.setVisible(false);
            login.setTitle(R.string.action_login);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && (requestCode == LOGIN || requestCode == REGISTER)){
//            lf.lastArticleId = null;
//            Disposable d = lf.getProductsResult()
//                    .subscribe(success -> lf.showProducts(success));
//        }
//    }

    public void navigateTo(Fragment fragment, Boolean back) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment);

        if (back) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rc.close();
    }

    public void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            super.onBackPressed();
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
