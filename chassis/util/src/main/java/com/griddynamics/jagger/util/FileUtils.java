package com.griddynamics.jagger.util;

/**
 * Created by kgribov on 3/24/14.
 */
public class FileUtils {

    public static String encodeFileName(String fileName){
        return "\'" + fileName + "\'";
    }

    public static String decodeFileName(String fileName){
        char charToRemove = '\'';
        if (fileName.charAt(0) == charToRemove && fileName.charAt(fileName.length()-1) == charToRemove){
            return fileName.substring(1, fileName.length()-1);
        }

        return fileName;
    }
}
