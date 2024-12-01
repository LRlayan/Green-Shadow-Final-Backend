package com.example.cropMonitorSystem.util;

import java.util.Base64;
import java.util.Random;

public class AppUtil {
    public static String imageBase64(byte[] image){
        return Base64.getEncoder().encodeToString(image);
    }
    public static String getOTP(){
        Random random = new Random();
        // Generate a random 4-digit number
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }
    public static String temporaryUserPasswordGenerator(){
        Random random = new Random();
        int otp = 10000 + random.nextInt(90000);
        return String.valueOf(otp);
    }
}
