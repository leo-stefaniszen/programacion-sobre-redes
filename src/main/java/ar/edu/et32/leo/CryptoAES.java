package ar.edu.et32.leo;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoAES {

    private static final String CLAVE =
            "1234567890123456";

    public static String encriptar(
            String texto
    ) throws Exception {

        SecretKeySpec key =
                new SecretKeySpec(
                        CLAVE.getBytes(),
                        "AES"
                );

        Cipher cipher =
                Cipher.getInstance("AES");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key
        );

        byte[] cifrado =
                cipher.doFinal(
                        texto.getBytes()
                );

        return Base64.getEncoder()
                .encodeToString(cifrado);
    }

    public static String desencriptar(
            String texto
    ) throws Exception {

        SecretKeySpec key =
                new SecretKeySpec(
                        CLAVE.getBytes(),
                        "AES"
                );

        Cipher cipher =
                Cipher.getInstance("AES");

        cipher.init(
                Cipher.DECRYPT_MODE,
                key
        );

        byte[] original =
                cipher.doFinal(
                        Base64.getDecoder()
                                .decode(texto)
                );

        return new String(original);
    }
}