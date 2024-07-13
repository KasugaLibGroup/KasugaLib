package kasuga.lib.core.util.glob;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GlobMatcher {

    protected GlobTreeNode root = new GlobTreeNode();

    public GlobMatcher(List<List<String>> patterns){
        for (List<String> pattern : patterns) {
            GlobTreeNode current = root;
            for (String part : pattern) {
                current = current.getOrCreateChildren(part);
            }
            current.setTerminator();
        }
    }

    public List<List<String>> match(
            Function<List<String>, List<String>> listProvider,
            Predicate<List<String>> willNext,
            boolean next,
            GlobTreeNode currentNode,
            LinkedList<String> currentPath
    ){
        List<String> entries = listProvider.apply(currentPath);
        List<List<String>> result = new ArrayList<>();
        if(currentNode.isTerminator()){
            result.add(new ArrayList<>(currentPath));
        }
        if(entries == null || !next)
            return result;
        LinkedList<String> path = new LinkedList<>(currentPath);
        boolean childrenNext = willNext.test(path);
        for (String entry : entries){
            path.addLast(entry);
            List<GlobTreeNode> children = currentNode.test(entry);
            if(children == null || children.isEmpty()){
                path.removeLast();
                continue;
            }
            for (GlobTreeNode child : children) {
                result.addAll(match(listProvider, willNext, childrenNext, child, path));
            }
            path.removeLast();
        }
        return result;
    }

    public List<List<String>> match(Function<List<String>, List<String>> listProvider, Predicate<List<String>> willNext){
        return match(listProvider, willNext, true, root, new LinkedList<>());
    }

    public List<List<String>> collect(Stream<List<String>> fileEntries) {
        GlobFilterNode filter = new GlobFilterNode();
        return fileEntries.filter((path)->validate(path, root)).toList();
    }

    public boolean validate(List<String> entry, GlobTreeNode node){
        String currentEntry = entry.get(0);
        List<GlobTreeNode> children = node.test(currentEntry);
        if(children == null || children.isEmpty()){
            return false;
        }
        boolean result = node.isTerminator();
        List<String> nextEntry = entry.subList(1, entry.size());

        if(nextEntry.isEmpty())
            return result || children.stream().anyMatch(GlobTreeNode::isTerminator);

        for (GlobTreeNode child : children) {
            result |= validate(nextEntry, child);
            if(result)
                break;
        }

        return result;
    }

    public static void main(String[] args){
        GlobMatcher matcher = new GlobMatcher(List.of(List.of("a","b","d"), List.of("a","**","d")));

        System.out.println(matcher.collect(Stream.of(
                List.of("a","b","c","d"),
                List.of("a","b","e","f"),
                List.of("b","b","c","d"),
                List.of("b","b","e","d"),
                List.of("a","b","e","d"),
                List.of("a","b","c","d")
        )));
    }
}
