package com.oumugai.densityadjuster;

import com.oumugai.densityadjuster.Utils.SystemLayer;

import java.io.File;
import java.io.IOException;
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
        if (fileExistsAtLocation("/system/bin/su")) {
            return "/system/bin/su";
        }
        if (fileExistsAtLocation("/system/xbin/su")) {
            return  "/system/xbin/su";
        }
        return null;
    }

    private static boolean fileExistsAtLocation(String path) {
        File target = new File(path);
        if (!target.exists()) {
            return false;
        }
        try {
            if (isSymlink(target)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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

    public static boolean isSymlink(File file) throws IOException {
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }
}
