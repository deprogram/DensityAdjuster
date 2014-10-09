package com.oumugai.densityadjuster;

import android.util.Log;

import com.oumugai.densityadjuster.Utils.Files;
import com.oumugai.densityadjuster.Utils.SystemLayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by deprogram on 10/8/2014.
 */
public class FileController {
    private static final String TAG = "FileController";

    public static boolean backupBuildProps(String dataDir) {

        try {
            Files.copyFile(PathHelper.PATH_BUILD_PROP,
                    PathHelper.buildPropertiesBackupPath(dataDir));
            Files.copyFile(PathHelper.PATH_BUILD_PROP,
                    PathHelper.buildTimeStampedPropertiesBackupPath(dataDir));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "failed to back up build.props: " + e, e);
            return false;
        }
    }

    public static boolean createNewBuildProps(String dataDir, String currentDensity, String newDensity) {
        try {
            String buildProperties = Files.convertFileToString(PathHelper.PATH_BUILD_PROP);
            Log.d(TAG, "read build props: " + buildProperties);

            String currentSetting = SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY + "=" + currentDensity;
            String newSetting = SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY + "=" + newDensity;

            String newBuildProperties = buildProperties.replace(currentSetting, newSetting);
            Log.d(TAG, "new build props: " + newBuildProperties);
            Files.writeStringToFile(newBuildProperties, PathHelper.buildNewPropertiesPath(dataDir));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "createNewBuildProps() threw: " + e, e);
            return false;
        }
    }

    public static boolean generateRestoreScript(String dataDir) {
        String blockDevice = SystemLayer.getSystemBlockDevice();

        String restoreScript = "";
        restoreScript += "#!/system/bin/sh" + "\n";
        restoreScript += "" + "\n";
        restoreScript += "mount -o rw,remount " + blockDevice + " /system" + "\n";
        restoreScript += "mv " + PathHelper.PATH_BUILD_PROP_BACKUP + " " + PathHelper.PATH_BUILD_PROP + "\n";
        restoreScript += "rm " + PathHelper.PATH_RESTORE_SCRIPT + "\n";
        restoreScript += "mount -o ro,remount " + blockDevice + " /system" + "\n";

        try {
            Files.writeStringToFile(restoreScript, PathHelper.buildRestoreScriptSourcePath(dataDir));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "generateRestoreScript() threw: " + e, e);
            return false;
        }
    }

    public static void copyRestoreScriptIntoInitD(String dataDir) throws Exception {
        String copyRestoreScriptCommand = "cp " + PathHelper.buildRestoreScriptSourcePath(dataDir) + " " + PathHelper.PATH_RESTORE_SCRIPT;
        SystemLayer.executeCommandAsSuperUser(copyRestoreScriptCommand);
        // let's be ultra paranoid
        File restoreScript = new File(PathHelper.PATH_RESTORE_SCRIPT);
        if (!restoreScript.exists()) {
            throw new Exception("failed to copy restore script!");
        }
        SystemLayer.executeCommandAsSuperUser("chmod 755 " + PathHelper.PATH_RESTORE_SCRIPT);
    }

    public static void copyNewBuildPropsIntoSystem(String dataDir) {
        String moveExistingPropsToBackupFileCommand = "mv " + PathHelper.PATH_BUILD_PROP + " " + PathHelper.PATH_BUILD_PROP_BACKUP;
        SystemLayer.executeCommandAsSuperUser(moveExistingPropsToBackupFileCommand);

        String copyNewBuildPropCommand = "cp " + PathHelper.buildNewPropertiesPath(dataDir) + " " + PathHelper.PATH_BUILD_PROP;
        SystemLayer.executeCommandAsSuperUser(copyNewBuildPropCommand);

        SystemLayer.executeCommandAsSuperUser("chmod 644 " + PathHelper.PATH_BUILD_PROP);
    }
}
