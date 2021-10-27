package com.fastcode.example.addons.encryption;

import com.fastcode.example.commons.logging.LoggingHelper;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *
 *         This class is used to decrypt the data provided by the encryptMe
 *         annotation with using AES (Advanced Encryption Standard) This class
 *         has a method name decrypt which takes encrypted string and decrypt it
 *         with the aes key and return the data it uses javax crypto package and
 *         uses AES/CBC/PKCS5PADDING for encryption
 *
 *
 */
@Component
public class Decrypter {

    public static String key = "qwertyuiopqwerty";

    @Autowired
    private LoggingHelper logHelper;

    SecureRandom random = new SecureRandom();

    /*
     * it takes encoded string as a parameter
     * a secret key for decoding
     *
     */

    public String decrypt(String encodedString) {
        try {
            byte[] bytesIV = new byte[16];
            random.nextBytes(bytesIV);
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secret, iv);
            byte[] originalString = cipher.doFinal(Base64.decodeBase64(encodedString));
            return new String(originalString);
        } catch (
            UnsupportedEncodingException
            | NoSuchAlgorithmException
            | NoSuchPaddingException
            | IllegalBlockSizeException
            | BadPaddingException
            | InvalidKeyException
            | InvalidAlgorithmParameterException e
        ) {
            return " exception >";
        }
    }
}
