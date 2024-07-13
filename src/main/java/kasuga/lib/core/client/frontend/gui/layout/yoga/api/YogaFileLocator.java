package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Envs;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
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
        }else if(Envs.isClient()){
            try{
                return YogaFileExtractor.extract() + File.separatorChar + "lwjgl-yoga-3.3.1" + File.separatorChar;
            }catch (Exception e){
                Minecraft.crash(CrashReport.forThrowable(e, "Failed to load yoga assembly"));
            }
        }
        throw new IllegalStateException("Illegal environment");
    }

    public static String getDevYogaAssemblyDirectory(){
        return "../src/generated/resources/libraries/lwjgl-yoga-3.3.1/";
    }
}
