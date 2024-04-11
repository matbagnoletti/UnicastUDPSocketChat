package chat;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Classe di avvio del Client UDP
 */
public class MainClient {
    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.avvia();
        } catch (SocketException e) {
            System.err.println("Errore: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Errore: host non raggiungibile");
        } catch (NumberFormatException e) {
            System.err.println("Errore: numero di porta invalido");
        }
    }
}
