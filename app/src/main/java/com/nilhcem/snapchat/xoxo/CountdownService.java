package com.nilhcem.snapchat.xoxo;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.nilhcem.snapchat.xoxo.core.Compatibility;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class CountdownService extends IntentService {

    private static final int TIMER_IN_SECONDS = 5;
    private static final String OUTPUT_DIR = "snapchatxoxo";
    private static final String TMP_DIR = "/data/local/tmp/";
    private static final String CMD_SCREENCAP = "/system/bin/screencap -p %s";
    private static final String CMD_MOVE = "/system/bin/mv %s %s";

    private boolean shouldMove = false;
    private String moveFileName;
    private WindowManager mWindowManager;
    private TextView mCountdownView;
    private Handler mUiThreadHandler = new Handler(Looper.getMainLooper());

    public CountdownService() {
        super(CountdownService.class.getSimpleName());
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
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            this.shouldMove = intent.getBooleanExtra("shouldMove", false);
            if (shouldMove) {
                Resources resources = getResources();
                mCountdownView.setTextColor(resources.getColor(R.color.countdown_move_text));
                mCountdownView.setBackground(resources.getDrawable(R.drawable.countdown_move_bg));
                this.moveFileName = intent.getStringExtra("fileName");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i = TIMER_IN_SECONDS; i >= 0; i--) {
            updateCountdownUi(i);

            if (i == 0) {
                mWindowManager.removeView(mCountdownView);
                takeScreenshotAsRoot();
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!shouldMove) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startMoveCountdownService(moveFileName);
        }
    }

    private void takeScreenshotAsRoot() {
        Process sh;
        String fname;
        if (!shouldMove)
            fname = Long.toString(System.currentTimeMillis()) + ".png";
        else
            fname = moveFileName;
        File outputFileTmp = new File(TMP_DIR, fname);
        File outputFile = new File(getImagesDirectory(), fname);

        try {
            if (!shouldMove) {
                // Run screencap as su.
                sh = Runtime.getRuntime().exec("su", null, null);
                OutputStream os = sh.getOutputStream();
                os.write((String.format(Locale.US, CMD_SCREENCAP, outputFileTmp.getAbsolutePath())).getBytes("ASCII"));
                os.flush();
                os.close();
                moveFileName = fname;
            } else {
                // Move screenshot to internal storage
                sh = Runtime.getRuntime().exec("su", null, null);
                OutputStream os = sh.getOutputStream();
                os.write((String.format(Locale.US, CMD_MOVE, outputFileTmp.getAbsolutePath(), outputFile.getAbsolutePath())).getBytes("ASCII"));
                os.flush();
                os.close();
            }

            // Do not force the MediaScanner to add the file, otherwise, Snapchat will be notified a screenshot was taken.
        } catch (IOException e) {
        }
    }

    private File getImagesDirectory() {
        File extStore = Environment.getExternalStorageDirectory();
        File outputDir = new File(extStore, OUTPUT_DIR);
        outputDir.mkdirs();
        return outputDir;
    }

    private void updateCountdownUi(final int secondsLeft) {
        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mCountdownView.setText(Integer.toString(secondsLeft));
            }
        });
    }

    private void startMoveCountdownService(String fileName) {
        Intent mIntent = new Intent(this, CountdownService.class);
        mIntent.putExtra("shouldMove", true);
        mIntent.putExtra("fileName", fileName);
        startService(mIntent);
    }
}
