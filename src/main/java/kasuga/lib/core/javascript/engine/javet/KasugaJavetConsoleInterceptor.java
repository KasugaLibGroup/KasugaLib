package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.interception.logging.BaseJavetConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import kasuga.lib.KasugaLib;
import org.slf4j.Logger;

public class KasugaJavetConsoleInterceptor extends BaseJavetConsoleInterceptor {
    
    private static final Logger LOGGER = KasugaLib.createLogger("Javascript Runtime");

    public KasugaJavetConsoleInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
    }

    @Override
    public void consoleDebug(V8Value... v8Values) {
        LOGGER.debug(concat(v8Values));
    }

    @Override
    public void consoleError(V8Value... v8Values) {
        LOGGER.error(concat(v8Values));
    }

    @Override
    public void consoleInfo(V8Value... v8Values) {
        LOGGER.info(concat(v8Values));
    }

    @Override
    public void consoleLog(V8Value... v8Values) {
        LOGGER.info(concat(v8Values));
    }

    @Override
    public void consoleTrace(V8Value... v8Values) {
        LOGGER.debug(concat(v8Values));
    }

    @Override
    public void consoleWarn(V8Value... v8Values) {
        LOGGER.warn(concat(v8Values));
    }
}
