/** Colores y formato de texto para la consola (secuencias ANSI). */
public class Consola {

    /** Se apaga con el parametro --sincolor si la terminal no soporta ANSI. */
    public static boolean color = true;

    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String GRIS = "\u001B[90m";

    public static String pinta(String texto, String codigo) {
        if (!color) {
            return texto;
        }
        return codigo + texto + RESET;
    }

    public static void titulo(String texto) {
        banner(texto, AZUL);
    }

    /** Cartel con el color que ya significa algo en el juego (verde flota, rojo danio). */
    public static void banner(String texto, String codigo) {
        String linea = repetir("=", texto.length() + 4);
        System.out.println();
        System.out.println(pinta(linea, codigo));
        System.out.println(pinta("  " + texto, NEGRITA + codigo));
        System.out.println(pinta(linea, codigo));
    }

    public static void aviso(String texto) {
        System.out.println(pinta("> " + texto, AMARILLO));
    }

    public static void error(String texto) {
        System.out.println(pinta("! " + texto, ROJO));
    }

    public static String repetir(String texto, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(texto);
        }
        return sb.toString();
    }
}
