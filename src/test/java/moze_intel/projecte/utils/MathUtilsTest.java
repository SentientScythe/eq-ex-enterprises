package moze_intel.projecte.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

@DisplayName("Test MathUtils")
class MathUtilsTest {

    @Test
    @DisplayName("Test scaleToRedstone")
    void testScaleToRedstone() {
        Assertions.assertEquals(0, MathUtils.scaleToRedstone(0, 100));
        Assertions.assertEquals(0, MathUtils.scaleToRedstone(-10, 100));
        Assertions.assertEquals(15, MathUtils.scaleToRedstone(100, 100));
        Assertions.assertEquals(15, MathUtils.scaleToRedstone(150, 100));
        
        // 50 / 100 = 0.5. 0.5 * 13 + 1 = 7.5 -> round to 8
        Assertions.assertEquals(8, MathUtils.scaleToRedstone(50, 100));
        
        // 1 / 100 = 0.01. 0.01 * 13 + 1 = 1.13 -> round to 1
        Assertions.assertEquals(1, MathUtils.scaleToRedstone(1, 100));
    }

    @Test
    @DisplayName("Test tickToSec")
    void testTickToSec() {
        Assertions.assertEquals(1.0f, MathUtils.tickToSec(20, 20.0f));
        Assertions.assertEquals(0.5f, MathUtils.tickToSec(10, 20.0f));
        Assertions.assertEquals(2.0f, MathUtils.tickToSec(40, 20.0f));
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> MathUtils.tickToSec(20, 0.0f));
        Assertions.assertThrows(IllegalArgumentException.class, () -> MathUtils.tickToSec(20, -1.0f));
    }

    @Test
    @DisplayName("Test secToTicks")
    void testSecToTicks() {
        Assertions.assertEquals(20, MathUtils.secToTicks(1.0f, 20.0f));
        Assertions.assertEquals(10, MathUtils.secToTicks(0.5f, 20.0f));
        Assertions.assertEquals(40, MathUtils.secToTicks(2.0f, 20.0f));
    }

    @Test
    @DisplayName("Test clampToInt")
    void testClampToInt() {
        Assertions.assertEquals(100, MathUtils.clampToInt(BigInteger.valueOf(100)));
        Assertions.assertEquals(Integer.MAX_VALUE, MathUtils.clampToInt(BigInteger.valueOf(Integer.MAX_VALUE)));
        Assertions.assertEquals(Integer.MAX_VALUE, MathUtils.clampToInt(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)));
        Assertions.assertEquals(-100, MathUtils.clampToInt(BigInteger.valueOf(-100)));
    }

    @Test
    @DisplayName("Test clampToLong")
    void testClampToLong() {
        Assertions.assertEquals(100L, MathUtils.clampToLong(BigInteger.valueOf(100L)));
        Assertions.assertEquals(Long.MAX_VALUE, MathUtils.clampToLong(BigInteger.valueOf(Long.MAX_VALUE)));
        Assertions.assertEquals(Long.MAX_VALUE, MathUtils.clampToLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
        Assertions.assertEquals(-100L, MathUtils.clampToLong(BigInteger.valueOf(-100L)));
    }

    @Test
    @DisplayName("Test isGreaterThanLong")
    void testIsGreaterThanLong() {
        Assertions.assertFalse(MathUtils.isGreaterThanLong(BigInteger.valueOf(100L)));
        Assertions.assertFalse(MathUtils.isGreaterThanLong(BigInteger.valueOf(Long.MAX_VALUE)));
        Assertions.assertTrue(MathUtils.isGreaterThanLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
    }
}
