package com.examen;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Proporciona metodos para validar datos ingresados por el usuario
 * a traves de la consola.
 * <p>
 * La validacion minima requerida es que el dato ingresado no este vacio.
 * No se permite el uso de la clase Scanner.
 */
public final class Validador {
	
    /**
     * Lee un texto ingresado por el usuario y valida que no sea vacio.
     * Si el usuario ingresa una cadena vacia, se muestra un mensaje de error
     * y se solicita nuevamente el ingreso.
     * <p>
     * Utiliza BufferedReader (NO Scanner).
     *
     * @param reader  BufferedReader conectado a System.in
     * @param mensaje mensaje a mostrar al usuario
     * @return el texto ingresado (garantizado no vacio)
     */
    private Validador() {
    }

    public static String leerNoVacio(BufferedReader reader, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String valor = reader.readLine();
                if (valor != null && !valor.trim().isEmpty()) {
                    return valor.trim();
                }
                System.out.println("El valor no puede estar vacio.");
            } catch (IOException e) {
                LogManager.registrarError("Error al leer desde consola", e);
                return "";
            }
        }
    }
}
