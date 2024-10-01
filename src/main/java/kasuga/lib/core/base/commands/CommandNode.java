package kasuga.lib.core.base.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import net.minecraft.commands.CommandSourceStack;

import java.util.LinkedList;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Inner
public class CommandNode {
    public String name;
    public ArgumentType parser;
    public boolean isLiteral;
    public boolean isLeaf = true;

    public CommandNode father = null;

    public boolean root;
    public boolean required;

    public final LinkedList<CommandNode> children = new LinkedList<>();

    public CommandNode(String name) {
        this(name, BaseArgument.STRING, true, true);
        this.root = true;
    }

    public CommandNode(String name, BaseArgument type, boolean isLiteral, boolean isRequired) {
        this.name = name;
        this.parser = type;
        this.isLiteral = isLiteral;
        this.required = isRequired;
    }

    public CommandNode setFather(CommandNode father) {
        this.father = father;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isRoot() {
        return root;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> parseArgumentType(CommandNode commandNode){
        if(commandNode.isLiteral){
            return literal(commandNode.name);
        }else{
            return argument(commandNode.name, commandNode.parser);
        }
    }
}
