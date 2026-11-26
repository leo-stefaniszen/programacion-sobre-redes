package recuperatorio;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cifrado {

    static final String RESET = "\u001B[0m";
    static final String VERDE = "\u001B[32m";
    static final String CIAN = "\u001B[36m";
    static final String MORADO = "\u001B[35m";
    static final String ROJO = "\u001B[31m";
    static final String AMARILLO = "\u001B[33m";

    static final String CLAVE = "ClaveSecreta2026";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String encriptar(String clave, String texto) {
        try {
            byte[] iv = new byte[16];
            RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec sks = new SecretKeySpec(clave.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(iv));

            byte[] cifrado = cipher.doFinal(texto.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(iv) + ":"
                    + Base64.getEncoder().encodeToString(cifrado);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decriptar(String clave, String paquete) {
        try {
            if (paquete == null || !paquete.contains(":")) {
                return null;
            }
            String[] partes = paquete.trim().split(":");
            if (partes.length != 2) {
                return null;
            }
            byte[] iv = Base64.getDecoder().decode(partes[0]);
            if (iv.length != 16) {
                return null;
            }
            byte[] cifrado = Base64.getDecoder().decode(partes[1]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec sks = new SecretKeySpec(clave.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(iv));

            return new String(cipher.doFinal(cifrado), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }
}
