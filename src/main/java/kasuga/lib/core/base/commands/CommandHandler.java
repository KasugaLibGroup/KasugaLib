package kasuga.lib.core.base.commands;

import com.mojang.brigadier.context.CommandContext;
import kasuga.lib.registrations.common.ArgumentTypeReg;
import net.minecraft.commands.CommandSourceStack;

import static kasuga.lib.KasugaLib.MAIN_LOGGER;

/**
 * Extend this class to create your command handler(Use CommandReg.INSTANCE.new)
 */
public abstract class CommandHandler {
    protected CommandContext<CommandSourceStack> ctx;

    public CommandHandler() {
    }

    public <T> T getParameter(String name, Class<T> type){
        String base = ctx.getArgument(name, String.class);
        return ArgumentTypeReg.INSTANCE.parse(base, type);
    }

    public int executeWithContext(CommandContext<CommandSourceStack> ctx){
        this.ctx = ctx;
        try {
            run();
        } catch (Exception e) {
            MAIN_LOGGER.error("Error during command: ", e);
            return -1;
        }
        return 1;
    }

    public abstract void run();
}
