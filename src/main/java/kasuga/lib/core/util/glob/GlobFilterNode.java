package kasuga.lib.core.util.glob;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlobFilterNode {
    public AtomicBoolean isUnreachable = new AtomicBoolean(false);
    public HashMap<String, GlobFilterNode> children = new HashMap<>();

    public int filterCount = 0;

    public static int MAX_FILTER_COUNT = 128;

    public @Nullable GlobFilterNode reach(String key) {
        if(filterCount > MAX_FILTER_COUNT)
            return children.get(key);
        return children.computeIfAbsent(key, (k) -> {
            filterCount++;
            return new GlobFilterNode();
        });
    }

    public boolean reachable() {
        return !isUnreachable.get();
    }

    public void setUnreachable() {
        isUnreachable.set(true);
    }
}
