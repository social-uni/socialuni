package com.socialuni.social.utils;

import java.util.Date;

/**
 * @author qinkaiyuan
 * @date 2019-09-07 10:53
 */
public class SystemUtil {
    public static Date getCurrentDate() {
        return new Date();
    }

    public static long getCurrentTimeSecond() {
        return System.currentTimeMillis() / 1000;
    }
}
