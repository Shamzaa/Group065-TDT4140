package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import program.ClientMain;


public class LectureStatListener implements Runnable{
	private LectureReciever controller;
	
	// connection atributes
	private Socket client;
	
	// in and out data channels
	private BufferedReader in;
	
	public LectureStatListener(ClientMain main, LectureReciever controller){
		this.controller = controller;
		client = main.getServerManager().getSocket();
		
	}

		
	
	@Override
	public void run() {
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			while(true){
				System.out.println("Listens for new notifications from server");
				String input = in.readLine();
				try{
					JSONObject obj = new JSONObject(input);
					System.out.println(obj.getString("Function"));
					
					/* just making a switch case because it's very limited 
					 * what the lecturer will listen to, compared to the server
					 */
					switch(obj.getString("Function")){
						// lecturer only
						case "addLectures":
							controller.recieveLectures(obj);
							break;
							
					}
				}catch(JSONException e){
					// data recieved wasn't a json object
					// close connection or ignore.
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
