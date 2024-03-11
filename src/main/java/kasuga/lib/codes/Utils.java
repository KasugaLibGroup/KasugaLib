package kasuga.lib.codes;

import kasuga.lib.codes.compute.infrastructure.Formula;

public class Utils {

    public static boolean checkBrackets(String input) {
        char[] chars = input.toCharArray();
        int front = 0, back = 0;
        for(char c : chars) {
            if(c == '(') front++;
            if(c == ')') back++;
        }
        return front == back;
    }

    public static boolean containsBrackets(String string) {
        return string.contains(Formula.FRONT_BRACKET_CODEC);
    }

    public static int[] positionBrackets(String input) {
        int[] result = new int[]{-1, -1};
        result[0] = input.indexOf(Formula.FRONT_BRACKET_CODEC);
        char[] chars = input.substring(result[0]).toCharArray();
        int counter = 0;
        int index = 0;
        for(char c : chars) {
            if(c == '(') counter ++;
            if(c == ')') counter --;
            if(counter == 0) {
                result[1] = result[0] + index;
                break;
            }
            index++;
        }
        return result;
    }
}
