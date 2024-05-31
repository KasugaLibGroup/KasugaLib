package kasuga.lib.registrations.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kasuga.lib.core.base.commands.ArgType;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.base.commands.CommandTree;
import kasuga.lib.core.base.commands.Node;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;
import java.util.function.Function;

import static kasuga.lib.KasugaLib.MAIN_LOGGER;
import static kasuga.lib.core.base.commands.ArgType.ROOT;
import static kasuga.lib.core.base.commands.ArgType.parseArgumentType;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CommandReg extends Reg {
    private static final LinkedList<CommandReg> ENTRIES = new LinkedList<>();
    private final String commandName;
    private final CommandTree tree;
    private CommandHandler handler;
    public static final CommandReg INSTANCE = new CommandReg("null");

    private boolean isClientOnly = false;
    private int permission = 2;

    private CommandReg(String registrationKey) {
        super(registrationKey);
        this.commandName = registrationKey;
        this.tree = new CommandTree(new Node(registrationKey));
    }

    /**
     * Registry of commands. Here's we go!
     *
     * @param registrationKey The name of your command. Duplicatable
     */
    public static CommandReg create(String registrationKey) {
        return new CommandReg(registrationKey);
    }

    /**
     * Append an enumable string parameter to the end of your command.
     *
     * @param list     List of your enums
     * @param optional Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendEnumable(ArrayList<String> list, boolean optional) {
        System.out.println(tree.leaves.size());
        tree.addEnums(optional, list);
        return this;
    }

    /**
     * Append an integer parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendInteger(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.INT);
        return this;
    }

    /**
     * Append a double parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendDouble(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.DOUBLE);
        return this;
    }

    /**
     * Append a single string parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendString(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.STRING);
        return this;
    }

    /**
     * Append a ResourceLocation parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendResourceLocation(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.RESOURCE_LOCATION);
        return this;
    }

    /**
     * Append a Vec3 parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendVec3(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.VECTOR3);
        return this;
    }

    /**
     * Append an custom parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    //TODO:Not completed yet
    public <T> CommandReg appendCustom(String defaultName, boolean optional, Function<String, T> function) {
        tree.addNode(optional, defaultName, ArgType.CUSTOM);
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
        return null;
    }

    @Override
    public String getIdentifier() {
        return "command";
    }

    /**
     * Subscribe to CommandEvent to get your commands registered.
     *
     * @param dispatcher
     * @see net.minecraftforge.event.RegisterCommandsEvent
     */
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        ENTRIES.forEach(commandReg -> {
            register(commandReg, dispatcher);
        });
    }

    /**
     * Internal, do not use directly
     */
    private static void register(final CommandReg commandReg, final CommandDispatcher<CommandSourceStack> dispatcher) {

        System.out.println(commandReg.tree.leaves.size());
        for (Node leaf : commandReg.tree.leaves) {
            while (true) {
                System.out.println(leaf.name);
                if (leaf.type == ROOT)
                    break;
                leaf = leaf.father;
            }
            System.out.println();
        }

        if (commandReg.handler == null) {
            throw new NullPointerException();
        }

        if (commandReg.tree.leaves.size() == 1) {
            dispatcher.register(literal(commandReg.commandName));
            return;
        }

        for (Node leaf : commandReg.tree.leaves) {
            Node node = leaf;
            LiteralArgumentBuilder<CommandSourceStack> builder = literal(commandReg.commandName);
            ArgumentBuilder<CommandSourceStack, ?> argument, post;
            argument = null;
            if (node.type == ArgType.ROOT) {
                dispatcher.register(builder.executes(ctx -> {
                    commandReg.handler.setCtx(ctx);
                    try {
                        commandReg.handler.run();
                    } catch (Exception e) {
                        MAIN_LOGGER.error("Error during command: ", e);
                        return -1;
                    }
                    return 1;
                }));
                continue;
            }
            while (true) {
                if (node.isLeaf) {
                    Node finalNode = node;
                    post = parseArgumentType(node)
                            .requires(p -> p.hasPermission(commandReg.permission))
                            .executes(ctx -> {
                                commandReg.handler.setCtx(ctx).setKeys(finalNode.parameters);
                                try {
                                    commandReg.handler.run();
                                } catch (Exception e) {
                                    MAIN_LOGGER.error("Error during command: ", e);
                                    return -1;
                                }
                                return 1;
                            });
                    if(node.father.type == ROOT){
                        dispatcher.register(builder.then(post));
                        break;
                    }
                } else {
                    post = argument;
                }
                if (node.father.type == ROOT) {
                    builder.then(argument == null ? parseArgumentType(node) : argument);
                    dispatcher.register(builder);
                    break;
                }
                node = node.father;
                argument = parseArgumentType(node).then(post);
            }
        }
    }
}
