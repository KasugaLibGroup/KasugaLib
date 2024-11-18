package kasuga.lib.core.menu.network;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.LabelType;
import net.minecraft.resources.ResourceLocation;

public class MenuAddressTypes {
    public static final LabelType<GuiClientMenuAddress> CLIENT =
            KasugaLib.STACKS.CHANNEL.labelTypeRegistry.register(
                    new ResourceLocation("kasuga_lib", "gui_client_menu"),
                    new LabelType<>(GuiClientMenuAddress::new)
            );

    public static final LabelType<GuiServerMenuAddress> SERVER =
            KasugaLib.STACKS.CHANNEL.labelTypeRegistry.register(
                    new ResourceLocation("kasuga_lib", "gui_server_menu"),
                    new LabelType<>(GuiServerMenuAddress::new)
            );
}