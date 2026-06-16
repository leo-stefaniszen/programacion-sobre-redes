package ar.edu.et32.leo.ejercicios;

import java.util.HashMap;
import java.util.Scanner;

public class Ejercicio08 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        HashMap<String, String> diccionario = new HashMap<>();

        diccionario.put("hola", "hello");
        diccionario.put("chau", "bye");
        diccionario.put("perro", "dog");
        diccionario.put("gato", "cat");
        diccionario.put("casa", "house");

        System.out.print("Ingrese una palabra: ");
        String palabra = sc.nextLine();

        if (diccionario.containsKey(palabra)) {

            System.out.println("Traducción: " + diccionario.get(palabra));

        } else {

            System.out.println("\u001B[31mPalabra no encontrada\u001B[0m");

        }
    }
}