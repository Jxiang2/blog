package com.jxiang.blog.utils;

import com.jxiang.blog.pojo.SysUser;

public class UserThreadLocal {

    // a local storage for each thread specifically
    private UserThreadLocal() {
    }

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void put(SysUser sysUser) {
        LOCAL.set(sysUser);
    }

    public static SysUser get() {
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }

}
