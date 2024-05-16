package kasuga.lib.core.packets.gui;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DevOpenScreenPacket extends S2CPacket {
    static CloseableHttpClient client = HttpClients.createDefault();
    String bundle;
    String serverName;
    boolean isSecureConnection;

    public DevOpenScreenPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
        CompoundTag tag = byteBuf.readNbt();
        if(tag == null)
            throw new IllegalStateException("Invalid packet");
        this.serverName = tag.getString("serverName");
        this.bundle = tag.getString("bundle");
        this.isSecureConnection = tag.getBoolean("isSecureConnection");
    }

    public DevOpenScreenPacket(String bundle, String serverName, Boolean isSecureConnection) {
        this.bundle = bundle;
        this.serverName = serverName;
        this.isSecureConnection = isSecureConnection;
    }

    @Override
    public void handle(Minecraft minecraft) {
        HttpGet bundleAccessor = new HttpGet(
                (isSecureConnection ? "https://" : "http://") +
                        serverName + "/" + bundle + ".bundle?platform=minecraft&dev=true&inlineSourceMap=false&modulesOnly=false&runModule=true&hot=true"
        );
        CloseableHttpResponse response;
        InputStream stream;
        InputStreamReader reader;
        try{
            response = client.execute(bundleAccessor);
            stream = response.getEntity().getContent();
            reader = new InputStreamReader(stream);
        }catch (IOException e){
            minecraft.player.sendSystemMessage(Component.literal("Failed to fetch"));
            return;
        }
        Source source;
        try{
            source = Source.newBuilder("js",reader,"bundled.js").build();
        }catch (IOException e){
            minecraft.player.sendSystemMessage(Component.literal("Failed to parse source:"+e.toString()));
            return;
        }
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> {
            Minecraft.getInstance().setScreen(KasugaLib.STACKS.GUI_MANAGER.create(source).createScreen());
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("bundle",bundle);
        nbt.putString("serverName",serverName);
        nbt.putBoolean("isSecureConnection",isSecureConnection);
        buf.writeNbt(nbt);
    }
}
