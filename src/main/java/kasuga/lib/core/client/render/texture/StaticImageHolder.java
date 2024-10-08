package kasuga.lib.core.client.render.texture;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class StaticImageHolder {
    private final ResourceLocation id;
    private final byte[] bytes;
    private int registerType;
    private final CompoundTag nbt;
    private final FriendlyByteBuf buf;
    private Supplier<StaticImage> image;

    public StaticImageHolder(ResourceLocation id) {
        this.id = id;
        this.bytes = new byte[0];
        registerType = 0;
        nbt = null;
        buf = null;
    }

    public StaticImageHolder(ResourceLocation id, InputStream stream) throws IOException {
        this.id = id;
        this.bytes = stream.readAllBytes();
        registerType = 1;
        nbt = null;
        buf = null;
    }

    public StaticImageHolder(ResourceLocation id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
        registerType = 1;
        nbt = null;
        buf = null;
    }

    public StaticImageHolder(CompoundTag nbt) {
        id = null;
        bytes = new byte[0];
        registerType = 2;
        this.nbt = nbt;
        buf = null;
    }

    public StaticImageHolder(FriendlyByteBuf buf) {
        this.id = null;
        this.bytes = new byte[0];
        registerType = 3;
        nbt = null;
        this.buf = buf;
    }

    public Supplier<StaticImage> getImage() throws IOException {
        if (!KasugaLib.STACKS.isTextureRegistryFired()) {
            StaticImage.HOLDERS.add(this);
            return null;
        }
        Supplier<StaticImage> image = switch (registerType) {
            case 0 -> StaticImage.createImage(id);
            case 1 -> StaticImage.createImage(id, new ByteArrayInputStream(bytes));
            case 2 -> StaticImage.createImage(nbt);
            case 3 -> StaticImage.createImage(buf);
            default -> this.image;
        };
        if (registerType > -1) this.image = image;
        registerType = -1;
        return image;
    }

    public Supplier<StaticImage> getImageSafe() {
        try {
            return getImage();
        } catch (IOException e) {
            try {
                return KasugaLibClient.NO_IMG.getImage();
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            } finally {
                KasugaLib.MAIN_LOGGER.warn("Failed to get specified picture!", e);
            }
        }
    }
}
