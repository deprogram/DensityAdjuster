package com.oumugai.densityadjuster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.oumugai.densityadjuster.Utils.SystemLayer;


public class RebootDeviceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot_device);

        findViewById(R.id.buttonReboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemLayer.rebootNow();
            }
        });
    }
}
