package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.HashMap;

import objects.Packet;

import java.io.ObjectOutputStream;

public class ConnectionHandler extends Thread {

    private Socket clientSocket;
    private Terminal terminal;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    private ComunicationManager manager;
    private boolean run = true;


    public ConnectionHandler(Socket clientSocket, ComunicationManager manager) throws IOException {
        this.clientSocket = clientSocket;
        this.manager = manager;
        this.terminal = Terminal.getInstance();
    }


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
                ServerServicesName action = ServerServicesName.valueOf(packet.command);
                HashMap<String, Object> params = new HashMap<>();

                if(packet.parameters != null) {
                    int parametreCount = packet.parameters.length;
                    //System.out.println("get: " + action.name() + " " + parametreCount + " params");

                
                    //riordino i dati e verifico la loro validità
                    for(int i = 0; i < parametreCount - (parametreCount % 2); i+=2) {
                        if(packet.parameters[i] instanceof String) {
                            params.put((String)packet.parameters[i], (Object)packet.parameters[i+1]);
                        }
                        else {
                            outputStream.writeObject(new IllegalArgumentException("La chiave del parametro non è di tipo \"String\""));
                            outputStream.flush();
                            continue;
                        }
                    }
                }
                else {
                    //System.out.println("get: " + action.name());
                }

                //se termino la connessiuone
                if(action == ServerServicesName.DISCONNECT) {
                    synchronized(this) {
                       outputStream.writeObject(packet.id);
                       outputStream.writeObject(null);
                    }
                    run = false;
                }
                //se voglio fare un test di ping
                else if(action == ServerServicesName.PING) {
                    Terminal.getInstance().printInfoln("ping response with " + Terminal.Color.MAGENTA_BRIGHT +clientIP + Terminal.Color.RESET);
                    synchronized(this) {
                       outputStream.writeObject(packet.id);
                       outputStream.writeObject(null);
                    }
                }
                //se devo eseguire una funzione
                else if(action != null) {
                    new Thread(() -> {
                        Object result = manager.getServerServiceFunction(action).apply(new Object[] {params, clientIP});
                        
                        synchronized(ConnectionHandler.class) 
                        {
                            if(!clientSocket.isClosed()) {
                                try {
                                    outputStream.writeObject(packet.id);
                                    outputStream.writeObject(result);
                                } 
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
                else {
                    terminal.printError("unknown socket function \""+ packet.command +"\"");
                    synchronized(this) {
                       outputStream.writeObject(packet.command);
                       outputStream.writeObject(new InvalidParameterException());
                    }
                }
            }
        } catch (Exception e) {
            terminal.printError(e.getMessage());
            //e.printStackTrace();
        }
        finally {
            if(clientSocket != null)
                try {clientSocket.close();} catch (IOException e) {e.printStackTrace();}
            manager.removeClientSocket(this);
        }
    } 

    protected Socket getSocket() {
        return clientSocket;
    }

    protected void terminate() {
        this.run = false;
        interrupt();
    }
}
