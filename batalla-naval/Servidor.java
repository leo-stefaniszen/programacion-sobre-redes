import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servidor de Batalla Naval. Empareja a los clientes de dos en dos y atiende
 * cada conexion en su propio hilo, por lo que varias partidas corren a la vez.
 *
 * Uso: java Servidor [puerto]
 */
public class Servidor {

    public static final int PUERTO_POR_DEFECTO = 5000;

    public static void main(String[] args) {
        int puerto = PUERTO_POR_DEFECTO;
        if (args.length > 0) {
            try {
                puerto = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto invalido, uso el " + PUERTO_POR_DEFECTO);
            }
        }

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor de Batalla Naval escuchando en el puerto " + puerto);
            Jugador enEspera = null;
            int numeroSala = 1;

            while (true) {
                Socket socket = servidor.accept();
                Jugador jugador;
                try {
                    jugador = new Jugador(socket);
                    jugador.leerNombre();
                } catch (IOException e) {
                    System.out.println("Conexion fallida: " + e.getMessage());
                    continue;
                } catch (ClassNotFoundException e) {
                    System.out.println("Mensaje inicial ilegible: " + e.getMessage());
                    continue;
                }

                System.out.println("Conectado: " + jugador.getNombre() + " desde "
                        + socket.getInetAddress().getHostAddress());

                if (enEspera == null) {
                    enEspera = jugador;
                    try {
                        jugador.enviar(new Mensaje("INFO", "Conectado. Esperando a un oponente..."));
                    } catch (IOException e) {
                        enEspera = null;
                        jugador.cerrar();
                    }
                } else {
                    Partida partida = new Partida(numeroSala, enEspera, jugador);
                    System.out.println("Sala " + numeroSala + ": " + enEspera.getNombre()
                            + " contra " + jugador.getNombre());
                    numeroSala++;
                    enEspera = null;
                    partida.iniciar();
                }
            }
        } catch (IOException e) {
            System.out.println("El servidor no pudo seguir escuchando: " + e.getMessage());
        }
    }
}

/** Conexion de un jugador: socket mas sus dos flujos de objetos. */
class Jugador {

    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;
    private String nombre = "Jugador";

    Jugador(Socket socket) throws IOException {
        this.socket = socket;
        this.salida = new ObjectOutputStream(socket.getOutputStream());
        this.salida.flush();
        this.entrada = new ObjectInputStream(socket.getInputStream());
    }

    void leerNombre() throws IOException, ClassNotFoundException {
        Mensaje m = recibir();
        if (m != null && "NOMBRE".equals(m.getTipo()) && m.getTexto() != null
                && !m.getTexto().trim().isEmpty()) {
            nombre = m.getTexto().trim();
        }
    }

    synchronized void enviar(Mensaje m) throws IOException {
        salida.writeObject(m);
        salida.flush();
        salida.reset();
    }

    Mensaje recibir() throws IOException, ClassNotFoundException {
        return (Mensaje) entrada.readObject();
    }

    String getNombre() {
        return nombre;
    }

    void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
            // La partida ya termino, no hay nada que recuperar.
        }
    }
}

/**
 * Estado de una partida entre dos jugadores. Los dos hilos de la sala entran
 * aqui, asi que todo lo que toca el estado compartido es synchronized.
 */
class Partida {

    private final int sala;
    private final Jugador[] jugadores;
    private final Tablero[] tableros = { new Tablero(), new Tablero() };
    private final boolean[] listo = { false, false };
    private int turno = 0;
    private boolean terminada = false;

    Partida(int sala, Jugador uno, Jugador dos) {
        this.sala = sala;
        this.jugadores = new Jugador[] { uno, dos };
    }

