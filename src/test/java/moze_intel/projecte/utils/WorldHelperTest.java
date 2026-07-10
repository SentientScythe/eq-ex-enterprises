package moze_intel.projecte.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Test WorldHelper")
class WorldHelperTest {

    @Test
    @DisplayName("Test getBroadBox")
    void testGetBroadBox() {
        BlockPos pos = BlockPos.ZERO;
        
        // Zero breadth
        AABB boxZero = WorldHelper.getBroadBox(pos, Direction.UP, 0);
        Assertions.assertEquals(new AABB(pos), boxZero);
        
        // UP / DOWN inflates X and Z
        AABB boxUp = WorldHelper.getBroadBox(pos, Direction.UP, 1);
        Assertions.assertEquals(new AABB(pos).inflate(1, 0, 1), boxUp);
        
        // EAST / WEST inflates Y and Z
        AABB boxEast = WorldHelper.getBroadBox(pos, Direction.EAST, 1);
        Assertions.assertEquals(new AABB(pos).inflate(0, 1, 1), boxEast);
        
        // SOUTH / NORTH inflates X and Y
        AABB boxSouth = WorldHelper.getBroadBox(pos, Direction.SOUTH, 1);
        Assertions.assertEquals(new AABB(pos).inflate(1, 1, 0), boxSouth);
    }

    @Test
    @DisplayName("Test getFlatYBox")
    void testGetFlatYBox() {
        BlockPos pos = BlockPos.ZERO;
        AABB box = WorldHelper.getFlatYBox(pos, 2);
        Assertions.assertEquals(new AABB(pos).inflate(2, 0, 2), box);
    }

    @Test
    @DisplayName("Test getDeepBox")
    void testGetDeepBox() {
        BlockPos pos = BlockPos.ZERO;
        // Direction.EAST means side hit is EAST, so expanding towards -WEST (which is EAST)
        AABB box = WorldHelper.getDeepBox(pos, Direction.EAST, 2);
        // Box broadness is 1. Inflates Y and Z by 1.
        // Depth is 2, expands towards 2 * -EAST (-2, 0, 0)
        AABB expected = new AABB(pos).inflate(0, 1, 1).expandTowards(-2, 0, 0);
        Assertions.assertEquals(expected, box);
    }

    @Test
    @DisplayName("Test getPositionsInBox")
    void testGetPositionsInBox() {
        AABB box = new AABB(0, 0, 0, 1, 1, 1); // Only BlockPos(0, 0, 0) should be included since it takes floor of bounds - epsilon
        List<BlockPos> positions = new ArrayList<>();
        WorldHelper.getPositionsInBox(box).forEach(positions::add);
        
        Assertions.assertEquals(1, positions.size());
        Assertions.assertEquals(new BlockPos(0, 0, 0), positions.get(0));
    }
}
