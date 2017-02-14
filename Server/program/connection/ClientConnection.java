package program.connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Runnable{
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	
	public ClientConnection(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		System.out.println("new connection!");
		
		// verify bla bla bla
		
		
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			String recievedString;
			while(true){
				System.out.println("Listens for new input");
				String input = in.readLine();
				// listen to data from client and process into JSON into application/database/assign role(student/lecturer)
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	

}
