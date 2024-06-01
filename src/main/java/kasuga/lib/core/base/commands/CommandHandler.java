package kasuga.lib.core.base.commands;

import com.mojang.brigadier.context.CommandContext;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Function;

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

    public <T extends BaseArgument> Object getArgument(String name, T type){
        String base = ctx.getArgument(name, String.class);
        try {
            Function func = (Function) BaseArgument.class.getDeclaredField("parser").get(type);
            return func.apply(base);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
