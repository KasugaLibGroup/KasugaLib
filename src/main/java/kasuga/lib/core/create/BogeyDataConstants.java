package kasuga.lib.core.create;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class BogeyDataConstants {

    public static final String BOGEY_ASSEMBLY_DIRECTION_KEY = "assembly_direction";
    public static final String BOGEY_DIRECTION_KEY = "is_forwards";
    public static final String BOGEY_LINK_KEY = "linked";
    public static final String PARTIAL_TICK_KEY = "partial_ticks";

    public static boolean isForwards(CompoundTag bogeyData, boolean inContraption) {
        boolean isForwards =
                bogeyData.contains(BOGEY_DIRECTION_KEY)
                        && bogeyData.getBoolean(BOGEY_DIRECTION_KEY);

        Direction direction =
                bogeyData.contains(BOGEY_ASSEMBLY_DIRECTION_KEY)
                        ? NBTHelper.readEnum(
                        bogeyData, BOGEY_ASSEMBLY_DIRECTION_KEY, Direction.class)
                        : Direction.NORTH;

        boolean isLinked = true;
        if (bogeyData.contains(BOGEY_LINK_KEY))
            isLinked = bogeyData.getBoolean(BOGEY_LINK_KEY);

        boolean isPosotive = isDirectionPositive(direction);

        if (isLinked && inContraption && isPosotive) isForwards = !isForwards;

        return isPosotive == isForwards;
    }

    public static boolean isDirectionPositive(Direction direction) {
        return switch (direction) {
            case NORTH, WEST, UP -> true;
            case SOUTH, DOWN, EAST -> false;
        };
    }
}
