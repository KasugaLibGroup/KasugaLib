package kasuga.lib.core.base;

import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class BucketItem extends net.minecraft.world.item.BucketItem {

    private final LazyRecomputable<Fluid> fluidSupplier;

    public BucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
        fluidSupplier = LazyRecomputable.of(supplier::get);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        Fluid content = fluidSupplier.get();
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, content == Fluids.EMPTY ? net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY : net.minecraft.world.level.ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(pPlayer, pLevel, itemstack, blockhitresult);
        if (ret != null) {
            return ret;
        } else if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos1, direction, itemstack)) {
                BlockState blockstate1;
                if (content == Fluids.EMPTY) {
                    blockstate1 = pLevel.getBlockState(blockpos);
                    if (blockstate1.getBlock() instanceof BucketPickup) {
                        BucketPickup bucketpickup = (BucketPickup)blockstate1.getBlock();
                        ItemStack itemstack1 = bucketpickup.pickupBlock(pLevel, blockpos, blockstate1);
                        if (!itemstack1.isEmpty()) {
                            pPlayer.awardStat(Stats.ITEM_USED.get(this));
                            bucketpickup.getPickupSound(blockstate1).ifPresent((p_150709_) -> {
                                pPlayer.playSound(p_150709_, 1.0F, 1.0F);
                            });
                            pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, blockpos);
                            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, pPlayer, itemstack1);
                            if (!pLevel.isClientSide) {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)pPlayer, itemstack1);
                            }

                            return InteractionResultHolder.sidedSuccess(itemstack2, pLevel.isClientSide());
                        }
                    }

                    return InteractionResultHolder.fail(itemstack);
                } else {
                    blockstate1 = pLevel.getBlockState(blockpos);
                    BlockPos blockpos2 = this.canBlockContainFluid(pLevel, blockpos, blockstate1) ? blockpos : blockpos1;
                    if (this.emptyContents(pPlayer, pLevel, blockpos2, blockhitresult, itemstack)) {
                        this.checkExtraContent(pPlayer, pLevel, itemstack, blockpos2);
                        if (pPlayer instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)pPlayer, blockpos2, itemstack);
                        }

                        pPlayer.awardStat(Stats.ITEM_USED.get(this));
                        return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(itemstack, pPlayer), pLevel.isClientSide());
                    } else {
                        return InteractionResultHolder.fail(itemstack);
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    public boolean emptyContents(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult, @Nullable ItemStack container) {
        Fluid content = fluidSupplier.get();
        if (!(content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = pLevel.getBlockState(pPos);
            Block block = blockstate.getBlock();
            boolean flag = blockstate.canBeReplaced(content);
            boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel, pPos, blockstate, content);
            java.util.Optional<net.minecraftforge.fluids.FluidStack> containedFluidStack = java.util.Optional.ofNullable(container).flatMap(net.minecraftforge.fluids.FluidUtil::getFluidContained);
            if (!flag1) {
                return pResult != null && this.emptyContents(pPlayer, pLevel, pResult.getBlockPos().relative(pResult.getDirection()), (BlockHitResult)null, container);
            } else if (containedFluidStack.isPresent() && content.getFluidType().isVaporizedOnPlacement(pLevel, pPos, containedFluidStack.get())) {
                content.getFluidType().onVaporize(pPlayer, pLevel, pPos, containedFluidStack.get());
                return true;
            } else if (pLevel.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel,pPos,blockstate,content)) {
                ((LiquidBlockContainer)block).placeLiquid(pLevel, pPos, blockstate, ((FlowingFluid) content).getSource(false));
                this.playEmptySound(pPlayer, pLevel, pPos);
                return true;
            } else {
                if (!pLevel.isClientSide && flag && !blockstate.liquid()) {
                    pLevel.destroyBlock(pPos, true);
                }

                if (!pLevel.setBlock(pPos, content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(pPlayer, pLevel, pPos);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos) {
        Fluid content = fluidSupplier.get();
        SoundEvent soundevent = content.getFluidType().getSound(pPlayer, pLevel, pPos, SoundActions.BUCKET_EMPTY);
        if (soundevent == null) {
            soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
        pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {
        if (this.getClass() == BucketItem.class)
            return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        else
            return super.initCapabilities(stack, nbt);
    }

    protected boolean canBlockContainFluid(Level worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, fluidSupplier.get());
    }
}
