package core.utils;

import java.security.MessageDigest;

/**
 * Хеширование паролей MD5.
 */
public class MD5Util {
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при хешировании MD5", e);
        }
    }
}
