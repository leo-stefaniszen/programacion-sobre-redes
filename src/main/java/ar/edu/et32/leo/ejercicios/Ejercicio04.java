package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Iterator;

public class Ejercicio04 {

    public static void main(String[] args) {

        ArrayList<String> nombres = new ArrayList<>();

        nombres.add("Juan");
        nombres.add("Ana");
        nombres.add("Pedro");
        nombres.add("María");
        nombres.add("Luis");

        System.out.println("FOR CLÁSICO");

        for (int i = 0; i < nombres.size(); i++) {
            System.out.println("\u001B[31m" + nombres.get(i) + "\u001B[0m");
        }

        System.out.println("\nFOR-EACH");

        for (String nombre : nombres) {
            System.out.println("\u001B[32m" + nombre + "\u001B[0m");
        }

        System.out.println("\nITERATOR");

        Iterator<String> it = nombres.iterator();

        while (it.hasNext()) {
            System.out.println("\u001B[34m" + it.next() + "\u001B[0m");
        }
    }
}