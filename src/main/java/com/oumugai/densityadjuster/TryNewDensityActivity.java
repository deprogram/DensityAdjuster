package com.oumugai.densityadjuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oumugai.densityadjuster.Utils.SystemLayer;


public class TryNewDensityActivity extends Activity {
    private static final String TAG = "TryNewDensityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_new_density);

        final String currentDensity = SystemLayer.getProperty(SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY);
        final EditText input = (EditText)findViewById(R.id.inputNewDensity);

        if (currentDensity != null) {
            input.setText(currentDensity);
        }

        findViewById(R.id.buttonTestNewDensity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: sanitize input
                view.setEnabled(false);
                tryNewDensity(input.getText().toString());
            }
        });
    }

    private void tryNewDensity(String newDensity) {
        String currentDensity = SystemLayer.getProperty(SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY);

        if (currentDensity == null) {
            // TODO: throw
            return;
        }

        final String appDataDir = getApplicationInfo().dataDir;

        emitStatus("backing up existing build.prop");
        FileController.backupBuildProps(appDataDir);
        emitStatus("creating new build.prop");
        FileController.createNewBuildProps(appDataDir, currentDensity, newDensity);
        emitStatus("generating restore script");
        FileController.generateRestoreScript(appDataDir);

        emitStatus("remounting system");
        SystemLayer.mountSystemReadWrite();

        // give the system five seconds to remount system
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    emitStatus("placing restore script");
                    FileController.copyRestoreScriptIntoInitD(appDataDir);
                    emitStatus("placing new build.prop");
                    FileController.copyNewBuildPropsIntoSystem(appDataDir);

                    Intent intent = new Intent(getApplicationContext(), RebootDeviceActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "tryNewDensity() threw copying files into system: " + e, e);
                }
            }
        }, 5000);
    }

    private void emitStatus(String status) {
        TextView statusView = (TextView)findViewById(R.id.textStatus);
        statusView.setText(statusView.getText() + status + "\n");
    }
}
