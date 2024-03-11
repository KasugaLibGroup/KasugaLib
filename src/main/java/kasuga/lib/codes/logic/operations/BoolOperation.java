package kasuga.lib.codes.logic.operations;

import kasuga.lib.codes.logic.infrastructure.LogicalData;
import kasuga.lib.codes.logic.infrastructure.LogicalOperator;

public class BoolOperation implements LogicalData, LogicalOperator {
    private final BoolType type;

    public BoolOperation(BoolType type) {
        this.type = type;
    }

    public BoolOperation(String type) {
        this.type = BoolType.fromString(type);
    }

    @Override
    public boolean operate(LogicalData former, LogicalData rear) {
        switch (type) {
            case AND -> {return former.getResult() && rear.getResult();}
            case OR -> {return former.getResult() || rear.getResult();}
            case NOT -> {return !rear.getResult();}
            default -> {return false;}
        }
    }

    public static boolean isBoolOperation(String typeFlag) {
        return BoolType.fromString(typeFlag.replace(" ", "")) != BoolType.INVALID;
    }

    public boolean is(Object type) {
        if(type instanceof BoolType)
            return type == this.type;
        return false;
    }

    @Override
    public boolean getResult() {
        return false;
    }

    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public BoolOperation clone() {
        return new BoolOperation(type);
    }
}
