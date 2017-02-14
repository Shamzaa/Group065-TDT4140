package program.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerManager {
	Socket client;
	DataOutputStream output;
	
	
	public ServerManager(String serverAdress, int portNumber){
		try{
			client = new Socket(serverAdress, portNumber);
			/*output = new DataOutputStream(client.getOutputStream());
			output.writeByte(1);
			output.writeUTF("cyka my blyat ya fuck");
			output.flush();
			output.close();*/
			
			// added a loop to keep the client active and not stop running(causes connection to drop)
			// TODO: remove once we have a javafx app running so the client won't stop running
			while(true){
				
			}
		} catch (IOException e){
			System.out.println(e);
		}
		
	}
	
}
