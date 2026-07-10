package moze_intel.projecte.gameObjs.container.inventory;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.Collections;

@DisplayName("Test TransmutationInventory")
class TransmutationInventoryTest {

    private ServerPlayer mockPlayer;
    private Level mockLevel;
    private IKnowledgeProvider provider;

    @BeforeEach
    void setup() {
        mockPlayer = Mockito.mock(ServerPlayer.class);
        mockPlayer.connection = Mockito.mock(ServerGamePacketListenerImpl.class);
        Scoreboard mockScoreboard = Mockito.mock(Scoreboard.class);
        Mockito.when(mockPlayer.getScoreboard()).thenReturn(mockScoreboard);
        mockLevel = Mockito.mock(Level.class);
        Mockito.when(mockPlayer.level()).thenReturn(mockLevel);
        
        provider = KnowledgeImpl.wrapAttachment(new KnowledgeImpl.KnowledgeAttachment());
        
        // Mock getCapability
        Mockito.when(mockPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)).thenReturn(provider);
    }

    @Test
    @DisplayName("Test Initialization")
    void testInit() {
        Mockito.when(mockLevel.isClientSide()).thenReturn(false);
        TransmutationInventory inventory = new TransmutationInventory(mockPlayer);
        
        Assertions.assertTrue(inventory.isServer());
        Assertions.assertFalse(inventory.isClient());
        
        Assertions.assertEquals(0, inventory.getAvailableEmcAsLong());
    }

    @Test
    @DisplayName("Test Add and Remove EMC")
    void testEmc() {
        Mockito.when(mockLevel.isClientSide()).thenReturn(false);
        TransmutationInventory inventory = new TransmutationInventory(mockPlayer);
        
        inventory.addEmc(BigInteger.valueOf(1000));
        Assertions.assertEquals(1000L, inventory.getAvailableEmcAsLong());
        Assertions.assertEquals(BigInteger.valueOf(1000), provider.getEmc());
        
        inventory.removeEmc(BigInteger.valueOf(500));
        Assertions.assertEquals(500L, inventory.getAvailableEmcAsLong());
        Assertions.assertEquals(BigInteger.valueOf(500), provider.getEmc());
    }
}
