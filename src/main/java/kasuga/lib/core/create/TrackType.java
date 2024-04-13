package kasuga.lib.core.create;

import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceLocation;

public class TrackType extends TrackMaterial.TrackType {
    public TrackType(ResourceLocation id, TrackBlockFactory factory) {
        super(id, factory);
    }

    public static TrackType of(ResourceLocation registrationId, TrackBlockFactory factory) {
        return new TrackType(registrationId, factory);
    }
}
