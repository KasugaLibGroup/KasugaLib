package kasuga.lib.example_env.block;

import kasuga.lib.core.base.UnModeledBlockProperty;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.block_entity.GreenAppleTile;
import kasuga.lib.example_env.network.ExampleS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GreenAppleBlock extends BaseEntityBlock {

    VoxelShape SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public static final UnModeledBlockProperty<Boolean, BooleanProperty> test = UnModeledBlockProperty.create(BooleanProperty.create("test"));
    public GreenAppleBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(test, false));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(test));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide) {
            AllExampleElements.Channel.sendToClient(new ExampleS2CPacket(), (ServerPlayer) pPlayer);
            // AllExampleElements.Channel.sendToServer(new ExampleC2SPacket());
        }
        if(pLevel.isClientSide){
            BlockEntity be = pLevel.getBlockEntity(pPos);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GreenAppleTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
