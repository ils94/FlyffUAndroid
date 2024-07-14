package com.droidev.flyffuwebviewclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    WebView mClientWebView, sClientWebView;
    FrameLayout mClient, sClient;
    LinearLayout linearLayout;
    FloatingActionButton floatingActionButton;
    Boolean exit = false, isOpen = false;
    TinyDB tinyDB;

    String rotationLock = "locked";

    Menu optionMenu;

    String url = "https://universe.flyff.com/play";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tinyDB = new TinyDB(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linearLayout = findViewById(R.id.linearLayout);

        mClient = findViewById(R.id.frameLayoutMainClient);

        sClient = findViewById(R.id.frameLayoutSecondClient);

        mClientWebView = new WebView(getApplicationContext());

        sClientWebView = new WebView(getApplicationContext());

        floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(view -> {

            if (sClient.getVisibility() == View.GONE) {

                sClient.setVisibility(View.VISIBLE);

                optionMenu.findItem(R.id.minimizeSecondClient).setTitle("Minimize Second Client");

                optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(true);

                optionMenu.findItem(R.id.minimizeMainClient).setTitle("Minimize Main Client");

                optionMenu.findItem(R.id.minimizeMainClient).setEnabled(true);
            } else {

                sClient.setVisibility(View.GONE);

                optionMenu.findItem(R.id.minimizeSecondClient).setTitle("Maximize Second Client");

                optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(true);

                optionMenu.findItem(R.id.minimizeMainClient).setTitle("Minimize Main Client");

                optionMenu.findItem(R.id.minimizeMainClient).setEnabled(false);

                mClient.setVisibility(View.VISIBLE);
            }
        });

        mainClient();

        if (savedInstanceState == null) {
            mClientWebView.loadUrl(url);
            sClientWebView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            fullScreenOff();
            new Handler().postDelayed(() -> exit = false, 3 * 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.removeAllViews();
        sClient.removeAllViews();
        mClientWebView.destroy();
        sClientWebView.destroy();
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.secondClient:

                if (sClient.getVisibility() == View.GONE && !isOpen) {

                    sClient.setVisibility(View.VISIBLE);

                    secondClient();

                    optionMenu.findItem(R.id.secondClient).setTitle("Close Second Client");

                    optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(true);

                    optionMenu.findItem(R.id.minimizeMainClient).setEnabled(true);

                    optionMenu.findItem(R.id.reloadSecondClient).setEnabled(true);

                    floatingActionButton.setVisibility(View.VISIBLE);

                    isOpen = true;
                } else {

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Are you sure you want to close the second client?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No", null)
                            .show();

                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                    positiveButton.setOnClickListener(v -> {

                        sClient.removeAllViews();

                        sClientWebView.loadUrl("about:blank");

                        sClient.setVisibility(View.GONE);

                        optionMenu.findItem(R.id.secondClient).setTitle("Open Second Client");

                        optionMenu.findItem(R.id.minimizeSecondClient).setTitle("Minimize Second Client");

                        optionMenu.findItem(R.id.minimizeMainClient).setTitle("Minimize Main Client");

                        optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(false);

                        optionMenu.findItem(R.id.minimizeMainClient).setEnabled(false);

                        optionMenu.findItem(R.id.reloadSecondClient).setEnabled(false);

                        if (mClient.getVisibility() == View.GONE) {

                            mClient.setVisibility(View.VISIBLE);
                        }

                        floatingActionButton.setVisibility(View.GONE);

                        isOpen = false;

                        dialog.dismiss();
                    });
                }

                break;

            case R.id.minimizeSecondClient:

                if (sClient.getVisibility() == View.VISIBLE) {

                    sClient.setVisibility(View.GONE);

                    optionMenu.findItem(R.id.minimizeSecondClient).setTitle("Maximize Second Client");

                    optionMenu.findItem(R.id.minimizeMainClient).setEnabled(false);
                } else {

                    sClient.setVisibility(View.VISIBLE);

                    optionMenu.findItem(R.id.minimizeSecondClient).setTitle("Minimize Second Client");

                    optionMenu.findItem(R.id.minimizeMainClient).setEnabled(true);
                }

                break;

            case R.id.minimizeMainClient:

                if (mClient.getVisibility() == View.VISIBLE) {

                    mClient.setVisibility(View.GONE);

                    optionMenu.findItem(R.id.minimizeMainClient).setTitle("Maximize Main Client");

                    optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(false);
                } else {

                    mClient.setVisibility(View.VISIBLE);

                    optionMenu.findItem(R.id.minimizeMainClient).setTitle("Minimize Main Client");

                    optionMenu.findItem(R.id.minimizeSecondClient).setEnabled(true);
                }

                break;

            case R.id.reloadMainClient:

                mClientWebView.loadUrl(url);

                break;

            case R.id.reloadSecondClient:

                sClientWebView.loadUrl(url);

                break;

            case R.id.fullScreen:

                fullScreenOn();

                break;

            case R.id.rotation:

                lockUnlockRotation();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        optionMenu = menu;

        loadRotationConfig();

        return super.onCreateOptionsMenu(menu);
    }

    private void fullScreenOn() {

        View decorView = getWindow().getDecorView();

        Objects.requireNonNull(getSupportActionBar()).hide();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void fullScreenOff() {

        View decorView = getWindow().getDecorView();

        Objects.requireNonNull(getSupportActionBar()).show();

        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void mainClient() {

        createWebViewer(mClientWebView, mClient);
    }

    private void secondClient() {

        createWebViewer(sClientWebView, sClient);
    }

    private void lockUnlockRotation() {

        if (rotationLock.equals("unlocked")) {

            rotationLock = "locked";

            Toast.makeText(this, "Rotation is locked", Toast.LENGTH_SHORT).show();

            optionMenu.findItem(R.id.rotation).setTitle("Rotation Locked");

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        } else if (rotationLock.equals("locked")) {

            rotationLock = "unlocked";

            Toast.makeText(this, "Rotation is unlocked", Toast.LENGTH_SHORT).show();

            optionMenu.findItem(R.id.rotation).setTitle("Rotation Unlocked");

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

        tinyDB.remove("rotation");
        tinyDB.putString("rotation", rotationLock);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void loadRotationConfig() {

        if (!tinyDB.getString("orientation").isEmpty()) {

            if (tinyDB.getString("orientation").equals("landscape")) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            } else if (tinyDB.getString("orientation").equals("portrait")) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }
        }

        if (!tinyDB.getString("rotation").isEmpty()) {

            rotationLock = tinyDB.getString("rotation");

            if (rotationLock.equals("locked")) {

                optionMenu.findItem(R.id.rotation).setTitle("Rotation Locked");

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            } else if (rotationLock.equals("unlocked")) {

                optionMenu.findItem(R.id.rotation).setTitle("Rotation Unlocked");

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        } else {

            tinyDB.putString("rotation", "locked");

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void createWebViewer(WebView webView, FrameLayout frameLayout) {

        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    if (!v.hasFocus()) {
                        v.requestFocus();
                    }
                    break;
            }
            return false;
        });

        frameLayout.addView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("FlyffU WebViewClient");

        webView.loadUrl(url);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mClientWebView.saveState(outState);
        sClientWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mClientWebView.restoreState(savedInstanceState);
        sClientWebView.saveState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        tinyDB.remove("orientation");

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            tinyDB.putString("orientation", "landscape");

            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            tinyDB.putString("orientation", "portrait");

            linearLayout.setOrientation(LinearLayout.VERTICAL);
        }

    }
}