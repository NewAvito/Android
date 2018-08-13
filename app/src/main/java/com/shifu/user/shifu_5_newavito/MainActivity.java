package com.shifu.user.shifu_5_newavito;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ListFragment(), "START")
                .commit();
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
    }

//    @Override
//    public void onBackPressed() {
//        Log.d("Main", "onBackPressed");
//        FragmentManager fm = getSupportFragmentManager();
//        OnBackPressedListener backPressedListener = null;
//
//        for (Fragment fragment: fm.getFragments()) {
//            if (fragment instanceof OnBackPressedListener) {
//                backPressedListener = (OnBackPressedListener) fragment;
//                break;
//            }
//        }
//
//        if (backPressedListener != null) {
//            Log.d("Main", "onBackPressed -> to fragment");
//            backPressedListener.onBackPressed();
//        } else {
//            super.onBackPressed();
//        }
//    }
}
