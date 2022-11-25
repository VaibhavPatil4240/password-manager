package com.example.passwordmanager.service.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class KeyGeneratorModule {
    public static final String AES= "AES";
    // Function to create a secret key
    public static SecretKey createAESKey() throws NoSuchAlgorithmException {
        // Creating a new instance of
        // SecureRandom class.
        SecureRandom securerandom = new SecureRandom();
        // Passing the string to
        // KeyGenerator
        KeyGenerator keygenerator = KeyGenerator.getInstance(AES);
        // Initializing the KeyGenerator
        // with 256 bits.
        keygenerator.init(256, securerandom);
        SecretKey key = keygenerator.generateKey();
        return key;
    }
}
