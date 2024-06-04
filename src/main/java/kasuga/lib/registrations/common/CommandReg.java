package kasuga.lib.registrations.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.base.commands.CommandNode;
import kasuga.lib.core.base.commands.CommandTree;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static kasuga.lib.core.base.commands.CommandNode.parseArgumentType;
import static net.minecraft.commands.Commands.literal;

public class CommandReg extends Reg {
    private static final LinkedList<CommandReg> ENTRIES = new LinkedList<>();
    private final String commandName;
    private final CommandTree tree;
    private CommandHandler handler;

    private boolean optionalStartFlag = false;
    private int permission = 2;

    private static final HashMap<String, Pair<LiteralArgumentBuilder<CommandSourceStack>, Dist>> ROOTS = new HashMap<>();
    public static final HashMap<ResourceLocation, BaseArgument> types = new HashMap<>();

    /**
     * Beginning of commands registries.
     * For instance, if you need /lp <Enumpara1> <int> and /lp <double> <url>,
     * please register twice.
     *
     * @param registrationKey The name of your command.
     */
    public CommandReg(String registrationKey) {
        super(registrationKey);
        this.commandName = registrationKey;
        this.tree = new CommandTree(new CommandNode(registrationKey));
        ROOTS.put(registrationKey, Pair.of(literal(registrationKey), null));
    }

    /**
     * Append an enumable string parameter to the end of your command.
     *
     * @param list     List of your enums
     * @param isOptional Is this property optional? You can't append required parameters after any optional parameters!
     * @return The Reg itself
     */
    public CommandReg appendEnumable(ArrayList<String> list, boolean isOptional) {
        System.out.println(tree.leaves.size());
        tree.addEnums(isOptional, list);
        if(this.optionalStartFlag && !isOptional){
            throw new IllegalArgumentException();
        }
        this.optionalStartFlag = isOptional;
        return this;
    }

    /**
     * Append an integer parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param isOptional Is this property optional? You can't append required parameters after any optional parameters!
     * @return The Reg itself
     */
    public CommandReg appendSingleParameter(String defaultName, boolean isOptional, Class target) {
        tree.addNode(isOptional, defaultName, ArgumentTypeReg.types.get(target.getName()).getSecond());
        if(this.optionalStartFlag && !isOptional){
            throw new IllegalArgumentException();
        }
        this.optionalStartFlag = isOptional;
        return this;
    }

    /**
     * What permission should the player have to use the command?
     *
     * @param level The level player should have to use the command
     * @return The Reg itself
     * @see net.minecraft.commands.Commands
     */
    public CommandReg requirePermissionLevel(int level) {
        if (level < 0) level = 0;
        if (level > 4) level = 4;
        this.permission = level;
        return this;
    }

    /**
     * Set all the command of the same prefix to be executed only in specified side.
     * @param dist The dist you'd like to let your command be executed. Null for both sides.
     * @return
     */
    public CommandReg onlyIn(Dist dist){
        ROOTS.remove(this.commandName);
        ROOTS.put(this.commandName, Pair.of(literal(registrationKey), dist));
        return this;
    }

    /**
     * @param runnable your command's handler.
     * @return The Reg itself
     */
    public CommandReg setHandler(CommandHandler runnable) {
        this.handler = runnable;
        return this;
    }

    /**
     * Marks that your command is over.
     *
     * @param registry the mod SimpleRegistry.
     * @return
     */
    @Override
    public CommandReg submit(SimpleRegistry registry) {
        ENTRIES.add(this);
        registry.command().put(this.commandName, this);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "command";
    }

    /**
     * Subscribe the CommandEvent to get your commands registered.
     *
     * @param dispatcher The dispatcher of CommandEvent
     * @see net.minecraftforge.event.RegisterCommandsEvent
     */
    @Inner
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        ENTRIES.forEach(CommandReg::register);
        ROOTS.values().forEach(pair -> {
            if(pair.getSecond() == null ){
                dispatcher.register(pair.getFirst());
                return;
            }
            switch (pair.getSecond()) {
                case CLIENT ->
                        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> dispatcher.register(pair.getFirst()));
                case DEDICATED_SERVER ->
                        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> dispatcher.register(pair.getFirst()));
            }
        });
    }

    @Inner
    private static void register(final CommandReg commandReg) {
        System.out.println(commandReg.tree.leaves.size());
        for (CommandNode leaf : commandReg.tree.leaves) {
            while (true) {
                System.out.println(leaf.name);
                if (leaf.isRoot())
                    break;
                leaf = leaf.father;
            }
            System.out.println();
        }

        if (commandReg.handler == null) {
            throw new NullPointerException();
        }

        for (CommandNode leaf : commandReg.tree.leaves) {
            CommandNode commandNode = leaf;
            LiteralArgumentBuilder<CommandSourceStack> builder = ROOTS.get(commandReg.commandName).getFirst();
            ArgumentBuilder<CommandSourceStack, ?> argument, post;
            argument = null;
            if (commandNode.isRoot()) {
                builder.executes(commandReg.handler::execute);
                continue;
            }
            while (true) {
                if (commandNode.isLeaf) {
                    post = parseArgumentType(commandNode)
                            .requires(p -> p.hasPermission(commandReg.permission))
                            .executes(commandReg.handler::execute);
                    if(commandNode.father.isRoot()){
                        builder.then(post);
                        break;
                    }
                } else {
                    post = argument;
                }
                if (commandNode.father.isRoot()) {
                    builder.then(argument == null ? parseArgumentType(commandNode) : argument);
                    break;
                }
                commandNode = commandNode.father;
                if(commandNode.children.stream().anyMatch(commandNode1 -> commandNode1.required)){
                    argument = parseArgumentType(commandNode).then(post);
                } else {
                    argument = parseArgumentType(commandNode)
                            .requires(p -> p.hasPermission(commandReg.permission))
                            .executes(commandReg.handler::execute)
                            .then(post);
                }
            }
        }
    }
}
