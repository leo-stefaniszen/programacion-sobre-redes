package ar.edu.et32.leo.ejercicios;

import java.io.*;

public class Ejercicio06 {

    public static void main(String[] args) throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(
                                "C:/TPArchivos/numeros.txt"));

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(
                                "C:/TPArchivos/primos.dat"));

        String linea;

        while ((linea = br.readLine()) != null) {

            int n = Integer.parseInt(linea);

            if (esPrimo(n)) {
                pw.println(n);
            }
        }

        br.close();
        pw.close();
    }

    public static boolean esPrimo(int n) {

        if (n < 2)
            return false;

        for (int i = 2; i < n; i++) {

            if (n % i == 0)
                return false;
        }

        return true;
    }
}