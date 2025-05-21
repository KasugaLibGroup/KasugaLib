package kasuga.lib.core.resource;

import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@FunctionalInterface
public interface CustomIoSupplier extends IoSupplier<InputStream> {

    static CustomIoSupplier create(InputStream stream) {
        return () -> stream;
    }

    static CustomIoSupplier create(byte[] bytes) {
        return () -> new ByteArrayInputStream(bytes);
    }

    static CustomIoSupplier create(Resource resource) {
        return resource::open;
    }
}
