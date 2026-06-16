package ar.edu.et32.leo;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {

            String paquete = "ar.edu.et32.leo.ejercicios";

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            String ruta = paquete.replace('.', '/');

            URL resource = classLoader.getResource(ruta);

            if (resource == null) {
                System.out.println("No se encontró la carpeta de ejercicios.");
                return;
            }

            File carpeta = new File(resource.toURI());

            List<String> clases = new ArrayList<>();

            for (File archivo : carpeta.listFiles()) {

                if (archivo.getName().endsWith(".class")) {

                    String nombreClase = archivo.getName()
                            .replace(".class", "");

                    clases.add(nombreClase);
                }
            }

            Collections.sort(clases);

            System.out.println("=== EJERCICIOS DISPONIBLES ===");

            for (int i = 0; i < clases.size(); i++) {
                System.out.println((i + 1) + ". " + clases.get(i));
            }

            Scanner sc = new Scanner(System.in);

            System.out.print("\nSeleccione un ejercicio: ");

            int opcion = sc.nextInt();

            if (opcion < 1 || opcion > clases.size()) {
                System.out.println("Opción inválida.");
                return;
            }

            String nombreClase = paquete + "." + clases.get(opcion - 1);

            Class<?> clase = Class.forName(nombreClase);

            Method metodoMain = clase.getMethod("main", String[].class);

            System.out.println("\n==============================\n");

            metodoMain.invoke(null, (Object) new String[]{});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}