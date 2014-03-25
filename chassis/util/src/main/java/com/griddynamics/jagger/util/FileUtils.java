package com.griddynamics.jagger.util;

/**
 * Created by kgribov on 3/24/14.
 */
public class FileUtils {

    private static final String spaceEscapeSymbol = "%20";

    public static String encodeFileName(String fileName){
        String result = fileName.replaceAll(" ", spaceEscapeSymbol);
        return "\'" + result + "\'";
    }

    public static String decodeFileName(String fileName){
        char charToRemove = '\'';
        if (fileName.charAt(0) == charToRemove && fileName.charAt(fileName.length()-1) == charToRemove){
            String result = fileName.substring(1, fileName.length()-1);
            return result.replaceAll(spaceEscapeSymbol, " ");
        }

        return fileName;
    }
}
