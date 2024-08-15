package kasuga.lib.core.util.data_type;

import org.jetbrains.annotations.NotNull;

public class PrioritizedItem<T> extends Pair<T,Integer> implements Comparable<PrioritizedItem<T>>{

    protected PrioritizedItem(T first, Integer second) {
        super(first, second);
    }

    @Override
    public int compareTo(@NotNull PrioritizedItem<T> o) {
        return (second.compareTo(o.second));
    }

    public static <T> PrioritizedItem<T> of(T first, Integer second) {
        return new PrioritizedItem<T>(first, second);
    }
}
