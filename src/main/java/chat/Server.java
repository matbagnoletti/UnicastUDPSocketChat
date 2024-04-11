package chat;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Server UDP
 */
public class Server {
    /**
     * Socket UDP
     */
    private DatagramSocket serverSocket;

    /**
     * Stream di input dell'utente
     */
    private final Scanner scanner;

    /**
     * Gli utenti comunicanti con il server
     */
    private ArrayList<InetSocketAddress> utenti;
    private ArrayList<String> aliasUtenti;
    
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
    
    public Server(int porta) throws SocketException, IllegalArgumentException {
        this.serverSocket = new DatagramSocket(porta);
        this.scanner = new Scanner(System.in);
        this.arrayInput = new byte[1024];
        this.utenti = new ArrayList<>();
        this.aliasUtenti = new ArrayList<>();
        this.ultimoMsgRicevuto = "";
        this.ultimoMsgInviato = "";
    }

    /**
     * Metodo che inizializza il server e avvia i metodi di ascolto e lettura in modo sequenziale
     */
    public void avvia() {
        stampa("Server avviato e in ascolto...");
        leggi();
        scrivi();
    }

    /**
     * Metodo per la lettura dei datagrammi ricevuti
     */
    private void leggi() {
        new Thread(() -> {
            while(!serverSocket.isClosed() || !ultimoMsgInviato.equalsIgnoreCase("exit")){
                DatagramPacket risposta = new DatagramPacket(arrayInput, 1024);
                try {
                    serverSocket.receive(risposta);
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    stampa("Errore in ricezione");
                }

                ultimoMsgRicevuto = new String(risposta.getData(), 0, risposta.getLength());
                gestionUtenti("add", (InetSocketAddress) risposta.getSocketAddress(), null);
                gestionUtenti("stampaRicevuto", (InetSocketAddress) risposta.getSocketAddress(), null);
            }
        }).start();
    }

    /**
     * Metodo per la scrittura di datagrammi
     */
    private void scrivi() {
        new Thread(() -> {
            System.out.println("Server pronto all'invio di messaggi.\nDigita 'exit' per terminare.\nDigita 'messaggio > UserN' per inviare un messaggio di risposta");
            while(!serverSocket.isClosed() || !ultimoMsgInviato.equalsIgnoreCase("exit")){
                ultimoMsgInviato = scanner.nextLine();
                
                if(ultimoMsgInviato.equalsIgnoreCase("exit")){
                    chiudi();
                    break;
                }
                
                String[] info = ultimoMsgInviato.split(">");
                
                if(info.length > 1){
                    String aliasDest = info[1].trim();
                    arrayOutput = info[0].trim().getBytes(StandardCharsets.UTF_8);

                    InetSocketAddress infoUtente = gestionUtenti("cercaScrivi", null, aliasDest);
                    if(infoUtente != null){
                        DatagramPacket invio = new DatagramPacket(arrayOutput, arrayOutput.length, infoUtente.getAddress(), infoUtente.getPort());
                        try {
                            serverSocket.send(invio);
                        } catch (IOException e) {
                            stampa("Errore in ricezione");
                        }
                    } else {
                        stampa("Errore: utente non trovato");
                    }
                } else {
                    stampa("Errore: messaggio invalido");
                }
            }
        }).start();
    }

    /**
     * Metodo per la gestione degli utenti con azioni. Non tutti i parametri sono necessari per tutte le operazioni.
     * @param azione l'azione da compiere con gli utenti
     * @param utente la socket del client da cui si è ricevuto un datagramma
     * @param alias il nome fittizio assegnato alla socket del client
     * @return la socket del client con l'azione "cercaScrivi", altrimenti null.
     */
    private synchronized InetSocketAddress gestionUtenti(String azione, InetSocketAddress utente, String alias){
        switch (azione){
            case "add" -> {
                if(!this.utenti.contains(utente)){
                    this.utenti.add(utente);
                    this.aliasUtenti.add("User" + utenti.size());
                }
                return null;
            }
            
            case "cercaScrivi" -> {
                int indice = this.aliasUtenti.indexOf(alias);
                if(indice != -1){
                    return utenti.get(indice);
                } else {
                    return null;
                }
            }
            
            case "stampaRicevuto" -> {
                int indice = this.utenti.indexOf(utente);
                if(indice != -1){
                    if(ultimoMsgRicevuto.equalsIgnoreCase("exit")){
                        stampa(aliasUtenti.get(indice) + " si è disconnesso");
                        gestionUtenti("remove", null, aliasUtenti.get(indice));
                    } else {
                        stampa(aliasUtenti.get(indice) + ":\033[1;37m " + ultimoMsgRicevuto + "\033[0m");
                    }
                }
                return null;
            }
            
            case "remove" -> {
                int indice = this.aliasUtenti.indexOf(alias);
                if(indice != -1) {
                    this.utenti.remove(indice);
                    this.aliasUtenti.remove(alias);
                }
                return null;
            }
            
            default -> {
                return null;
            }
        }
    }

    /**
     * Metodo sincronizzato per la scrittura di messaggi sulla console
     * @param msg il messaggio da stampare in console
     */
    private synchronized void stampa(String msg){
        System.out.println("\033[1;37m> \033[0m" + msg);
    }

    /**
     * Metodo per la chiusura del server
     */
    private synchronized void chiudi(){
        ultimoMsgInviato = "server-exit";
        arrayOutput = ultimoMsgInviato.getBytes(StandardCharsets.UTF_8);
        for (InetSocketAddress inetSocketAddress : utenti) {
            DatagramPacket segnalaChiusura = new DatagramPacket(arrayOutput, arrayOutput.length, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            try {
                serverSocket.send(segnalaChiusura);
            } catch (IOException e) {
                stampa("Errore nell'invio della segnalazione di chiusura server a " + inetSocketAddress.getHostString());
            }
        }
        
        serverSocket.close();
        stampa("Server terminato!");
        System.exit(0);
    }
}
