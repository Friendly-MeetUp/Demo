package com.defect.defectTracker.utils;

import java.sql.Date;
import java.util.regex.Pattern;

public class Utils {

    public static boolean stringValidation(String name) {
        if (name != null && !name.trim().isEmpty()) {
            String word[] = name.split("[;:-]");
            int size = word.length;
            if (size > 0) {
                return false;
            }

        }
        return true;
    }

    public static boolean stringValidationUsingPattern(String name) {
        if (Pattern.matches("^[a-zA-Z]{4,20}$", name.trim())) {
            return true;
        }
        return false;
    }

    public static boolean patternMatches(String emailAddress) {
        if (Pattern.matches("^(.+)@(\\S+)$", emailAddress.trim())) {
            return true;
        }
        return false;

    }

    public static boolean notNullValidation(String name) {
        if (name != null && !name.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean idValidation(Long id) {
        if (id != null) {
            return true;
        }
        return false;
    }

    public static boolean dateValidation(Date startDate, Date endDate) {
        if (endDate.after(startDate)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[a-z].*")
                && password.matches(".*[A-Z].*") && password.matches(".*[@#$%^&+=].*");
    }

}
