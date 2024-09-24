package com.md.backend.utility;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
public class RSAKeyUtil {
    private static final String RSA = "RSA";

    /**
     * Generates an RSA key pair with the specified key size.
     *
     * @param size the size of the key (e.g., 1024, 2048)
     * @return the generated KeyPair
     * @throws RuntimeException if the RSA algorithm is not available
     */
    public static KeyPair generateKeyPair(int size) {
        KeyPairGenerator keyPairGenerator = null;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGenerator.initialize(size);
        return keyPairGenerator.generateKeyPair();
    }
}
