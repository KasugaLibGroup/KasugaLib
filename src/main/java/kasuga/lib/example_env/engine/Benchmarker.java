package kasuga.lib.example_env.engine;


import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.core.javascript.engine.javet.JavetKasugaConverter;
import kasuga.lib.core.javascript.engine.javet.converter.FastJavetClassConverter;

public class Benchmarker {
    public static String benchmarkCode = """
measureTime();
var i;
for(i=0;i<1000;i++){
    benchmarker.directCall();
    var a = benchmarker.directCallWithObjectReturnType();
    benchmarker.directCallWithObjectParameter(a);
    benchmarker.directCallWithOverloading("Hello,World");
}

console.info("ΔT1=", measureTime() / 1000);

measureTime();
for(i=0;i<10000;i++){
    benchmarker.directCall();
}
console.info("ΔTD=", measureTime() / 1000);

measureTime();
for(i=0;i<10000;i++){
    benchmarker.directCallWithOverloading("Hello,World");
}
console.info("ΔTO=", measureTime() / 1000);

var a = benchmarker.directCallWithObjectReturnType();

measureTime();
for(i=0;i<10000;i++){
    benchmarker.directCallWithObjectParameter(a);
}
console.info("ΔTP=", measureTime() / 1000);
""";


    public static void benchmarkOriginal() throws JavetException {
        var runtime = V8Host.getV8Instance().createV8Runtime();
        runtime.setConverter(new JavetKasugaConverter(runtime));
        JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(runtime);
        consoleInterceptor.register(runtime.getGlobalObject());
        var ref = new Object() {
            long initTime = System.nanoTime();
        };
        runtime.getGlobalObject().bindFunction(new JavetCallbackContext(
                "measureTime",
                JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult)(V8Value...object)->{
                    long oldInitTime = ref.initTime;
                    ref.initTime = System.nanoTime();
                    return runtime.getConverter().toV8Value(runtime, ((int) (System.nanoTime() - oldInitTime)));
                }
        ));

        System.out.println("-----Benchmarking Result of JavetKasugaConverter-----");

        long nanoTime = System.nanoTime();

        runtime.getGlobalObject().set("benchmarker",new ClassBenchmarker());

        System.out.printf("ΔT初 = %d\n", (System.nanoTime() - nanoTime));

        int beforeRef = runtime.getReferenceCount();

        int beforeCtx = runtime.getCallbackContextCount();

        runtime.getExecutor(benchmarkCode).execute();

        runtime.lowMemoryNotification();
        System.gc();

        System.out.printf("ΔREF = %d\n" , beforeRef - runtime.getReferenceCount());
        System.out.printf("ΔCTX = %d\n" , beforeCtx - runtime.getCallbackContextCount());

        runtime.close();
    }

    public static void benchmarkNeo() throws JavetException {
        var runtime = V8Host.getV8Instance().createV8Runtime();
        runtime.setConverter(new FastJavetClassConverter(runtime));
        JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(runtime);
        consoleInterceptor.register(runtime.getGlobalObject());
        var ref = new Object() {
            long initTime = System.nanoTime();
        };
        runtime.getGlobalObject().bindFunction(new JavetCallbackContext(
                "measureTime",
                JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult)(V8Value...object)->{
                    long oldInitTime = ref.initTime;
                    ref.initTime = System.nanoTime();
                    return runtime.getConverter().toV8Value(runtime, ((int) (System.nanoTime() - oldInitTime)));
                }
        ));
        System.out.println("-----Benchmarking Result of FastJavetClassConverter-----");
        long nanoTime = System.nanoTime();
        runtime.getGlobalObject().set("benchmarker",new ClassBenchmarker());
        System.out.printf("ΔT初 = %d\n", (System.nanoTime() - nanoTime));

        int beforeRef = runtime.getReferenceCount();

        int beforeCtx = runtime.getCallbackContextCount();

        runtime.getExecutor(benchmarkCode).execute();

        runtime.lowMemoryNotification();
        System.gc();

        System.out.printf("ΔREF = %d\n" , beforeRef - runtime.getReferenceCount());
        System.out.printf("ΔCTX = %d\n" , beforeCtx - runtime.getCallbackContextCount());

        runtime.close();
    }

    public static void start() throws JavetException {
        benchmarkOriginal();
        benchmarkNeo();
    }
}
