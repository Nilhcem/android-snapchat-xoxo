package com.nilhcem.snapchat.xoxo;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.nilhcem.snapchat.xoxo.core.Compatibility;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class CountdownService extends Service {

    private static final String OUTPUT_DIR = "snapchatxoxo";
    private static final String CMD_SCREENCAP = "/system/bin/screencap -p %s";

    private WindowManager mWindowManager;
    private TextView mCountdownView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new CountDownTimer(5000, 250) {
            @Override
            public void onTick(final long millisUntilFinished) {
                mCountdownView.setText(Integer.toString(Math.round((float) millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                mWindowManager.removeView(mCountdownView);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        takeScreenshotAsRoot();
                        return null;
                    }
                }.execute();

                stopSelf();
            }
        }.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Resources resources = getResources();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        int textSize = Compatibility.convertDpToIntPixel(10f, this);
        int paddingSize = Compatibility.convertDpToIntPixel(20f, this);
        int countdownSize = Compatibility.convertDpToIntPixel(50f, this);

        mCountdownView = new TextView(this);
        mCountdownView.setGravity(Gravity.CENTER);
        mCountdownView.setTextSize(textSize);
        mCountdownView.setTextColor(resources.getColor(R.color.countdown_text));
        mCountdownView.setBackground(resources.getDrawable(R.drawable.countdown_bg));
        Compatibility.setElevation(4f, mCountdownView);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                countdownSize, countdownSize,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = paddingSize;
        params.y = paddingSize;

        mWindowManager.addView(mCountdownView, params);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void takeScreenshotAsRoot() {
        Process sh;
        File outputFile = new File(getImagesDirectory(), Long.toString(System.currentTimeMillis()) + ".png");

        try {
            // Run screencap as su.
            sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write((String.format(Locale.US, CMD_SCREENCAP, outputFile.getAbsolutePath())).getBytes("ASCII"));
            os.flush();
            os.close();

            // Force the MediaScanner to add the file (so it is visible on the gallery).
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
        } catch (IOException e) {
        }
    }

    private File getImagesDirectory() {
        File extStore = Environment.getExternalStorageDirectory();
        File outputDir = new File(extStore, OUTPUT_DIR);
        outputDir.mkdirs();
        return outputDir;
    }
}
