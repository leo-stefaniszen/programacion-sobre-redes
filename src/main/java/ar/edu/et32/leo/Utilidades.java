package ar.edu.et32.leo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utilidades {

    private static final BufferedReader BR =
            new BufferedReader(
                    new InputStreamReader(System.in)
            );

    public static String leerTexto(
            String mensaje
    ) throws Exception {

        System.out.print(mensaje);

        return BR.readLine();
    }

    public static int aInt(
            String texto
    ) {

        return Integer.parseInt(texto);
    }

    public static float aFloat(
            String texto
    ) {

        return Float.parseFloat(texto);
    }
}