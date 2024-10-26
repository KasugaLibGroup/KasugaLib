package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.boundary.CustomSegmentUtil;
import kasuga.lib.core.create.graph.EdgeExtraData;
import kasuga.lib.example_env.boundary.ExampleBoundaryModule;
import kasuga.lib.example_env.boundary.ExampleSegment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.simibubi.create.content.trains.graph.EdgeData.passiveGroup;
import static kasuga.lib.example_env.boundary.ExampleBoundaryModule.EXAMPLE_SEGMENT_FEATURE;

@Mixin(value = Train.class, remap = false)
public class TestGraphDataTrainMixin {
    @Shadow
    public List<Carriage> carriages;
    @Shadow
    public TrackGraph graph;
    @Inject(method = "tick", at=@At("HEAD"))
    public void beforeTick(Level level, CallbackInfo ci){
        TravellingPoint tp = carriages.get(0).getLeadingPoint();
        if(tp==null)
            return;
        EdgeData edgeData = tp.edge.getEdgeData();
        if(edgeData == null)
            return;
        Player player = Minecraft.getInstance().player;
        if(player == null)
            return;

        ExampleSegment data = (ExampleSegment)
                CustomSegmentUtil.getSegment(
                        graph,
                        tp.edge,
                        ExampleBoundaryModule.EXAMPLE_BOUNDARY,
                        tp.position
                );

        EdgeExtraData extraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph).getEdgeData(tp.edge);

        StringBuilder text = new StringBuilder();
        text.append("RTN=");
        if(data != null ){
            text.append(data.getId().toString().substring(28));
        } else text.append("< NULL >");
        text.append(", CUR=");
        if(extraData.hasBoundaryFeature(EXAMPLE_SEGMENT_FEATURE))
            text.append(extraData.getBoundaryFeature(EXAMPLE_SEGMENT_FEATURE).toString().substring(28));
        else text.append("< NULL >");
        text.append(", SIG=" + edgeData.getGroupAtPosition(graph, tp.position).toString().substring(28));
        player.displayClientMessage(Component.literal(text.toString()),true);
    }
}
