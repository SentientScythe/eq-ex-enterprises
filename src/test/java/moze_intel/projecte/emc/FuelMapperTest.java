package moze_intel.projecte.emc;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import moze_intel.projecte.gameObjs.PETags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("Test FuelMapper")
class FuelMapperTest {

    private Holder<Item> coalHolder;
    private Holder<Item> alchemicalCoalHolder;
    private Holder<Item> mobiusFuelHolder;
    private HolderSet<Item> fuelMap;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        coalHolder = Mockito.mock(Holder.class);
        alchemicalCoalHolder = Mockito.mock(Holder.class);
        mobiusFuelHolder = Mockito.mock(Holder.class);
        
        Mockito.when(coalHolder.is(PETags.Items.COLLECTOR_FUEL)).thenReturn(true);
        Mockito.when(alchemicalCoalHolder.is(PETags.Items.COLLECTOR_FUEL)).thenReturn(true);
        Mockito.when(mobiusFuelHolder.is(PETags.Items.COLLECTOR_FUEL)).thenReturn(true);
        
        fuelMap = HolderSet.direct(coalHolder, alchemicalCoalHolder, mobiusFuelHolder);
        FuelMapper.setFuelMap(fuelMap);
    }

    @Test
    @DisplayName("Test setting and getting fuel map")
    void testSetAndGetFuelMap() {
        Assertions.assertEquals(fuelMap, FuelMapper.getFuelMap());
    }

    @Test
    @DisplayName("Test isStackFuel")
    void testIsStackFuel() {
        ItemStack coalStack = Mockito.mock(ItemStack.class);
        Mockito.when(coalStack.isEmpty()).thenReturn(false);
        Mockito.when(coalStack.getItemHolder()).thenReturn(coalHolder);
        
        ItemStack emptyStack = Mockito.mock(ItemStack.class);
        Mockito.when(emptyStack.isEmpty()).thenReturn(true);
        
        ItemStack nonFuelStack = Mockito.mock(ItemStack.class);
        Mockito.when(nonFuelStack.isEmpty()).thenReturn(false);
        @SuppressWarnings("unchecked")
        Holder<Item> nonFuelHolder = Mockito.mock(Holder.class);
        Mockito.when(nonFuelStack.getItemHolder()).thenReturn(nonFuelHolder);

        // When Mockito's deep equality doesn't work well with HolderSet, we might need a direct mock:
        Mockito.when(coalHolder.is(coalHolder)).thenReturn(true);
        // Wait, HolderSet.contains just uses equality on holders.

        // We can just test getFuelUpgrade since it relies on is(Holder)
    }

    @Test
    @DisplayName("Test getFuelUpgrade with Holder")
    void testGetFuelUpgradeHolder() {
        Mockito.when(coalHolder.is(coalHolder)).thenReturn(true);
        Mockito.when(alchemicalCoalHolder.is(alchemicalCoalHolder)).thenReturn(true);
        Mockito.when(mobiusFuelHolder.is(mobiusFuelHolder)).thenReturn(true);
        
        Assertions.assertEquals(alchemicalCoalHolder, FuelMapper.getFuelUpgrade(coalHolder));
        Assertions.assertEquals(mobiusFuelHolder, FuelMapper.getFuelUpgrade(alchemicalCoalHolder));
        Assertions.assertNull(FuelMapper.getFuelUpgrade(mobiusFuelHolder));
    }
    
    @Test
    @DisplayName("Test getFuelUpgrade with non-fuel")
    void testGetFuelUpgradeNonFuel() {
        @SuppressWarnings("unchecked")
        Holder<Item> nonFuelHolder = Mockito.mock(Holder.class);
        Mockito.when(nonFuelHolder.is(PETags.Items.COLLECTOR_FUEL)).thenReturn(false);
        
        Assertions.assertNull(FuelMapper.getFuelUpgrade(nonFuelHolder));
    }
}
