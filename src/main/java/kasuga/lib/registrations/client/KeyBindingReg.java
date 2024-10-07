package kasuga.lib.registrations.client;

import com.mojang.blaze3d.platform.InputConstants;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.common.ChannelReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.function.Consumer;

public class KeyBindingReg extends Reg {
    public final String translationKey;
    public final String category;
    private int keyCode = -1;
    private KeyMapping mapping;
    private Environment env = Environment.ALL;
    private Consumer<LocalPlayer> clientHandler;
    private Consumer<ServerPlayer> serverHandler;
    private KeyModifier modifier = KeyModifier.NONE;
    private InputConstants.Type type = InputConstants.Type.KEYSYM;
    private static final ChannelReg keyChannel = new ChannelReg("kasuga_lib_key_bindings")
            .brand("1.0")
            .loadPacket(KeyBindingReg.KeySyncPacket.class, KeyBindingReg.KeySyncPacket::new)
            .submit(KasugaLibStacks.REGISTRY);;
    private static final LinkedList<KeyBindingReg> registered = new LinkedList<>();

    /**
     * The beginning of your registry
     * @param translationKey Your key binding's translatable name
     * @param categoryName Your key binding's category name
     * @see KeyMapping
     */
    public KeyBindingReg(String translationKey, String categoryName) {
        super(translationKey);
        this.translationKey = translationKey;
        this.category = categoryName;
    }

    /**
     * Set your input handler on logical client side
     * @param consumer The handler
     * @return The Reg itself
     */
    public KeyBindingReg setClientHandler(Consumer<LocalPlayer> consumer){
        clientHandler = consumer;
        return this;
    }

    /**
     * Set your input handler on logical server side
     * @param consumer The handler
     * @return The Reg itself
     */
    public KeyBindingReg setServerHandler(Consumer<ServerPlayer> consumer){
        serverHandler = consumer;
        return this;
    }

    /**
     * Set your key binding's default modifier, which means user should hold them down at once.
     * Can hold one modifier at most.
     * @param modifier Your key's modifier, can be control, shift or alt.
     * @return The reg itself.
     */
    public KeyBindingReg setModifier(KeyModifier modifier){
        this.modifier = modifier;
        return this;
    }

    /**
     * Set your key binding's input type. Can be a keyboard (KEYSYM) or mouse (MOUSE). It is highly recommended not to use scannode (SCANNODE) as it is platform-specific.
     * @param defaultKeyCode Your key's default GLFW code.
     * @param type Your key's input type.
     * @return The reg itself.
     */
    public KeyBindingReg setKeycode(int defaultKeyCode, InputConstants.Type type){
        this.keyCode = defaultKeyCode;
        this.type = type;
        return this;
    }

    /**
     * Set your key binding's environment, means it can be called at which moment.
     * @param env Your key's environment. Can be IN_GUI(Only call when any Screen is open), IN_GAME(Only call when none Screen is open) or ALL(With no restriction).
     * @return The reg itself.
     */
    public KeyBindingReg setEnvironment(Environment env){
        this.env = env;
        return this;
    }

    /**
     * Marks your registry is over
     * @param registry Your mod's SimpleRegistry.
     * @return The Reg itself
     */
    @Override
    public KeyBindingReg submit(SimpleRegistry registry) {
        if(keyCode <= -1){
            throw new IllegalArgumentException("Invalid keycode");
        }
        this.mapping = new KeyMapping(registrationKey, env.context, modifier, type, keyCode, category);
        registry.key().put(this.toString(), this);
        registered.add(this);
        return this;
    }

    public KeyMapping getMapping() {
        return mapping;
    }

    @Inner
    public static void onClientTick(){
        registered.stream().filter(reg -> reg.mapping.consumeClick())
                .forEach(reg -> {
                    if(reg.clientHandler != null){
                        reg.clientHandler.accept(Minecraft.getInstance().player);
                    }
                    keyChannel.sendToServer(new KeySyncPacket().setProperties(reg.toString()));
                });
    }

    @Override
    public String getIdentifier() {
        return "key_binding";
    }

    @Inner
    @Override
    public String toString() {
        return keyCode + category + mapping.getKey() + mapping.getName() + mapping.getCategory();
    }

    @Inner
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        KeyBindingReg that = (KeyBindingReg) o;

        return new EqualsBuilder()
                .append(keyCode, that.keyCode)
                .append(category, that.category)
                .append(registrationKey, that.registrationKey)
                .isEquals();
    }

    public enum Environment{
        ALL(KeyConflictContext.UNIVERSAL),
        IN_GUI(KeyConflictContext.GUI),
        IN_GAME(KeyConflictContext.IN_GAME);

        private final KeyConflictContext context;

        Environment(KeyConflictContext context) {
            this.context = context;
        }
    }

    private static class KeySyncPacket extends C2SPacket{
        String key;

        public KeySyncPacket() {
            super();
        }

        public KeySyncPacket(FriendlyByteBuf buf) {
            super(buf);
            int length = buf.readInt();
            this.key = (String) buf.readCharSequence(length, StandardCharsets.UTF_8);
        }

        public KeySyncPacket setProperties(String key) {
            this.key = key;
            return this;
        }

        @Override
        public void handle(NetworkEvent.Context context) {
            KeyBindingReg keyBinding = KeyBindingReg.registered.stream().filter(reg -> reg.toString().equals(key)).toList().get(0);
            if(keyBinding.serverHandler != null){
                keyBinding.serverHandler.accept(context.getSender());
            }
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(key.length());
            buf.writeCharSequence(key, StandardCharsets.UTF_8);
        }
    }

    public static void invoke(){}
}
