package kasuga.lib.core.util.data_type;

public class Couple<T> extends Pair<T,T> {
    public Couple(T first, T second) {
        super(first, second);
    }

    public static <T> Couple<T> couple(T first, T second) {
        return new Couple<T>(first, second);
    }
}
