package ar.edu.et32.leo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Agenda {

    private final List<Contacto> contactos =
            new ArrayList<>();

    private final File archivo =
            new File("agenda.dat");

    public Agenda() throws Exception {

        cargar();
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void agregar(
            Contacto contacto
    ) throws Exception {

        contactos.add(contacto);

        guardar();
    }

    public void eliminar(
            String nombre
    ) throws Exception {

        contactos.removeIf(
                c -> c.getNombre()
                        .equalsIgnoreCase(nombre)
        );

        guardar();
    }

    public void cargar()
            throws Exception {

        contactos.clear();

        if (!archivo.exists()) {

            archivo.createNewFile();

            return;
        }

        BufferedReader br =
                new BufferedReader(
                        new FileReader(archivo)
                );

        String linea;

        while (
                (linea = br.readLine())
                        != null
        ) {

            String[] datos =
                    linea.split(";");

            contactos.add(
                    new Contacto(
                            datos[0],
                            datos[1],
                            datos[2],
                            datos[3]
                    )
            );
        }

        br.close();
    }

    public void guardar()
            throws Exception {

        File temporal =
                new File("agenda.tmp");

        PrintWriter pw =
                new PrintWriter(
                        new FileWriter(temporal)
                );

        for (
                Contacto c : contactos
        ) {

            pw.println(
                    c.getNombre()
                            + ";"
                            + c.getTelefono()
                            + ";"
                            + c.getEmail()
                            + ";"
                            + CryptoAES.encriptar(
                            c.getNotaPrivada()
                    )
            );
        }

        pw.close();

        Files.move(
                temporal.toPath(),
                archivo.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }
}