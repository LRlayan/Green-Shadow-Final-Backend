package com.example.cropMonitorSystem.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static Matcher idValidator(String id){
        return Pattern.compile("\\b(?:MEMBER|FIELD|CROP|EQUIPMENT|VEHICLE|LOG|STEQDET)-\\d+\\b").matcher(id);
    }

    public static Matcher emailValidator(String email){
        return Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matcher(email);
    }

    public static Matcher roleValidator(String role){
        return Pattern.compile("^[A-Z]+$").matcher(role);
    }

    public static Matcher contactNoValidator(String contact){
        return Pattern.compile("^(?:0?(77|76|78|34|75|72|74)[0-9]{7}|(77|76|78|34|75|72|74)[0-9]{8})$").matcher(contact);
    }
}
