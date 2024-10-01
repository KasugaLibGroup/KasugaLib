package kasuga.lib.core.javascript.module;

public class ModuleLoadException extends RuntimeException{
    public ModuleLoadException(String moduleName, String cause){
        super(generateExceptionDescription(moduleName, cause));
    }

    public ModuleLoadException(String moduleName, String cause, Throwable parent){
        super(generateExceptionDescription(moduleName, cause), parent);
    }

    public static String generateExceptionDescription(String moduleName, String cause){
        return String.format("Failed to require module %s, %s",moduleName,cause);
    }
}
