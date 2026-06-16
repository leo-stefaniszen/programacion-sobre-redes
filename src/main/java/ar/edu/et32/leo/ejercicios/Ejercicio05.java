package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Scanner;

public class Ejercicio05 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese una frase: ");
        String frase = sc.nextLine();

        String[] palabras = frase.split("\\s+");

        ArrayList<String> lista = new ArrayList<>();

        for (String palabra : palabras) {
            lista.add(palabra);
        }

        System.out.println("Cantidad de palabras: " + lista.size());

        String masLarga = "";

        for (String palabra : lista) {
            if (palabra.length() > masLarga.length()) {
                masLarga = palabra;
            }
        }

        System.out.println("Palabra más larga: " + masLarga);

        System.out.print("Letra a buscar: ");
        String letra = sc.nextLine();

        int contador = 0;

        for (String palabra : lista) {
            if (palabra.contains(letra)) {
                contador++;
            }
        }

        System.out.println("Contienen '" + letra + "': " + contador);
    }
}