package com.oumugai.densityadjuster.Utils;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deprogram on 10/8/2014.
 */
public class SystemLayerTest extends AndroidTestCase {
    public void testGetsSystemBlockDeviceFromCommandOutput() {
        List<String> output = new ArrayList<String>(0);
        output.add("rootfs / rootfs ro,relatime 0 0");
        output.add("tmpfs /dev tmpfs rw,nosuid,relatime,mode=755 0 0");
        output.add("devpts /dev/pts devpts rw,relatime,mode=600 0 0");
        output.add("proc /proc proc rw,relatime 0 0");
        output.add("sysfs /sys sysfs rw,relatime 0 0");
        output.add("none /acct cgroup rw,relatime,cpuacct 0 0");
        output.add("tmpfs /mnt/asec tmpfs rw,relatime,mode=755,gid=1000 0 0");
        output.add("tmpfs /mnt/obb tmpfs rw,relatime,mode=755,gid=1000 0 0");
        output.add("none /dev/cpuctl cgroup rw,relatime,cpu 0 0");
        output.add("/dev/block/platform/omap/omap_hsmmc.0/by-name/system /system ext4 ro,relatime,barrier=1,data=ordered 0 0");
        output.add("/dev/block/platform/omap/omap_hsmmc.0/by-name/userdata /data ext4 rw,nosuid,nodev,noatime,errors=panic,barrier=1,nomblk_io_submit,data=ordered 0 0");
        output.add("/dev/block/platform/omap/omap_hsmmc.0/by-name/cache /cache ext4 rw,nosuid,nodev,noatime,errors=panic,barrier=1,nomblk_io_submit,data=ordered 0 0");
        output.add("/dev/block/platform/omap/omap_hsmmc.0/by-name/efs /factory ext4 ro,relatime,barrier=1,data=ordered 0 0");
        output.add("/sys/kernel/debug /sys/kernel/debug debugfs rw,relatime 0 0");
        output.add("/dev/fuse /mnt/sdcard fuse rw,nosuid,nodev,relatime,user_id=1023,group_id=1023,default_permissions,allow_other 0 0");

        String actual = SystemLayer.getSystemBlockDeviceFromCommandOutput(output);
        String expected = "/dev/block/platform/omap/omap_hsmmc.0/by-name/system";

        assertEquals("should have correct block device", expected, actual);
    }
}
