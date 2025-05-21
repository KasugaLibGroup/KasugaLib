package kasuga.lib.core.client.block_bench_model.json_data;

import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public interface IElement {

    String getName();
    Vector3f getPivot();
    int getPreviewColorType();
    UUID getId();
    boolean isExport();
    boolean isLocked();
    boolean isVisibility();
}
