package com.nilhcem.snapchat.xoxo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    private static final String SNAPCHAT_PACKAGE_NAME = "com.snapchat.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSnapchatApp();
        startCountdownService();
        finish();
    }

    private void startSnapchatApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
        startActivity(launchIntent);
    }

    private void startCountdownService() {
        startService(new Intent(this, CountdownService.class));
    }
}
