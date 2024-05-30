package kasuga.lib.registrations.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import kasuga.lib.core.events.both.CommandEvent;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;
import java.util.function.Function;

import static kasuga.lib.KasugaLib.MAIN_LOGGER;
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
        this.tree = new CommandTree(new CommandNode(registrationKey));
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
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendEnumable(ArrayList<String> list, boolean optional) {
        tree.addEnums(optional, list.toArray(new String[]{}));
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
     * Append a Path parameter to the end of your command.
     *
     * @param defaultName Name of your property
     * @param optional    Is this property optional?
     * @return The Reg itself
     */
    public CommandReg appendPath(String defaultName, boolean optional) {
        tree.addNode(optional, defaultName, ArgType.PATH);
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
     * Should this command be client-only?
     * @param arg Should this command be client-only?
     * @return The Reg itself
     */
    public CommandReg isClientOnly(boolean arg) {
        this.isClientOnly = arg;
        return this;
    }

    /**
     * Marks that your command is over.
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
     * @param dispatcher
     * @see net.minecraftforge.event.RegisterCommandsEvent
     */
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        ENTRIES.forEach(commandReg -> {
            if(commandReg.isClientOnly){
                DistExecutor.safeRunWhenOn(Dist.CLIENT, ()-> ()->register(commandReg, dispatcher));
            } else {
                register(commandReg,dispatcher);
            }
        });
    }

    /**
     * Internal, do not use directly
     */
    private static void register(final CommandReg commandReg, final CommandDispatcher<CommandSourceStack> dispatcher){

            if (commandReg.handler == null) {
                throw new NullPointerException();
            }

            if (commandReg.tree.leaves.size() == 1) {
                dispatcher.register(literal(commandReg.commandName));
                return;
            }

            for (CommandNode leaf : commandReg.tree.leaves) {
                CommandNode node = leaf;
                LiteralArgumentBuilder<CommandSourceStack> builder = literal(commandReg.commandName);
                ArgumentBuilder<CommandSourceStack, ?> argument, post;
                argument = literal("NULL_FLAG");
                boolean nullFlag = true;
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
                while (node.father.type != ArgType.ROOT) {
                    if (node.isLeaf) {
                        CommandNode finalNode = node;
                        post = setArgumentType(node)
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
                    } else {
                        post = argument;
                    }
                    node = node.father;
                    argument = setArgumentType(node).then(post);
                    nullFlag = false;
                }
                if (!nullFlag) {
                    builder.then(argument);
                } else {
                    builder.then(setArgumentType(node));
                }
                dispatcher.register(builder);
            }
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setArgumentType(CommandNode node) {
        if (node.isLiteral) {
            return literal(node.name);
        }
        switch (node.type) {
            case INT -> {
                return argument(node.name, IntegerArgumentType.integer());
            }
            case DOUBLE -> {
                return argument(node.name, DoubleArgumentType.doubleArg());
            }
            case RESOURCE_LOCATION -> {
                return argument(node.name, ResourceLocationArgument.id());
            }
            case STRING -> {
                return argument(node.name, StringArgumentType.string());
            }
            case VECTOR3 -> {
                return argument(node.name, Vec3Argument.vec3());
            }
            default -> {
                return argument("null", StringArgumentType.string());
            }
        }
    }

    /**
     * Internal, do not use directly
     */
    public class CommandTree {
        public CommandNode root;
        public LinkedList<CommandNode> leaves = new LinkedList<>();

        public CommandTree(CommandNode root) {
            this.root = root;
            leaves.add(root);
        }

        public void addEnums(boolean optional, String... strings) {
            HashSet<CommandNode> update = new HashSet<>();
            for (CommandNode n : leaves) {
                for (String str : strings) {
                    update.add(new CommandNode(str, ArgType.STRING, true).setFather(n));
                }
                if (optional) {
                    update.add(n);
                }
            }
            leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
            leaves = new LinkedList<>(update);
            System.out.println(leaves);
            System.out.println(leaves.size());
            System.out.println();
        }

        public void addNode(boolean optional, String name, ArgType argType) {
            HashSet<CommandNode> update = new HashSet<>();
            for (CommandNode n : leaves) {
                update.add(new CommandNode(name, argType).setFather(n));
                if (optional) {
                    update.add(n);
                }
            }
            leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
            leaves = new LinkedList<>(update);
            System.out.println(leaves);
            System.out.println(leaves.size());
            System.out.println(argType);
            System.out.println();
        }
    }

    /**
     * Internal, do not use directly
     */
    public class CommandNode {
        protected String name;
        protected ArgType type;
        protected boolean isLiteral;
        protected boolean isLeaf = true;
        protected HashMap<String, ArgType> parameters;

        protected CommandNode father = null;

        public CommandNode(String name) {
            this(name, ArgType.ROOT, false);
        }

        public CommandNode(String name, ArgType type) {
            this(name, type, false);
        }

        public CommandNode(String name, ArgType type, boolean isLiteral) {
            this.name = name;
            this.type = type;
            this.isLiteral = isLiteral;
            this.parameters = new HashMap<>();
            this.parameters.put(this.name, this.type);
        }

        public CommandNode setFather(CommandNode father) {
            this.father = father;
            this.parameters.putAll(father.parameters);
            return this;
        }
    }

    /**
     * Internal, do not use directly
     */
    public enum ArgType {
        ROOT,
        INT,
        DOUBLE,
        RESOURCE_LOCATION,
        STRING,
        PATH,    //TODO add parser
        VECTOR3,
        CUSTOM   //TODO add parser
    }

    /**
     * Extend this class to create your command handler(Use CommandReg.INSTANCE.new)
     */
    public abstract class CommandHandler {
        protected CommandContext<CommandSourceStack> ctx;
        protected HashMap<String, ArgType> keys;

        public CommandHandler() {
            this.keys = new HashMap<>();
        }

        /**
         * Internal, do not use directly
         */
        public CommandHandler setCtx(CommandContext<CommandSourceStack> ctx) {
            this.ctx = ctx;
            return this;
        }

        /**
         * Internal, do not use directly
         */
        public void setKeys(HashMap<String, ArgType> keys) {
            this.keys = keys;
        }

        public Integer getInteger(String name) {
            if (!keys.containsKey(name) || keys.get(name) != ArgType.INT) {
                MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
                throw new NoSuchElementException();
            }
            return IntegerArgumentType.getInteger(ctx, name);
        }

        public Double getDouble(String name) {
            if (!keys.containsKey(name) || keys.get(name) != ArgType.DOUBLE) {
                MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
                throw new NoSuchElementException();
            }
            return DoubleArgumentType.getDouble(ctx, name);
        }

        public String getString(String name) {
            if (!keys.containsKey(name) || keys.get(name) != ArgType.STRING) {
                MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
                throw new NoSuchElementException();
            }
            return StringArgumentType.getString(ctx, name);
        }

        public ResourceLocation getResourceLocation(String name) {
            if (!keys.containsKey(name) || keys.get(name) != ArgType.RESOURCE_LOCATION) {
                MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
                throw new NoSuchElementException();
            }
            return ResourceLocationArgument.getId(ctx, name);
        }

        public Vec3 getVec3(String name) {
            if (!keys.containsKey(name) || keys.get(name) != ArgType.VECTOR3) {
                MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
                throw new NoSuchElementException();
            }
            return Vec3Argument.getVec3(ctx, name);
        }

        public Integer getOptionalInteger(String name, int fallback) {
            try {
                return IntegerArgumentType.getInteger(ctx, name);
            } catch (Exception e) {
                return fallback;
            }
        }

        public Double getOptionalDouble(String name, double fallback) {
            try {
                return DoubleArgumentType.getDouble(ctx, name);
            } catch (Exception e) {
                return fallback;
            }
        }

        public String getOptionalString(String name, String fallback) {
            try {
                return StringArgumentType.getString(ctx, name);
            } catch (Exception e) {
                return fallback;
            }
        }

        public ResourceLocation getOptionalResourceLocation(String name, ResourceLocation fallback) {
            try {
                return ResourceLocationArgument.getId(ctx, name);
            } catch (Exception e) {
                return fallback;
            }
        }

        public Vec3 getOptionalVec3(String name, Vec3 fallback) {
            try {
                return Vec3Argument.getVec3(ctx, name);
            } catch (Exception e) {
                return fallback;
            }
        }

//TODO:Fix this

//        public Path getPath(String name){
//            try {
//                return IntegerArgumentType.getInteger(ctx, name);
//            }catch(Exception e){
//                KasugaLib.MAIN_LOGGER.error("not a valid argument:", e);
//                return 0;
//            }
//        }

//TODO:Fix this

//        public <T> T getCustom(String name, Class<T> type){
//            return
//        }

        public abstract void run();
    }
}
