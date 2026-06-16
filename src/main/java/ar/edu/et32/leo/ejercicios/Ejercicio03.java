package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio03 {

    public static void main(String[] args) throws Exception {

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                "C:/TPArchivos/numeros.txt"));

        for (int i = 0; i <= 1000; i += 2) {
            pw.println(i);
        }

        pw.close();

        System.out.println("Archivo creado.");
    }
}