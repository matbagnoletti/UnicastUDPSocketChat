package udpchat;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Client UDP
 */
public class Client {
    
    /**
     * Socket UDP
     */
    private DatagramSocket socket;

    /**
     * Stream di input dell'utente
     */
    private final Scanner scanner;

    /**
     * L'indirizzo dell'host destinatario
     */
    private InetAddress dest;

    /**
     * Il numero di porta dell'host destinatario
     */
    private int porta;

    /**
     * L'array di dati ricevuti in input attraverso la socket UDP
     */
    private byte[] arrayInput;
    private String ultimoMsgRicevuto;

    /**
     * L'array di dati inviati in output attraverso la socket UDP
     */
    private byte[] arrayOutput;
    private String ultimoMsgInviato;
    
    public Client() throws SocketException {
        this.socket = new DatagramSocket();
        this.scanner = new Scanner(System.in);
        this.arrayInput = new byte[1024];
        this.arrayOutput = new byte[1024];
        this.ultimoMsgRicevuto = "";
        this.ultimoMsgInviato = "";
    }

    /**
     * Metodo che inizializza il client e avvia i metodi di ascolto e lettura
     * @throws UnknownHostException se l'host inserito non è raggiungibile
     * @throws NumberFormatException se la porta inserita non è un numero valido
     */
    public void avvia() throws UnknownHostException, NumberFormatException {
        System.out.print("Host (server) da raggiungere: ");
        String host = scanner.nextLine();
        this.dest = InetAddress.getByName(host);

        System.out.print("Numero di porta: ");
        this.porta = Integer.parseInt(scanner.nextLine());
        
        scrivi();
        leggi();
    }

    /**
     * Metodo per la lettura dei datagrammi ricevuti
     */
    private void leggi() {
        new Thread(() -> {
            while(!socket.isClosed() || !ultimoMsgInviato.equalsIgnoreCase("exit")){
                DatagramPacket risposta = new DatagramPacket(arrayInput, 1024);
                try {
                    socket.receive(risposta);
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    stampa("Errore in ricezione");
                }
                
                ultimoMsgRicevuto = new String(risposta.getData(), 0, risposta.getLength());
                
                if(ultimoMsgRicevuto.equalsIgnoreCase("server-exit")){
                    stampa("Il server non è più disponibile");
                    chiudi();
                } else {
                    stampa("Server: \033[1;37m" + ultimoMsgRicevuto + "\033[0m");
                }
            }
        }).start();
    }

    /**
     * Metodo per la scrittura di datagrammi
     */
    private void scrivi() {
        new Thread(() -> {
            System.out.println("Client pronto all'invio di messaggi. Digita 'exit' per terminare");
            while(!socket.isClosed() || !ultimoMsgInviato.equalsIgnoreCase("exit")){
                ultimoMsgInviato = scanner.nextLine();
                
                arrayOutput = ultimoMsgInviato.getBytes(StandardCharsets.UTF_8);
                DatagramPacket invio = new DatagramPacket(arrayOutput, arrayOutput.length, dest, porta);
                try {
                    socket.send(invio);
                } catch (IOException e) {
                    stampa("Errore nell'invio");
                }

                if(ultimoMsgInviato.equalsIgnoreCase("exit")){
                    chiudi();
                    break;
                }
            }
        }).start();
    }

    /**
     * Metodo sincronizzato per la scrittura di messaggi sulla console
     * @param msg il messaggio da stampare in console
     */
    private synchronized void stampa(String msg){
        System.out.println("\033[1;37m> \033[0m" + msg);
    }

    /**
     * Metodo per la chiusura del client
     */
    private synchronized void chiudi(){
        socket.close();
        stampa("Client terminato!");
        System.exit(0);
    }
}
