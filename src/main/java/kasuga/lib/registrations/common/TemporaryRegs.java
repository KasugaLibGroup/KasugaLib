package kasuga.lib.registrations.common;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.gui.commands.SimpleStringInfo;
import kasuga.lib.core.client.gui.commands.SimpleStringParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TemporaryRegs {
    public static void register(IEventBus bus){
        DeferredRegister<ArgumentTypeInfo<?, ?>> args =
                DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, KasugaLib.MOD_ID);
        args.register("simple_string",
                () -> ArgumentTypeInfos.registerByClass(SimpleStringParser.class, new SimpleStringInfo()));
        args.register(bus);
    }
}
