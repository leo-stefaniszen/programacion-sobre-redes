package ar.edu.et32.leo.ejercicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Ejercicio12 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<String> alumnos = new ArrayList<>();
        HashMap<String, Integer> notas = new HashMap<>();

        int opcion;

        do {

            System.out.println("\n=== SISTEMA DE ALUMNOS ===");
            System.out.println("1. Agregar alumno");
            System.out.println("2. Mostrar alumnos");
            System.out.println("3. Buscar alumno");
            System.out.println("4. Modificar nota");
            System.out.println("5. Mostrar promedio");
            System.out.println("0. Salir");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {

                case 1:

                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();

                    System.out.print("Nota: ");
                    int nota = sc.nextInt();
                    sc.nextLine();

                    alumnos.add(nombre);
                    notas.put(nombre, nota);

                    break;

                case 2:

                    for (String alumno : alumnos) {

                        int n = notas.get(alumno);

                        String estado =
                                n >= 6
                                        ? "\u001B[32mAprobado\u001B[0m"
                                        : "\u001B[31mDesaprobado\u001B[0m";

                        System.out.println(
                                alumno + " - " +
                                        n + " - " +
                                        estado
                        );
                    }

                    break;

                case 3:

                    System.out.print("Buscar alumno: ");
                    String buscar = sc.nextLine();

                    if (notas.containsKey(buscar)) {

                        System.out.println(
                                buscar + " -> " +
                                        notas.get(buscar)
                        );

                    } else {

                        System.out.println(
                                "\u001B[31mNo encontrado\u001B[0m"
                        );
                    }

                    break;

                case 4:

                    System.out.print("Alumno: ");
                    String alumno = sc.nextLine();

                    if (notas.containsKey(alumno)) {

                        System.out.print("Nueva nota: ");
                        int nuevaNota = sc.nextInt();
                        sc.nextLine();

                        notas.put(alumno, nuevaNota);
                    }

                    break;

                case 5:

                    double suma = 0;

                    for (int n : notas.values()) {
                        suma += n;
                    }

                    if (!notas.isEmpty()) {

                        System.out.println(
                                "Promedio: " +
                                        (suma / notas.size())
                        );
                    }

                    break;
            }

        } while (opcion != 0);
    }
}