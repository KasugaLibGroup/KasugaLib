package kasuga.lib.codes.logic.operations;

public enum MathType {
    LARGER,
    SMALLER,
    EQUALS,
    NOT_EQU,
    LARGER_EQU,
    SMALLER_EQU,
    INVALID;

    @Override
    public String toString() {
        switch (this) {
            case LARGER -> {return ">";}
            case SMALLER -> {return "<";}
            case LARGER_EQU -> {return "<=";}
            case SMALLER_EQU -> {return ">=";}
            case EQUALS -> {return "==";}
            case NOT_EQU -> {return "!=";}
            default -> {return "invalid";}
        }
    }

    public static MathType fromString(String string) {
        switch (string) {
            case ">" -> {return LARGER;}
            case "<" -> {return SMALLER;}
            case "==" -> {return EQUALS;}
            case ">=" -> {return LARGER_EQU;}
            case "<=" -> {return SMALLER_EQU;}
            case "<>", "!=" -> {return NOT_EQU;}
            default -> {return INVALID;}
        }
    }
}
