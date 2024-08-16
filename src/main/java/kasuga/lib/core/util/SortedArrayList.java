package kasuga.lib.core.util;

import java.util.ArrayList;
import java.util.Collections;

public class SortedArrayList<T extends Comparable<T>> extends ArrayList<T> {
    public void insert(T value) {
        int i = Collections.binarySearch(this, value);
        add(i < 0 ? -i - 1 : i, value);
    }
}