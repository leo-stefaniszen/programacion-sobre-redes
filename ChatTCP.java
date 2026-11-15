import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/**
 * Sistema distribuido cliente/servidor con Sockets TCP, sin hilos.
 * Chatbot de soporte de una tienda ficticia, con canal cifrado y log por consola.
 *
 *   java ChatTCP.java servidor [puerto]
 *   java ChatTCP.java cliente  [host] [puerto]
 *
 * Tambien se puede compilar antes:  javac ChatTCP.java
 */
public class ChatTCP {

    static final String ALUMNO = "Pepito González";
    static final int PUERTO_POR_DEFECTO = 5000;

    public static void main(String[] args) {
        String modo = args.length > 0 ? args[0].toLowerCase() : "";

        if (modo.equals("servidor")) {
            int puerto = args.length > 1 ? Integer.parseInt(args[1]) : PUERTO_POR_DEFECTO;
            Servidor.iniciar(puerto);
        } else if (modo.equals("cliente")) {
            String host = args.length > 1 ? args[1] : "127.0.0.1";
            int puerto = args.length > 2 ? Integer.parseInt(args[2]) : PUERTO_POR_DEFECTO;
            Cliente.iniciar(host, puerto);
        } else {
            System.out.println("Uso:");
            System.out.println("  java ChatTCP.java servidor [puerto]");
            System.out.println("  java ChatTCP.java cliente  [host] [puerto]");
        }
    }

    // ===================== Cifrado del canal =====================

    /**
     * Cifrado simetrico propio: XOR con clave rotativa mas un desplazamiento.
     * El resultado se transporta en Base64 porque writeUTF se rompe con bytes de control.
     */
    static final class Cripto {

        private static final byte[] CLAVE =
                "Clave-TP-Sockets-2026".getBytes(StandardCharsets.UTF_8);
        private static final int DESPLAZAMIENTO = 7;

        static String cifrar(String textoPlano) {
            return Base64.getEncoder().encodeToString(
                    cifrarBytes(textoPlano.getBytes(StandardCharsets.UTF_8)));
        }

        static String descifrar(String textoCifrado) {
            return new String(descifrarBytes(Base64.getDecoder().decode(textoCifrado)),
                    StandardCharsets.UTF_8);
        }

        static byte[] cifrarBytes(byte[] datos) {
            byte[] salida = new byte[datos.length];
            for (int i = 0; i < datos.length; i++) {
                salida[i] = (byte) ((datos[i] ^ CLAVE[i % CLAVE.length]) + DESPLAZAMIENTO);
            }
            return salida;
        }

        static byte[] descifrarBytes(byte[] datos) {
            byte[] salida = new byte[datos.length];
            for (int i = 0; i < datos.length; i++) {
                salida[i] = (byte) (((datos[i] - DESPLAZAMIENTO) & 0xFF) ^ CLAVE[i % CLAVE.length]);
            }
            return salida;
        }

        /** Recorta el criptograma para que el log no se llene de Base64. */
        static String muestra(String textoCifrado) {
            if (textoCifrado.length() <= 44) {
                return textoCifrado;
            }
            return textoCifrado.substring(0, 44) + "...(" + textoCifrado.length() + " b)";
        }
    }

    // ===================== Log de consola =====================

    /** Log tabulado con un color por tipo de evento, como pide la consigna. */
    static final class Consola {

        static final String RESET    = "\u001B[0m";
        static final String NEGRITA  = "\u001B[1m";
        static final String TENUE    = "\u001B[2m";
        static final String ROJO     = "\u001B[91m";
        static final String VERDE    = "\u001B[92m";
        static final String AMARILLO = "\u001B[93m";
        static final String AZUL     = "\u001B[94m";
        static final String MAGENTA  = "\u001B[95m";
        static final String CYAN     = "\u001B[96m";
        static final String GRIS     = "\u001B[90m";
        static final String BLANCO   = "\u001B[97m";

        private static final DateTimeFormatter RELOJ = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        private static final String SANGRIA = GRIS + "              |          |             | " + RESET + "\t";

        private static String rol = "SISTEMA";
        private static String colorRol = CYAN;

