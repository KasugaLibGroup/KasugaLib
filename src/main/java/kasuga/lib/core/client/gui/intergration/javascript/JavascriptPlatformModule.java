package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.runtime.PlatformModule;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.openjdk.nashorn.internal.ir.Module;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;

public class JavascriptPlatformModule implements PlatformModule {
    Source source;
    String fileName;
    String dirName;

    public JavascriptPlatformModule(Source compiledSource,String fileName,String dirName) {
        this.source = compiledSource;
        this.fileName = fileName;
        this.dirName = dirName;
    }

    public static JavascriptPlatformModule fromLocation(ResourceLocation location) {
        if(!location.toString().endsWith(".js"))
            throw new IllegalArgumentException("Invalid module: a javascript module should end with .js");
        MultipleReader reader;
        Source compiledSource;
        try{
            Resource resource = Resources.getResource(location);
            BufferedReader sourceBuf = resource.openAsReader();
            StringReader headReader = new StringReader("(function(exports, require, module, __filename, __dirname) {");
            StringReader tailReader = new StringReader(";});");
            reader = new MultipleReader(headReader,sourceBuf,tailReader);

            compiledSource =
                    Source.newBuilder("js",reader,location.toString())
                            .encoding(StandardCharsets.UTF_8)
                            .cached(true)
                            .build();
        }catch (IOException e){
            throw new IllegalArgumentException("Failed to read module: "+e.toString(),e);
        }
        return new JavascriptPlatformModule(compiledSource,location.toString(),location.getNamespace() + ":" + Path.of(location.getPath()).getFileName());
    }

    public Source getSource() {
        return source;
    }

    public Value asCommonJs(JavascriptPlatformRuntime target){
        Value module = target.javascriptVmContext.eval(source);
        if(!module.canExecute()){
            throw new IllegalStateException("This module is not a valid module or error");
        }
        Value moduleWrapper = target.javascriptVmContext.eval("js","({exports:{}})");
        module.execute(moduleWrapper.getMember("exports"),
                (Function<String,Value>)(i)->target.getModule(new ResourceLocation(i)),
                moduleWrapper,
                fileName,
                dirName);
        return module.getMember("exports");
    }
}
