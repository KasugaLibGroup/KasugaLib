package kasuga.lib.registrations.exception;

public class RegistryElementNotPresentException extends Exception {
    private final Class<?> clazz;
    private final String output;
    public RegistryElementNotPresentException(Class<?> clazz, String output) {
        super();
        this.clazz = clazz;
        this.output = output;
    }

    @Override
    public void printStackTrace() {
        System.out.println(output);
        super.printStackTrace();
    }

    public static RegistryElementNotPresentException of(Class<?> clazz, String regType, String method) {
        String builder = "Registry Element " + clazz + " is not present, " +
                "pls create a " + regType + " registration before calling " + method;
        return new RegistryElementNotPresentException(clazz, builder);
    }
}
