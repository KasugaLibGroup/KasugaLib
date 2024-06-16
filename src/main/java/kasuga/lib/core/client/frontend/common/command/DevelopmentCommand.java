package kasuga.lib.core.client.frontend.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.packets.gui.DevOpenScreenPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DevelopmentCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("kasugalib")
                .then(literal("gui")
                        .then(literal("debug").then(
                                        Commands.argument("bundle", BaseArgument.STRING)
                                                .executes((ctx) -> {
                                                    //kasugalib gui debug <Bundle>
                                                    String bundleName = StringArgumentType.getString(ctx, "bundle");
                                                    execute(ctx, bundleName);
                                                    return 1;
                                                })
                                )
                        )
                ).then(literal("js")
                        .then(literal("open").then(
                                        argument("img", BaseArgument.STRING)
                                                .executes((ctx) -> {
                                                    //kasugalib js open <ImageLocation>
                                                    String imageAddress = StringArgumentType.getString(ctx, "img");
                                                    //TODO Handle me
                                                    return 1;
                                                }).then(
                                                        argument("container-id", BaseArgument.STRING)
                                                                .executes((ctx) -> {
                                                                    //kasugalib js open <ImageLocation> [ContainerID]
                                                                    String imageAddress = StringArgumentType.getString(ctx, "img");
                                                                    String container = StringArgumentType.getString(ctx, "container-id");
                                                                    //TODO Handle me
                                                                    return 1;
                                                                })
                                                )
                                )
                        ).then(literal("stop").then(
                                        argument("context-id", BaseArgument.STRING)
                                                .executes((ctx) -> {
                                                    //kasugalib js stop <ContextID>
                                                    String contextID = StringArgumentType.getString(ctx, "context-id");
                                                    //TODO Handle me
                                                    return 1;
                                                })
                                )
                        ).then(literal("require").then(
                                        argument("context-id", BaseArgument.STRING).then(
                                                argument("module-id", StringArgumentType.greedyString())
                                                        .executes((ctx) -> {
                                                            //kasugalib js require <ContextID> <ModuleID>
                                                            String contextID = StringArgumentType.getString(ctx, "context-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        })
                                        )
                                )
                        ).then(literal("client")
                                .then(literal("open").then(
                                                argument("img", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js client open <ImageLocation>
                                                            String imageAddress = StringArgumentType.getString(ctx, "img");
                                                            //TODO Handle me
                                                            return 1;
                                                        }).then(
                                                                argument("container-id", BaseArgument.STRING)
                                                                        .executes((ctx) -> {
                                                                            //kasugalib js client open <ImageLocation> [ContainerID]
                                                                            String imageAddress = StringArgumentType.getString(ctx, "img");
                                                                            String container = StringArgumentType.getString(ctx, "container-id");
                                                                            //TODO Handle me
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                                ).then(literal("stop").then(
                                                argument("context-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js client stop <ContextID>
                                                            String contextID = StringArgumentType.getString(ctx, "context-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        })
                                        )
                                ).then(literal("require").then(
                                                argument("context-id", BaseArgument.STRING).then(
                                                        argument("module-id", StringArgumentType.greedyString())
                                                                .executes((ctx) -> {
                                                                    //kasugalib js client require <ContextID> <ModuleID>
                                                                    String contextID = StringArgumentType.getString(ctx, "context-id");
                                                                    //TODO Handle me
                                                                    return 1;
                                                                })
                                                )
                                        )
                                )
                        ).then(literal("channel")
                                .then(literal("info")
                                        .executes((ctx) -> {
                                            //kasugalib js channel info
                                            //TODO Handle me
                                            return 1;
                                        }).then(argument("channel-id", BaseArgument.STRING)
                                                .executes((ctx) -> {
                                                    //kasugalib js channel info [ChannelID]
                                                    String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                    //TODO Handle me
                                                    return 1;
                                                })
                                        )

                                ).then(literal("monitor")
                                        .then(literal("start")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel monitor start <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        ).then(literal("stop")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel monitor stop <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        })))
                                ).then(literal("mock")
                                        .then(literal("start")
                                                .executes((ctx) -> {
                                                    //kasugalib js channel mock start
                                                    //TODO Handle me
                                                    return 1;
                                                })
                                        ).then(literal("accept")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel mock accept <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        ).then(literal("next")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel mock next <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        ).then(literal("disconnect")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel mock disconnect <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        ).then(literal("active")
                                                .then(argument("channel-id", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel mock active <ChannelID>
                                                            String channelID = StringArgumentType.getString(ctx, "channel-id");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        )
                                        .then(literal("send")
                                                .then(argument("content", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasugalib js channel mock send <Content>
                                                            String content = StringArgumentType.getString(ctx, "content");
                                                            //TODO Handle me
                                                            return 1;
                                                        }))
                                        ).then(literal("stop")
                                                .executes((ctx) -> {
                                                    //kasugalib js channel mock stop
                                                    //TODO Handle me
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );

        dispatcher.register(
                literal("kasuga")
                        .then(
                                literal("gui-debug").then(
                                        literal("load-metro").then(
                                                argument("bundle", BaseArgument.STRING)
                                                        .executes((ctx) -> {
                                                            //kasuga gui-debug load-metro <Bundle>
                                                            String bundleName = StringArgumentType.getString(ctx, "bundle");
                                                            loadMetro(bundleName);
                                                            return 1;
                                                        }).then(
                                                                argument("server-address", BaseArgument.STRING)
                                                                        .executes((ctx) -> {
                                                                            //kasuga gui-debug load-metro <Bundle> [Server-Address]
                                                                            String bundleName = StringArgumentType.getString(ctx, "bundle");
                                                                            String address = StringArgumentType.getString(ctx, "server-address");
                                                                            loadMetro(bundleName, address);
                                                                            return 1;
                                                                        })
                                                        ))
                                )
                        ).then(
                                literal("open").then(
                                        argument("loc", ResourceLocationArgument.id())
                                                .executes((ctx) -> {
                                                    //kasuga open <ResourceLocation:loc>
                                                    open(ResourceLocationArgument.getId(ctx, "loc"));
                                                    return 1;
                                                })
                                )
                        ).then(
                                literal("list").executes((ctx) -> {
                                    //kasuga list
                                    list();
                                    return 1;
                                })
                        )
        );
    }

    public static void loadMetro(String bundle) {
        loadMetro(bundle, "localhost");
    }

    public static void loadMetro(String bundle, String serverAddress) {
        //TODO Handle me
    }

    public static void open(ResourceLocation loc) {
        //TODO Handle me
    }

    public static void list() {
        //TODO Handle me
    }

    public static void execute(CommandContext<CommandSourceStack> ctx, String bundle) {
        execute(ctx, bundle, "localhost:8081", false);
    }

    public static void execute(CommandContext<CommandSourceStack> ctx, String bundle, String serverName, Boolean isSecureConnection) {
        CommandSourceStack command_source = ctx.getSource();
        if (!command_source.isPlayer())
            return;
        AllPackets.channel.getChannel().send(PacketDistributor.PLAYER.with(command_source::getPlayer), new DevOpenScreenPacket(bundle, serverName, isSecureConnection));
    }
}