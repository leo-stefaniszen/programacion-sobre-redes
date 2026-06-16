package ar.edu.et32.leo.ejercicios;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ejercicio10 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese una frase: ");
        String frase = sc.nextLine().toLowerCase();

        String[] palabras = frase.split("\\s+");

        HashMap<String, Integer> contador = new HashMap<>();

        for (String palabra : palabras) {

            if (contador.containsKey(palabra)) {

                contador.put(
                        palabra,
                        contador.get(palabra) + 1
                );

            } else {

                contador.put(palabra, 1);

            }
        }

        for (Map.Entry<String, Integer> entrada : contador.entrySet()) {

            System.out.println(
                    entrada.getKey() + ": " +
                            entrada.getValue()
            );
        }
    }
}