    void iniciar() {
        for (int i = 0; i < 2; i++) {
            final int indice = i;
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    atender(indice);
                }
            }, "sala-" + sala + "-jugador-" + (i + 1));
            hilo.start();
        }
    }

    private void atender(int idx) {
        try {
            jugadores[idx].enviar(new Mensaje("COLOCAR",
                    "Sala " + sala + ". Tu oponente es " + jugadores[1 - idx].getNombre()));
            while (!estaTerminada()) {
                Mensaje m = jugadores[idx].recibir();
                if (m == null) {
                    break;
                }
                procesar(idx, m);
            }
        } catch (IOException e) {
            avisarDesconexion(idx);
        } catch (ClassNotFoundException e) {
            avisarDesconexion(idx);
        } finally {
            jugadores[idx].cerrar();
        }
    }

    private synchronized boolean estaTerminada() {
        return terminada;
    }

    private synchronized void procesar(int idx, Mensaje m) throws IOException {
        if (terminada) {
            return;
        }
        String tipo = m.getTipo();
        if ("BARCO".equals(tipo)) {
            boolean ok = tableros[idx].colocarBarco(m.getNombre(), m.getFila(), m.getCol(),
                    m.getLongitud(), m.isHorizontal());
            if (ok) {
                jugadores[idx].enviar(new Mensaje("BARCO_OK", m.getNombre() + " colocado"));
            } else {
                jugadores[idx].enviar(new Mensaje("BARCO_ERROR",
                        "El servidor rechazo la posicion de " + m.getNombre()));
            }
        } else if ("LISTO".equals(tipo)) {
            listo[idx] = true;
            if (listo[0] && listo[1]) {
                repartirTurnos();
            } else {
                jugadores[idx].enviar(new Mensaje("INFO",
                        "Flota lista. Esperando a que " + jugadores[1 - idx].getNombre()
                                + " coloque la suya..."));
            }
        } else if ("DISPARO".equals(tipo)) {
            resolverDisparo(idx, m.getFila(), m.getCol());
        } else {
            jugadores[idx].enviar(new Mensaje("INFO", "Mensaje no reconocido: " + tipo));
        }
    }

    private void resolverDisparo(int idx, int fila, int col) throws IOException {
        if (!listo[0] || !listo[1]) {
            jugadores[idx].enviar(new Mensaje("INFO", "La partida todavia no empieza."));
            return;
        }
        if (idx != turno) {
            jugadores[idx].enviar(new Mensaje("INFO", "No es tu turno."));
            return;
        }
        Tablero rival = tableros[1 - idx];
        if (!rival.posicionValida(fila, col) || rival.getCelda(fila, col).isDisparada()) {
            jugadores[idx].enviar(new Mensaje("TURNO",
                    "Casilla fuera del tablero o ya disparada. Elige otra."));
            return;
        }

        String resultado = rival.disparar(fila, col);
        boolean impacto = rival.getCelda(fila, col).getTipo() == TipoCelda.IMPACTO;

        jugadores[idx].enviar(Mensaje.jugada("RESULTADO", fila, col, resultado, impacto));
        jugadores[1 - idx].enviar(Mensaje.jugada("ATAQUE", fila, col, resultado, impacto));

        System.out.println("Sala " + sala + ": " + jugadores[idx].getNombre()
                + " dispara a (" + fila + "," + col + ") -> " + resultado);

        if (rival.todosLosBarcosCaidos()) {
            terminada = true;
            System.out.println("Sala " + sala + ": gano " + jugadores[idx].getNombre());
            jugadores[idx].enviar(new Mensaje("FIN", "GANASTE"));
            jugadores[1 - idx].enviar(new Mensaje("FIN", "PERDISTE"));
            guardarHistorial(jugadores[idx].getNombre(), jugadores[1 - idx].getNombre());
            return;
        }

        turno = 1 - idx;
        repartirTurnos();
    }

    private void repartirTurnos() throws IOException {
        jugadores[turno].enviar(new Mensaje("TURNO", "Es tu turno."));
        jugadores[1 - turno].enviar(new Mensaje("ESPERA",
                "Turno de " + jugadores[turno].getNombre() + "."));
    }

    private synchronized void avisarDesconexion(int idx) {
        if (terminada) {
            return;
        }
        terminada = true;
        System.out.println("Sala " + sala + ": " + jugadores[idx].getNombre() + " se desconecto");
        try {
            jugadores[1 - idx].enviar(new Mensaje("FIN",
                    "DESCONEXION: " + jugadores[idx].getNombre() + " dejo la partida."));
        } catch (IOException e) {
            // El otro jugador tambien se fue.
        }
    }

    /** Historial simple de partidas en un archivo de texto. */
    private void guardarHistorial(String ganador, String perdedor) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (PrintWriter escritor = new PrintWriter(new FileWriter("partidas.txt", true))) {
            escritor.println(fecha + " | sala " + sala + " | gano " + ganador + " | perdio " + perdedor);
        } catch (IOException e) {
            System.out.println("No se pudo escribir partidas.txt: " + e.getMessage());
        }
    }
}
