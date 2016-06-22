package com.lecturenotes.emiliyan.lecturenotes;

/**
 * Author: Emiliyan Grigorov
 * Based on skeleton created by Emiliyan Grigorov on 3/10/2015 used in the  team17.lloyds_banking_app project
 * Purpose: provide validation methods for login inputs
 *
 */
public class Validator {

    /*allowed characters for username: "any letter, digit, -, _,." 1 up to 10 times */
    private static final String USER_REGEX = "[a-zA-Z0-9-_.]{1,10}";
    /*allowed characters for password: "any character" 1 up to 16 times */
    private static final String PASS_REGEX = ".{1,16}";
    /* allowed characters for pin: exactly 6 digits */
    private static final String PIN_REGEX ="[0-9]{6}";
    /* allowed characters for module name: any character 1 or more times */
    private static final String MODULE_NAME_REGEX ="[a-zA-Z0-9]{1}[a-zA-Z0-9-_. ]*";

    public static boolean validatePin(String pin){
        return pin.matches(PIN_REGEX);
    }
    public static boolean validatePassword(String password){return password.matches(PASS_REGEX);}
    public static boolean validateUsername(String username){return username.matches(USER_REGEX);}
    public static boolean validateModuleName(String moduleName){return moduleName.matches(MODULE_NAME_REGEX);}

}
