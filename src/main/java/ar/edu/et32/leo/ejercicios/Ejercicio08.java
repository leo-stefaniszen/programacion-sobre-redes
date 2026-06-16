package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio08 {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                "C:/TPArchivos/pagina.html"));

        StringBuilder html =
                new StringBuilder();

        String linea;

        while ((linea = br.readLine()) != null) {

            html.append(linea)
                    .append("\n");
        }

        br.close();

        String resultado =
                html.toString()
                        .replace("Lorem ipsum dolor sit amet", "");

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                "C:/TPArchivos/pagina.html"));

        pw.print(resultado);

        pw.close();

        System.out.println("Lorem eliminado.");
    }
}