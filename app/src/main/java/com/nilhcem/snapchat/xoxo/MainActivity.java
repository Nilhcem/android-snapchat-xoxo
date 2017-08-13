package com.nilhcem.snapchat.xoxo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String SNAPCHAT_PACKAGE_NAME = "com.snapchat.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSnapchatApp();
        if (!isFinishing()) {
            startCountdownService();
            finish();
        }
    }

    private void startSnapchatApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
        if (launchIntent == null) {
            Toast.makeText(this, R.string.snapchat_not_found, Toast.LENGTH_LONG).show();
            finish();
        } else {
            startActivity(launchIntent);
        }
    }

    private void startCountdownService() {
        Intent mIntent = new Intent(this, CountdownService.class);
        mIntent.putExtra("shouldMove", false);
        startService(mIntent);
    }
}
