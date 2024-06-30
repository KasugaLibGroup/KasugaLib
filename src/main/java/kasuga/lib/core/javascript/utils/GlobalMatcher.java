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

//    public Stream<List<String>> match(Stream<List<String>> input) {
//
//    }

    /**
     * Get all matched files from the root directory
     * @param input Get children of a folder, return null if it's a file
     * @param shouldScanChildren A validator for whether the children folders of the folder should be scanned
     * @return All matched files
     */
    public List<String> match(Function<String, List<String>> input, Predicate<String> shouldScanChildren) {
        List<String> result = new LinkedList<>();
        List<String> dir = input.apply("/");
        if (dir != null) {
            for (String file : dir) {
                result.addAll(dfs(file, input, shouldScanChildren));
            }
        }
        return result;
    }

    private List<String> dfs(String file, Function<String, List<String>> input, Predicate<String> shouldScanChildren) {
        List<String> result = new LinkedList<>();
        List<String> children = input.apply(file);
        if (children != null) {
            for (String path : children) {
                if (validate(path)) {
                    result.add(path);
                }
                if (input.apply(path) != null && shouldScanChildren.test(file) && validate(path)) {
                    result.addAll(dfs(path, input, shouldScanChildren));
                }
            }
        }
        return result;
    }

    private boolean validate(String path) {
        if(path.charAt(0) == '/'){//May cause bugs if we don't remove it
            path = path.substring(1);
        }
        String[] parts = path.split("/");
        if (parts.length == 0) {//Means "/"
            return false;
        }
        int unmatchCount = 0;//Count unmatched cases

        for(List<String> matcher : matchers){
            int i = 0, j = 0;
            while (i < parts.length && j < matcher.size()) {
                if (Objects.equals(parts[i], matcher.get(j))) {
                    //Matched, pass
                    i++;
                    j++;
                } else if (matcher.get(j).equals("*")) {
                    //Matched, pass
                    i++;
                    j++;
                } else if (matcher.get(j).equals("**")) {
                    if (i + 1 < parts.length && j + 1 < matcher.size() && parts[i + 1].equals(matcher.get(j+1))) {
                        //The next param matches, "**" ends
                        i++;
                        j++;
                    } else if (i + 1 >= parts.length || j + 1 >= matcher.size()) {
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
}
