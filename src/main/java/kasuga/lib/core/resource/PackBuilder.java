package kasuga.lib.core.resource;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.server.packs.PackType;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class PackBuilder {

    private final String id;
    private Pack.Position defaultPosition;
    private boolean hidden;
    private PackSource source;
    private final Component title, description;
    private final int packFormat;
    private final Map<PackType, Integer> packTypeVersions;
    private PackCompatibility compatibility;
    private boolean required;
    private boolean fixedPos;
    private final Set<PackType> dists;
    private final Set<String> namespaces;

    @NonNull
    private LazyRecomputable<PackResources> packSupplier;

    public PackBuilder(String id, Component title,
                       Component description, int packFormat,
                       PackType... dists) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.packFormat = packFormat;
        defaultPosition = Pack.Position.TOP;
        hidden = false;
        source = PackSource.BUILT_IN;
        packTypeVersions = new HashMap<>();
        compatibility = PackCompatibility.COMPATIBLE;
        required = true;
        fixedPos = false;
        this.namespaces = new HashSet<>();
        packSupplier = LazyRecomputable.of(() -> new KasugaPackResource(this.id,
                namespaces.toArray(new String[0])));
        this.dists = new HashSet<>();
        if (dists != null && dists.length > 0) {
            this.dists.addAll(Arrays.asList(dists));
        }
    }

    @Optional
    public PackBuilder onDist(PackType... dists) {
        this.dists.addAll(Arrays.asList(dists));
        return this;
    }

    @Optional
    public PackBuilder setCompatibility(PackCompatibility compatibility) {
        this.compatibility = compatibility;
        return this;
    }

    @Optional
    public PackBuilder setSource(PackSource source) {
        this.source = source;
        return this;
    }

    @Optional
    public PackBuilder setDefaultPosition(Pack.Position position) {
        this.defaultPosition = position;
        return this;
    }

    @Optional
    public PackBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Optional
    public PackBuilder addPackTypeVersion(PackType packType, int version) {
        packTypeVersions.put(packType, version);
        return this;
    }

    @Optional
    public PackBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Optional
    public PackBuilder setFixedPos(boolean fixedPos) {
        this.fixedPos = fixedPos;
        return this;
    }

    @Optional
    public PackBuilder setPackSupplier(@NonNull Function<PackBuilder,
            PackResources> packSupplier) {
        this.packSupplier = LazyRecomputable.of(() -> packSupplier.apply(this));
        return this;
    }

    @Optional
    public PackBuilder addNamespace(String... namespace) {
        namespaces.addAll(Set.of(namespace));
        return this;
    }

    public Pack create(Pack.PackConstructor constructor) {
        return constructor.create(this.id, title, required, packSupplier::get,
                new PackMetadataSection(description, packFormat, packTypeVersions), defaultPosition,
                source, hidden);
    }
}
