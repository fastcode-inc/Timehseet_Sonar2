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
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class ConvertEncryptAndDecrypt implements AttributeConverter<String, String> {

    public static String key = "qwertyuiopqwerty";

    @Autowired
    private LoggingHelper logHelper;

    SecureRandom random = new SecureRandom();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            byte[] bytesIV = new byte[16];
            random.nextBytes(bytesIV);
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secret, iv);
            byte[] encrypted = cipher.doFinal(attribute.getBytes());
            return Base64.encodeBase64String(encrypted);
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

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            byte[] bytesIV = new byte[16];
            random.nextBytes(bytesIV);
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secret, iv);
            byte[] originalString = cipher.doFinal(Base64.decodeBase64(dbData));
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
