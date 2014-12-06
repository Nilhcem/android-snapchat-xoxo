package com.nilhcem.snapchat.xoxo;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class CountdownService extends IntentService {

    private static final int FOREGROUND_ID = 1338;
    private static final long SLEEP_TIME = 4000;

    private static final String OUTPUT_DIR = "snapchatxoxo";
    private static final String CHARSET_NAME = "ASCII";
    private static final String SCREENSHOTS_EXT = ".png";

    private static final String CMD_SU = "su";
    private static final String CMD_SCREENCAP = "/system/bin/screencap -p %s";

    public CountdownService() {
        super(CountdownService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startForeground(FOREGROUND_ID, buildForegroundNotification());

        try {
            Thread.sleep(SLEEP_TIME);
            takeScreenshotAsRoot();
            stopForeground(true);
        } catch (Exception e) {
        }
    }

    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setTicker(getString(R.string.say_cheese))
                .build();
    }

    private void takeScreenshotAsRoot() {
        Process sh;
        File outputFile = new File(getImagesDirectory(), Long.toString(System.currentTimeMillis()) + SCREENSHOTS_EXT);

        try {
            // Run screencap as su.
            sh = Runtime.getRuntime().exec(CMD_SU, null, null);
            OutputStream os = sh.getOutputStream();
            os.write((String.format(Locale.US, CMD_SCREENCAP, outputFile.getAbsolutePath())).getBytes(CHARSET_NAME));
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
