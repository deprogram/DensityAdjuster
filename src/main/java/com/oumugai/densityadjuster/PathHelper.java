package com.oumugai.densityadjuster;

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
    public static final String PATH_SU_BINARY = "/system/bin/su";


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
