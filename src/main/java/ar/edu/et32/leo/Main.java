package ar.edu.et32.leo;

import ar.edu.et32.leo.modelo.Servidor;
import ar.edu.et32.leo.persistencia.BinarioManager;
import ar.edu.et32.leo.persistencia.JsonManager;
import ar.edu.et32.leo.servicio.MonitorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static List<Servidor> servidores = new ArrayList<>();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> cargarBinario();
                case 2 -> alta();
                case 3 -> baja();
                case 4 -> modificar();
                case 5 -> MonitorService.simularChequeo(servidores);
                case 6 -> BinarioManager.guardar(servidores);
                case 7 -> JsonManager.exportar(servidores);
                case 8 -> importarJson();
                case 9 -> MonitorService.mostrarOrdenado(servidores);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("1. Cargar Nodos (Binario)");
        System.out.println("2. Alta de Servidor");
        System.out.println("3. Baja de Servidor");
        System.out.println("4. Modificar Servidor");
        System.out.println("5. Simular Chequeo de Red");
        System.out.println("6. Guardar Estado (Binario)");
        System.out.println("7. Exportar Configuración (JSON)");
        System.out.println("8. Importar Configuración (JSON)");
        System.out.println("9. Mostrar Monitor Ordenado");
        System.out.println("0. Salir");
    }

    private static void cargarBinario() {
        servidores = BinarioManager.cargar();
        System.out.println("Nodos cargados en memoria.");
    }

    private static void importarJson() {
        List<Servidor> importados = JsonManager.importar();
        if (importados.isEmpty()) {
            System.out.println("No se importaron servidores.");
            return;
        }

        agregarOActualizar(importados);
        System.out.println("Configuración JSON importada.");
    }

    private static void agregarOActualizar(List<Servidor> nuevos) {
        for (Servidor nuevo : nuevos) {
            int indice = buscarIndice(nuevo.getNombreServidor(), nuevo.getIpUrl());
            if (indice >= 0) {
                servidores.set(indice, nuevo);
            } else {
                servidores.add(nuevo);
            }
        }
    }

    private static void alta() {
        String ip = leerTexto("IP/URL: ");
        String nombre = leerTexto("Nombre del Servidor: ");
        String endpoint = leerTexto("Endpoint de la API: ");
        int codigo = leerEntero("Código HTTP: ");
        int tiempo = leerEntero("Tiempo de Respuesta (ms): ");

        Servidor s = new Servidor(ip, nombre, endpoint, codigo, tiempo);

        if (!s.esValido()) {
            System.out.println("Datos inválidos.");
            return;
        }

        servidores.add(s);
        System.out.println("Servidor dado de alta.");
    }

    private static void baja() {
        String nombre = leerTexto("Nombre del Servidor a borrar: ");
        String ip = leerTexto("IP/URL a borrar: ");

        boolean eliminado = servidores.removeIf(s ->
                s.getNombreServidor().equalsIgnoreCase(nombre)
                        && s.getIpUrl().equalsIgnoreCase(ip));

        System.out.println(eliminado ? "Servidor eliminado." : "No se encontró el servidor.");
    }

    private static void modificar() {
        String nombre = leerTexto("Nombre del Servidor a modificar: ");
        String ip = leerTexto("IP/URL actual: ");

        int indice = buscarIndice(nombre, ip);
        if (indice < 0) {
            System.out.println("No se encontró el servidor.");
            return;
        }

        Servidor s = servidores.get(indice);

        String nuevaIp = leerTexto("Nueva IP/URL: ");
        String nuevoNombre = leerTexto("Nuevo Nombre: ");
        String nuevoEndpoint = leerTexto("Nuevo Endpoint: ");
        int nuevoCodigo = leerEntero("Nuevo Código HTTP: ");
        int nuevoTiempo = leerEntero("Nuevo Tiempo de Respuesta (ms): ");

        s.setIpUrl(nuevaIp);
        s.setNombreServidor(nuevoNombre);
        s.setEndpointApi(nuevoEndpoint);
        s.setCodigoHttp(nuevoCodigo);
        s.setTiempoRespuesta(nuevoTiempo);

        if (!s.esValido()) {
            System.out.println("Datos inválidos. No se aplicaron cambios.");
            return;
        }

        servidores.set(indice, s);
        System.out.println("Servidor modificado.");
    }

    private static int buscarIndice(String nombre, String ip) {
        for (int i = 0; i < servidores.size(); i++) {
            Servidor s = servidores.get(i);
            if (s.getNombreServidor().equalsIgnoreCase(nombre)
                    && s.getIpUrl().equalsIgnoreCase(ip)) {
                return i;
            }
        }
        return -1;
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido.");
            }
        }
    }
}