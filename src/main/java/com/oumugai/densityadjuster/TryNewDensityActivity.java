package com.oumugai.densityadjuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oumugai.densityadjuster.Utils.SystemLayer;

import java.io.File;


public class TryNewDensityActivity extends Activity {
    private static final String TAG = "TryNewDensityActivity";
    private static final Integer MINIMUM_DENSITY = 50;
    private static final Integer MAXIUMUM_DENSITY = 500;

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
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                String newDensity = input.getText().toString();
                if (!isValidDensity(newDensity)) {
                    Toast.makeText(getApplicationContext(), "Please enter an integer between " + MINIMUM_DENSITY + " and " + MAXIUMUM_DENSITY, Toast.LENGTH_LONG).show();
                    return;
                }

                view.setEnabled(false);
                findViewById(R.id.textTestInstructions).setVisibility(View.GONE);

                tryNewDensity(Integer.valueOf(newDensity));
            }
        });
    }

    private void tryNewDensity(int newDensity) {
        final String appDataDir = getApplicationInfo().dataDir;

        emitStatus("backing up existing build.prop");
        FileController.backupBuildProps(appDataDir);
        emitStatus("creating new build.prop");
        FileController.createNewBuildProps(appDataDir, newDensity);
        emitStatus("generating restore scripts");
        FileController.generateRestoreScripts(appDataDir);

        emitStatus("remounting system");
        SystemLayer.mountSystemReadWrite();

        // give the system five seconds to remount system
        new Handler().postDelayed(new Runnable() {
            public void run() {
                emitStatus("placing restore script");
                FileController.copyRecoveryScript(appDataDir);
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // let's be ultra paranoid
                File restoreScript = new File(PathHelper.getRestoreScriptPath());
                if (!restoreScript.exists()) {
                    emitStatus("failed to copy restore script!");
                    emitStatus("aborting...");
                    return;
                }

                emitStatus("placing new build.prop");
                FileController.copyNewBuildPropsIntoSystem(appDataDir);

                Intent intent = new Intent(getApplicationContext(), RebootDeviceActivity.class);
                startActivity(intent);
            }
        }, 6000);
    }

    private void emitStatus(String status) {
        TextView statusView = (TextView)findViewById(R.id.textStatus);
        statusView.setText(statusView.getText() + status + "\n");
    }

    public static boolean isValidDensity(String density) {
        try {
            Integer value = Integer.valueOf(density);
            return (value != null && value > MINIMUM_DENSITY && value < MAXIUMUM_DENSITY);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
