package com.jxiang.blog.utils;

import com.jxiang.blog.pojo.SysUser;

public class SysUserThreadLocal {

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    // a local storage for each thread specifically
    private SysUserThreadLocal() {
    }

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
