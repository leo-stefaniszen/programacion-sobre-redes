package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Scanner;

public class Ejercicio02 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<String> nombres = new ArrayList<>();

        nombres.add("Juan");
        nombres.add("Ana");
        nombres.add("Pedro");
        nombres.add("María");
        nombres.add("Luis");

        System.out.print("Nombre a buscar: ");
        String nombre = sc.nextLine();

        if (nombres.contains(nombre)) {

            System.out.println("Existe.");
            System.out.println("Posición: " + nombres.indexOf(nombre));

        } else {

            System.out.println("\u001B[31mNo existe.\u001B[0m");

        }
    }
}