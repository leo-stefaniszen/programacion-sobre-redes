package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Scanner;

public class Ejercicio01 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ArrayList<String> nombres = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            System.out.print("Ingrese nombre " + (i + 1) + ": ");
            nombres.add(sc.nextLine());
        }

        System.out.println("\nLista completa:");
        System.out.println(nombres);

        System.out.println("Cantidad: " + nombres.size());

        System.out.println("Primero: " + nombres.get(0));
        System.out.println("Último: " + nombres.get(nombres.size() - 1));

        System.out.println("\nMayúsculas:");

        for (String nombre : nombres) {
            System.out.println(nombre.toUpperCase());
        }
    }
}