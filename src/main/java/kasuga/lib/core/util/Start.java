package kasuga.lib.core.util;

import org.slf4j.Logger;

public final class Start {
    public static final String[] LOGO = new String[] {
            "  _  __                             _     _ _      ____  ",
            " | |/ /__ _ ___ _   _  __ _  __ _  | |   (_| |__   \\ \\ \\ ",
            " | ' // _` / __| | | |/ _` |/ _` | | |   | | '_ \\   \\ \\ \\",
            " | . | (_| \\__ | |_| | (_| | (_| | | |___| | |_) |  / / /",
            " |_|\\_\\__,_|___/\\__,_|\\__, |\\__,_| |_____|_|_.__/  /_/_/ ",
            "                      |___/                              "
    };

    public static final String[] CREDIT = new String[]{
            "MegumiKasuga"
    };

    public static void printLogo(Logger logger) {
        // logger.info(stringCentered("", "-", 57));
        for(String s : LOGO) {logger.info(s);}
        logger.info(stringCentered(getCredits(), "-", 57));
    }

    public static void printLogo() {
        // System.out.println(stringCentered("", "-", 57));
        for(String s : LOGO) {System.out.println(s);}
        System.out.println(stringCentered(getCredits(), "-", 57));
    }

    public static String getCredits() {
        StringBuilder builder = new StringBuilder("By ");
        int index = 0;
        for(String s : CREDIT) {
            builder.append(s);
            if(index < CREDIT.length - 1)
                builder.append(", ");
            index++;
        }
        return builder.toString();
    }

    public static String stringCentered(String input, String pattern, int size) {
        if(input.length() == 0) {return pattern.repeat(size);}
        if(size < input.length()) return input;
        int len = size - input.length();
        int leftSide = len / 2 / pattern.length(),
                rightSide = len % 2 == 1 ? len / 2 / pattern.length() + pattern.length() : len / 2 / pattern.length();
        StringBuilder builder = new StringBuilder();
        builder.append(pattern.repeat(leftSide));
        builder.append(input);
        builder.append(pattern.repeat(rightSide));
        return builder.toString();
    }
}