        static {
            // Sin esto, los acentos salen rotos en la consola de Windows.
            try {
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
                System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
            } catch (Exception ignorado) {
                // Si el charset no esta disponible se sigue con el stream por defecto.
            }
        }

        static void definirRol(String nuevoRol, String color) {
            rol = nuevoRol;
            colorRol = color;
        }

        static void encabezado(String titulo, String[] datos) {
            System.out.println();
            System.out.println(NEGRITA + BLANCO + titulo + RESET);
            for (String dato : datos) {
                System.out.println("  " + dato);
            }
            System.out.println();
            System.out.println(NEGRITA + GRIS + "   HORA       | ORIGEN   | EVENTO      | DETALLE" + RESET);
            separador();
        }

        static void separador() {
            System.out.println(GRIS + repetir("-", 94) + RESET);
        }

        static void log(Evento evento, String detalle) {
            String cabecera = GRIS + " " + LocalDateTime.now().format(RELOJ) + " " + RESET
                    + GRIS + "| " + RESET + colorRol + pad(rol, 9) + RESET
                    + GRIS + "| " + RESET + evento.color + pad(evento.etiqueta, 12) + RESET
                    + GRIS + "| " + RESET;

            String[] partes = detalle.split("\n", -1);
            System.out.println(cabecera + evento.color + partes[0] + RESET);
            for (int i = 1; i < partes.length; i++) {
                System.out.println(SANGRIA + evento.color + partes[i] + RESET);
            }
        }

        static void detalle(String clave, String valor) {
            System.out.println(SANGRIA + TENUE + pad(clave, 14) + RESET + GRIS + " > " + RESET + valor);
        }

        static String pad(String texto, int ancho) {
            StringBuilder sb = new StringBuilder(texto);
            while (sb.length() < ancho) {
                sb.append(' ');
            }
            return sb.toString();
        }

