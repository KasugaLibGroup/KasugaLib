package kasuga.lib.core.base.commands;

import java.util.HashMap;

/**
 * Internal, do not use directly
 */
public class Node {
    public String name;
    public ArgType type;
    public boolean isLiteral;
    public boolean isLeaf = true;
    public HashMap<String, ArgType> parameters;

    public Node father = null;

    public Node(String name) {
        this(name, ArgType.ROOT, false);
    }

    public Node(String name, ArgType type) {
        this(name, type, false);
    }

    public Node(String name, ArgType type, boolean isLiteral) {
        this.name = name;
        this.type = type;
        this.isLiteral = isLiteral;
        this.parameters = new HashMap<>();
        this.parameters.put(this.name, this.type);
    }

    public Node setFather(Node father) {
        this.father = father;
        this.parameters.putAll(father.parameters);
        return this;
    }
}
