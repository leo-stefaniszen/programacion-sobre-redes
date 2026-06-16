package ar.edu.et32.leo.ejercicios;

import java.io.*;
import java.util.ArrayList;

public class Ejercicio09 {

    private static final String ARCHIVO =
            "C:/TPArchivos/clima.txt";

    public static void main(String[] args) throws Exception {

        BufferedReader consola =
                new BufferedReader(
                        new InputStreamReader(System.in));

        int opcion;

        do {

            System.out.println("\n=== SISTEMA CLIMA ===");
            System.out.println("1 - Agregar dato");
            System.out.println("2 - Mostrar datos");
            System.out.println("3 - Eliminar registro");
            System.out.println("0 - Salir");

            System.out.print("Opción: ");

            opcion =
                    Integer.parseInt(
                            consola.readLine()
                    );

            switch (opcion) {

                case 1:
                    agregarDato(consola);
                    break;

                case 2:
                    mostrarDatos();
                    break;

                case 3:
                    eliminarDato(consola);
                    break;

                case 0:
                    System.out.println("Fin.");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private static void agregarDato(
            BufferedReader consola
    ) throws Exception {

        System.out.print("Fecha: ");
        String fecha = consola.readLine();

        System.out.print("Temperatura: ");
        String temperatura = consola.readLine();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                ARCHIVO,
                                true
                        )
                );

        pw.println(
                fecha + ";" + temperatura
        );

        pw.close();

        System.out.println(
                "Registro agregado."
        );
    }

    private static void mostrarDatos()
            throws Exception {

        File archivo =
                new File(ARCHIVO);

        if (!archivo.exists()) {

            System.out.println(
                    "No hay registros."
            );

            return;
        }

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                ARCHIVO
                        )
                );

        String linea;

        System.out.println(
                "\n=== DATOS ==="
        );

        while (
                (linea = br.readLine())
                        != null
        ) {

            String[] datos =
                    linea.split(";");

            System.out.println(
                    "Fecha: "
                            + datos[0]
                            + " | Temp: "
                            + datos[1]
                            + "°"
            );
        }

        br.close();
    }

    private static void eliminarDato(
            BufferedReader consola
    ) throws Exception {

        System.out.print(
                "Fecha a eliminar: "
        );

        String fecha =
                consola.readLine();

        ArrayList<String> lineas =
                new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                ARCHIVO
                        )
                );

        String linea;

        while (
                (linea = br.readLine())
                        != null
        ) {

            if (
                    !linea.startsWith(
                            fecha + ";"
                    )
            ) {

                lineas.add(linea);
            }
        }

        br.close();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                ARCHIVO
                        )
                );

        for (
                String l : lineas
        ) {

            pw.println(l);
        }

        pw.close();

        System.out.println(
                "Registro eliminado."
        );
    }
}