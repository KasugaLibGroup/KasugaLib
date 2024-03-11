package kasuga.lib.codes.compute.exceptions;

import kasuga.lib.codes.compute.data.Operational;
import kasuga.lib.codes.compute.infrastructure.Formula;

public class FormulaOperationError extends RuntimeException {
    private final String statement;
    public FormulaOperationError(Formula former, Operational operational, Formula rear) {
        this.statement = "<" + former.getString() + operational.getString() + rear.getString() + ">";
    }

    @Override
    public void printStackTrace() {
        System.err.println("Cannot operate via " + statement + ", pls check your input");
        super.printStackTrace();
    }
}
