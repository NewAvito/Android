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

import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Product;

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

    private Boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rc = new RealmController(this);

//        //myTest
//        {
//            Realm realm = Realm.getDefaultInstance();
//            realm.executeTransaction(trRealm -> {
//                trRealm.deleteAll();
//                Author author = trRealm.createObject(Author.class, "Вася");
//                author.setMobile("89217917922");
//                author.setLocation("Moscow");
//            });
//        }
        new RealmRVAdapter(getApplicationContext(), rc.getBase(Product.class, "date"));

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
                        lf.setFBState(false);
                    } else {
                        intent.putExtra("requestCode", LOGIN);
                        startActivityForResult(intent, LOGIN);
                    }
                    break;
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

        if (rc.getSize(Author.class) > 0) {
            lf.setFBState(true);
            register.setVisible(false);
            login.setTitle(R.string.action_logout);

            username.setText(rc.getItem(Author.class, null).getUsername());

        } else {
            lf.setFBState(false);
            register.setVisible(true);
            login.setTitle(R.string.action_login);
        }
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
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
