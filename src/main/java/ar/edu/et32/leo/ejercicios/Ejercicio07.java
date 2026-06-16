package ar.edu.et32.leo.ejercicios;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ejercicio07 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        HashMap<String, String> diccionario = new HashMap<>();

        for (int i = 0; i < 5; i++) {

            System.out.print("Palabra en español: ");
            String esp = sc.nextLine();

            System.out.print("Traducción: ");
            String ing = sc.nextLine();

            diccionario.put(esp, ing);
        }

        System.out.println("\nENTRY SET");

        for (Map.Entry<String, String> entrada : diccionario.entrySet()) {
            System.out.println(entrada.getKey() + " -> " + entrada.getValue());
        }

        System.out.println("\nKEY SET");
        System.out.println(diccionario.keySet());

        System.out.println("\nVALUES");
        System.out.println(diccionario.values());
    }
}