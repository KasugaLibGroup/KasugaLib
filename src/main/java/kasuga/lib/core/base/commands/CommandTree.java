package kasuga.lib.core.base.commands;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.registrations.common.CommandReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

@Inner
public class CommandTree {
    public Node root;
    public LinkedList<Node> allNodes = new LinkedList<>();
    public LinkedList<Node> optionalNodes = new LinkedList<>();
    public LinkedList<Node> leaves = new LinkedList<>();

    public CommandTree(Node root) {
        this.root = root;
        leaves.add(root);
    }

    public void addEnums(boolean optional, ArrayList<String> strings) {
        HashSet<Node> update = new HashSet<>();
        for (Node n : leaves) {
            for (String str : strings) {
                Node node = new Node(str, CommandReg.getType("string"), true, !optional).setFather(n);
                update.add(node);
                n.children.add(node);
                allNodes.add(node);
            }
        }
        leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
        leaves = new LinkedList<>(update);
    }

    public void addNode(boolean optional, String name, BaseArgument argType) {
        HashSet<Node> update = new HashSet<>();
        for (Node n : leaves) {
            Node node = new Node(name, argType, false, !optional).setFather(n);
            update.add(node);
            n.children.add(node);
            allNodes.add(node);
        }
        leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
        leaves = new LinkedList<>(update);
    }
}
