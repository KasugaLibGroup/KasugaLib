package kasuga.lib.core.client.block_bench_model.json_data;

import com.mojang.math.Vector3f;

import java.util.UUID;

public interface IElement {

    String getName();
    Vector3f getPivot();
    int getPreviewColorType();
    UUID getId();
    boolean isExport();
    boolean isLocked();
    boolean isVisibility();
}
