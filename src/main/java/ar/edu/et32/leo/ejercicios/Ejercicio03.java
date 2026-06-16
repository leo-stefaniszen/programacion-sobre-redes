package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.Scanner;

public class Ejercicio03 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<String> lista = new ArrayList<>();

        lista.add("Juan");
        lista.add("Ana");
        lista.add("Pedro");
        lista.add("María");
        lista.add("Luis");

        System.out.println("Lista original:");
        System.out.println(lista);

        System.out.print("Nuevo valor para la tercera posición: ");
        String nuevo = sc.nextLine();

        lista.set(2, nuevo);

        System.out.print("Nombre a eliminar: ");
        String eliminar = sc.nextLine();

        lista.remove(eliminar);

        System.out.println("Lista final:");
        System.out.println(lista);
    }
}