package com.droidev.flyffuwebviewclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    Menu optionMenu;

    String url = "https://universe.flyff.com/play";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("FlyffU Android - Main Client");

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

            if (sClient.getVisibility() == View.VISIBLE) {

                sClient.setVisibility(View.GONE);

                mClient.setVisibility(View.VISIBLE);

                setTitle("FlyffU Android - Main Client");

            } else if (sClient.getVisibility() == View.GONE) {

                sClient.setVisibility(View.VISIBLE);

                mClient.setVisibility(View.GONE);

                setTitle("FlyffU Android - Second Client");
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

                        optionMenu.findItem(R.id.reloadSecondClient).setEnabled(false);

                        if (mClient.getVisibility() == View.GONE) {

                            mClient.setVisibility(View.VISIBLE);
                        }

                        floatingActionButton.setVisibility(View.GONE);

                        isOpen = false;

                        setTitle("FlyffU Android - Main Client");

                        Toast.makeText(this, "Second Client closed.", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    });
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
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        optionMenu = menu;

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

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(userAgent);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        frameLayout.addView(webView);

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
}