package me.xkuyax.hdfilme.rest.api;

public class FileUtils {

    public static String removeInvalidFileNameChars(String title) {
        return title.replaceAll("[\\\\/:*?\"<>|]", "").trim();
    }
}
