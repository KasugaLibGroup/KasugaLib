package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Envs;
import org.lwjgl.system.Configuration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class YogaFileLocator {

    public static void configureLWJGLPath(){
        Configuration.LIBRARY_PATH.set(Configuration.LIBRARY_PATH.get() + File.pathSeparator + YogaFileLocator.getYogaAssemblyDirectory());
    }

    public static String getYogaAssemblyDirectory(){
        if(Envs.isDevEnvironment()){
            return getDevYogaAssemblyDirectory();
        }
        URI path;
        try{
            path = KasugaLib.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        }catch (URISyntaxException uriSyntaxException){
            return null;
        }
        if (!path.getPath().contains(".jar") || path.getPath().contains(".gradle")){
            return null;
        }
        String jarPath = path.getPath();
        return jarPath.substring(1, path.getPath().indexOf(".jar") + 4);
    }

    public static String getDevYogaAssemblyDirectory(){
        return "../src/generated/resources/yoga/";
    }
}
