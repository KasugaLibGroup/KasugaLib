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

    /**
     * Internal, do not use directly
     */
    private CommandHandler setCtx(CommandContext<CommandSourceStack> ctx) {
        this.ctx = ctx;
        return this;
    }

    public <T> T parseString(String name, Class<T> type){
        String base = ctx.getArgument(name, String.class);
        return ArgumentTypeReg.INSTANCE.parse(base, type);
    }

    public int execute(CommandContext<CommandSourceStack> ctx){
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
