package recuperatorio;

import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	public static void main(String[] args) {
		
		final int PORT = 5000;
		final String BIENVENIDA = "Bienvenido al chis!";
		
		try(ServerSocket server = new ServerSocket( PORT )){
			
			System.out.println("Listening to port: " + PORT + "...");
			
			while (true) {
				Socket cliente = server.accept();
				
				System.out.println("[CLIENTE CONECTADO]");
				
				// System.out.println("[CLIENTE DESCONECTADO]");
			}
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
}
