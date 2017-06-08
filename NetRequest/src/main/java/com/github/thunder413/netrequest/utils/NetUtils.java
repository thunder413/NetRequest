package com.github.thunder413.netrequest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * NetUtils
 * <p>Bunch of utilities </p>
 * @author Thunder413
 * @version 1.2
 */
public class NetUtils {
    /**
     * Generate MD5 hash
     *
     * @param s String message to encrypt
     * @return Hash
     */
    public static String md5(final String s) {
        if (s == null) return "";
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
