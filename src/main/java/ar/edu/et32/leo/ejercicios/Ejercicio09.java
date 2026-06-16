package ar.edu.et32.leo.ejercicios;

import java.util.HashMap;
import java.util.Scanner;

public class Ejercicio09 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        HashMap<String, String> diccionario = new HashMap<>();

        diccionario.put("hola", "hello");
        diccionario.put("chau", "bye");
        diccionario.put("perro", "dog");
        diccionario.put("gato", "cat");
        diccionario.put("casa", "house");

        System.out.print("Ingrese una frase: ");
        String frase = sc.nextLine().toLowerCase();

        String[] palabras = frase.split("\\s+");

        for (String palabra : palabras) {

            if (diccionario.containsKey(palabra)) {
                System.out.print(diccionario.get(palabra) + " ");
            } else {
                System.out.print("[???] ");
            }
        }
    }
}