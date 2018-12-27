package com.thor.common.util;

/**
 * @author ldang
 */
public class NumberUtil {

    public static boolean isPositiveNumber(String s){
        try{
            return Long.parseLong(s) > 0;
        }catch (NumberFormatException nfe){
            return false;
        }
    }
}
