package kasuga.lib.core.javascript.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GlobalMatcher {
    private final List<List<String>> matchers;

    /**
     * Standard constructor
     * @param expressions the matchers
     */
    public GlobalMatcher(List<List<String>> expressions) {
        this.matchers = expressions;
    }

    public Stream<List<String>> match(Stream<List<String>> input) {
        return input.filter(this::validate);
    }

    /**
     * Get all matched files from the root directory
     * @param input Get children of a folder, return null if it's a file
     * @param shouldScanChildren A validator for whether the children folders of the folder should be scanned
     * @return All matched files
     */
    public List<List<String>> match(Function<List<String>, List<String>> input, Predicate<List<String>> shouldScanChildren) {
        List<List<String>> result = new LinkedList<>();
        List<String> dir = input.apply(List.of("/"));
        if (dir != null) {
            for (String file : dir) {
                List<String> path = new LinkedList<>();
                path.add(file);
                result.addAll(dfs(path, input, shouldScanChildren));
            }
        }
        return result;
    }

    private List<List<String>> dfs(List<String> file, Function<List<String>, List<String>> input, Predicate<List<String>> shouldScanChildren) {
        List<List<String>> result = new LinkedList<>();
        List<String> children = input.apply(file);
        if (children != null) {
            for (String name : children) {
                file.add(name);
                if (validate(file)) {
                    result.add(file);
                }
                if (input.apply(file) != null && shouldScanChildren.test(file) && validate(file)) {
                    result.addAll(dfs(file, input, shouldScanChildren));
                }
                file.remove(file.size()-1);
            }
        }
        return result;
    }

    private boolean validate(List<String> path) {
        if (path.size() == 0) {//Means "/"
            return false;
        }
        int unmatchCount = 0;//Count unmatched cases

        for(List<String> matcher : matchers){
            int i = 0, j = 0;
            while (i < path.size() && j < matcher.size()) {
                if (Objects.equals(path.get(i), matcher.get(j))) {
                    //Matched, pass
                    i++;
                    j++;
                } else if (matcher.get(j).equals("*")) {
                    if(j == path.size()-1){
                        //Have reached the end
                        return true;
                    } else if(i == path.size()-1){
                        //Path ends but matcher not
                        unmatchCount++;
                    }else if(j != matcher.size() -1 && path.get(i).equals(matcher.get(j+1))){
                        //Matcher skipped
                        j++;
                    } else {
                        i++;
                        j++;
                    }
                } else if (matcher.get(j).equals("**")) {
                    if (j + 1 < matcher.size() && path.get(i).equals(matcher.get(j+1))) {
                        //"**" matches nothing
                        j++;
                    } else if (i + 1 < path.size() && j + 1 < matcher.size() && path.get(i+1).equals(matcher.get(j+1))) {
                        //The next param matches, "**" ends
                        i++;
                        j++;
                    } else if (i + 1 >= path.size() || j + 1 >= matcher.size()) {
                        //Reached an end
                        return true;
                    } else {
                        //"**" continues
                        i++;
                    }
                } else {
                    //Something unmatched
                    unmatchCount++;
                }
            }
        }
        //If everything were unmatched then return false
        return unmatchCount < matchers.size();
    }

    private static String join(List<String> components){
        if(components.size() != 0){
            StringBuilder builder = new StringBuilder();
            for (String str : components) {
                builder.append(str).append("/");
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }else{
            return "/";
        }
    }
}
