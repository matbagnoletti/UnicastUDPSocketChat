<h1 align="center">UnicastUDPSocketChat</h1>

<p align="center" style="font-family: monospace">Made by <a href="https://github.com/matbagnoletti">@matbagnoletti</a></p>
<p align="center" style="font-family: monospace">Docenti: prof.ssa <a href="https://github.com/mciuchetti">@mciuchetti</a> e prof.ssa Fioroni</p>
<p align="center" style="font-family: monospace">Corso TPSIT a.s. 2023/2024, <a href="https://www.avoltapg.edu.it/">ITTS A. Volta (PG)</a></p>
<p align="center">
    <img src="https://img.shields.io/github/last-commit/matbagnoletti/UnicastUDPSocketChat?style=for-the-badge" alt="Ultimo commit">
    <img src="https://img.shields.io/github/languages/top/matbagnoletti/UnicastUDPSocketChat?style=for-the-badge" alt="Linguaggio">
</p>

## Descrizione
Applicazione Java che utilizza le socket per implementare una comunicazione UDP unicast tra più Client e un Server.

## Requisiti
- [JDK](https://www.oracle.com/it/java/technologies/downloads/) (v8 o superiore)
- [Gradle](https://gradle.org/install/) (v8.6)

È possibile visualizzare le versioni già presenti sul proprio dispositivo mediante i seguenti comandi:
```
java -version
gradle -v
```

## Installazione e utilizzo
1. Scaricare il file compresso del progetto
2. Estrarre il progetto
3. Eseguire il Server e il Client separatamente:
    - Tramite IDE
    - Tramite terminale:
        1. Naviga nella root del progetto
        2. Esegui la build del progetto: `gradle build`
        3. Identifica il file `jar` nella directory `/build/libs/`
        4. Esegui il Server: `java -cp build/libs/<nome-del-file-jar>.jar chat.MainServer`
        5. Esegui il Client in un'altra finestra: `java -cp build/libs/<nome-del-file-jar>.jar chat.MainClient`

## Struttura e funzionamento
Il progetto si compone da quattro classi:

- Nel package `chat`:
    - L'entità [`Client`](src/main/java/chat/Client.java)
    - [`MainClient`](src/main/java/chat/MainClient.java), la classe di avvio del Client
    - L'entità [`Server`](src/main/java/chat/Server.java)
    - [`MainServer`](src/main/java/chat/MainServer.java), la classe di avvio del Server

Le due entità, dopo una configurazione iniziale, permetteranno all'utente di poter scrivere messaggi da inviare all'altra entità e ricevere, contemporaneamente (tramite l'utilizzo dei thread), messaggi che verranno stampati a video.

Il Server permette a più Client di comunicare con lui, ma i Client potranno comunicare solo con il Server, essendo una comunicazione unicast.

Lato-Server per poter comunicare con uno dei possibili Client connessi sarà necessario scrivere:
```text
<payload> > <usernameDestinatario>
```

La conversazione termina quando uno dei due digita `exit` nella console: in questo caso verranno invocati i rispettivi metodi di chiusura delle socket e segnalazione alle altre entità dell'avvenuta "disconnessione" (`chiudi()`).

## Licenza d'uso
Questo progetto (e tutte le sue versioni) sono rilasciate sotto la [MB General Copyleft License](LICENSE).