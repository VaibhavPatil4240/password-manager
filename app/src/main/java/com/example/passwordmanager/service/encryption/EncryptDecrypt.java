package com.example.passwordmanager.service.encryption;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncryptDecrypt {
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private final SecretKey secretKey;

    public EncryptDecrypt(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String encryptMessage(String message)
            throws InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] messageArray=message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessage = cipher.doFinal(messageArray);
        return Base64.encodeToString(encryptedMessage,0);
    }

    public String decryptMessage(String encryptedMessage)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedMessageArray=Base64.decode(encryptedMessage,0);
        byte[] clearMessage = cipher.doFinal(encryptedMessageArray);
        return new String(clearMessage);
    }
}
