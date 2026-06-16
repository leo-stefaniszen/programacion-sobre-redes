package ar.edu.et32.leo.persistencia;

import ar.edu.et32.leo.modelo.Servidor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinarioManager {
    private static final String ARCHIVO = "nodos.dat";

    public static void guardar(List<Servidor> servidores) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            out.writeObject(new ArrayList<>(servidores));
        } catch (IOException e) {
            System.out.println("No se pudo guardar el estado binario.");
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Servidor> cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return (List<Servidor>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No se pudo cargar el estado binario.");
        }

        return new ArrayList<>();
    }
}