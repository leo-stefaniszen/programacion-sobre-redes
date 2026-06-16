package ar.edu.et32.leo;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static final BufferedReader BR =
            new BufferedReader(
                    new InputStreamReader(System.in)
            );

    public static void main(String[] args) {

        try {

            Agenda agenda = new Agenda();

            int opcion;

            do {

                mostrarMenu();

                opcion = Integer.parseInt(
                        BR.readLine()
                );

                switch (opcion) {

                    case 1 -> agregarContacto(
                            agenda
                    );

                    case 2 -> mostrarContactos(
                            agenda
                    );

                    case 3 -> editarContacto(
                            agenda
                    );

                    case 4 -> eliminarContacto(
                            agenda
                    );

                    case 0 -> System.out.println(
                            Colores.VERDE +
                                    "\nPrograma finalizado." +
                                    Colores.RESET
                    );

                    default -> System.out.println(
                            Colores.ROJO +
                                    "Opción inválida." +
                                    Colores.RESET
                    );
                }

            } while (opcion != 0);

        } catch (Exception e) {

            System.out.println(
                    Colores.ROJO +
                            e.getMessage() +
                            Colores.RESET
            );
        }
    }

    private static void mostrarMenu() {

        System.out.println(
                Colores.CYAN +
                        "\n================================="
        );

        System.out.println(
                "            AGENDA"
        );

        System.out.println(
                "================================="
                        + Colores.RESET
        );

        System.out.println("1 - Agregar");
        System.out.println("2 - Mostrar");
        System.out.println("3 - Editar");
        System.out.println("4 - Eliminar");
        System.out.println("0 - Salir");

        System.out.print("\nOpción: ");
    }

    private static void agregarContacto(
            Agenda agenda
    ) throws Exception {

        System.out.print("Nombre: ");
        String nombre = BR.readLine();

        System.out.print("Teléfono: ");
        String telefono = BR.readLine();

        System.out.print("Email: ");
        String email = BR.readLine();

        System.out.print("Nota privada: ");
        String nota = BR.readLine();

        agenda.agregar(
                new Contacto(
                        nombre,
                        telefono,
                        email,
                        nota
                )
        );

        System.out.println(
                Colores.VERDE +
                        "\nContacto agregado." +
                        Colores.RESET
        );
    }

    private static void mostrarContactos(
            Agenda agenda
    ) throws Exception {

        List<Contacto> lista =
                agenda.getContactos();

        System.out.println(
                "\n--------------------------------------------------------------------------------"
        );

        System.out.printf(
                "%-15s %-15s %-25s %-20s%n",
                "NOMBRE",
                "TELÉFONO",
                "EMAIL",
                "NOTA"
        );

        System.out.println(
                "--------------------------------------------------------------------------------"
        );

        for (Contacto c : lista) {

            System.out.printf(
                    "%-15s %-15s %-25s %-20s%n",
                    c.getNombre(),
                    c.getTelefono(),
                    c.getEmail(),
                    CryptoAES.desencriptar(
                            c.getNotaPrivada()
                    )
            );
        }
    }

    private static void editarContacto(
            Agenda agenda
    ) throws Exception {

        System.out.print(
                "Nombre del contacto: "
        );

        String nombre =
                BR.readLine();

        for (
                Contacto c :
                agenda.getContactos()
        ) {

            if (
                    c.getNombre()
                            .equalsIgnoreCase(
                                    nombre
                            )
            ) {

                System.out.print(
                        "Nuevo teléfono: "
                );

                c.setTelefono(
                        BR.readLine()
                );

                System.out.print(
                        "Nuevo email: "
                );

                c.setEmail(
                        BR.readLine()
                );

                System.out.print(
                        "Nueva nota: "
                );

                c.setNotaPrivada(
                        BR.readLine()
                );

                agenda.guardar();

                System.out.println(
                        Colores.AMARILLO +
                                "Contacto actualizado." +
                                Colores.RESET
                );

                return;
            }
        }

        System.out.println(
                Colores.ROJO +
                        "Contacto no encontrado." +
                        Colores.RESET
        );
    }

    private static void eliminarContacto(
            Agenda agenda
    ) throws Exception {

        System.out.print(
                "Nombre a eliminar: "
        );

        String nombre =
                BR.readLine();

        agenda.eliminar(
                nombre
        );

        System.out.println(
                Colores.ROJO +
                        "Contacto eliminado." +
                        Colores.RESET
        );
    }
}