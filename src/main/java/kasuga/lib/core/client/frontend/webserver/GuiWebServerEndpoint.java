package kasuga.lib.core.client.frontend.webserver;

import kasuga.lib.core.addons.minecraft.ClientAddon;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.resource.ResourceManagerPackageProvider;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.core.webserver.KasugaHttpServer;
import kasuga.lib.core.webserver.KasugaServerAuthenticationGateway;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GuiWebServerEndpoint {

    protected static Collection<NodePackage> findPackages(){
        ResourceManagerPackageProvider provider = ClientAddon.provider;

        HashSet<NodePackage> packages = new HashSet<>();

        for(Pair<NodePackage, List<NodePackage>> pair : provider.packages) {
            packages.add(pair.getFirst());
            packages.addAll(pair.getSecond());
        }

        return packages;
    }
    public static void invoke() {
        KasugaHttpServer.CLIENT.get("/addons/<filename>", KasugaHttpServer.CLIENT_AUTH.withAuthentication((ctx)->{

            String packageName = ctx.pathParam("filename");

            if(ClientAddon.provider == null) {
                ctx.status(500);
                ctx.result("Server has not ready");
            }

            Collection<NodePackage> packages = findPackages();

            for (NodePackage aPackage : packages) {
                if(!packageName.startsWith(aPackage.packageName)) {
                    continue;
                }
                String pathName = packageName.substring(aPackage.packageName.length());

                if(aPackage.reader.exists(pathName)) {
                    ctx.result(aPackage.reader.open(pathName));
                    return;
                }
            }
            ctx.status(404);
            ctx.result("Not found");
        }));

        KasugaHttpServer.CLIENT.get("/addons", KasugaHttpServer.CLIENT_AUTH.withAuthentication((ctx)-> {
            ctx.json(findPackages());
        }));
    }
}
