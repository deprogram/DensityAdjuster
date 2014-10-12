package com.oumugai.densityadjuster;

import com.oumugai.densityadjuster.Utils.SystemLayer;

import java.io.File;
import java.util.Date;

/**
 * Created by deprogram on 10/8/2014.
 */
public class PathHelper {
    public static final String PATH_BUILD_PROP = "/system/build.prop";
    public static final String PATH_BUILD_PROP_BACKUP = PATH_BUILD_PROP + ".original";
    public static final String PATH_RESTORE_SCRIPT = "/etc/install-recovery.sh";
    public static final String PATH_RESTORE_SCRIPT_BACKUP = PATH_RESTORE_SCRIPT + ".original";
    public static final String PATH_RESTORE_INIT_SCRIPT = "/etc/init.d/99restorebuildprops";
    public static final String PATH_SU_BINARY = getSuperUserPath();  // hmmm static initializers?

    public static String getSuperUserPath() {
        if (new File("/system/bin/su").exists()) {
            return "/system/bin/su";
        }
        if (new File("/system/xbin/su").exists()) {
            return  "/system/xbin/su";
        }
        return null;
    }

    public static String getRestoreScriptPath() {
        return SystemLayer.hasInitDSupport() ? PATH_RESTORE_INIT_SCRIPT : PATH_RESTORE_SCRIPT;
    }

    public static String buildPropertiesBackupPath(String appDataDir) {
        return appDataDir + "/build.prop";
    }
    public static String buildTimeStampedPropertiesBackupPath(String appDataDir) {
        return buildPropertiesBackupPath(appDataDir) + "." + new Date().getTime();
    }
    public static String buildNewPropertiesPath(String appDataDir) {
        return appDataDir + "/build.prop.new";
    }

    public static String buildRestoreRecoveryScriptSourcePath(String dataDir) {
        return dataDir + "/install-recovery.sh";
    }

    public static String buildRestoreInitScriptSourcePath(String dataDir) {
        return dataDir + "/99restorebuildprops";
    }
}
