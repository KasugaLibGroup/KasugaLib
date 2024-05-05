package kasuga.lib.registrations.create;

import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class MovementReg<T extends MovementBehaviour> extends Reg implements InteractionMovementReg {
    private TagKey[] tags = null;
    private BlockReg[] blocks = null;
    private InteractionReg.InteractionPredicate predicate = null;
    private Predicate<BlockReg> blockPredicate = null;
    private Predicate<BlockState> statePredicate = null;
    private MovementBehaviour behaviour = null;
    private InteractionReg.WorkType workType = InteractionReg.WorkType.blocks;
    public MovementReg(String registrationKey) {
        super(registrationKey);
    }

    public MovementReg<T> behaviour(T behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    public MovementReg<T> sortByTags(TagKey... keys) {
        this.tags = keys;
        workType = InteractionReg.WorkType.tag;
        return this;
    }

    public MovementReg<T> sortByBlocks(BlockReg... blocks) {
        this.blocks = blocks;
        workType = InteractionReg.WorkType.blocks;
        return this;
    }

    public MovementReg<T> mixedPredicate(InteractionReg.InteractionPredicate predicate) {
        this.predicate = predicate;
        workType = InteractionReg.WorkType.mixed_predicate;
        return this;
    }

    public MovementReg<T> blockPredicate(Predicate<BlockReg> predicate) {
        this.blockPredicate = predicate;
        workType = InteractionReg.WorkType.predicate;
        return this;
    }

    public MovementReg<T> statePredicate(Predicate<BlockState> predicate) {
        this.statePredicate = predicate;
        workType = InteractionReg.WorkType.state_predicate;
        return this;
    }

    @Override
    public MovementReg<T> submit(SimpleRegistry registry) {
        if (!(registry instanceof CreateRegistry createRegistry))
            crashOnNotPresent(InteractionReg.class, getIdentifier(), "Use CreateRegistry instead of SimpleRegistry");
        return this;
    }

    public void onSetup() {
        if (behaviour == null)
            crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a type of interaction for registration");
        switch (workType) {
            case blocks -> {
                if (blocks == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a list of block for registration.");
                for (BlockReg block : blocks)
                    AllMovementBehaviours.registerBehaviour(block.getBlock(), behaviour);
            }
            case predicate -> {
                if (blocks == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a list of block for registration.");
                if (blockPredicate == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a predictor for registration.");
                for (BlockReg block : blocks) {
                    if (blockPredicate.test(block))
                        AllMovementBehaviours.registerBehaviour(block.getBlock(), behaviour);
                }
            }
            case state_predicate -> {
                if (statePredicate == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a predictor for registration.");
                AllMovementBehaviours.registerBehaviourProvider(
                        state -> statePredicate.test(state) ? behaviour : null
                );
            }
            case tag -> {
                if (tags == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a list of tags for registration.");
                AllMovementBehaviours.registerBehaviourProvider(
                        state -> {
                            boolean flag = false;
                            for (TagKey tag : tags) {
                                if (state.is(tag)) {
                                    flag = true;
                                    break;
                                }
                            }
                            return flag ? behaviour : null;
                        }
                );
            }
            case mixed_predicate -> {
                if (blocks == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a list of block for registration.");
                if (tags == null)
                    crashOnNotPresent(MovementReg.class, getIdentifier(), "you must provide a list of tags for registration.");
                for (BlockReg reg : blocks) {
                    boolean flag = false;
                    for (TagKey tag : tags) {
                        if (predicate.test(reg, tag)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) AllMovementBehaviours.registerBehaviour(reg.getBlock(), behaviour);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "movement_behaviour";
    }
}
