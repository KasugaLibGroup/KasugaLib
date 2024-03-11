package kasuga.lib.codes.logic.operations;

import kasuga.lib.codes.logic.data.LogicalNumeric;
import kasuga.lib.codes.logic.infrastructure.LogicalData;
import kasuga.lib.codes.logic.infrastructure.LogicalOperator;

public class MathOperation implements LogicalData, LogicalOperator {
    private final MathType type;

    public MathOperation(MathType type) {
        this.type = type;
    }

    public MathOperation(String type) {
        this.type = MathType.fromString(type);
    }

    @Override
    public boolean operate(LogicalData former, LogicalData rear) {
        if(!(former instanceof LogicalNumeric numeric1) || !(rear instanceof LogicalNumeric numeric2)) {
            if(type == MathType.EQUALS) {return former.getResult() == rear.getResult();}
            throw new RuntimeException();
        }
        switch (type) {
            case EQUALS -> {return numeric1.mathResult() == numeric2.mathResult();}
            case LARGER -> {return numeric1.mathResult() > numeric2.mathResult();}
            case SMALLER -> {return numeric1.mathResult() < numeric2.mathResult();}
            case LARGER_EQU -> {return numeric1.mathResult() >= numeric2.mathResult();}
            case SMALLER_EQU -> {return numeric1.mathResult() <= numeric2.mathResult();}
            case NOT_EQU -> {return numeric1.mathResult() != numeric2.mathResult();}
            default -> {return false;}
        }
    }

    public static boolean isMathOperation(String mathFlag) {
        return MathType.fromString(mathFlag.replace(" ", "")) != MathType.INVALID;
    }

    @Override
    public String toString() {
        return type.toString();
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
    public MathOperation clone() {
        return new MathOperation(type);
    }

    public boolean is(Object type) {
        if(type instanceof MathType)
            return type == this.type;
        return false;
    }
}
