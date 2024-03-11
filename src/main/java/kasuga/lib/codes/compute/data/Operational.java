package kasuga.lib.codes.compute.data;

import kasuga.lib.codes.compute.exceptions.FormulaSynatxError;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.compute.exceptions.FormulaOperationError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Operational implements Formula {
    String operation = "+";
    boolean shouldRemove = false;

    public Operational(String string) {
        fromString(string);
    }

    @Override
    public String getString() {
        return operation;
    }

    @Override
    public String getIdentifier() {
        return "operational";
    }

    @Override
    public float getResult() {
        return 0;
    }

    @Override
    public List<Formula> getElements() {
        return List.of(this);
    }

    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public boolean shouldRemove() {
        return shouldRemove;
    }

    @Override
    public void fromString(String string) {
        String s = string.trim();
        if(isOperational(s))
            this.operation = string;
    }

    public static int getOperationIndex(String string, int index) {
        String x = string.replaceAll("(\\+|-|\\*|/|\\^|%)", "OPT");
        return x.indexOf("OPT", index);
    }

    public static int getOperationIndex(String string) {
        return getOperationIndex(string, 0);
    }

    public float operate(Formula former, Formula rear) {
        if(former instanceof Operational) {
            throw new FormulaOperationError(former, this, rear);
        } else {
            switch (operation) {
                case "+" -> {return former.getResult() + rear.getResult();}
                case "-" -> {return former.getResult() - rear.getResult();}
                case "*" -> {return former.getResult() * rear.getResult();}
                case "/" -> {return former.getResult() / rear.getResult();}
                case "%" -> {return former.getResult() % rear.getResult();}
                case "^" -> {return (float) Math.pow(former.getResult(), rear.getResult());}
                default -> {return 0;}
            }
        }
    }

    public static boolean isOperational(String string) {
        String s = string.replaceAll(" ", "");
        return (s.equals("+") || s.equals("-") || s.equals("*") ||
                s.equals("/") || s.equals("%") || s.equals("^"));
    }

    public void mergeOperation(Formula f, Formula parent, int index) {
        if(f instanceof Operational fromer) {
            switch (operation) {
                case "+" -> {
                    switch (fromer.operation) {
                        case "+" -> fromer.shouldRemove = true;
                        case "-" -> {
                            fromer.shouldRemove = true;
                            this.operation = "-";
                        }
                        default -> shouldRemove = true;
                    }
                }
                case "-" -> {
                    switch (fromer.operation) {
                        case "+" -> fromer.shouldRemove = true;
                        case "-" -> {
                            fromer.shouldRemove = true;
                            this.operation = "+";
                        }
                    }
                }
                case "*" -> {
                    if (fromer.operation.equals("*")) {
                        fromer.shouldRemove = true;
                        this.operation = "^";
                    } else {
                        throw new FormulaSynatxError(parent, index);
                    }
                }
                default -> throw new FormulaSynatxError(parent, index);
            }
        }
    }

    public Operational clone() {
        return new Operational(String.copyValueOf(this.operation.toCharArray()));
    }

    @Override
    public void flipOutput(boolean flip) {}

    @Override
    public boolean isOutputFlipped() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Operational operational)) return false;
        return operational.operation.equals(operation);
    }
}
