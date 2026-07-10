package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Set;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.config.MappingConfig;
import moze_intel.projecte.api.mapper.IEMCMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;

@DisplayName("Test EMCMappingHandler")
class EMCMappingHandlerTest {

    private ItemInfo item1;
    private ItemInfo item2;
    private Object2LongMap<ItemInfo> mockMap;
    private MockedStatic<MappingConfig> mockConfig;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        mockConfig = Mockito.mockStatic(MappingConfig.class);
        mockConfig.when(() -> MappingConfig.isEnabled(Mockito.any(moze_intel.projecte.api.components.IDataComponentProcessor.class))).thenReturn(true);
        mockConfig.when(() -> MappingConfig.isEnabled(Mockito.any(IEMCMapper.class))).thenReturn(true);

        EMCMappingHandler.clearEmcMap();
        
        Holder<Item> mockHolder1 = Mockito.mock(Holder.class);
        Mockito.when(mockHolder1.kind()).thenReturn(Holder.Kind.REFERENCE);
        
        Holder<Item> mockHolder2 = Mockito.mock(Holder.class);
        Mockito.when(mockHolder2.kind()).thenReturn(Holder.Kind.REFERENCE);
        
        item1 = ItemInfo.fromItem(mockHolder1, net.minecraft.core.component.DataComponentPatch.EMPTY);
        item2 = ItemInfo.fromItem(mockHolder2, net.minecraft.core.component.DataComponentPatch.EMPTY);
        
        mockMap = new Object2LongOpenHashMap<>();
        mockMap.put(item1, 100L);
        mockMap.put(item2, 250L);
    }

    @AfterEach
    void tearDown() {
        if (mockConfig != null) {
            mockConfig.close();
        }
    }

    @Test
    @DisplayName("Test updating and getting EMC values")
    void testUpdateAndGetEmcValues() {
        Assertions.assertEquals(0, EMCMappingHandler.getEmcMapSize());
        Assertions.assertFalse(EMCMappingHandler.hasEmcValue(item1));
        
        int size = EMCMappingHandler.updateEmcValues(mockMap);
        
        Assertions.assertEquals(2, size);
        Assertions.assertEquals(2, EMCMappingHandler.getEmcMapSize());
        
        Assertions.assertTrue(EMCMappingHandler.hasEmcValue(item1));
        Assertions.assertEquals(100L, EMCMappingHandler.getStoredEmcValue(item1));
        
        Assertions.assertTrue(EMCMappingHandler.hasEmcValue(item2));
        Assertions.assertEquals(250L, EMCMappingHandler.getStoredEmcValue(item2));
    }
    
    @Test
    @DisplayName("Test clearEmcMap")
    void testClearEmcMap() {
        EMCMappingHandler.updateEmcValues(mockMap);
        Assertions.assertEquals(2, EMCMappingHandler.getEmcMapSize());
        
        EMCMappingHandler.clearEmcMap();
        
        Assertions.assertEquals(0, EMCMappingHandler.getEmcMapSize());
        Assertions.assertFalse(EMCMappingHandler.hasEmcValue(item1));
        Assertions.assertEquals(0L, EMCMappingHandler.getStoredEmcValue(item1));
    }
    
    @Test
    @DisplayName("Test getMappedItems")
    void testGetMappedItems() {
        Set<ItemInfo> emptyItems = EMCMappingHandler.getMappedItems();
        Assertions.assertTrue(emptyItems.isEmpty());
        
        EMCMappingHandler.updateEmcValues(mockMap);
        
        Set<ItemInfo> items = EMCMappingHandler.getMappedItems();
        Assertions.assertEquals(2, items.size());
        Assertions.assertTrue(items.contains(item1));
        Assertions.assertTrue(items.contains(item2));
    }
}
