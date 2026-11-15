import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Cliente de Batalla Naval por consola.
 *
 * Uso: java Cliente [ip] [puerto] [--sincolor]
 * Sin parametros pregunta la IP y el puerto al arrancar.
 */
public class Cliente {

    private static final String[] NOMBRES_FLOTA =
            { "Portaaviones", "Acorazado", "Crucero", "Submarino", "Destructor" };
    private static final int[] LONGITUDES_FLOTA = { 5, 4, 3, 3, 2 };

    private static final Scanner TECLADO = new Scanner(System.in);
    private static final Random AZAR = new Random();

    private static final Tablero tableroPropio = new Tablero();
    private static final Tablero tableroEnemigo = new Tablero();

    public static void main(String[] args) {
        ArrayList<String> parametros = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--sincolor")) {
                Consola.color = false;
            } else {
                parametros.add(args[i]);
            }
        }

        Consola.titulo("BATALLA NAVAL");
        String ip = parametros.size() > 0 ? parametros.get(0) : preguntar("IP del servidor", "127.0.0.1");
        int puerto = parametros.size() > 1 ? aEntero(parametros.get(1), Servidor.PUERTO_POR_DEFECTO)
                : aEntero(preguntar("Puerto", String.valueOf(Servidor.PUERTO_POR_DEFECTO)),
                        Servidor.PUERTO_POR_DEFECTO);
        String nombre = preguntar("Tu nombre", "Jugador");

        try (Socket socket = new Socket(ip, puerto)) {
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            salida.writeObject(new Mensaje("NOMBRE", nombre));
            salida.flush();
            Consola.aviso("Conectado a " + ip + ":" + puerto + " como " + nombre);

            jugar(salida, entrada);
        } catch (ConnectException e) {
            Consola.error("No hay servidor en " + ip + ":" + puerto + ". Revisa que este levantado.");
        } catch (UnknownHostException e) {
            Consola.error("No pude resolver la direccion " + ip);
        } catch (SocketException e) {
            Consola.error("Se corto la conexion: " + e.getMessage());
        } catch (IOException e) {
            Consola.error("Error de entrada/salida: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Consola.error("El servidor envio un mensaje que no entiendo: " + e.getMessage());
        }
        System.out.println("Fin del cliente.");
    }

    /** Bucle principal: el cliente reacciona a lo que manda el servidor. */
    private static void jugar(ObjectOutputStream salida, ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        while (true) {
            Mensaje m = (Mensaje) entrada.readObject();
            String tipo = m.getTipo();

            if ("INFO".equals(tipo) || "ESPERA".equals(tipo)) {
                Consola.aviso(m.getTexto());

            } else if ("COLOCAR".equals(tipo)) {
                Consola.aviso(m.getTexto());
                colocarFlota(salida, entrada);

            } else if ("TURNO".equals(tipo)) {
                Consola.aviso(m.getTexto());
                mostrarTableros();
                int[] casilla = pedirCasilla();
                salida.writeObject(Mensaje.disparo(casilla[0], casilla[1]));
                salida.flush();

            } else if ("RESULTADO".equals(tipo)) {
                tableroEnemigo.marcarDisparoEnemigo(m.getFila(), m.getCol(), m.isImpacto());
                if (m.getTexto() != null && m.getTexto().startsWith("¡HUNDIDO!")) {
                    String hundido = m.getTexto().substring("¡HUNDIDO! ".length());
                    tableroEnemigo.anotarBarcoHundido(hundido, longitudDe(hundido));
                }
                System.out.println("Tu disparo a (" + m.getFila() + "," + m.getCol() + "): "
                        + pintarResultado(m.getTexto(), m.isImpacto()));

            } else if ("ATAQUE".equals(tipo)) {
                tableroPropio.disparar(m.getFila(), m.getCol());
                System.out.println("El rival disparo a (" + m.getFila() + "," + m.getCol() + "): "
                        + pintarResultado(m.getTexto(), m.isImpacto()));
                mostrarTableros();

            } else if ("BARCO_ERROR".equals(tipo)) {
                Consola.error(m.getTexto());

            } else if ("FIN".equals(tipo)) {
                mostrarTableros();
                if ("GANASTE".equals(m.getTexto())) {
                    tableroEnemigo.verificarVictoria();
                } else if ("PERDISTE".equals(m.getTexto())) {
                    tableroPropio.verificarDerrota();
                } else {
                    Consola.titulo(m.getTexto());
                }
                return;
            }
        }
    }

    /** Pide las cinco posiciones al jugador y las manda al servidor. */
    private static void colocarFlota(ObjectOutputStream salida, ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        Consola.titulo("COLOCA TU FLOTA");
        System.out.println("Escribe: fila columna orientacion   (ejemplo: 3 4 H)");
        System.out.println("Filas y columnas van de 0 a 9. H = horizontal, V = vertical.");
        System.out.println("Escribe " + Consola.pinta("R", Consola.AMARILLO)
                + " para que ese barco se coloque solo.");

        for (int i = 0; i < NOMBRES_FLOTA.length; i++) {
            String nombre = NOMBRES_FLOTA[i];
            int longitud = LONGITUDES_FLOTA[i];
            boolean colocado = false;

            while (!colocado) {
                System.out.println();
                tableroPropio.mostrarTablero(false);
                System.out.print(Consola.pinta(nombre + " (" + longitud + " casillas) > ",
                        Consola.VERDE));
                String linea = leerLinea().trim();

                if (linea.equalsIgnoreCase("R")) {
                    int fila;
                    int col;
                    boolean horizontal;
                    do {
                        fila = AZAR.nextInt(Tablero.TAMANIO);
                        col = AZAR.nextInt(Tablero.TAMANIO);
                        horizontal = AZAR.nextBoolean();
                    } while (!tableroPropio.colocarBarco(nombre, fila, col, longitud, horizontal));
                    enviarBarco(salida, entrada, nombre, fila, col, longitud, horizontal);
                    colocado = true;
                    continue;
                }

                String[] partes = linea.split("\\s+");
                if (partes.length != 3) {
                    Consola.error("Necesito tres datos: fila, columna y orientacion.");
                    continue;
                }
                int fila = aEntero(partes[0], -1);
                int col = aEntero(partes[1], -1);
                String orientacion = partes[2].toUpperCase();
                if (!orientacion.equals("H") && !orientacion.equals("V")) {
                    Consola.error("La orientacion es H o V.");
                    continue;
                }
                boolean horizontal = orientacion.equals("H");

                if (!tableroPropio.colocarBarco(nombre, fila, col, longitud, horizontal)) {
                    Consola.error("Ahi no cabe o choca con otro barco. Prueba otra posicion.");
                    continue;
                }
                enviarBarco(salida, entrada, nombre, fila, col, longitud, horizontal);
                colocado = true;
            }
        }

        System.out.println();
        Consola.titulo("TU FLOTA");
        tableroPropio.mostrarTablero(false);
        tableroPropio.mostrarBarcos();

        salida.writeObject(new Mensaje("LISTO", "Flota colocada"));
        salida.flush();
    }

    private static void enviarBarco(ObjectOutputStream salida, ObjectInputStream entrada,
            String nombre, int fila, int col, int longitud, boolean horizontal)
            throws IOException, ClassNotFoundException {
        salida.writeObject(Mensaje.barco(nombre, fila, col, longitud, horizontal));
        salida.flush();
        Mensaje respuesta = (Mensaje) entrada.readObject();
        if ("BARCO_ERROR".equals(respuesta.getTipo())) {
            Consola.error(respuesta.getTexto());
        } else {
            System.out.println(Consola.pinta(nombre + " listo en (" + fila + "," + col + ") "
                    + (horizontal ? "horizontal" : "vertical"), Consola.GRIS));
        }
    }

    /** Los dos tableros lado a lado: el propio con barcos, el del rival oculto. */
    private static void mostrarTableros() {
        ArrayList<String> propio = tableroPropio.dibujar(false);
        ArrayList<String> enemigo = tableroEnemigo.dibujar(true);
        System.out.println();
        System.out.println(encabezadoDoble("TU FLOTA", "FLOTA ENEMIGA"));
        for (int i = 0; i < propio.size(); i++) {
            System.out.println(propio.get(i) + "   " + enemigo.get(i));
        }
        System.out.println(Consola.pinta("~ agua   B barco   X impacto   O fallo", Consola.GRIS));
        System.out.println();
        System.out.println("Tus barcos:");
        tableroPropio.mostrarBarcos();
        if (!tableroEnemigo.getBarcos().isEmpty()) {
            System.out.println("Barcos enemigos hundidos:");
            tableroEnemigo.mostrarBarcos();
        }
        System.out.println();
    }

    private static String encabezadoDoble(String izquierda, String derecha) {
        int ancho = 23;
        String uno = izquierda + Consola.repetir(" ", Math.max(0, ancho - izquierda.length()));
        return Consola.pinta(uno, Consola.NEGRITA) + "   "
                + Consola.pinta(derecha, Consola.NEGRITA);
    }

    private static int[] pedirCasilla() {
        while (true) {
            System.out.print(Consola.pinta("Dispara (fila columna, ejemplo 5 7) > ", Consola.AMARILLO));
            String linea = leerLinea().trim();
            String[] partes = linea.split("\\s+");
            if (partes.length != 2) {
                Consola.error("Necesito dos numeros: fila y columna.");
                continue;
            }
            int fila = aEntero(partes[0], -1);
            int col = aEntero(partes[1], -1);
            if (!tableroEnemigo.posicionValida(fila, col)) {
                Consola.error("Fila y columna van de 0 a 9.");
                continue;
            }
            if (tableroEnemigo.getCelda(fila, col).isDisparada()) {
                Consola.error("Ya disparaste en esa casilla.");
                continue;
            }
            return new int[] { fila, col };
        }
    }

    private static String pintarResultado(String texto, boolean impacto) {
        if (texto == null) {
            return "";
        }
        if (texto.startsWith("¡HUNDIDO!")) {
            return Consola.pinta(texto, Consola.ROJO + Consola.NEGRITA);
        }
        if (impacto) {
            return Consola.pinta(texto, Consola.ROJO);
        }
        return Consola.pinta(texto, Consola.AZUL);
    }

    private static int longitudDe(String nombreBarco) {
        for (int i = 0; i < NOMBRES_FLOTA.length; i++) {
            if (NOMBRES_FLOTA[i].equals(nombreBarco)) {
                return LONGITUDES_FLOTA[i];
            }
        }
        return 1;
    }

    /** Lee una linea del teclado y corta limpio si la entrada se termina. */
    private static String leerLinea() {
        try {
            return TECLADO.nextLine();
        } catch (NoSuchElementException e) {
            Consola.error("Se termino la entrada por teclado, cierro el cliente.");
            System.exit(0);
            return "";
        }
    }

    private static String preguntar(String etiqueta, String porDefecto) {
        System.out.print(etiqueta + " [" + porDefecto + "]: ");
        String linea = leerLinea().trim();
        return linea.isEmpty() ? porDefecto : linea;
    }

    private static int aEntero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
