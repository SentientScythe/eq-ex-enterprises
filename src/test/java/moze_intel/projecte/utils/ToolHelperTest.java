package moze_intel.projecte.utils;

import moze_intel.projecte.gameObjs.IMatterType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.BiFunction;

@DisplayName("Test ToolHelper")
class ToolHelperTest {

    @Test
    @DisplayName("Test performActions")
    void testPerformActions() {
        UseOnContext context = Mockito.mock(UseOnContext.class);
        BlockState state = Mockito.mock(BlockState.class);

        // If first action consumes, return immediately
        Assertions.assertEquals(InteractionResult.SUCCESS, ToolHelper.performActions(context, state, InteractionResult.SUCCESS));
        Assertions.assertEquals(InteractionResult.CONSUME, ToolHelper.performActions(context, state, InteractionResult.CONSUME));

        // If first action passes, it will check the second action
        BiFunction<UseOnContext, BlockState, InteractionResult> secondary1 = (ctx, st) -> InteractionResult.SUCCESS;
        Assertions.assertEquals(InteractionResult.SUCCESS, ToolHelper.performActions(context, state, InteractionResult.PASS, secondary1));

        // If first action fails and second action fails, it returns fail
        BiFunction<UseOnContext, BlockState, InteractionResult> secondaryFail = (ctx, st) -> InteractionResult.FAIL;
        Assertions.assertEquals(InteractionResult.FAIL, ToolHelper.performActions(context, state, InteractionResult.FAIL, secondaryFail));

        // If first action fails but second action passes, it should return fail because in performActions:
        // result = secondaryAction.apply. If result.consumesAction() (SUCCESS or CONSUME), it returns it!
        Assertions.assertEquals(InteractionResult.SUCCESS, ToolHelper.performActions(context, state, InteractionResult.FAIL, secondary1));

        // If first action fails and second action passes (InteractionResult.PASS), hasFailed becomes false and it returns PASS at the end
        BiFunction<UseOnContext, BlockState, InteractionResult> secondaryPass = (ctx, st) -> InteractionResult.PASS;
        Assertions.assertEquals(InteractionResult.PASS, ToolHelper.performActions(context, state, InteractionResult.FAIL, secondaryPass));
    }

    @Test
    @DisplayName("Test getDestroySpeed")
    void testGetDestroySpeed() {
        IMatterType matterType = Mockito.mock(IMatterType.class);
        Mockito.when(matterType.getChargeModifier()).thenReturn(2.0f);

        // If parent destroy speed is 1, returns 1
        Assertions.assertEquals(1.0f, ToolHelper.getDestroySpeed(1.0f, matterType, 3));

        // Otherwise parent + chargeMod * charge
        // 5.0 + 2.0 * 3 = 11.0
        Assertions.assertEquals(11.0f, ToolHelper.getDestroySpeed(5.0f, matterType, 3));
    }
}
