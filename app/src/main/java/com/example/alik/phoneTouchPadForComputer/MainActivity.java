package com.example.alik.phoneTouchPadForComputer;

import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.alik.phoneTouchPadForComputer.fragments.TouchPadFragment;

public class MainActivity extends AppCompatActivity implements TouchPadFragment.OnTouchPadFragmentInteractionListener {

    public static FragmentManager fragmentManager;
    public static int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new TouchPadFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onTouchPadFragmentInteraction(Uri uri) {

    }
}