        static String repetir(String s, int n) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append(s);
            }
            return sb.toString();
        }

        enum Evento {
            RED("RED", AZUL),
            CONEXION("CONEXION", VERDE),
            AUTENTICA("AUTENTICA", MAGENTA),
            RECIBIDO("RECIBIDO", CYAN),
            ENVIADO("ENVIADO", BLANCO),
            COMANDO("COMANDO", AMARILLO),
            CHATBOT("CHATBOT", MAGENTA),
            STOCK("STOCK", VERDE),
            ARCHIVO("ARCHIVO", AMARILLO),
            ERROR("ERROR", ROJO),
            CIERRE("CIERRE", ROJO),
            INFO("INFO", BLANCO);

            final String etiqueta;
            final String color;

            Evento(String etiqueta, String color) {
                this.etiqueta = etiqueta;
                this.color = color;
            }
        }
    }

    // ===================== Mensajes entre los dos extremos =====================

    static final String T_SISTEMA   = "SYS";
    static final String T_BOT       = "BOT";
    static final String T_OK        = "OK";
    static final String T_ERROR     = "ERR";
    static final String T_MENU      = "MENU";
    static final String T_STOCK     = "STOCK";
    static final String T_HISTORIAL = "HIST";
    static final String T_LISTO     = "READY";
    static final String T_FIN       = "FIN";

    static void enviar(DataOutputStream salida, String textoPlano) throws IOException {
        String cifrado = Cripto.cifrar(textoPlano);
        Consola.log(Consola.Evento.ENVIADO, resumen(textoPlano));
        Consola.detalle("cifrado", Cripto.muestra(cifrado));
        salida.writeUTF(cifrado);
        salida.flush();
    }

    static String recibir(DataInputStream entrada) throws IOException {
        String cifrado = entrada.readUTF();
        String textoPlano = Cripto.descifrar(cifrado);
        Consola.log(Consola.Evento.RECIBIDO, resumen(textoPlano));
        Consola.detalle("criptograma", Cripto.muestra(cifrado));
        return textoPlano;
    }

    static String etiqueta(String mensaje) {
        int corte = mensaje.indexOf("|");
        return corte < 0 ? T_SISTEMA : mensaje.substring(0, corte);
    }

    static String cuerpo(String mensaje) {
        int corte = mensaje.indexOf("|");
        return corte < 0 ? mensaje : mensaje.substring(corte + 1);
    }

    static String marcar(String etiqueta, String cuerpo) {
        return etiqueta + "|" + cuerpo;
    }

    private static String resumen(String texto) {
        String[] lineas = texto.split("\n", -1);
        String visible = lineas.length > 1 ? lineas[0] + " [+" + (lineas.length - 1) + " lineas]" : texto;
        return visible.length() > 70 ? visible.substring(0, 70) + "..." : visible;
    }

    static String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase().trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\s+", " ");
    }

    static void cerrar(AutoCloseable recurso) {
        if (recurso == null) {
            return;
        }
        try {
            recurso.close();
        } catch (Exception e) {
            Consola.log(Consola.Evento.ERROR, "No se pudo cerrar un recurso: " + e.getMessage());
        }
    }

    // ===================== Servidor =====================

    /** Atiende un cliente por vez: accept, conversacion completa, cierre, y vuelve a accept. */
    static final class Servidor {

        // Datos de prueba de una tienda ficticia, cargados en memoria como pide la consigna.
        private static final String[] PRODUCTOS = {
                "teclado mecanico", "mouse gamer", "monitor 24",
                "auriculares bt", "notebook i5", "webcam hd", "silla gamer"
        };
        private static final int[] CANTIDADES = { 14, 37, 8, 52, 3, 0, 21 };
        private static final int[] PRECIOS = { 89900, 42500, 215000, 31750, 980000, 27300, 345000 };

        private static final String[][] FRASES = {
                { "envio",      "Su pedido ya salio del deposito. Entrega estimada: 48 a 72 hs habiles." },
                { "demora",     "Si el pedido pasa las 72 hs, le devolvemos el costo del envio." },
                { "devolucion", "Tiene 10 dias para devolver el producto. El retiro es sin cargo." },
                { "reclamo",    "Su reclamo quedo registrado. Un asesor lo contacta en 24 hs." },
                { "factura",    "La factura se envia al correo de la compra. Si no llego, revise el spam." },
                { "pago",       "Aceptamos tarjeta, transferencia y billeteras virtuales." },
                { "garantia",   "Los productos tienen garantia del fabricante por 12 meses." },
                { "stock",      "Para ver existencias use: ver stock de <producto>" },
                { "precio",     "Los precios se muestran junto al stock: ver stock de <producto>" },
                { "problema",   MENU_SOPORTE },
                { "pedido",     MENU_SOPORTE },
                { "hola",       "Hola " + ALUMNO + ", soy el bot de soporte. Escriba ayuda para ver el menu." },
                { "gracias",    "De nada. Quedo a disposicion." }
        };

        private static final String USUARIO = "pepito";
        private static final String CLAVE = "1234";
        private static final File CARPETA = new File("comprobantes_recibidos");
        private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

        private static boolean autenticado;
        private static String usuarioActual = "anonimo";
        private static final List<String> HISTORIAL = new ArrayList<>();
        private static int sesiones;

        static void iniciar(int puerto) {
            Consola.definirRol("SERVIDOR", Consola.VERDE);
            Consola.encabezado("SERVIDOR TCP - Soporte TiendaTECH (tienda ficticia, datos de prueba)",
                    new String[]{
                            "Alumno: " + ALUMNO,
                            "Modo: secuencial, un cliente por vez, sin hilos",
                            "Puerto: " + puerto,
                            "Cifrado: XOR con clave rotativa sobre Base64",
                            "Acceso de prueba: usuario " + USUARIO + ", clave " + CLAVE
                    });

            if (!CARPETA.exists() && CARPETA.mkdirs()) {
                Consola.log(Consola.Evento.INFO, "Carpeta creada: " + CARPETA.getName());
            }

            ServerSocket servidor = null;
            try {
                servidor = new ServerSocket();
                servidor.bind(new InetSocketAddress("0.0.0.0", puerto));
                Consola.log(Consola.Evento.RED, "ServerSocket escuchando en 0.0.0.0:" + puerto);
                Consola.log(Consola.Evento.RED, "Esperando conexiones (espera activa)...");

                while (true) {
                    Socket cliente = servidor.accept();
                    sesiones++;
                    atender(cliente);
                    Consola.log(Consola.Evento.RED, "Servidor libre, esperando otro cliente...");
                    Consola.separador();
                }
            } catch (IOException e) {
                Consola.log(Consola.Evento.ERROR, "Fallo del ServerSocket: " + e.getMessage());
            } finally {
                cerrar(servidor);
            }
        }

        private static void atender(Socket socket) {
            DataInputStream entrada = null;
            DataOutputStream salida = null;

            autenticado = false;
            usuarioActual = "anonimo";
            HISTORIAL.clear();

            String remoto = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

            try {
                socket.setTcpNoDelay(true);
                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());

                Consola.separador();
                Consola.log(Consola.Evento.CONEXION, "Cliente aceptado, sesion numero " + sesiones);
                Consola.detalle("origen", remoto);
                Consola.detalle("local", socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());

                enviar(salida, marcar(T_SISTEMA,
                        "Conectado al soporte de TiendaTECH. El canal viaja cifrado.\n"
                      + "Atiende el servidor de " + ALUMNO + ".\n"
                      + "Comandos: autenticar, ayuda, ver stock de <producto>, "
                      + "subir comprobante <ruta>, ver historial de chat, salir"));

                boolean enLinea = true;
                while (enLinea) {
                    String mensaje = recibir(entrada);
                    registrar("CLIENTE", mensaje);
                    enLinea = procesar(mensaje, entrada, salida);
                }

            } catch (EOFException fin) {
                Consola.log(Consola.Evento.CIERRE, "El cliente cortó la conexión (EOF).");
            } catch (IOException e) {
                Consola.log(Consola.Evento.ERROR, "Error de E/S con " + remoto + ": " + e.getMessage());
            } finally {
                cerrar(entrada);
                cerrar(salida);
                cerrar(socket);
                Consola.log(Consola.Evento.CIERRE, "Streams y socket cerrados para " + remoto);
            }
        }

        /** Devuelve false cuando la sesion termina. */
        private static boolean procesar(String mensaje, DataInputStream entrada, DataOutputStream salida)
                throws IOException {

            String limpio = mensaje.trim();
            String clave = normalizar(limpio);

            if (clave.isEmpty()) {
                responder(salida, T_BOT, MENU_SOPORTE);
                return true;
            }
            if (clave.startsWith("autenticar")) {
                Consola.log(Consola.Evento.COMANDO, "autenticar");
                autenticar(limpio, salida);
                return true;
            }
            if (clave.equals("ayuda")) {
                Consola.log(Consola.Evento.COMANDO, "ayuda");
                responder(salida, T_MENU, menuGuiado());
                return true;
            }
            if (clave.startsWith("ver stock")) {
                Consola.log(Consola.Evento.COMANDO, "ver stock");
                if (faltaAutenticar(salida)) {
                    return true;
                }
                responder(salida, T_STOCK, consultarStock(clave.substring("ver stock".length())));
                return true;
            }
            if (clave.startsWith("subir comprobante")) {
                Consola.log(Consola.Evento.COMANDO, "subir comprobante");
                if (faltaAutenticar(salida)) {
                    return true;
                }
                recibirArchivo(limpio, entrada, salida);
                return true;
            }
            if (clave.startsWith("ver historial")) {
                Consola.log(Consola.Evento.COMANDO, "ver historial de chat");
                responder(salida, T_HISTORIAL, historial());
                return true;
            }
            if (clave.equals("salir")) {
                Consola.log(Consola.Evento.COMANDO, "salir");
                responder(salida, T_FIN, "Sesión cerrada. Hasta luego, " + ALUMNO + ".");
                return false;
            }

            Consola.log(Consola.Evento.CHATBOT, "Respuesta precargada para: " + limpio);
            responder(salida, T_BOT, frasePara(clave));
            return true;
        }

        private static void autenticar(String mensaje, DataOutputStream salida) throws IOException {
            String[] partes = mensaje.split("\\|");
            if (partes.length < 3) {
                Consola.log(Consola.Evento.ERROR, "Comando autenticar sin credenciales.");
                responder(salida, T_ERROR, "Use el comando autenticar y cargue usuario y clave.");
                return;
            }

            String usuario = partes[1].trim();
            if (usuario.equalsIgnoreCase(USUARIO) && partes[2].trim().equals(CLAVE)) {
                autenticado = true;
                usuarioActual = usuario;
                Consola.log(Consola.Evento.AUTENTICA, "Acceso concedido a " + usuario);
                responder(salida, T_OK, "Acceso correcto. Bienvenido " + ALUMNO + ".\n"
                        + "Ya puede consultar stock y subir comprobantes.");
            } else {
                autenticado = false;
                Consola.log(Consola.Evento.ERROR, "Acceso denegado a " + usuario);
                responder(salida, T_ERROR, "Usuario o clave incorrectos. Reintente con autenticar.");
            }
        }

        private static boolean faltaAutenticar(DataOutputStream salida) throws IOException {
            if (autenticado) {
                return false;
            }
            Consola.log(Consola.Evento.ERROR, "Comando rechazado: sesión sin autenticar.");
            responder(salida, T_ERROR, "Primero ejecute el comando autenticar.");
            return true;
        }

        private static String consultarStock(String consulta) {
            String pedido = normalizar(consulta).replace("de ", "").trim();

            if (pedido.isEmpty()) {
                StringBuilder sb = new StringBuilder("Inventario cargado en el servidor:\n");
                sb.append(String.format("  %-20s %9s %12s %12s\n",
                        "PRODUCTO", "CANTIDAD", "PRECIO", "ESTADO"));
                for (int i = 0; i < PRODUCTOS.length; i++) {
                    sb.append(String.format("  %-20s %9d %12s %12s\n",
                            PRODUCTOS[i], CANTIDADES[i], "$ " + PRECIOS[i], estado(CANTIDADES[i])));
                }
                Consola.log(Consola.Evento.STOCK, "Enviado el arreglo completo, " + PRODUCTOS.length + " productos");
                return sb.toString().trim();
            }

            for (int i = 0; i < PRODUCTOS.length; i++) {
                String nombre = PRODUCTOS[i];
                if (nombre.contains(pedido) || pedido.contains(nombre) || coincide(nombre, pedido)) {
                    Consola.log(Consola.Evento.STOCK,
                            "Encontrado en la posición " + i + ": " + nombre + ", " + CANTIDADES[i] + " u.");
                    return "Producto: " + nombre + "\n"
                         + "Cantidad: " + CANTIDADES[i] + " unidades\n"
                         + "Precio: $ " + PRECIOS[i] + "\n"
                         + "Estado: " + estado(CANTIDADES[i]) + "\n"
                         + "Posición " + i + " del arreglo del servidor";
                }
            }

            Consola.log(Consola.Evento.ERROR, "Producto fuera del arreglo: " + pedido);
            return "No encontré " + pedido.trim() + " en el inventario.\n"
                 + "Hay: " + String.join(", ", PRODUCTOS);
        }

        private static boolean coincide(String nombre, String pedido) {
            for (String palabra : pedido.split("\\s+")) {
                if (palabra.length() >= 4 && nombre.contains(palabra)) {
                    return true;
                }
            }
            return false;
        }

        private static String estado(int cantidad) {
            if (cantidad == 0) {
                return "SIN STOCK";
            }
            return cantidad < 10 ? "POCAS" : "DISPONIBLE";
        }

        /** El cliente avisa el nombre, el servidor confirma, y recien ahi llegan los bytes. */
        private static void recibirArchivo(String mensaje, DataInputStream entrada, DataOutputStream salida)
                throws IOException {

            String nombre = mensaje.contains("|")
                    ? mensaje.substring(mensaje.indexOf("|") + 1).trim()
                    : "comprobante.dat";
            nombre = new File(nombre.isEmpty() ? "comprobante.dat" : nombre).getName();

            responder(salida, T_LISTO, "Listo para recibir " + nombre);

            int bytesCifrados = entrada.readInt();
            Consola.log(Consola.Evento.ARCHIVO, "Entra una transferencia");
            Consola.detalle("archivo", nombre);
            Consola.detalle("bytes", String.valueOf(bytesCifrados));

            byte[] cifrado = new byte[bytesCifrados];
            entrada.readFully(cifrado);
            byte[] original = Cripto.descifrarBytes(cifrado);

            File destino = new File(CARPETA,
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) + "_" + nombre);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(destino);
                fos.write(original);
                fos.flush();
            } finally {
                cerrar(fos);
            }

            Consola.log(Consola.Evento.ARCHIVO, "Archivo descifrado y guardado");
            Consola.detalle("destino", destino.getPath());

            responder(salida, T_OK, "Comprobante recibido y descifrado.\n"
                    + "Guardado como: " + destino.getName() + "\n"
                    + "Tamaño original: " + original.length + " bytes\n"
                    + "Viajaron " + bytesCifrados + " bytes cifrados");
        }

        private static String menuGuiado() {
            return "Preguntas frecuentes, escriba la palabra entre parentesis:\n"
                 + "  Donde esta mi envio (envio)\n"
                 + "  Como devuelvo un producto (devolucion)\n"
                 + "  Quiero hacer un reclamo (reclamo)\n"
                 + "  Necesito la factura (factura)\n"
                 + "  Formas de pago (pago)\n"
                 + "  Garantia (garantia)\n"
                 + "\n"
                 + "Comandos:\n"
                 + "  autenticar                 valida el acceso\n"
                 + "  ayuda                      muestra este menu\n"
                 + "  ver stock de <producto>    consulta el arreglo del servidor\n"
                 + "  subir comprobante <ruta>   manda un archivo al servidor\n"
                 + "  ver historial de chat      lista los mensajes de la sesion\n"
                 + "  salir                      cierra streams y sockets";
        }

        private static String frasePara(String clave) {
            if (clave.equals("1")) {
                return buscar("envio");
            }
            if (clave.equals("2")) {
                return buscar("devolucion");
            }
            if (clave.equals("3")) {
                return buscar("reclamo");
            }
            for (String[] par : FRASES) {
                if (clave.contains(par[0])) {
                    return par[1];
                }
            }
            return MENU_SOPORTE;
        }

        private static String buscar(String clave) {
            for (String[] par : FRASES) {
                if (par[0].equals(clave)) {
                    return par[1];
                }
            }
            return MENU_SOPORTE;
        }

        private static String historial() {
            if (HISTORIAL.isEmpty()) {
                return "Todavía no hay mensajes en esta sesión.";
            }
            StringBuilder sb = new StringBuilder("Historial de la sesión, usuario " + usuarioActual + ":\n");
            int i = 1;
            for (String linea : HISTORIAL) {
                sb.append(String.format("  %02d) %s\n", i++, linea));
            }
            return sb.toString().trim();
        }

        private static void responder(DataOutputStream salida, String etiqueta, String cuerpo) throws IOException {
            enviar(salida, marcar(etiqueta, cuerpo));
            registrar("SERVIDOR", cuerpo);
        }

        private static void registrar(String origen, String texto) {
            String visible = texto.startsWith("autenticar") && texto.contains("|")
                    ? "autenticar (credenciales ocultas)"
                    : texto.split("\n", -1)[0];
            if (visible.length() > 80) {
                visible = visible.substring(0, 80) + "...";
            }
            HISTORIAL.add("[" + LocalTime.now().format(HORA) + "] " + origen + ": " + visible);
        }
    }

    static final String MENU_SOPORTE =
            "Menú de Soporte: 1. Estado de Envío | 2. Devoluciones | 3. Reclamos. "
          + "Por favor, introduzca un comando válido.";

    // ===================== Cliente =====================

    /** Manda un comando y espera la respuesta antes de aceptar el siguiente. */
    static final class Cliente {

        private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
        private static final List<String> HISTORIAL = new ArrayList<>();

        static void iniciar(String host, int puerto) {
            Consola.definirRol("CLIENTE", Consola.CYAN);
            Consola.encabezado("CLIENTE TCP - Soporte TiendaTECH", new String[]{
                    "Alumno: " + ALUMNO,
                    "Servidor: " + host + ":" + puerto,
                    "Cifrado: todo sale cifrado y entra descifrado",
                    "Comandos: autenticar, ayuda, ver stock de <producto>,",
                    "          subir comprobante <ruta>, ver historial de chat, salir"
            });

            Socket socket = null;
            DataInputStream entrada = null;
            DataOutputStream salida = null;
            Scanner teclado = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            try {
                Consola.log(Consola.Evento.RED, "Abriendo socket hacia " + host + ":" + puerto + "...");
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, puerto), 10000);
                socket.setTcpNoDelay(true);

                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());

                Consola.log(Consola.Evento.CONEXION, "Conectado al servidor");
                Consola.detalle("local", socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
                Consola.detalle("remoto", socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                mostrar(recibir(entrada));

                // El saludo deja el nombre del alumno impreso en las dos consolas.
                chatear(salida, "Hola, mi nombre es " + ALUMNO, true);
                mostrar(recibir(entrada));

                boolean enLinea = true;
                while (enLinea) {
                    System.out.print(Consola.NEGRITA + Consola.CYAN + "\n  " + ALUMNO + " > " + Consola.RESET);
                    System.out.flush();

                    if (!teclado.hasNextLine()) {
                        chatear(salida, "salir", false);
                        mostrar(recibir(entrada));
                        break;
                    }

                    String linea = teclado.nextLine().trim();
                    System.out.println(Consola.BLANCO + linea + Consola.RESET);
                    if (linea.isEmpty()) {
                        continue;
                    }

                    String clave = normalizar(linea);

                    if (clave.startsWith("autenticar")) {
                        pedirCredenciales(teclado, salida);
                        mostrar(recibir(entrada));
                        continue;
                    }
                    if (clave.startsWith("subir comprobante")) {
                        subirComprobante(linea, entrada, salida);
                        continue;
                    }

                    chatear(salida, linea, !esComando(clave));
                    mostrar(recibir(entrada));

                    if (clave.equals("salir")) {
                        enLinea = false;
                    } else if (clave.startsWith("ver historial")) {
                        historialLocal();
                    }
                }

            } catch (EOFException fin) {
                Consola.log(Consola.Evento.CIERRE, "El servidor cerró la conexión (EOF).");
            } catch (IOException e) {
                Consola.log(Consola.Evento.ERROR, "Error de red: " + e.getMessage());
                Consola.detalle("revisar", "IP, puerto y que el servidor esté levantado");
            } finally {
                cerrar(entrada);
                cerrar(salida);
                cerrar(socket);
                teclado.close();
                Consola.log(Consola.Evento.CIERRE, "Streams y socket cerrados.");
                Consola.log(Consola.Evento.INFO, "Fin del cliente de " + ALUMNO);
            }
        }

        private static void pedirCredenciales(Scanner teclado, DataOutputStream salida) throws IOException {
            System.out.print(Consola.MAGENTA + "    usuario: " + Consola.RESET);
            String usuario = teclado.hasNextLine() ? teclado.nextLine().trim() : "";
            System.out.println(Consola.BLANCO + usuario + Consola.RESET);

            System.out.print(Consola.MAGENTA + "    clave:   " + Consola.RESET);
            String password = teclado.hasNextLine() ? teclado.nextLine().trim() : "";
            System.out.println(Consola.BLANCO + Consola.repetir("*", password.length()) + Consola.RESET);

            Consola.log(Consola.Evento.AUTENTICA, "Mando las credenciales cifradas de " + usuario);
            enviar(salida, "autenticar|" + usuario + "|" + password);
            anotar(ALUMNO, "autenticar (credenciales ocultas)");
        }

        private static void subirComprobante(String linea, DataInputStream entrada, DataOutputStream salida)
                throws IOException {

            String ruta = linea.substring("subir comprobante".length()).trim();
            if (ruta.isEmpty()) {
                ruta = "comprobante_demo.txt";
                Consola.log(Consola.Evento.INFO, "Sin ruta, uso el archivo de prueba " + ruta);
            }

            File archivo = new File(ruta);
            if (!archivo.isFile()) {
                Consola.log(Consola.Evento.ERROR, "No existe el archivo: " + archivo.getAbsolutePath());
                Consola.detalle("uso", "subir comprobante C:\\ruta\\archivo.pdf");
                return;
            }

            byte[] contenido = leer(archivo);
            byte[] cifrado = Cripto.cifrarBytes(contenido);

            Consola.log(Consola.Evento.ARCHIVO, "Preparo el envío de " + archivo.getName());
            Consola.detalle("ruta", archivo.getAbsolutePath());
            Consola.detalle("tamaño", contenido.length + " bytes");

            enviar(salida, "subir comprobante|" + archivo.getName());
            anotar(ALUMNO, "subir comprobante " + archivo.getName());

            String respuesta = recibir(entrada);
            mostrar(respuesta);
            if (!etiqueta(respuesta).equals(T_LISTO)) {
                Consola.log(Consola.Evento.ERROR, "El servidor no aceptó la transferencia.");
                return;
            }

            salida.writeInt(cifrado.length);
            salida.write(cifrado);
            salida.flush();
            Consola.log(Consola.Evento.ARCHIVO, "Bloque cifrado enviado por el socket");

            mostrar(recibir(entrada));
        }

        private static void chatear(DataOutputStream salida, String texto, boolean comoChat) throws IOException {
            if (comoChat) {
                System.out.println(Consola.NEGRITA + Consola.CYAN + "  " + ALUMNO + " dice: "
                        + Consola.RESET + Consola.BLANCO + texto + Consola.RESET);
            }
            enviar(salida, texto);
            anotar(ALUMNO, texto);
        }

        private static boolean esComando(String clave) {
            return clave.equals("ayuda")
                    || clave.equals("salir")
                    || clave.startsWith("ver stock")
                    || clave.startsWith("ver historial")
                    || clave.startsWith("autenticar")
                    || clave.startsWith("subir comprobante");
        }

        private static void mostrar(String mensaje) {
            String etiqueta = etiqueta(mensaje);
            String cuerpo = cuerpo(mensaje);

            String color;
            String titulo;
            switch (etiqueta) {
                case T_OK:        color = Consola.VERDE;    titulo = "SERVIDOR, operación correcta"; break;
                case T_ERROR:     color = Consola.ROJO;     titulo = "SERVIDOR, error";              break;
                case T_MENU:      color = Consola.AMARILLO; titulo = "CHATBOT, menú de ayuda";       break;
                case T_STOCK:     color = Consola.VERDE;    titulo = "SERVIDOR, stock";              break;
                case T_HISTORIAL: color = Consola.CYAN;     titulo = "SERVIDOR, historial";          break;
                case T_BOT:       color = Consola.MAGENTA;  titulo = "CHATBOT";                      break;
                case T_LISTO:     color = Consola.AMARILLO; titulo = "SERVIDOR, listo";              break;
                case T_FIN:       color = Consola.ROJO;     titulo = "SERVIDOR, fin de sesión";      break;
                default:          color = Consola.AZUL;     titulo = "SERVIDOR";                     break;
            }

            System.out.println();
            System.out.println(color + "  " + Consola.NEGRITA + titulo + Consola.RESET
                    + color + "  " + LocalTime.now().format(HORA) + Consola.RESET);
            for (String l : cuerpo.split("\n", -1)) {
                System.out.println(color + "  | " + Consola.RESET + "\t" + l);
            }

            anotar("SERVIDOR", cuerpo.split("\n", -1)[0]);
        }

        private static void historialLocal() {
            System.out.println();
            System.out.println(Consola.CYAN + "  " + Consola.NEGRITA + "HISTORIAL LOCAL DEL CLIENTE"
                    + Consola.RESET + Consola.CYAN + "  " + HISTORIAL.size() + " entradas" + Consola.RESET);
            int i = 1;
            for (String linea : HISTORIAL) {
                System.out.println(Consola.CYAN + "  | " + Consola.RESET + "\t"
                        + String.format("%02d) %s", i++, linea));
            }
        }

        private static void anotar(String origen, String texto) {
            String primera = texto.split("\n", -1)[0];
            if (primera.length() > 80) {
                primera = primera.substring(0, 80) + "...";
            }
            HISTORIAL.add("[" + LocalTime.now().format(HORA) + "] " + origen + ": " + primera);
        }

        private static byte[] leer(File archivo) throws IOException {
            byte[] datos = new byte[(int) archivo.length()];
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(archivo);
                int leidos = 0;
                while (leidos < datos.length) {
                    int n = fis.read(datos, leidos, datos.length - leidos);
                    if (n < 0) {
                        break;
                    }
                    leidos += n;
                }
            } finally {
                cerrar(fis);
            }
            return datos;
        }
    }
}
