package kasuga.lib.core.base.commands;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.registrations.common.ArgumentTypeReg;

import java.util.HashSet;
import java.util.LinkedList;

@Inner
public class CommandTree {
    public CommandNode root;
    public LinkedList<CommandNode> allCommandNodes = new LinkedList<>();
    public LinkedList<CommandNode> leaves = new LinkedList<>();

    public CommandTree(CommandNode root) {
        this.root = root;
        leaves.add(root);
    }

    public void addLiteral(boolean optional, String string) {
        HashSet<CommandNode> update = new HashSet<>();
        for (CommandNode n : leaves) {
                CommandNode commandNode = new CommandNode(string, ArgumentTypeReg.types.get(String.class.getName()).getSecond(),
                                    true, !optional).setFather(n);
                update.add(commandNode);
                n.children.add(commandNode);
                allCommandNodes.add(commandNode);
        }
        leaves.stream().filter(commandNode -> !update.contains(commandNode)).forEach(commandNode -> commandNode.isLeaf = false);
        leaves = new LinkedList<>(update);
    }

    public void addNode(boolean optional, String name, BaseArgument argType) {
        HashSet<CommandNode> update = new HashSet<>();
        for (CommandNode n : leaves) {
            CommandNode commandNode = new CommandNode(name, argType, false, !optional).setFather(n);
            update.add(commandNode);
            n.children.add(commandNode);
            allCommandNodes.add(commandNode);
        }
        leaves.stream().filter(commandNode -> !update.contains(commandNode)).forEach(commandNode -> commandNode.isLeaf = false);
        leaves = new LinkedList<>(update);
    }
}
