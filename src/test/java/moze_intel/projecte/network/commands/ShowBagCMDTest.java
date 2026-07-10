package moze_intel.projecte.network.commands;

import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.OptionalInt;

@DisplayName("Test ShowBagCMD")
class ShowBagCMDTest {

    @Test
    @DisplayName("Test showBag online player")
    void testShowBagOnline() throws Exception {
        CommandContext<CommandSourceStack> mockContext = Mockito.mock(CommandContext.class);
        CommandSourceStack mockSource = Mockito.mock(CommandSourceStack.class);
        ServerPlayer mockSender = Mockito.mock(ServerPlayer.class);
        ServerPlayer mockTarget = Mockito.mock(ServerPlayer.class);
        
        Mockito.when(mockContext.getSource()).thenReturn(mockSource);
        Mockito.when(mockSource.getPlayerOrException()).thenReturn(mockSender);
        
        IAlchBagProvider mockBagProvider = Mockito.mock(IAlchBagProvider.class);
        Mockito.when(mockTarget.getCapability(PECapabilities.ALCH_BAG_CAPABILITY)).thenReturn(mockBagProvider);
        IItemHandlerModifiable mockInv = Mockito.mock(IItemHandlerModifiable.class);
        Mockito.when(mockBagProvider.getBag(DyeColor.BLUE)).thenReturn(mockInv);
        
        Method showBagMethod = ShowBagCMD.class.getDeclaredMethod("showBag", CommandContext.class, DyeColor.class, ServerPlayer.class);
        showBagMethod.setAccessible(true);
        
        // Mock openMenu on sender to return a menu ID to avoid NPE or logic crashing
        Mockito.when(mockSender.openMenu(Mockito.any(MenuProvider.class), (java.util.function.Consumer) Mockito.any())).thenReturn(OptionalInt.of(1));

        int result = (int) showBagMethod.invoke(null, mockContext, DyeColor.BLUE, mockTarget);
        
        Assertions.assertEquals(1, result);
        
        // Verify sender was prompted to open the menu
        ArgumentCaptor<MenuProvider> captor = ArgumentCaptor.forClass(MenuProvider.class);
        Mockito.verify(mockSender).openMenu(captor.capture(), (java.util.function.Consumer) Mockito.any());
        
        MenuProvider provider = captor.getValue();
        Assertions.assertNotNull(provider);
    }
}
