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
        startCoundownService();
        finish();
    }

    private void startSnapchatApp() {
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
        startActivity(LaunchIntent);
    }

    private void startCoundownService() {
        startService(new Intent(this, CountdownService.class));
    }
}
