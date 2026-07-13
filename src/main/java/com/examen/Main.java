package com.examen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.crypto.SecretKey;

/**
 * Punto de entrada principal del programa.
 * <p>
 * Orquesta la ejecucion:
 * 1. Inicializa LogManager
 * 2. Crea un Estandarizador y estandariza juegos.dat
 * 3. Crea un GestorPartidas
 * 4. Inicia el menu interactivo
 */
public class Main {

    public static void main(String[] args) {
        LogManager.inicializar();

        Estandarizador estandarizador = new Estandarizador();
        File original = new File("juegos.dat");
        GestorPartidas gestor = new GestorPartidas();

        if (original.exists()) {
            estandarizador.estandarizar("juegos.dat");
            gestor.cargar("juegos.csv");
            guardarEncriptado(gestor);
        } else {
            File csv = new File("juegos.csv");
            if (csv.exists()) {
                gestor.cargar("juegos.csv");
            } else {
                cargarEncriptado(gestor);
            }
        }

        Menu menu = new Menu(gestor);
        menu.iniciar();
    }

    public static void guardarEncriptado(GestorPartidas gestor) {
        try {
            File archivoClave = new File("AES.key");
            SecretKey clave;

            if (archivoClave.exists()) {
                clave = Estandarizador.recuperarClave("AES.key");
            } else {
                clave = Estandarizador.generarClaveAES();
                Estandarizador.guardarClave(clave, "AES.key");
            }

            String textoEncriptado = Estandarizador.encriptar(gestor.obtenerTextoCsv(), clave);
            java.io.BufferedWriter escritor = new java.io.BufferedWriter(
                    new java.io.FileWriter("juegos_encriptado.csv", false));
            escritor.write(textoEncriptado);
            escritor.flush();
            escritor.close();
        } catch (Exception e) {
            LogManager.registrarError("Error al guardar el archivo encriptado", e);
        }
    }

    private static void cargarEncriptado(GestorPartidas gestor) {
        BufferedReader lector = null;
        try {
            File archivoEncriptado = new File("juegos_encriptado.csv");
            File archivoClave = new File("AES.key");

            if (!archivoEncriptado.exists() || !archivoClave.exists()) {
                return;
            }

            lector = new BufferedReader(new FileReader(archivoEncriptado));
            String textoEncriptado = "";
            String linea = lector.readLine();
            while (linea != null) {
                textoEncriptado += linea;
                linea = lector.readLine();
            }

            SecretKey clave = Estandarizador.recuperarClave("AES.key");
            String textoPlano = Estandarizador.desencriptar(textoEncriptado, clave);
            if (textoPlano != null) {
                gestor.cargarDesdeTexto(textoPlano);
            }
        } catch (Exception e) {
            LogManager.registrarError("Error al cargar el archivo encriptado", e);
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar el archivo encriptado", e);
            }
        }
    }
}
