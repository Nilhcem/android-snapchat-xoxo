package com.nilhcem.snapchat.xoxo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity {

    private static final long SLEEP_TIME = 4000;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mLayout.getWidth() > 0) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Thread.sleep(SLEEP_TIME);
                                takeScreenshotAsRoot();
                            } catch (Exception e) {
                            }
                            return null;
                        }
                    }.execute();
                    mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    void takeScreenshotAsRoot() throws IOException {
        Process sh = Runtime.getRuntime().exec("su", null, null);
        OutputStream os = sh.getOutputStream();
        os.write(("/system/bin/screencap -p " + "/sdcard/snapshat-" + Long.toString(System.currentTimeMillis()) + ".png").getBytes("ASCII"));
        os.flush();
        os.close();
    }
}
