package com.oumugai.densityadjuster;

import android.util.Log;

import com.oumugai.densityadjuster.Utils.Files;
import com.oumugai.densityadjuster.Utils.SystemLayer;

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

    public static boolean createNewBuildProps(String dataDir, int newDensity) {
        try {
            // TODO: we don't really need to read twice
            String buildProperties = Files.convertFileToString(PathHelper.PATH_BUILD_PROP);
            Log.d(TAG, "read build props: " + buildProperties);
            String savedDensity = SystemLayer.getDensityFromBuildProps();

            String currentSetting = SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY + "=" + savedDensity;
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

    public static void generateRestoreScripts(String dataDir) {
        generateInitRestoreScript(dataDir);
        generateRecoveryRestoreScript(dataDir);
    }

    public static boolean generateRecoveryRestoreScript(String dataDir) {
        String blockDevice = SystemLayer.getSystemBlockDevice();

        String restoreScript = "";
        restoreScript += "#!/system/bin/sh" + "\n";
        restoreScript += "" + "\n";
        restoreScript += "mount -o rw,remount " + blockDevice + " /system" + "\n";
        restoreScript += "mv " + PathHelper.PATH_BUILD_PROP_BACKUP + " " + PathHelper.PATH_BUILD_PROP + "\n";
        restoreScript += "rm " + PathHelper.PATH_RESTORE_SCRIPT + "\n";
        restoreScript += "mv " + PathHelper.PATH_RESTORE_SCRIPT_BACKUP + " " + PathHelper.PATH_RESTORE_SCRIPT + "\n";
        restoreScript += "mount -o ro,remount " + blockDevice + " /system" + "\n";
        restoreScript += PathHelper.PATH_RESTORE_SCRIPT + "\n";

        try {
            Files.writeStringToFile(restoreScript, PathHelper.buildRestoreRecoveryScriptSourcePath(dataDir));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "generateRestoreScript() threw: " + e, e);
            return false;
        }
    }

    public static boolean generateInitRestoreScript(String dataDir) {
        String blockDevice = SystemLayer.getSystemBlockDevice();

        String restoreScript = "";
        restoreScript += "#!/system/bin/sh" + "\n";
        restoreScript += "" + "\n";
        restoreScript += "mount -o rw,remount " + blockDevice + " /system" + "\n";
        restoreScript += "mv " + PathHelper.PATH_BUILD_PROP_BACKUP + " " + PathHelper.PATH_BUILD_PROP + "\n";
        restoreScript += "rm " + PathHelper.PATH_RESTORE_INIT_SCRIPT + "\n";
        restoreScript += "mount -o ro,remount " + blockDevice + " /system" + "\n";

        try {
            Files.writeStringToFile(restoreScript, PathHelper.buildRestoreInitScriptSourcePath(dataDir));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "generateRestoreScript() threw: " + e, e);
            return false;
        }
    }

    private static void copyRecoveryScriptIntoEtc(String dataDir) {
        String moveExistingRecoveryScript = "mv " + PathHelper.PATH_RESTORE_SCRIPT + " " + PathHelper.PATH_RESTORE_SCRIPT_BACKUP;
        SystemLayer.executeCommandAsSuperUser(moveExistingRecoveryScript);

        String copyRestoreScriptCommand = "cp " + PathHelper.buildRestoreRecoveryScriptSourcePath(dataDir) + " " + PathHelper.PATH_RESTORE_SCRIPT;
        SystemLayer.executeCommandAsSuperUser(copyRestoreScriptCommand);
        SystemLayer.executeCommandAsSuperUser("chmod 755 " + PathHelper.PATH_RESTORE_SCRIPT);
    }
    private static void copyRestoreScriptIntoInitD(String dataDir) {
        String copyRestoreScriptCommand = "cp " + PathHelper.buildRestoreInitScriptSourcePath(dataDir) + " " + PathHelper.PATH_RESTORE_INIT_SCRIPT;
        SystemLayer.executeCommandAsSuperUser(copyRestoreScriptCommand);
        SystemLayer.executeCommandAsSuperUser("chmod 755 " + PathHelper.PATH_RESTORE_INIT_SCRIPT);
    }

    public static void copyNewBuildPropsIntoSystem(String dataDir) {
        String moveExistingPropsToBackupFileCommand = "mv " + PathHelper.PATH_BUILD_PROP + " " + PathHelper.PATH_BUILD_PROP_BACKUP;
        SystemLayer.executeCommandAsSuperUser(moveExistingPropsToBackupFileCommand);

        String copyNewBuildPropCommand = "cp " + PathHelper.buildNewPropertiesPath(dataDir) + " " + PathHelper.PATH_BUILD_PROP;
        SystemLayer.executeCommandAsSuperUser(copyNewBuildPropCommand);

        SystemLayer.executeCommandAsSuperUser("chmod 644 " + PathHelper.PATH_BUILD_PROP);
    }


    public static void copyRecoveryScript(String dataDir) {
        if (SystemLayer.hasInitDSupport()) {
            copyRestoreScriptIntoInitD(dataDir);
        } else {
            copyRecoveryScriptIntoEtc(dataDir);
        }
    }


}
