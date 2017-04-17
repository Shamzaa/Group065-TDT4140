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
	
	private boolean listening;
	
	public ClientListener(ClientMain main, QuestionReciever controller){
		this.main = main;
		this.client = main.getServerManager().getSocket();
		this.controller = controller;
		listening = true;
	}
		
	public void stopListening(){
		try {
			System.out.println("closing listener");
			listening = false;
			/*
			 * what I did is that I make the socket throw an exception if it doesn't recieve
			 * new data within 1 millisecond, and then set it back to infinite when I am able
			 *  to exit the in.readLine() blocking the object from exiting the loop.
			 */
			client.setSoTimeout(1);
			
			// closing socket makes the server think the user disconnected, when we just want the user to stop this listener and still keep an open connection with the server.
			//client.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Override
	public void run() {
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			while(listening){
				System.out.println("Listens for new notifications from server");

				try{
					String input = in.readLine();
					JSONObject obj = new JSONObject(input);
					
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
				}catch(JSONException e){
					// data recieved wasn't a json object
					// close connection or ignore.
					e.printStackTrace();
				}catch(SocketTimeoutException s){
					// listener interrupted through stopListening();
					client.setSoTimeout(0);
					return; // return from run-while method and object stops running
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
