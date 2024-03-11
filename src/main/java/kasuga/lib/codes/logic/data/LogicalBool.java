package kasuga.lib.codes.logic.data;

import kasuga.lib.codes.logic.infrastructure.LogicalData;

public class LogicalBool implements LogicalData {

    private final boolean flag;

    public LogicalBool(boolean flag) {
        this.flag = flag;
    }

    public LogicalBool(String boolFlag) {
        this.flag = boolFlag.replace(" ", "").equals("True");
    }

    public static boolean isBool(String boolFlag) {
        String s = boolFlag.replaceAll("( )|(\\()|(\\))", "");
        return s.equals("True") || s.equals("False");
    }

    public static LogicalBool defaultTrue() {
        return new LogicalBool(true);
    }

    public static LogicalBool defaultFalse() {
        return new LogicalBool(false);
    }

    @Override
    public boolean getResult() {
        return flag;
    }

    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public LogicalBool clone() {
        return new LogicalBool(flag);
    }

    @Override
    public String toString() {
        return flag ? "True" : "False";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LogicalBool bool)) return false;
        return bool.flag == this.flag;
    }
}
