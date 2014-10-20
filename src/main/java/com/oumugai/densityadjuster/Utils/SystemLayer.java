package com.oumugai.densityadjuster.Utils;

import android.util.Log;

import com.oumugai.densityadjuster.PathHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deprogram on 10/8/2014.
 */
public class SystemLayer {
    public static final java.lang.String SYSTEM_PROPERTY_LCD_DENSITY = "ro.sf.lcd_density";
    private static final String TAG = "SystemLayer";

    public static String getProperty(String key) {
        List<String> output = getProcessOutput(new String[]{"/system/bin/getprop", key});
        return output.get(0);
    }

    public static String getSystemBlockDevice() {
        List<String> output = getProcessOutput(new String[]{"/system/bin/mount"});
        return getSystemBlockDeviceFromCommandOutput(output);
    }

    private static List<String> getProcessOutput(String[] args) {
        List<String> output = new ArrayList<String>(0);
        try {
            Process p = new ProcessBuilder(args).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                output.add(line);
            }
            p.destroy();
        } catch (IOException e) {
            Log.e(TAG, "getProcessOutput failed: " + e, e);
        }
        return output;
    }

    protected static String getSystemBlockDeviceFromCommandOutput(List<String> output) {
        for (String line : output) {
            String[] components = line.split(" ");
            if (components.length > 2 && "/system".equals(components[1])) {
                return components[0];
            }
        }

        return null;
    }

    public static void rebootNow() {
        try {
            Runtime.getRuntime().exec(new String[]{PathHelper.PATH_SU_BINARY, "-c", "reboot now"});
        } catch (IOException e) {
            Log.e(TAG, "rebootNow() failed: " + e, e);
        }
    }

    public static boolean hasSuperUser() {
        return PathHelper.PATH_SU_BINARY != null;
    }

    public static boolean executeCommandAsSuperUser(String command) {
        try {
            Log.d(TAG, "executing command: " + command);
            Runtime.getRuntime().exec("su"); // ok, on slim rom the first su call was failing
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            return true;
        } catch (Exception e) {
            Log.e(TAG, "executeCommandAsSuperUser() failed for command " + command + ": " + e, e);
            return false;
        }
    }

    public static void mountSystemReadWrite() {
        String blockDevice = SystemLayer.getSystemBlockDevice();
        executeCommandAsSuperUser("mount -o rw,remount " + blockDevice + " /system");
    }
    public static void mountSystemReadOnly() {
        String blockDevice = SystemLayer.getSystemBlockDevice();
        executeCommandAsSuperUser("mount -o ro,remount " + blockDevice + " /system");
    }

    public static String getDensityFromBuildProps() {
        List<String> buildProps = Files.convertFileToStrings(PathHelper.PATH_BUILD_PROP);
        for (String property : buildProps) {
            if (property.startsWith(SystemLayer.SYSTEM_PROPERTY_LCD_DENSITY)) {
                String[] tuple = property.split("=");
                return tuple[1];
            }
        }
        return null;
    }

    public static boolean hasInitDSupport() {
        return new File("/etc/init.d").exists();
    }
}
