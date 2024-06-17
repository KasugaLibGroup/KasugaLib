package kasuga.lib.core.javascript.commands;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.registrations.common.ArgumentTypeReg;
import kasuga.lib.registrations.common.CommandReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import java.io.File;
import java.net.URL;

@SuppressWarnings("Unused")
public class JavascriptModuleCommands {
    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);

    public static final ArgumentTypeReg type = ArgumentTypeReg.INSTANCE.registerType(File.class, File::new)
            .submit(REGISTRY);

    public static final CommandReg JS_OPEN = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("open", false)
            .addResourceLocation("ImageLocation", false)
            .addString("ContainerID", true)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js open <ImageLocation> [ContainerID]
                    ResourceLocation image = getParameter("ImageLocation", ResourceLocation.class);
                    String containerID;
                    try{
                        containerID = getParameter("ContainerID", String.class);
                    }catch (IllegalArgumentException e){
                        //Known, ignore
                    }
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command11 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("stop", false)
            .addString("ContextID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js stop <ContextID>
                    String contextID = getParameter("ContextID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command12 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("require", false)
            .addString("ContextID",  false)
            .addString("ModuleID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js require <ContextID> <ModuleID>
                    String contextID = getParameter("ContextID", String.class);
                    String moduleID = getParameter("ModuleID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command20 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("client", false)
            .addLiteral("open", false)
            .addResourceLocation("ImageLocation",  false)
            .addString("ContainerID",  true)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js client open <ImageLocation> [ContainerID]
                    ResourceLocation image = getParameter("ImageLocation", ResourceLocation.class);
                    String containerID;
                    try{
                        containerID = getParameter("ContainerID", String.class);
                    }catch (IllegalArgumentException e){
                        //Known, ignore
                    }
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command21 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("client", false)
            .addLiteral("stop", false)
            .addString("ContextID",  false)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js client stop <ContextID>
                    String contextID = getParameter("ContextID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command22 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("client", false)
            .addLiteral("require", false)
            .addString("ContextID",  false)
            .addString("ModuleID",  false)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js client require <ContextID> <ModuleID>
                    String contextID = getParameter("ContextID", String.class);
                    String moduleID = getParameter("ModuleID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command30 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("info",  false)
            .addString("ChannelID",  true)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel info [ChannelID]
                    String channelID;
                    try {
                        channelID = getParameter("ChannelID", String.class);
                    }catch (IllegalArgumentException e){
                        //Known,ignore
                    }
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command31 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("monitor",  false)
            .addLiteral("start",  false)
            .addString("ChannelID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel monitor start <ChannelID>
                    String channelID = getParameter("ChannelID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command32 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("monitor",  false)
            .addLiteral("stop",  false)
            .addString("ChannelID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel monitor stop <ChannelID>
                    String channelID = getParameter("ChannelID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command33 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("start",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock start
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command34 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("accept",  false)
            .addString("ChannelID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock accept <ChannelID>
                    String channelID = getParameter("ChannelID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command35 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("next",  false)
            .addString("ChannelID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock next <ChannelID>
                    String channelID = getParameter("ChannelID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command36 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("active",  false)
            .addString("ChannelID",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock active <ChannelID>
                    String channelID = getParameter("ChannelID", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command37 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("send",  false)
            .addString("Content",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock send <Content>
                    String content = getParameter("Content", String.class);
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command38 = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("channel", false)
            .addLiteral("mock",  false)
            .addLiteral("stop",  false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasugalib js channel mock stop
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command40 = new CommandReg("kasuga")
            .addLiteral("gui-debug", false)
            .addLiteral("load-metro", false)
            .addString("Bundle", false)
            .addURL("Server-Address", true)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasuga gui-debug load-metro <Bundle> [Server-Address]
                    String bundle = getParameter("Bundle", String.class);
                    URL containerID;
                    try{
                        containerID = getParameter("Server-Address", URL.class);
                    }catch (IllegalArgumentException e){
                        //Known, ignore
                    }
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static final CommandReg command42 = new CommandReg("kasuga")
            .addLiteral("list", false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasuga list
                    //TODO Handle me!
                }
            }).submit(REGISTRY);

    public static void invoke(){
        REGISTRY.submit();
    }
}
