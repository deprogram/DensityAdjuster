package com.oumugai.densityadjuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oumugai.densityadjuster.Utils.SystemLayer;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onResume() {
        super.onResume();

        final String currentDensity = SystemLayer.getProperty(SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY);
        if (currentDensity != null) {
            TextView tv = (TextView)findViewById(R.id.textCurrentDensity);
            tv.setText(currentDensity);
        }

        final String savedDensity = SystemLayer.getDensityFromBuildProps();
        if (savedDensity != null) {
            TextView tv = (TextView)findViewById(R.id.textSavedDensity);
            tv.setText(savedDensity);
        }

        Button tryNewDensity = (Button)findViewById(R.id.buttonTryNewDensity);
        if (!SystemLayer.hasSuperUser()) {
            // we can't set densities
            Toast.makeText(this, "No superuser binary found, can't set new density.", Toast.LENGTH_LONG).show();
            tryNewDensity.setEnabled(false);
        } else {
            tryNewDensity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), TryNewDensityActivity.class));
                }
            });
        }

        final Button saveCurrentDensity = (Button)findViewById(R.id.buttonPersistExistingDensity);
        if (!currentDensity.equals(savedDensity)) {
            saveCurrentDensity.setVisibility(View.VISIBLE);

            saveCurrentDensity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    persistCurrentDensity(currentDensity);

                    final String savedDensity = SystemLayer.getDensityFromBuildProps();
                    if (savedDensity != null) {
                        TextView tv = (TextView)findViewById(R.id.textSavedDensity);
                        tv.setText(savedDensity);
                    }
                    saveCurrentDensity.setVisibility(View.GONE);
                }
            });
        }
    }

    private void persistCurrentDensity(String currentDensity) {
        String appDataDir = getApplicationInfo().dataDir;

        FileController.createNewBuildProps(appDataDir, currentDensity);
        SystemLayer.mountSystemReadWrite();
        FileController.copyNewBuildPropsIntoSystem(appDataDir);
        SystemLayer.mountSystemReadOnly();
    }
}
