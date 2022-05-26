package com.droidev.flyffuwebviewclient;

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

public class MainActivity extends AppCompatActivity {

    WebView mClientWebView, sClientWebView;
    FrameLayout mClient, sClient;
    LinearLayout linearLayout;
    Boolean exit = false, isOpen = false;

    Menu optionMenu;

    String url = "https://universe.flyff.com/play";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fullScreenOn();

        linearLayout = findViewById(R.id.linearLayout);

        mClient = findViewById(R.id.frameLayoutMainClient);

        sClient = findViewById(R.id.frameLayoutSecondClient);

        mClientWebView = new WebView(getApplicationContext());

        sClientWebView = new WebView(getApplicationContext());

        mainClient();
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

                mClientWebView.reload();

                break;

            case R.id.reloadSecondClient:

                sClientWebView.reload();

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

        getSupportActionBar().hide();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void fullScreenOff() {

        View decorView = getWindow().getDecorView();

        getSupportActionBar().show();

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

        frameLayout.addView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("FlyffU WebViewClient");

        webView.loadUrl(url);
    }
}