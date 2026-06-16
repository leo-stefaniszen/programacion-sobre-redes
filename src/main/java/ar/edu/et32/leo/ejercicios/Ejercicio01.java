package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio01 {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));

        System.out.print("Ingrese un dato: ");

        String dato = br.readLine();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter("ultimoDato.txt"));

        pw.println(dato);

        pw.close();

        System.out.println("Guardado.");
    }
}