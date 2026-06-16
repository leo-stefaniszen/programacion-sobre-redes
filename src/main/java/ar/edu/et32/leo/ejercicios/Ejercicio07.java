package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio07 {

    public static void main(String[] args) throws Exception {

        BufferedReader consola =
                new BufferedReader(
                        new InputStreamReader(System.in));

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                "C:/TPArchivos/caracteres.dat"));

        for (int i = 0; i < 10; i++) {

            System.out.print("Palabra: ");

            pw.println(consola.readLine());
        }

        pw.close();

        System.out.println("Fichero original:");

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                "C:/TPArchivos/caracteres.dat"));

        String linea;
        StringBuilder sb = new StringBuilder();

        while ((linea = br.readLine()) != null) {

            System.out.println(linea);

            sb.append(
                    linea.replace("ñ", "nie-nio")
            ).append("\n");
        }

        br.close();

        pw = new PrintWriter(
                new FileWriter(
                        "C:/TPArchivos/caracteres.dat"));

        pw.print(sb);

        pw.close();

        System.out.println("\nFichero arreglado:");

        br = new BufferedReader(
                new FileReader(
                        "C:/TPArchivos/caracteres.dat"));

        while ((linea = br.readLine()) != null) {
            System.out.println(linea);
        }

        br.close();
    }
}