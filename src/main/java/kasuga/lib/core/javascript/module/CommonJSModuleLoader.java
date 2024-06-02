package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.utils.MultipleReader;
import kasuga.lib.core.javascript.utils.OptionalUtil;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

public class CommonJSModuleLoader implements ModuleLoader {

    @Override
    public boolean isLoadable(String identifier) {
        return true;
    }

    @Override
    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext) {
        Optional<ResourceLocation> locationParseResult = ResourceLocation.read(module).result();
        if(locationParseResult.isEmpty())
            return Optional.empty(); // @TODO: Relative path
        ResourceLocation location = locationParseResult.get();
        Optional<Resource> resourceGetResult = OptionalUtil.firstNotEmpty(
                ()->Resources.attemptGetResource(location),
                ()->Resources.attemptGetResource(
                        new ResourceLocation(
                                location.getNamespace(),
                                location.getPath() + ".js"
                        )
                ),
                ()->Resources.attemptGetResource(
                        new ResourceLocation(
                                location.getNamespace(),
                                location.getPath() + "/index.js"
                        )
                )
        );
        if(resourceGetResult.isEmpty())
            return Optional.empty();
        Resource resource = resourceGetResult.get();
        Source source = readAndParseSource(resource,location.toString());
        return Optional.of(execute(context, source, requireFn, location));
    }

    protected Source readAndParseSource(Resource resource, String moduleName){
        MultipleReader reader;
        Source compiledSource;
        try{
            BufferedReader sourceBuf = resource.openAsReader();

            StringReader headReader = new StringReader("(function(exports, {require}, module, __filename, __dirname) {");
            StringReader tailReader = new StringReader(";});");

            reader = new MultipleReader(headReader,sourceBuf,tailReader);

            compiledSource =
                    Source.newBuilder("js",reader,moduleName)
                            .encoding(StandardCharsets.UTF_8)
                            .cached(true)
                            .build();
        }catch (IOException e){
            throw new ModuleLoadException(moduleName,"Failed to read module: "+e.toString(),e);
        }
        return compiledSource;
    }

    protected Value execute(Context context, Source source, Value requireFn, ResourceLocation location){
        Value module = context.eval(source);
        if(!module.canExecute()){
            throw new IllegalStateException("This module is not a valid module or error");
        }
        Value moduleWrapper = context.eval("js","({exports:{}})");
        module.execute(moduleWrapper.getMember("exports"),
                requireFn,
                moduleWrapper,
                location.toString(),
                location.getNamespace() + ":" + Path.of(location.getPath()).getFileName());
        return moduleWrapper.getMember("exports");
    }
}
