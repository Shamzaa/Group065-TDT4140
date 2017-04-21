package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import program.ClientMain;
import program.uiController.LectureReviewController;
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
		
	public void stopListening(){
		try {
			System.out.println("closing listener");
			Thread.currentThread().interrupt();
			/*
			 * How this stop listening works:
			 * I set the interrupt flag to true, and the loop runs while the flag is false.
			 * Using the .ready() attribute of the bufferedreader, I can run the loop
			 * without making it freeze this object. I also make this object sleep for 500ms each loop
			 * so I don't fry the CPU. 8)
			 */
			
			
			// closing socket makes the server think the user disconnected, when we just want the user to stop this listener and still keep an open connection with the server.
			//client.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			while(!Thread.currentThread().isInterrupted()){
				
				try{
					if(in.ready()){
						System.out.println("Listens for new notifications from server, client listener");
						String input = in.readLine();
						System.out.println("socket: " + input);
						JSONObject obj = new JSONObject(input);
					/** TODO check if this is wrong?
					System.out.println("testing: " + input);
					JSONObject obj = new JSONObject(input);
					
					/* just making a switch case because it's very limited 
					 * what the lecturer will listen to, compared to the server
					 *
					switch(obj.getString("Function")){						
						//Student Only*/
						
						/* just making a switch case because it's very limited 
						 * what the lecturer will listen to, compared to the server
						 */
						switch(obj.getString("Function")){						
							//Student Only
							
							//Both client-types live
							case "updateScore":
								controller.updateQuestionScore(obj.getInt("QuestionID"), obj.getInt("Score"));
								break;
							case "addQuestions":
								controller.recieveQuestions(obj);
								break;
							case "SetLiveLectureID":
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
					}else{
						Thread.currentThread().sleep(500);
					}
				}catch(SocketTimeoutException s){
					// listener interrupted through stopListening();
					client.setSoTimeout(0);
					return; // return from run-while method and object stops running
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}catch(Exception e){
					// data recieved wasn't a json object
					// close connection or ignore.
					e.printStackTrace();
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
