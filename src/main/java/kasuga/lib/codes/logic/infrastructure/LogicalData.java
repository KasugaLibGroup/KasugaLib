package kasuga.lib.codes.logic.infrastructure;

public interface LogicalData {
    boolean getResult();
    boolean isAtomic();
    LogicalData clone();
    String toString();
}
