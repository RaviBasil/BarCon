package com.ravibasil.thebigdream.barcon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Toast;

import com.ravibasil.thebigdream.barcon.barcode.fragment.QRCodeFragment;
import com.ravibasil.thebigdream.barcon.pdf.fragment.Home;
import com.ravibasil.thebigdream.barcon.textables.TextablesFragment;

public class BarcodeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA_RESULT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

      //  toolbar.setTitle("Shop");

        loadFragment(new Home());
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    setActionBarTitle("QR Code",1);
                    checkPermissionCamera();

                    return true;
                case R.id.navigation_gallery:
                    setActionBarTitle("Textables",2);

                    loadFragment(new TextablesFragment());
                    return true;
                case R.id.navigation_document:
                   setActionBarTitle("Images To PDF",3);
                    loadFragment(new Home());
                    return true;
            }
            return false;
        }
    };

    private void checkPermissionCamera() {

        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA_RESULT);
            } else {
                loadFragment(new QRCodeFragment());
            }
        } else {
            loadFragment(new QRCodeFragment());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFragment(new QRCodeFragment());
                    Toast.makeText(this, R.string.toast_permissions_given, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.toast_insufficient_permissions, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setActionBarTitle(String title,int i) {
        ActionBar actionBar = getSupportActionBar();


        //actionBar.setTitle(text);

        if(i==2) {
            Spannable text = new SpannableString(title);

            text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CCFAEF")));
            actionBar.setTitle(text);
        }else{
            Spannable text = new SpannableString(title);

            text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#424242")));
            actionBar.setTitle(text);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.home != seletedItemId) {
            loadFragment(new Home());
        } else {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
