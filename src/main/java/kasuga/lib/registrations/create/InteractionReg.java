package kasuga.lib.registrations.create;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.exception.RegistryElementNotPresentException;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class InteractionReg<T extends MovingInteractionBehaviour> extends Reg {
    private TagKey[] tags = null;
    private BlockReg[] blocks = null;
    private InteractionPredicate predicate = null;
    private Predicate<BlockReg> blockPredicate = null;
    private Predicate<BlockState> statePredicate = null;
    private MovingInteractionBehaviour behaviour = null;
    private WorkType workType = WorkType.blocks;
    public InteractionReg(String registrationKey) {
        super(registrationKey);
    }

    public InteractionReg<T> behaviour(T behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    public InteractionReg<T> sortByTags(TagKey... keys) {
        this.tags = keys;
        workType = WorkType.tag;
        return this;
    }

    public InteractionReg<T> sortByBlocks(BlockReg... blocks) {
        this.blocks = blocks;
        workType = WorkType.blocks;
        return this;
    }

    public InteractionReg<T> mixedPredicate(InteractionPredicate predicate) {
        this.predicate = predicate;
        workType = WorkType.mixed_predicate;
        return this;
    }

    public InteractionReg<T> blockPredicate(Predicate<BlockReg> predicate) {
        this.blockPredicate = predicate;
        workType = WorkType.predicate;
        return this;
    }

    public InteractionReg<T> statePredicate(Predicate<BlockState> predicate) {
        this.statePredicate = predicate;
        workType = WorkType.state_predicate;
        return this;
    }

    @Override
    public InteractionReg<T> submit(SimpleRegistry registry) {
        if (behaviour == null)
            crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a type of interaction for registration");
        switch (workType) {
            case blocks -> {
                if (blocks == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a list of block for registration.");
                for (BlockReg block : blocks)
                    AllInteractionBehaviours.registerBehaviour(block.getBlock(), behaviour);
            }
            case predicate -> {
                if (blocks == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a list of block for registration.");
                if (blockPredicate == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a predictor for registration.");
                for (BlockReg block : blocks) {
                    if (blockPredicate.test(block))
                        AllInteractionBehaviours.registerBehaviour(block.getBlock(), behaviour);
                }
            }
            case state_predicate -> {
                if (statePredicate == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a predictor for registration.");
                AllInteractionBehaviours.registerBehaviourProvider(
                        state -> statePredicate.test(state) ? behaviour : null
                );
            }
            case tag -> {
                if (tags == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a list of tags for registration.");
                AllInteractionBehaviours.registerBehaviourProvider(
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
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a list of block for registration.");
                if (tags == null)
                    crashOnNotPresent(InteractionReg.class, getIdentifier(), "you must provide a list of tags for registration.");
                for (BlockReg reg : blocks) {
                    boolean flag = false;
                    for (TagKey tag : tags) {
                        if (predicate.test(reg, tag)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) AllInteractionBehaviours.registerBehaviour(reg.getBlock(), behaviour);
                }
            }
        }
        return this;
    }

    @Override
    public String getIdentifier() {
        return "interaction_behaviour";
    }

    protected enum WorkType {
        blocks,
        predicate,
        state_predicate,
        mixed_predicate,
        tag;
    }

    public interface InteractionPredicate {
        boolean test(BlockReg reg, TagKey tag);
    }
}
