package com.nilhcem.snapchat.xoxo;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.io.OutputStream;

public class CountdownService extends IntentService {

    private static int FOREGROUND_ID = 1338;
    private static final long SLEEP_TIME = 4000;

    public CountdownService() {
        super(CountdownService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startForeground(FOREGROUND_ID, buildForegroundNotification());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SLEEP_TIME);
                    takeScreenshotAsRoot();
                    stopForeground(true);
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setTicker(getString(R.string.say_cheese))
                .build();
    }

    private void takeScreenshotAsRoot() {
        Process sh;
        try {
            sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p " + "/sdcard/snapshat-" + Long.toString(System.currentTimeMillis()) + ".png").getBytes("ASCII"));
            os.flush();
            os.close();
        } catch (IOException e) {
        }
    }
}
