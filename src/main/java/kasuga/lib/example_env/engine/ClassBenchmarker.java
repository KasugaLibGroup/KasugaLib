package kasuga.lib.example_env.engine;

import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public class ClassBenchmarker {

    public static int alive = 0;
    ClassBenchmarker(){
        alive ++;
        System.out.println("+ " + String.valueOf(System.identityHashCode(this)));
    }
    @HostAccess.Export
    public void directCall(){}

    @HostAccess.Export
    public void directCallWithOverloading(String a){}
    @HostAccess.Export
    public void directCallWithOverloading(String a, String b){}
    @HostAccess.Export
    public void directCallWithOverloading(String a, Boolean b){}

    @HostAccess.Export
    public Object directCallWithObjectReturnType(){
        return new ClassBenchmarker();
    }

    @HostAccess.Export
    public void directCallWithObjectParameter(ClassBenchmarker benchmarker){
        return;
    }

    @Override
    protected void finalize() throws Throwable {
        alive--;
        System.out.println("- " + String.valueOf(System.identityHashCode(this)));
        super.finalize();
    }
}
