package com.oumugai.densityadjuster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oumugai.densityadjuster.Utils.SystemLayer;

import java.text.DecimalFormat;


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

        TextView currentDensity = (TextView)findViewById(R.id.textCurrentDensity);
        currentDensity.setText(String.valueOf(getCurrentDensity()));

        final String savedDensity = SystemLayer.getDensityFromBuildProps();
        if (savedDensity != null) {
            TextView view = (TextView)findViewById(R.id.textSavedDensity);
            view.setText(savedDensity);
        }

        Button tryNewDensity = (Button)findViewById(R.id.buttonTryNewDensity);
        if (!SystemLayer.hasSuperUser()) {
            // we can't set densities
            Toast.makeText(this, "No superuser binary found, can't set new density!",
                           Toast.LENGTH_LONG).show();
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
        if (!String.valueOf(getCurrentDensity()).equals(savedDensity)) {
            saveCurrentDensity.setVisibility(View.VISIBLE);

            saveCurrentDensity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    persistCurrentDensity(getCurrentDensity());

                    final String persistedDensity = SystemLayer.getDensityFromBuildProps();
                    if (persistedDensity != null) {
                        TextView tv = (TextView)findViewById(R.id.textSavedDensity);
                        tv.setText(persistedDensity);
                    }

                    // quick sanity check
                    if (String.valueOf(getCurrentDensity()).equals(persistedDensity)) {
                        saveCurrentDensity.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong persisting density! Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        final ImageView arrows = (ImageView)findViewById(R.id.imageArrows);
        arrows.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateDimensions();
                arrows.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void updateDimensions() {
        final ImageView arrows = (ImageView)findViewById(R.id.imageArrows);
        final TextView verticalDimension = (TextView)findViewById(R.id.textVerticalSize);
        final TextView horizontalDimension = (TextView)findViewById(R.id.textHorizontalSize);

        DecimalFormat format = new DecimalFormat("#.00");
        String verticalDp = format.format(convertPixelsToDp(arrows.getMeasuredHeight()));
        String horizontalDp = format.format(convertPixelsToDp(arrows.getMeasuredWidth()));

        verticalDimension.setText(verticalDp + "dp");
        horizontalDimension.setText(horizontalDp + "dp");
    }

    private int getCurrentDensity() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

    private float convertPixelsToDp(float pixels) {
        return pixels / (getCurrentDensity() / 160f);
    }

    private void persistCurrentDensity(int currentDensity) {
        String appDataDir = getApplicationInfo().dataDir;

        FileController.createNewBuildProps(appDataDir, currentDensity);
        SystemLayer.mountSystemReadWrite();
        FileController.copyNewBuildPropsIntoSystem(appDataDir);
        SystemLayer.mountSystemReadOnly();
    }
}
