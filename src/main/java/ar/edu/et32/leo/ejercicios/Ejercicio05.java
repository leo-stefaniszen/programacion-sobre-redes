package ar.edu.et32.leo.ejercicios;

import java.io.*;
import java.util.ArrayList;

public class Ejercicio05 {

    public static void main(String[] args) throws Exception {

        ArrayList<String> lista = new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                "C:/TPArchivos/numeros.txt"));

        String linea;

        while ((linea = br.readLine()) != null) {

            int numero = Integer.parseInt(linea);

            if (numero % 3 != 0) {
                lista.add(linea);
            }
        }

        br.close();

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                "C:/TPArchivos/numeros.txt"));

        for (String s : lista) {
            pw.println(s);
        }

        pw.close();
    }
}