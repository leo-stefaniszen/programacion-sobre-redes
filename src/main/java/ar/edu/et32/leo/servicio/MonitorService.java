package ar.edu.et32.leo.servicio;

import ar.edu.et32.leo.modelo.Servidor;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MonitorService {
    private static final Random RANDOM = new Random();
    private static final String RESET = "\u001B[0m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String ROJO_BRILLANTE = "\u001B[91m";

    public static void simularChequeo(List<Servidor> servidores) {
        int[] estados = {200, 200, 200, 404, 500, 503};

        for (Servidor s : servidores) {
            int estado = estados[RANDOM.nextInt(estados.length)];
            int tiempo = 50 + RANDOM.nextInt(2600);
            s.setCodigoHttp(estado);
            s.setTiempoRespuesta(tiempo);
        }
    }

    public static void mostrarOrdenado(List<Servidor> servidores) {
        servidores.sort(Comparator
                .comparing(Servidor::estaCaido).reversed()
                .thenComparing(Servidor::getNombreServidor, String.CASE_INSENSITIVE_ORDER));

        System.out.println();
        System.out.printf("%-22s\t%-20s\t%-18s\t%-8s\t%-8s%n", "IP/URL", "NOMBRE", "ENDPOINT", "HTTP", "MS");

        for (Servidor s : servidores) {
            String color = colorDe(s);
            System.out.printf(
                    color + "%-22s\t%-20s\t%-18s\t%-8d\t%-8d" + RESET + "%n",
                    s.getIpUrl(),
                    s.getNombreServidor(),
                    s.getEndpointApi(),
                    s.getCodigoHttp(),
                    s.getTiempoRespuesta()
            );
        }
    }

    private static String colorDe(Servidor s) {
        if (s.estaOnline()) {
            return VERDE;
        }
        if (s.estaCaido()) {
            return ROJO_BRILLANTE;
        }
        return AMARILLO;
    }
}