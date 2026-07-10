package moze_intel.projecte.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import net.minecraft.world.item.ItemStack;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import org.mockito.Mockito;

@DisplayName("Test EMCHelper")
class EMCHelperTest {

    @Test
    @DisplayName("Test intMapOf")
    void testIntMapOf() {
        Object2IntMap<String> map1 = EMCHelper.intMapOf("A", 1);
        Assertions.assertEquals(1, map1.size());
        Assertions.assertEquals(1, map1.getInt("A"));

        Object2IntMap<String> map2 = EMCHelper.intMapOf("A", 1, "B", 2);
        Assertions.assertEquals(2, map2.size());
        Assertions.assertEquals(1, map2.getInt("A"));
        Assertions.assertEquals(2, map2.getInt("B"));

        Object2IntMap<String> map3 = EMCHelper.intMapOf("A", 1, "B", 2, "C", 3);
        Assertions.assertEquals(3, map3.size());
        Assertions.assertEquals(3, map3.getInt("C"));

        Object2IntMap<String> map4 = EMCHelper.intMapOf("A", 1, "B", 2, "C", 3, "D", 4);
        Assertions.assertEquals(4, map4.size());
        Assertions.assertEquals(4, map4.getInt("D"));
    }

    @Test
    @DisplayName("Test formatEmc")
    void testFormatEmc() {
        Assertions.assertEquals("100", EMCHelper.formatEmc(100L));
        Assertions.assertEquals("1,000", EMCHelper.formatEmc(1000L));
        Assertions.assertEquals("100.5", EMCHelper.formatEmc(100.5));
    }
}
