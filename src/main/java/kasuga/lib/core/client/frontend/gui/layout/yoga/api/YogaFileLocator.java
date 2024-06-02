package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import net.minecraft.client.main.Main;
import org.lwjgl.system.Configuration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class YogaFileLocator {

    public static void configureLWJGLPath(){
        Configuration.LIBRARY_PATH.set(Configuration.LIBRARY_PATH.get() + File.pathSeparator + YogaFileLocator.getYogaAssemblyDirectory());
    }

    public static String getYogaAssemblyDirectory(){
        URI path;
        try{
            path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        }catch (URISyntaxException uriSyntaxException){
            return null;
        }
        if (!path.getPath().contains(".jar") || path.getPath().contains(".gradle")){
            return getDevYogaWebAssemblyDirectory();
        }
        String jarPath = path.getPath();

        return jarPath.substring(1, jarPath.length() - 4)+"!/yoga/";
    }

    public static String getDevYogaWebAssemblyDirectory(){
        return "../src/generated/resources/yoga/";
    }
}
