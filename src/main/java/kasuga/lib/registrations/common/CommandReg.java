package kasuga.lib.registrations.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgumentInfo;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.base.commands.CommandTree;
import kasuga.lib.core.base.commands.Node;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static kasuga.lib.KasugaLib.MAIN_LOGGER;
import static kasuga.lib.KasugaLib.MOD_ID;
import static kasuga.lib.core.base.commands.Node.parseArgumentType;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.core.Registry.COMMAND_ARGUMENT_TYPE_REGISTRY;

public class CommandReg extends Reg {
    private static final LinkedList<CommandReg> ENTRIES = new LinkedList<>();
    private final String commandName;
    private final CommandTree tree;
    private CommandHandler handler;

    private boolean isClientOnly = false;
    private int permission = 2;

    public static final HashMap<ResourceLocation, BaseArgument> types = new HashMap<>();
    public static final CommandReg INSTANCE = new CommandReg("null");

    static  {
        INSTANCE.registerType("int", new BaseArgument(Integer::parseInt));
        INSTANCE.registerType("double", new BaseArgument(Double::parseDouble));
        INSTANCE.registerType("string", new BaseArgument(s->s));
        INSTANCE.registerType("resource_location", new BaseArgument(ResourceLocation::new));

        DeferredRegister<ArgumentTypeInfo<?, ?>> register = DeferredRegister.create(COMMAND_ARGUMENT_TYPE_REGISTRY, MOD_ID);
        register.register("base", ()->ArgumentTypeInfos.registerByClass(BaseArgument.class, new BaseArgumentInfo()));
        register.register(KasugaLib.EVENTS);
    }

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
        this.tree = new CommandTree(new Node(registrationKey));
    }


    /**
     * Call this at least once to register your own type before using them.
     *
     * @return The Reg itself
     */
    public CommandReg registerType(String name, BaseArgument type){
        CommandReg.types.put(new ResourceLocation(KasugaLib.MOD_ID, name), type);
        return this;
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
    public CommandReg appendSingleParameter(String defaultName, boolean optional, BaseArgument type) {
        tree.addNode(optional, defaultName, type);
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
        return this;
    }

    @Override
    public String getIdentifier() {
        return "command";
    }

    /**
     * Subscribe to CommandEvent to get your commands registered.
     *
     * @param dispatcher The dispatcher of CommandEvent
     * @see net.minecraftforge.event.RegisterCommandsEvent
     */
    @Inner
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        ENTRIES.forEach(commandReg -> {
            register(commandReg, dispatcher);
        });
    }

    /**
     * Get your registered type
     *
     * @param name your type's name
     * @return BaseArgument
     */
    public static BaseArgument getType(String name){
        return getType(new ResourceLocation(KasugaLib.MOD_ID, name));
    }

    private static BaseArgument getType(ResourceLocation name){
        if(!types.containsKey(name))
            throw new NullPointerException();
        return types.get(name);
    }

    @Inner
    private static void register(final CommandReg commandReg, final CommandDispatcher<CommandSourceStack> dispatcher) {

        System.out.println(commandReg.tree.leaves.size());
        for (Node leaf : commandReg.tree.leaves) {
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

        if (commandReg.tree.leaves.size() == 1) {
            dispatcher.register(literal(commandReg.commandName));
            return;
        }

        for (Node leaf : commandReg.tree.leaves) {
            Node node = leaf;
            LiteralArgumentBuilder<CommandSourceStack> builder = literal(commandReg.commandName);
            ArgumentBuilder<CommandSourceStack, ?> argument, post;
            argument = null;
            if (node.isRoot()) {
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
                    post = parseArgumentType(node)
                            .requires(p -> p.hasPermission(commandReg.permission))
                            .executes(ctx -> {
                                commandReg.handler.setCtx(ctx);
                                try {
                                    commandReg.handler.run();
                                } catch (Exception e) {
                                    MAIN_LOGGER.error("Error during command: ", e);
                                    return -1;
                                }
                                return 1;
                            });
                    if(node.father.isRoot()){
                        dispatcher.register(builder.then(post));
                        break;
                    }
                } else {
                    post = argument;
                }
                if (node.father.isRoot()) {
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
