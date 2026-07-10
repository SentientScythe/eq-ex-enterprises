package moze_intel.projecte.network.commands;

import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

@DisplayName("Test RemoveEmcCMD")
class RemoveEmcCMDTest {

    @Test
    @DisplayName("Test getHeldStack")
    void testGetHeldStack() throws Exception {
        CommandContext<CommandSourceStack> mockContext = Mockito.mock(CommandContext.class);
        CommandSourceStack mockSource = Mockito.mock(CommandSourceStack.class);
        ServerPlayer mockPlayer = Mockito.mock(ServerPlayer.class);
        
        Mockito.when(mockContext.getSource()).thenReturn(mockSource);
        Mockito.when(mockSource.getPlayerOrException()).thenReturn(mockPlayer);
        
        ItemStack testStack = new ItemStack(Items.DIRT);
        Mockito.when(mockPlayer.getMainHandItem()).thenReturn(testStack);
        Mockito.when(mockPlayer.getOffhandItem()).thenReturn(ItemStack.EMPTY);

        NSSItem nssItem = RemoveEmcCMD.getHeldStack(mockContext);
        Assertions.assertNotNull(nssItem);
    }
}
