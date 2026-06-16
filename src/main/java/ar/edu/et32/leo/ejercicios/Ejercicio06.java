package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Scanner;

public class Ejercicio06 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese una frase: ");
        String frase = sc.nextLine();

        String[] palabras = frase.split("\\s+");

        ArrayList<String> lista = new ArrayList<>();

        for (String palabra : palabras) {

            palabra = palabra.trim()
                    .toLowerCase()
                    .replaceAll("[aeiou]", "*");

            lista.add(palabra);
        }

        System.out.println(lista);
    }
}