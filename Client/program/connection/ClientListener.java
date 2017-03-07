package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import program.ClientMain;
import program.uiController.LecturerWindowController;
import program.uiController.StudentWindowController;

public class ClientListener implements Runnable{
	// references
	ClientMain main;
	QuestionReciever controller;
	
	// connection atributes
	private Socket client;
	
	// in and out data channels
	private BufferedReader in;
	
	public ClientListener(ClientMain main, QuestionReciever controller){
		this.main = main;
		this.client = main.getServerManager().getSocket();
		this.controller = controller;
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
					
					/* just making a switch case because it's very limited 
					 * what the lecturer will listen to, compared to the server
					 */
					switch(obj.getString("Function")){
						//Student Only
						
						//Both client-types
						case "addQuestions":
							controller.recieveQuestions(obj);
							break;
						case "SetLiveLectureID":
							System.out.println(">>>> CASE:  SET LIVE LECTURE");
							controller.setLiveLectureID(obj.getInt("LiveLectureID"));
							break;
						//Lecturer Only
						case "StudentLost":
							((LecturerWindowController) controller).studentLost();
							break;
						case "JoinedLecture":
							((LecturerWindowController) controller).studentJoined();
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
