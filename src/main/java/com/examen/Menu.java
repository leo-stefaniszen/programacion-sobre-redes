package com.examen;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Menu principal de la aplicacion.
 * Presenta opciones al usuario y ejecuta las acciones correspondientes.
 * <p>
 * NO se permite usar la clase Scanner. Se utiliza BufferedReader + InputStreamReader.
 * <p>
 * Opciones:
 * 1 - Mostrar todas las partidas
 * 2 - Eliminar 1 partida elegida por el usuario
 * 3 - Ver mes con mayor cantidad de victorias
 * 4 - Salir (guarda datos y cierra)
 */
public class Menu {
    private final GestorPartidas gestor;
    private final BufferedReader reader;
    
    /**
     * Crea el menu asociado a un gestor de partidas.
     * Inicializa el BufferedReader para leer desde la consola.
     *
     * @param gestor el gestor de partidas
     */

    public Menu(GestorPartidas gestor) {
        this.gestor = gestor;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    /**
     * Inicia el bucle principal del menu.
     * Muestra las opciones, solicita la eleccion al usuario y ejecuta la accion.
     * El bucle se repite hasta que el usuario elija la opcion 4 (Salir).
     */

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = Validador.leerNoVacio(reader, "Seleccione una opcion: ");
            try {
                opcion = opcion.toUpperCase();
                if (opcion.equals("A") || opcion.equals("1")) {
                    ingresarPartida();
                } else if (opcion.equals("B")) {
                    gestor.mostrarTodos();
                } else if (opcion.equals("2")) {
                    eliminarPartida();
                } else if (opcion.equals("C") || opcion.equals("3")) {
                    mostrarEstadisticas();
                } else if (opcion.equals("D") || opcion.equals("4")) {
                    gestor.guardar("juegos.csv");
                    Main.guardarEncriptado(gestor);
                    continuar = false;
                } else {
                    System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                LogManager.registrarError("Error procesando una opcion del menu", e);
            }
        }
    }

    private void mostrarMenu() {
        String azul = "\u001B[34m";
        String reset = "\u001B[0m";
        System.out.println(azul + "\n====================================");
        System.out.println("       HISTORIAL LEAGUE OF LEGENDS");
        System.out.println("====================================" + reset);
        System.out.println("A. Ingresar nueva partida");
        System.out.println("B. Mostrar historial");
        System.out.println("C. Estadisticas del jugador");
        System.out.println("D. Guardar y salir");
        System.out.println("2. Eliminar una partida");
    }

    private void ingresarPartida() {
        String fecha = Validador.leerNoVacio(reader, "Fecha (dd/MM): ");
        boolean gano = leerBooleano("Gano (1/0): ");
        boolean torreta = leerBooleano("Primera torreta (1/0): ");
        boolean sangre = leerBooleano("Primera sangre (1/0): ");
        int asesinatos = leerEntero("Asesinatos: ");
        int muertes = leerEntero("Muertes: ");
        int asistencias = leerEntero("Asistencias: ");
        gestor.agregar(new Partida(fecha, gano, torreta, sangre, asesinatos, muertes, asistencias));
        System.out.println("Partida agregada.");
    }

    private void eliminarPartida() {
        gestor.mostrarTodos();
        int indice = leerEntero("Indice de la partida a eliminar: ");
        gestor.eliminar(indice);
    }

    private void mostrarEstadisticas() {
        System.out.println("Mejor mes: " + gestor.mesMasVictorias());
        double kda = gestor.calcularKdaGeneral();
        if (Double.isInfinite(kda)) {
            System.out.println("KDA general: PERFECTO");
        } else {
            System.out.printf("KDA general: %.2f%n", kda);
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = Validador.leerNoVacio(reader, mensaje);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= 0) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                LogManager.registrarError("Entrada numerica invalida", e);
            }
            System.out.println("Ingrese un numero entero no negativo.");
        }
    }

    private boolean leerBooleano(String mensaje) {
        while (true) {
            String texto = Validador.leerNoVacio(reader, mensaje);
            if ("1".equals(texto)) {
                return true;
            }
            if ("0".equals(texto)) {
                return false;
            }
            System.out.println("Ingrese 1 para si o 0 para no.");
        }
    }


}
