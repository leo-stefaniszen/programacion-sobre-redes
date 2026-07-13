package com.examen;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la coleccion de partidas cargadas desde el archivo CSV estandarizado.
 * Provee metodos para cargar, mostrar, analizar y guardar los datos.
 */
public class GestorPartidas {

    private ArrayList<Partida> partidas;

    public GestorPartidas() {
        partidas = new ArrayList<Partida>();
    }
    

    /**
     * Carga los datos desde el archivo CSV estandarizado.
     * Lee cada linea, la parsea y crea objetos Partida.
     * La primera linea (encabezados) debe ignorarse.
     * <p>
     * Formato esperado: dd/MM ; 1 ; 0 ; 1 ; 4 ; 7 ; 5
     * <p>
     * Los errores deben registrarse en crash.log sin mostrar en consola.
     *
     * @param rutaCsv ruta del archivo CSV a cargar
     */

    public void cargar(String rutaCsv) {
        BufferedReader lector = null;
        partidas.clear();
        try {
            lector = new BufferedReader(new FileReader(rutaCsv));
            cargarDesdeBufferedReader(lector);
        } catch (Exception e) {
            LogManager.registrarError("Error al cargar las partidas", e);
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar el archivo", e);
            }
        }
    }

    public void cargarDesdeTexto(String texto) {
        partidas.clear();
        try {
            String[] lineas = texto.split("\\r?\\n");
            for (int i = 1; i < lineas.length; i++) {
                cargarLinea(lineas[i]);
            }
        } catch (Exception e) {
            LogManager.registrarError("Error al cargar datos desencriptados", e);
        }
    }

    private void cargarDesdeBufferedReader(BufferedReader lector) throws Exception {
        String linea = lector.readLine();
        linea = lector.readLine();
        while (linea != null) {
            cargarLinea(linea);
            linea = lector.readLine();
        }
    }

    private void cargarLinea(String linea) {
        if (linea == null || linea.trim().equals("")) {
            return;
        }

        try {
            String[] campos = linea.split(";");
            if (campos.length >= 7) {
                Partida partida = new Partida(
                        campos[0].trim(),
                        campos[1].trim().equals("1"),
                        campos[2].trim().equals("1"),
                        campos[3].trim().equals("1"),
                        Integer.parseInt(campos[4].trim()),
                        Integer.parseInt(campos[5].trim()),
                        Integer.parseInt(campos[6].trim()));
                partidas.add(partida);
            }
        } catch (Exception e) {
            LogManager.registrarError("Linea incorrecta: " + linea, e);
        }
    }

    public void mostrarTodos() {
        String reset = "\u001B[0m";
        String cyan = "\u001B[36m";
        String verde = "\u001B[32m";
        String blanco = "\u001B[37m";

        if (partidas.size() == 0) {
            System.out.println("No hay partidas cargadas.");
            return;
        }

        System.out.printf(cyan + "%-5s %-8s %-8s %-15s %-15s %-12s %-9s %-12s%n" + reset,
                "Nro", "Fecha", "Gano", "Primera torreta", "Primera sangre",
                "Asesinatos", "Muertes", "Asistencias");

        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            String color;
            if (i % 2 == 0) {
                color = verde;
            } else {
                color = blanco;
            }

            System.out.printf(color + "%-5d %-8s %-8s %-15s %-15s %-12d %-9d %-12d%n" + reset,
                    i, p.getFecha(), p.isGano() ? "Si" : "No",
                    p.isPrimerTorreta() ? "Si" : "No",
                    p.isPrimeraSangre() ? "Si" : "No",
                    p.getAsesinatos(), p.getMuertes(), p.getAsistencias());
        }
    }
    
    /**
     * Analiza las partidas y determina en que mes hubo mas victorias.
     * La fecha tiene formato dd/MM (el mes esta despues de la barra).
     * <p>
     * En caso de empate, devuelve cualquiera de los meses.
     *
     * @return String con numero de mes (dos digitos, ej: "04").
     *         Si no hay partidas, retorna "SIN DATOS".
     */

    public String mesMasVictorias() {
        if (partidas.size() == 0) {
            return "SIN DATOS";
        }

        int[] victorias = new int[12];
        boolean hayVictorias = false;

        for (int i = 0; i < partidas.size(); i++) {
            Partida partida = partidas.get(i);
            if (partida.isGano()) {
                String[] fecha = partida.getFecha().split("/");
                if (fecha.length >= 2) {
                    try {
                        int mes = Integer.parseInt(fecha[1].trim());
                        if (mes >= 1 && mes <= 12) {
                            victorias[mes - 1]++;
                            hayVictorias = true;
                        }
                    } catch (Exception e) {
                        LogManager.registrarError("Fecha incorrecta", e);
                    }
                }
            }
        }

        if (!hayVictorias) {
            return "SIN DATOS";
        }

        int posicionMayor = 0;
        for (int i = 1; i < victorias.length; i++) {
            if (victorias[i] > victorias[posicionMayor]) {
                posicionMayor = i;
            }
        }

        int mesMayor = posicionMayor + 1;
        if (mesMayor < 10) {
            return "0" + mesMayor;
        }
        return String.valueOf(mesMayor);
    }
    
    /**
     * Guarda todas las partidas en el archivo CSV.
     * Primero escribe la linea de encabezados y luego cada partida.
     * Si el archivo ya existe, se sobrescribe.
     *
     * @param rutaCsv ruta del archivo CSV donde guardar
     */

    public double calcularKdaGeneral() {
        int asesinatos = 0;
        int muertes = 0;
        int asistencias = 0;

        for (int i = 0; i < partidas.size(); i++) {
            asesinatos += partidas.get(i).getAsesinatos();
            muertes += partidas.get(i).getMuertes();
            asistencias += partidas.get(i).getAsistencias();
        }

        if (muertes == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) (asesinatos + asistencias) / muertes;
    }

    public String obtenerTextoCsv() {
        String texto = "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias\n";
        for (int i = 0; i < partidas.size(); i++) {
            texto += partidas.get(i).toString() + "\n";
        }
        return texto;
    }

    public void guardar(String rutaCsv) {
        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(rutaCsv, false));
            escritor.write(obtenerTextoCsv());
            escritor.flush();
        } catch (Exception e) {
            LogManager.registrarError("Error al guardar las partidas", e);
        } finally {
            try {
                if (escritor != null) {
                    escritor.close();
                }
            } catch (Exception e) {
                LogManager.registrarError("Error al cerrar el archivo", e);
            }
        }
    }

    public void eliminar(int indice) {
        if (indice >= 0 && indice < partidas.size()) {
            partidas.remove(indice);
            System.out.println("Partida eliminada correctamente.");
        } else {
            System.out.println("Indice invalido.");
        }
    }

    public void agregar(Partida partida) {
        if (partida != null) {
            partidas.add(partida);
        }
    }

    public int cantidadPartidas() {
        return partidas.size();
    }
    
    /**
     * @return la lista interna de partidas
     */

    public List<Partida> getPartidas() {
        return partidas;
    }
}
