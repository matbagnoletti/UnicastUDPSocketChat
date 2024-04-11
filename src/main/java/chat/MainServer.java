package udpchat;

import java.net.BindException;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Classe di avvio del Server UDP
 */
public class MainServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Inserisci il numero di porta in cui avviare il server: ");
            int porta = Integer.parseInt(scanner.nextLine());
            Server server = new Server(porta);
            server.avvia();
        } catch (NumberFormatException e) {
            System.err.println("Errore: porta non valida");
        } catch (BindException e){
            System.err.println("Errore: porta occupata o inutilizzabile");
        } catch (SocketException e){
            System.err.println("Errore: " + e.getMessage());
        }
    }
}
