package com.itheima.qq17.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者： itheima
 * 时间：2016-12-01 15:39
 * 网址：http://www.itheima.com
 */

public class StringUtils {
    private static final String REGEX_USERNAME = "^[a-zA-Z]\\w{2,19}$";
    private static final String REGEX_PWD = "^[0-9]{3,20}$";

    /**
     * 用户名：字母开头，长度3到20位，后面可以跟数字、字母和_
     * @param username
     * @return
     */
    public static boolean checkUsername(String username){

       if (TextUtils.isEmpty(username)){
           return false;
       }
        return  username.matches(REGEX_USERNAME);
    }

    /**
     * 0-9的数字，3到20位
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd){
        if (TextUtils.isEmpty(pwd)){
            return false;
        }
        return pwd.matches(REGEX_PWD);
    }

    public static String getInitial(String contact){
        if (TextUtils.isEmpty(contact)){
            return "";
        }
        return contact.substring(0,1).toUpperCase();
    }

    public static String getDateString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
