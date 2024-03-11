package kasuga.lib.codes.compute.exceptions;

import kasuga.lib.codes.compute.infrastructure.Formula;

public class FormulaSynatxError extends RuntimeException {

    private final Formula formula;
    private final int position;
    public FormulaSynatxError(Formula formula, int position) {
        this.formula = formula;
        this.position = position;
    }
}
