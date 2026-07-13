package com.examen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Se encarga de leer el archivo original juegos.dat y reestructurarlo
 * a un formato estandarizado CSV con separador " ; ".
 * Luego elimina el archivo original.
 * <p>
 * El archivo original usa '+' como separador.
 * Ejemplo de linea original: 20/04+1+0+1+4+7+5
 * Ejemplo de linea estandarizada: 20/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5
 */

public class Estandarizador {

    /**
     * Lee el archivo original (juegos.dat), reemplaza el caracter '+'
     * por el separador " ; ", guarda el resultado como juegos.csv
     * y elimina el archivo original juegos.dat.
     * <p>
     * Si ocurre cualquier error, se debe capturar la excepcion y
     * registrarla en crash.log mediante LogManager.
     * El error NO debe mostrarse por consola.
     *
     * @param rutaOriginal ruta completa del archivo juegos.dat a procesar
     */
    public void estandarizar(String rutaOriginal) {
        File original = new File(rutaOriginal);
        if (!original.exists()) {
            return;
        }

        File destino = new File(original.getParent() == null
                ? "juegos.csv"
                : original.getParent() + File.separator + "juegos.csv");

        BufferedReader lector = null;
        BufferedWriter escritor = null;

        try {
            lector = new BufferedReader(new FileReader(original));
            escritor = new BufferedWriter(new FileWriter(destino, false));

            String linea;
            while ((linea = lector.readLine()) != null) {
                escritor.write(linea.replace("+", " ; "));
                escritor.newLine();
            }
            escritor.flush();
            lector.close();
            escritor.close();
            lector = null;
            escritor = null;

            original.delete();
        } catch (Exception e) {
            LogManager.registrarError("Error al estandarizar el archivo", e);
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
                if (escritor != null) {
                    escritor.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar archivos", e);
            }
        }
    }
    
    /**
     * Genera una nueva clave simétrica AES de 256 bits.
     * Este método solo debe llamarse la primera vez que se ejecuta el programa.
     */

    public static SecretKey generarClaveAES() {
        try {
            KeyGenerator generador = KeyGenerator.getInstance("AES");
            generador.init(128);
            return generador.generateKey();
        } catch (Exception e) {
            LogManager.registrarError("Error al generar la clave AES", e);
            return null;
        }
    }
    
    /**
     * Convierte la clave AES a texto (Base64) y la guarda en un archivo.
     * @param clave La clave generada que se desea guardar.
     * @param rutaArchivo Ruta donde se guardará (ej: "clave.key").
     */

    public static void guardarClave(SecretKey clave, String rutaArchivo) {
        BufferedWriter escritor = null;
        try {
            String claveEnTexto = Base64.getEncoder().encodeToString(clave.getEncoded());
            escritor = new BufferedWriter(new FileWriter(new File(rutaArchivo), false));
            escritor.write(claveEnTexto);
            escritor.flush();
        } catch (Exception e) {
            LogManager.registrarError("Error al guardar la clave AES", e);
        } finally {
            try {
                if (escritor != null) {
                    escritor.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar AES.key", e);
            }
        }
    }
    
    /**
     * Lee el archivo de texto y reconstruye la clave AES para poder usarla.
     * @param rutaArchivo Ruta del archivo donde está guardada la clave (ej: "clave.key").
     * @return El objeto SecretKey reconstruido, o null si falla.
     */

    public static SecretKey recuperarClave(String rutaArchivo) {
        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(new File(rutaArchivo)));
            String textoLeido = lector.readLine();
            byte[] bytesClave = Base64.getDecoder().decode(textoLeido);
            return new SecretKeySpec(bytesClave, 0, bytesClave.length, "AES");
        } catch (Exception e) {
            LogManager.registrarError("Error al recuperar la clave AES", e);
            return null;
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar AES.key", e);
            }
        }
    }
    
    /**
     * Encripta un texto (ej: el contenido del CSV) usando la clave proporcionada.
     * @param datos El texto plano a encriptar.
     * @param clave La clave AES.
     * @return El texto encriptado convertido a formato Base64 para guardarlo seguro.
     */

    public static String encriptar(String datos, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            byte[] datosEncriptados = cipher.doFinal(datos.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(datosEncriptados);
        } catch (Exception e) {
            LogManager.registrarError("Error al encriptar los datos", e);
            return null;
        }
    }
    
    /**
     * Desencripta un texto en Base64 para recuperar los datos originales.
     * @param datosEncriptados El texto encriptado (leído del archivo).
     * @param clave La clave AES.
     * @return El texto plano original (contenido del CSV).
     */

    public static String desencriptar(String datosEncriptados, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, clave);
            byte[] datos = Base64.getDecoder().decode(datosEncriptados);
            byte[] datosDesencriptados = cipher.doFinal(datos);
            return new String(datosDesencriptados, "UTF-8");
        } catch (Exception e) {
            LogManager.registrarError("Error al desencriptar los datos", e);
            return null;
        }
    }
}
