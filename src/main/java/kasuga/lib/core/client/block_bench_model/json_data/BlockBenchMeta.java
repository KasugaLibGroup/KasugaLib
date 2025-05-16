package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.openjdk.nashorn.internal.objects.annotations.Getter;

@OnlyIn(Dist.CLIENT)
public class BlockBenchMeta {

    private final String formatVersion, modelFormat;
    private final boolean boxUV;

    public BlockBenchMeta(JsonObject json) {
        this.formatVersion = json.get("format_version").getAsString();
        this.modelFormat = json.get("model_format").getAsString();
        this.boxUV = json.get("box_uv").getAsBoolean();
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getModelFormat() {
        return modelFormat;
    }

    public boolean isBoxUV() {
        return boxUV;
    }
}
