package moze_intel.projecte.network.commands;

import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.math.BigInteger;

@DisplayName("Test EMCCMD")
class EMCCMDTest {

    @Test
    @DisplayName("Test Handle Method via Reflection")
    void testHandle() throws Exception {
        CommandContext<CommandSourceStack> mockContext = Mockito.mock(CommandContext.class);
        CommandSourceStack mockSource = Mockito.mock(CommandSourceStack.class);
        ServerPlayer mockPlayer = Mockito.mock(ServerPlayer.class);
        EntitySelector mockSelector = Mockito.mock(EntitySelector.class);
        IKnowledgeProvider mockProvider = Mockito.mock(IKnowledgeProvider.class);

        Mockito.when(mockContext.getSource()).thenReturn(mockSource);
        Mockito.when(mockContext.getArgument("player", EntitySelector.class)).thenReturn(mockSelector);
        Mockito.when(mockSelector.findSinglePlayer(mockSource)).thenReturn(mockPlayer);
        
        Mockito.when(mockPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)).thenReturn(mockProvider);
        Mockito.when(mockProvider.getEmc()).thenReturn(BigInteger.valueOf(5000));
        
        Mockito.when(mockContext.getArgument("value", String.class)).thenReturn("1000");

        Class<?> actionTypeClass = Class.forName("moze_intel.projecte.network.commands.EMCCMD$ActionType");
        Object addAction = Enum.valueOf((Class<Enum>) actionTypeClass, "ADD");
        Object setAction = Enum.valueOf((Class<Enum>) actionTypeClass, "SET");
        Object testAction = Enum.valueOf((Class<Enum>) actionTypeClass, "TEST");

        Method handleMethod = EMCCMD.class.getDeclaredMethod("handle", CommandContext.class, actionTypeClass);
        handleMethod.setAccessible(true);

        // Test ADD
        int resultAdd = (int) handleMethod.invoke(null, mockContext, addAction);
        Assertions.assertEquals(1, resultAdd);
        Mockito.verify(mockProvider).setEmc(BigInteger.valueOf(6000));

        // Test SET
        int resultSet = (int) handleMethod.invoke(null, mockContext, setAction);
        Assertions.assertEquals(1, resultSet);
        Mockito.verify(mockProvider).setEmc(BigInteger.valueOf(1000));
        
        // Test TEST
        int resultTest = (int) handleMethod.invoke(null, mockContext, testAction);
        Assertions.assertEquals(1, resultTest); // 5000 >= 1000 is true, returns 1
    }
}
