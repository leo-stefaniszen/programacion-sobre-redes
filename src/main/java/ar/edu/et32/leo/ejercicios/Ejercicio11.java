package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Ejercicio11 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> numeros = new ArrayList<>();

        System.out.println("Ingrese 10 números:");

        for (int i = 0; i < 10; i++) {
            numeros.add(sc.nextInt());
        }

        HashSet<Integer> sinDuplicados = new HashSet<>(numeros);

        System.out.println("Lista original:");
        System.out.println(numeros);

        System.out.println("Sin repetidos:");
        System.out.println(sinDuplicados);
    }
}