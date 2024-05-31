package kasuga.lib.core.base.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Internal, do not use directly
 */
public enum ArgType {
    ROOT,
    INT,
    DOUBLE,
    RESOURCE_LOCATION,
    STRING,
    VECTOR3,
    CUSTOM;   //TODO add parser


    public static ArgumentBuilder<CommandSourceStack, ?> parseArgumentType(Node node) {
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
}
