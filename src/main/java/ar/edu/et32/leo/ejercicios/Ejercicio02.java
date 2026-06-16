package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio02 {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter("numericos.txt"));

        System.out.println("Ingrese datos (fin para terminar)");

        while (true) {

            String dato = br.readLine();

            if (dato.equalsIgnoreCase("fin"))
                break;

            try {
                Double.parseDouble(dato);
                pw.println(dato);
            }
            catch (Exception ignored) {
            }
        }

        pw.close();
    }
}