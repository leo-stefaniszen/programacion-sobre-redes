package ar.edu.et32.leo;

import java.io.*;
import java.util.ArrayList;

public class Main {

    private static final String ARCHIVO =
            "Inventario.dat";

    public static void main(String[] args) {

        try {

            crearArchivo();

            int opcion;

            do {

                limpiarPantalla();

                mostrarProductos();

                pausa();

                mostrarMenu();

                opcion = Utilidades.aInt(
                        Utilidades.leerTexto(
                                "Opción: "
                        )
                );

                switch (opcion) {

                    case 1 -> agregarProducto();

                    case 2 -> mostrarProductos();

                    case 3 -> editarProducto();

                    case 4 -> eliminarProducto();

                    case 0 ->
                            System.out.println(
                                    Colores.VERDE +
                                            "\nPrograma finalizado." +
                                            Colores.RESET
                            );

                    default ->
                            System.out.println(
                                    Colores.ROJO +
                                            "Opción inválida." +
                                            Colores.RESET
                            );
                }

            } while (opcion != 0);

        } catch (Exception e) {

            System.out.println(
                    Colores.ROJO +
                            e.getMessage() +
                            Colores.RESET
            );
        }
    }

    private static void crearArchivo()
            throws Exception {

        File archivo =
                new File(ARCHIVO);

        if (!archivo.exists()) {

            archivo.createNewFile();
        }
    }

    private static void pausa()
            throws Exception {

        System.out.println(
                "\nPresione ENTER para abrir el menú..."
        );

        System.in.read();

        while (System.in.available() > 0) {
            System.in.read();
        }
    }

    private static void mostrarMenu() {

        System.out.println(
                Colores.CYAN +
                        "\n================================="
        );

        System.out.println(
                "      SISTEMA INVENTARIO"
        );

        System.out.println(
                "================================="
                        + Colores.RESET
        );

        System.out.println(
                "1 - Agregar producto"
        );

        System.out.println(
                "2 - Mostrar productos"
        );

        System.out.println(
                "3 - Editar producto"
        );

        System.out.println(
                "4 - Eliminar producto"
        );

        System.out.println(
                "0 - Salir\n"
        );
    }

    private static void agregarProducto()
            throws Exception {

        String nombre =
                Utilidades.leerTexto(
                        "Nombre: "
                );

        float compra =
                Utilidades.aFloat(
                        Utilidades.leerTexto(
                                "Precio compra: "
                        )
                );

        float venta =
                Utilidades.aFloat(
                        Utilidades.leerTexto(
                                "Precio venta: "
                        )
                );

        int stock =
                Utilidades.aInt(
                        Utilidades.leerTexto(
                                "Stock: "
                        )
                );

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                ARCHIVO,
                                true
                        )
                );

        pw.println(
                nombre + ";" +
                        compra + ";" +
                        venta + ";" +
                        stock
        );

        pw.close();

        System.out.println(
                Colores.VERDE +
                        "Producto agregado." +
                        Colores.RESET
        );
    }

    private static void mostrarProductos()
            throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                ARCHIVO
                        )
                );

        String linea;

        System.out.println(
                "\n----------------------------------------------------------"
        );

        System.out.printf(
                "%-15s %-15s %-15s %-10s%n",
                "NOMBRE",
                "COMPRA",
                "VENTA",
                "STOCK"
        );

        System.out.println(
                "----------------------------------------------------------"
        );

        while ((linea = br.readLine()) != null) {

            String[] datos =
                    linea.split(";");

            System.out.printf(
                    "%-15s %-15s %-15s %-10s%n",
                    datos[0],
                    datos[1],
                    datos[2],
                    datos[3]
            );
        }

        br.close();
    }

    private static void editarProducto()
            throws Exception {

        String buscar =
                Utilidades.leerTexto(
                        "Producto a editar: "
                );

        ArrayList<String> lista =
                new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                ARCHIVO
                        )
                );

        String linea;

        while ((linea = br.readLine()) != null) {

            String[] datos =
                    linea.split(";");

            if (
                    datos[0].equalsIgnoreCase(
                            buscar
                    )
            ) {

                String nombre =
                        Utilidades.leerTexto(
                                "Nuevo nombre: "
                        );

                float compra =
                        Utilidades.aFloat(
                                Utilidades.leerTexto(
                                        "Nuevo precio compra: "
                                )
                        );

                float venta =
                        Utilidades.aFloat(
                                Utilidades.leerTexto(
                                        "Nuevo precio venta: "
                                )
                        );

                int stock =
                        Utilidades.aInt(
                                Utilidades.leerTexto(
                                        "Nuevo stock: "
                                )
                        );

                linea =
                        nombre + ";" +
                                compra + ";" +
                                venta + ";" +
                                stock;
            }

            lista.add(linea);
        }

        br.close();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                ARCHIVO
                        )
                );

        for (String l : lista) {

            pw.println(l);
        }

        pw.close();
    }

    private static void eliminarProducto()
            throws Exception {

        String buscar =
                Utilidades.leerTexto(
                        "Producto a eliminar: "
                );

        ArrayList<String> lista =
                new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                ARCHIVO
                        )
                );

        String linea;

        while ((linea = br.readLine()) != null) {

            String[] datos =
                    linea.split(";");

            if (
                    !datos[0].equalsIgnoreCase(
                            buscar
                    )
            ) {

                lista.add(linea);
            }
        }

        br.close();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                ARCHIVO
                        )
                );

        for (String l : lista) {

            pw.println(l);
        }

        pw.close();
    }

    private static void limpiarPantalla() {

        for (int i = 0; i < 25; i++) {
            System.out.println();
        }
    }
}