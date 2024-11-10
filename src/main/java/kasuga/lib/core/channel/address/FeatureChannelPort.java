package kasuga.lib.core.channel.address;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class FeatureChannelPort extends ChannelPort{
    protected final ResourceLocation feature;

    public FeatureChannelPort(ResourceLocation feature) {
        this.feature = feature;
    }

    public static FeatureChannelPort of(ResourceLocation feature) {
        return new FeatureChannelPort(feature);
    }


    public static FeatureChannelPort read(FriendlyByteBuf byteBuf) {
        return new FeatureChannelPort(byteBuf.readResourceLocation());
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(0);
        byteBuf.writeResourceLocation(feature);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FeatureChannelPort that = (FeatureChannelPort) object;
        return Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature);
    }
}
