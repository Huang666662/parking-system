package com.parking.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return DigestUtils.md5Hex(password);
    }
}
