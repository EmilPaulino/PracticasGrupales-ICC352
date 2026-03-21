package edu.pucmm.eict.web.util;
import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptUtil {
    private static final String SECRET_KEY = "mi_clave_super_secreta";

    private static BasicTextEncryptor textEncryptor;

    static {
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(SECRET_KEY);
    }

    public static String encrypt(String text) {
        return textEncryptor.encrypt(text);
    }

    public static String decrypt(String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }
}
