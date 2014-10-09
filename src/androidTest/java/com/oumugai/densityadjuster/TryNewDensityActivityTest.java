package com.oumugai.densityadjuster;

import android.test.AndroidTestCase;

/**
 * Created by deprogram on 10/8/2014.
 */
public class TryNewDensityActivityTest extends AndroidTestCase {
    public void testIsValidDensity() {
        assertTrue(TryNewDensityActivity.isValidDensity("320"));
        assertTrue(TryNewDensityActivity.isValidDensity("120"));
        assertTrue(TryNewDensityActivity.isValidDensity("480"));

        assertFalse(TryNewDensityActivity.isValidDensity("10"));
        assertFalse(TryNewDensityActivity.isValidDensity("600"));
        assertFalse(TryNewDensityActivity.isValidDensity("-20"));
        assertFalse(TryNewDensityActivity.isValidDensity("abc"));
        assertFalse(TryNewDensityActivity.isValidDensity(null));
    }
}
