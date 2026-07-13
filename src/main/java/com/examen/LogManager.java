package com.examen;

/**
 * Sistema de registro de errores que escribe en el archivo crash.log.
 * <p>
 * Ningun error debe mostrarse por consola, todos deben registrarse aqui.
 * Utiliza FileWriter y PrintWriter para escribir en el archivo.
 */

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LogManager {
	
    /**
     * Inicializa el sistema de log.
     * Crea o verifica que el archivo crash.log existe y esta listo para escribir.
     * Este metodo debe llamarse una unica vez al iniciar el programa.
     */
	
    private static Logger LOG = Logger.getLogger(LogManager.class.getName());
    private static boolean iniciado = false;

    public static void inicializar() {
        if (!iniciado) {
            try {
                LOG.setUseParentHandlers(false);
                FileHandler archivoLog = new FileHandler("crash.log", true);
                SimpleFormatter formato = new SimpleFormatter();
                archivoLog.setFormatter(formato);
                LOG.addHandler(archivoLog);
            } catch (IOException e) {
                // No se muestra el error porque el manejo debe ser silencioso.
            }
            iniciado = true;
        }
        /**
         * Registra un error en crash.log.
         * Escribe la fecha/hora, el mensaje de contexto y el stack trace de la excepcion.
         *
         * @param mensaje descripcion del contexto donde ocurrio el error
         * @param e       la excepcion capturada
         */
    }

    public static void registrarError(String mensaje, Exception e) {
        inicializar();
        LOG.log(Level.SEVERE, mensaje, e);
    }
    
    /**
     * Registra un mensaje informativo en crash.log.
     *
     * @param mensaje mensaje informativo a registrar
     */

    public static void registrarInfo(String mensaje) {
        inicializar();
        LOG.log(Level.INFO, mensaje);
    }
}
