package kasuga.lib.core.base.commands;

import kasuga.lib.registrations.common.CommandReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Internal, do not use directly
 */
public class CommandTree {
    public Node root;
    public LinkedList<Node> leaves = new LinkedList<>();

    public CommandTree(Node root) {
        this.root = root;
        leaves.add(root);
    }

    public void addEnums(boolean optional, ArrayList<String> strings) {
        HashSet<Node> update = new HashSet<>();
        for (Node n : leaves) {
            for (String str : strings) {
                update.add(new Node(str, ArgType.STRING, true).setFather(n));
            }
            if (optional) {
                update.add(n);
            }
        }
        leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
        leaves = new LinkedList<>(update);
    }

    public void addNode(boolean optional, String name, ArgType argType) {
        HashSet<Node> update = new HashSet<>();
        for (Node n : leaves) {
            update.add(new Node(name, argType).setFather(n));
            if (optional) {
                update.add(n);
            }
        }
        leaves.stream().filter(node -> !update.contains(node)).forEach(node -> node.isLeaf = false);
        leaves = new LinkedList<>(update);
    }
}
