package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.HashMap;

import enumclass.ServerServicesName;
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

    protected Socket getSocket() {
        return clientSocket;
    }

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
