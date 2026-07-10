package moze_intel.projecte.impl.capability;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;

@DisplayName("Test KnowledgeImpl")
class KnowledgeImplTest {

    @Test
    @DisplayName("Test EMC get and set")
    void testEmc() {
        IKnowledgeProvider provider = KnowledgeImpl.wrapAttachment(new KnowledgeImpl.KnowledgeAttachment());
        
        Assertions.assertEquals(BigInteger.ZERO, provider.getEmc());
        
        provider.setEmc(BigInteger.valueOf(100));
        Assertions.assertEquals(BigInteger.valueOf(100), provider.getEmc());
    }

    @Test
    @DisplayName("Test Full Knowledge")
    void testFullKnowledge() {
        IKnowledgeProvider provider = KnowledgeImpl.wrapAttachment(new KnowledgeImpl.KnowledgeAttachment());
        
        Assertions.assertFalse(provider.hasFullKnowledge());
        
        provider.setFullKnowledge(true);
        Assertions.assertTrue(provider.hasFullKnowledge());
        
        provider.clearKnowledge();
        Assertions.assertFalse(provider.hasFullKnowledge());
    }

    @Test
    @DisplayName("Test Knowledge Add and Remove")
    void testKnowledge() {
        IKnowledgeProvider provider = KnowledgeImpl.wrapAttachment(new KnowledgeImpl.KnowledgeAttachment());
        
        ItemInfo mockInfo = Mockito.mock(ItemInfo.class);
        @SuppressWarnings("unchecked")
        net.minecraft.core.Holder<Item> mockHolder = Mockito.mock(net.minecraft.core.Holder.class);
        Mockito.when(mockInfo.getItem()).thenReturn(mockHolder);
        
        Assertions.assertFalse(provider.hasKnowledge(mockInfo));
        
        // Add
        Assertions.assertTrue(provider.addKnowledge(mockInfo));
        Assertions.assertTrue(provider.hasKnowledge(mockInfo));
        Assertions.assertEquals(1, provider.getKnowledge().size());
        
        // Adding again should return false (already present)
        Assertions.assertFalse(provider.addKnowledge(mockInfo));
        
        // Remove
        Assertions.assertTrue(provider.removeKnowledge(mockInfo));
        Assertions.assertFalse(provider.hasKnowledge(mockInfo));
        Assertions.assertEquals(0, provider.getKnowledge().size());
        
        // Removing again should return false (not present)
        Assertions.assertFalse(provider.removeKnowledge(mockInfo));
    }
}
