package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

public class Server extends Thread
{
	private static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.start();
		
	}
	
	public void run() 
	{
		System.out.println("Started ");
		ServerSocket server = null;

		try {
			server = new ServerSocket(Server.PORT);
		} 
		//if an I/O error occurs when opening the socket. 
		catch(IOException e) {
		
		} 
		//if the port parameter is outside the specified range of valid 
		//port values, which is between 0 and 65535, inclusive.
		catch(IllegalArgumentException e) {
			System.out.println("Invalid port number");
		}
		
		try {
			//aspetto una richiesta
			Socket socket = server.accept();
			System.out.println("Connection accepted: " + socket);
			
			
		} 
		//if an I/O error occurs when waiting for a connection.
		catch(IOException e) {
		
		} 
		//if a security manager exists and its checkAccept method doesn't allow 
		//the operation.
		catch(SecurityException e) {
	
		} 
		//if a timeout was previously set with setSoTimeout and the timeout 
		//has been reached.
		/*catch(SocketTimeoutException e) {
		
		}*/
		//if this socket has an associated channel, the channel is in non-blocking 
		//mode, and there is no connection ready to be accepted  
		catch(IllegalBlockingModeException e) {
		
		}  
		finally {
			//socket.close();
		}

		System.out.println("Closing...");
		//socket.close();
	}
}