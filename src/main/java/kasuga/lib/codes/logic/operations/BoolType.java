package kasuga.lib.codes.logic.operations;

public enum BoolType {

    AND,
    OR,
    NOT,
    INVALID;

    @Override
    public String toString() {
        switch (this) {
            case OR -> {return "or";}
            case AND -> {return "and";}
            case NOT -> {return "not";}
            default -> {return "invalid";}
        }
    }

    public static BoolType fromString(String string) {
        switch (string) {
            case "or" -> {return OR;}
            case "and" -> {return AND;}
            case "not" -> {return NOT;}
            default -> {return INVALID;}
        }
    }
}
