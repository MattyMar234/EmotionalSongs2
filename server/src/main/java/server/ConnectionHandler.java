package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.HashMap;

import enumclass.ServerServicesName;
import objects.Packet;

import java.io.ObjectOutputStream;


/**
 * La classe ConnectionHandler gestisce la comunicazione con un client attraverso un socket.
 * Ogni istanza di questa classe viene creata per gestire la connessione con un singolo client.
 */
public class ConnectionHandler extends Thread {

    private Socket clientSocket;
    private Terminal terminal;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    private ComunicationManager manager;
    private boolean run = true;



/**
 * Costruisce un nuovo gestore di connessione per il client utilizzando il socket del client e il gestore delle comunicazioni forniti.
 *
 * Questo costruttore accetta un oggetto Socket rappresentante la connessione del client e un oggetto ComunicationManager
 * che gestisce le comunicazioni del sistema. Il terminale viene inizializzato utilizzando l'istanza singola di Terminal.
 *
 * @param clientSocket Il socket della connessione del client.
 * @param manager L'oggetto ComunicationManager che gestisce le comunicazioni del sistema.
 * @throws IOException Se si verifica un errore durante l'inizializzazione del terminale o durante la creazione del gestore di connessione.
 */
    public ConnectionHandler(Socket clientSocket, ComunicationManager manager) throws IOException {
        this.clientSocket = clientSocket;
        this.manager = manager;
        this.terminal = Terminal.getInstance();
    }


/**
 * La classe CommandsExecutor gestisce l'esecuzione dei comandi ricevuti dal client.
 * Ogni istanza di questa classe viene creata per eseguire un comando specifico.
 */
    private class CommandsExecutor extends Thread {

        private Packet packet;
        private String clientIP;

        public CommandsExecutor(Packet packet, String clientIP) {
            this.packet = packet;
            this.clientIP = clientIP;
        }

        private void writeOnSocket(String Id, Object data) throws IOException 
        {
            synchronized(ConnectionHandler.class) {
                if(!clientSocket.isClosed()) {
                    outputStream.writeObject(Id);
                    outputStream.writeObject(data);    
                }
            }      
        }


        @Override
        public void run() {
            try {
                ServerServicesName action = ServerServicesName.valueOf(packet.command);
                HashMap<String, Object> params = new HashMap<>();

                if(action == null)
                    throw new InvalidParameterException("unknown socket function \""+ packet.command +"\"");

                switch (action) 
                {
                    //se termino la connessiuone
                    case DISCONNECT -> {
                        synchronized(ConnectionHandler.class) {
                            if(!run) return;
                            writeOnSocket(packet.id, true);
                            run = false;
                        }
                    }
                    case PING -> {
                        //new Thread(() -> {Terminal.getInstance().printInfoln("ping with " + Terminal.Color.MAGENTA_BRIGHT + clientIP + Terminal.Color.RESET);}).start();
                        writeOnSocket(packet.id, true);
                    }
                    
                    default -> {

                        if(packet.parameters == null)
                            throw new InvalidParameterException("packet.parameters is null");
                        
                        int parametreCount = packet.parameters.length;
                    
                        //riordino i dati e verifico la loro validità
                        for(int i = 0; i < parametreCount - (parametreCount % 2); i+=2) {
                            if(!(packet.parameters[i+0] instanceof String))
                                throw new IllegalArgumentException("packet.parameters key must be a String object");
                            
                            params.put((String)packet.parameters[i+0], (Object)packet.parameters[i+1]);
                        }

                        Object result = manager.executeServerServiceFunction(action, params, clientIP);
                        writeOnSocket(packet.id, result);
                    }
                }
            } 
            catch (InvalidParameterException e) {
                terminal.printErrorln(e.getMessage());
                try {writeOnSocket(packet.id, e);} catch (Exception k) {terminal.printErrorln(k.getMessage());}
            }
            catch (IllegalArgumentException e) {
                terminal.printErrorln(e.getMessage());
                try {writeOnSocket(packet.id, e);} catch (Exception k) {terminal.printErrorln(k.getMessage());}
            }
            catch (Exception e) {
                terminal.printError(e.getMessage());
            }
        }
    }



/**
 * Implementa il comportamento del gestore di connessione durante l'esecuzione del thread.
 *
 * Durante l'esecuzione del thread, il metodo inizializza gli stream di input e output del socket del client,
 * quindi entra in un ciclo while che ascolta continuamente per pacchetti dal client. Per ogni pacchetto ricevuto,
 * crea un nuovo oggetto `CommandsExecutor` e lo avvia come thread per eseguire i comandi associati al pacchetto.
 * Inoltre, stampa un messaggio informativo nel terminale riguardante la richiesta del client.
 *
 * Se si verifica un'eccezione durante l'esecuzione, stampa un messaggio di errore nel terminale.
 * Infine, chiude il socket del client e rimuove il gestore di connessione dal gestore delle comunicazioni.
 */
    @Override
    public void run() 
    {
        try {
            final String clientIP = clientSocket.getInetAddress().getHostAddress();
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            while(run) 
            {
                final Packet packet = (Packet) inputStream.readObject(); 

                new CommandsExecutor(packet, clientIP).start();

                new Thread(() -> {
                    Terminal.getInstance().printInfoln("Host: " + Terminal.Color.MAGENTA_BRIGHT + clientIP + Terminal.Color.RESET + "  request: " + Terminal.Color.CYAN_BOLD_BRIGHT + ServerServicesName.valueOf(packet.command) + Terminal.Color.RESET);
                }).start();

                //Terminal.getInstance().printInfoln("ping response with " + Terminal.Color.MAGENTA_BRIGHT + clientIP + Terminal.Color.RESET);
            }
        } 
        catch (Exception e) {
            new Thread(() -> {
                terminal.printErrorln(e.getMessage());
            }).start();
        }
        finally {
            if(clientSocket != null)
                try {clientSocket.close();} catch (IOException e) {e.printStackTrace();}
            manager.removeClientSocket(this);
        }
    } 



/**
 * Restituisce il socket del client associato a questo gestore di connessione.
 *
 * Questo metodo restituisce l'istanza del socket del client utilizzata da questo gestore di connessione.
 * È protetto e può essere utilizzato dalle classi derivate o all'interno del pacchetto in cui è dichiarato.
 *
 * @return Il socket del client associato a questo gestore di connessione.
 */
    protected Socket getSocket() {
        return clientSocket;
    }



/**
 * Termina il gestore di connessione chiudendo il suo ciclo di esecuzione e interrompendo il thread.
 *
 * Questo metodo imposta il flag `run` su false, interrompe il thread e chiude il socket del client associato a questo gestore di connessione.
 * È protetto e può essere utilizzato dalle classi derivate o all'interno del pacchetto in cui è dichiarato.
 */
    protected void terminate() {
        this.run = false;
        interrupt();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
