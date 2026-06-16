package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio04 {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                "C:/TPArchivos/numeros.txt"));

        String linea;

        while ((linea = br.readLine()) != null) {
            System.out.println(linea);
        }

        br.close();
    }
}