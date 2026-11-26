package recuperatorio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


public class Cliente {
    static String PING = "/PING_CHECK";
    static String PONG = "/PONG_ALIVE";
    
	public static void main(String[] args) {
		String IP = "130.10.2.254";
		int PORT = 5000;

		
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		
		
		try(Socket socket = new Socket(IP, PORT)) {

		    
			String linea;
			
	        BufferedReader entrada = new BufferedReader(
	                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
	        PrintWriter salida = new PrintWriter(
	                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

			System.out.println("[CONECTADO AL SERVIDOR]" + IP + ":" + PORT);
			
			Thread receptor = new Thread(() -> escuchar(entrada, salida), "RECEPTOR");
            receptor.setDaemon(true);
            receptor.start();
            
            while ((linea = teclado.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                salida.println(linea);

                if (linea.equalsIgnoreCase("/logout")) {
                    System.out.println(Cifrado.AMARILLO + "[SISTEMA] Cerrando sesión..." + Cifrado.RESET);
                    break;
                }
            }

			socket.close();
			
		} catch (IOException e) {
			System.out.println("ERROR: " + e);
		}
	}
	
    private static void escuchar(BufferedReader entrada, PrintWriter salida) {
        try {
            String recibido;
            while ((recibido = entrada.readLine()) != null) {
                String claro = Cifrado.decriptar(Cifrado.CLAVE, recibido);
                String texto = (claro != null) ? claro : recibido;

                if (texto.trim().equals(PING)) {
                    String pong = Cifrado.encriptar(Cifrado.CLAVE, PONG);
                    if (pong != null) {
                        salida.println(pong);
                    }
                    continue;
                }

                if (claro != null) {
                    System.out.println(Cifrado.MORADO + "\t[SERVIDOR] " + Cifrado.RESET + texto);
                } else {
                    System.out.println(Cifrado.CIAN + "\t[SERVIDOR] " + Cifrado.RESET + texto);
                }
            }
            System.out.println(Cifrado.ROJO + "\n[SISTEMA] El servidor cerró la conexión." + Cifrado.RESET);
        } catch (IOException e) {
        }
    }
}
