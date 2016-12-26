package me.xkuyax.hdfilme.rest.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static String extract(String search, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(search);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    public static String extractLast(String search, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(search);
        if (matcher.find()) {
            return matcher.group(matcher.groupCount());
        } else {
            return "";
        }
    }
}

