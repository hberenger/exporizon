package com.bureau.nocomment.exporizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bureau.nocomment.exporizon.App;

import java.io.IOException;

/**
 * Created by RVB on 03/12/2016.
 */
public class MainActivity extends AppCompatActivity {

    private boolean shouldLoadAppContent = false; // TODO: 01/06/2016 improve this.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldLoadAppContent = true;
        ((App) getApplicationContext()).getBeaconDetector().initialize(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shouldLoadAppContent = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!shouldLoadAppContent) {
            finish();
            return;
        }
        shouldLoadAppContent = false;
        startRelevantActivity();
    }

    private void startRelevantActivity() {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }
}
