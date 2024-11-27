package com.example.cropMonitorSystem.util;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class AppUtil {
    private static final Set<Integer> generatedNumbers = new HashSet<>();
    public static String imageBase64(byte[] image){
        return Base64.getEncoder().encodeToString(image);
    }
}
