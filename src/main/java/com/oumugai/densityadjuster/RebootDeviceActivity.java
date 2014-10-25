package com.oumugai.densityadjuster;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

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


        // TODO can we reliably get exit status from su?
        // give the system time to settle before rebooten
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Button rebootButton = (Button)findViewById(R.id.buttonReboot);
                rebootButton.setEnabled(true);
                rebootButton.setText("Reboot Now");
            }
        }, 4500);
    }
}
