package kasuga.lib.core.client.gui.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.packets.gui.DevOpenScreenPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DevelopmentCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal("kasugalib").then(
                        Commands.literal("gui")
                                .then(
                                        Commands.literal("debug")
                                                .then(
                                                        Commands.argument("bundle",StringArgumentType.greedyString())
                                                                .executes((ctx)->{
                                                                    String bundleName = StringArgumentType.getString(ctx,"bundle");
                                                                    execute(ctx,bundleName);
                                                                    return 1;
                                                                })
                                                )
                                )
                )
        );
    }


    public static void execute(CommandContext<CommandSourceStack> ctx, String bundle){
        execute(ctx,bundle,"localhost:8081", false);
    }

    public static void execute(CommandContext<CommandSourceStack> ctx,String bundle, String serverName, Boolean isSecureConnection){
        CommandSourceStack command_source = ctx.getSource();
        if(!command_source.isPlayer())
            return;
        AllPackets.channel.getChannel().send(PacketDistributor.PLAYER.with(command_source::getPlayer), new DevOpenScreenPacket(bundle,serverName,isSecureConnection));
    }
}
