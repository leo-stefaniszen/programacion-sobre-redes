package ar.edu.et32.leo.persistencia;

import ar.edu.et32.leo.modelo.Servidor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonManager {
    private static final String ARCHIVO = "config_red.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LISTA_TIPO = new TypeToken<List<Servidor>>() {}.getType();

    public static void exportar(List<Servidor> servidores) {
        try (FileWriter writer = new FileWriter(ARCHIVO)) {
            GSON.toJson(servidores, writer);
        } catch (Exception e) {
            System.out.println("No se pudo exportar el JSON.");
        }
    }

    public static List<Servidor> importar() {
        try (FileReader reader = new FileReader(ARCHIVO)) {
            List<Servidor> brutos = GSON.fromJson(reader, LISTA_TIPO);
            if (brutos == null) {
                return new ArrayList<>();
            }

            List<Servidor> validados = new ArrayList<>();
            Map<String, Servidor> unicos = new LinkedHashMap<>();

            for (Servidor s : brutos) {
                if (s != null && s.esValido()) {
                    String clave = s.getNombreServidor().trim().toLowerCase() + "|" + s.getIpUrl().trim().toLowerCase();
                    unicos.putIfAbsent(clave, s);
                }
            }

            validados.addAll(unicos.values());
            return validados;
        } catch (Exception e) {
            System.out.println("No se pudo importar el JSON.");
            return new ArrayList<>();
        }
    }
}