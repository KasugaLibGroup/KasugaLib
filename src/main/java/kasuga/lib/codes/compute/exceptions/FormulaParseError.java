package kasuga.lib.codes.compute.exceptions;

public class FormulaParseError extends RuntimeException {

    private final Exception main;
    private final String parsing;
    public FormulaParseError(Exception exception, String parsing) {
        this.main = exception;
        this.parsing = parsing;
    }

    @Override
    public void printStackTrace() {
        System.err.println("Unexpected error while parsing " + parsing);
        main.printStackTrace();
        super.printStackTrace();
    }
}
