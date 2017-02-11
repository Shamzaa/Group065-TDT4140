package program.connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnection implements Runnable{
	private Socket clientSocket;
	
	public ClientConnection(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		System.out.println("new connection!");
		
		// verify bla bla bla
		
		DataInputStream inputStream;
		try{
			inputStream = new DataInputStream(clientSocket.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		
		
		String recievedString;
		while(true){
			// listen to data from client and process into application/database
			try{
				/*if(inputStream.readByte() == 1){
					recievedString = inputStream.readUTF();
					System.out.println(recievedString);
				}
				
				output.writeBytes(recievedString + "\n\r");
				output.flush();*/
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
	}
	

}
