package kasuga.lib.core.util.glob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobTreeNode {
    protected HashMap<String, GlobTreeNode> children = new HashMap<>();

    protected boolean isTerminator = false;

    public GlobTreeNode getOrCreateChildren(String key) {
        if (!children.containsKey(key)) {
            children.put(key, new GlobTreeNode());
        }
        return children.get(key);
    }

    public List<GlobTreeNode> test(String original){
        ArrayList<GlobTreeNode> result = new ArrayList<>();
        if(children.containsKey(original)){
            result.add(children.get(original));
        }
        if(children.containsKey("*")){
            result.add(children.get("*"));
        }
        if(children.containsKey("**")){
            result.add(children.get("**"));
            result.add(this);
        }
        return result;
    }

    public void setTerminator(){
        isTerminator = true;
    }

    public boolean isTerminator(){
        return isTerminator;
    }
}
