package com.elpadrino.util;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado de contraseñas con PBKDF2-HMAC-SHA256 (incluido en el JDK, sin librerías externas).
 * Formato almacenado: pbkdf2$iteraciones$salBase64$hashBase64
 */
@Component
public class PasswordHasher {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_BITS = 256;
    private static final int LONGITUD_SAL = 16;

    private final SecureRandom random = new SecureRandom();

    public String cifrar(String password) {
        byte[] sal = new byte[LONGITUD_SAL];
        random.nextBytes(sal);
        byte[] hash = derivar(password, sal, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    public boolean coincide(String password, String almacenado) {
        if (password == null || almacenado == null) {
            return false;
        }
        String[] partes = almacenado.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(password, sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] derivar(String password, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, LONGITUD_BITS);
            try {
                return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
            } finally {
                spec.clearPassword();
            }
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        }
    }
}
