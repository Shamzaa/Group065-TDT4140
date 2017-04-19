package program.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import program.ClientMain;
import program.uiController.LectureReviewController;


public class LectureStatListener implements Runnable{
	private LectureReciever controller;
	private int listenerCounter;
	// connection atributes
	private Socket client;
	
	// in and out data channels
	private BufferedReader in;
	private boolean interrupted = false;
	
	public LectureStatListener(ClientMain main, LectureReciever controller, int listenerCounter){
		this.controller = controller;
		this.listenerCounter = listenerCounter;
		client = main.getServerManager().getSocket();
	}

		
	
	@Override
	public void run() {
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			for(int i = 0; i < listenerCounter; i++){
				System.out.println("Listens for new notifications, lecture stats");
				String input = in.readLine();
				try{
					JSONObject obj = new JSONObject(input);
					System.out.println(obj.getString("Function"));
					
					/* just making a switch case because it's very limited 
					 * what the lecturer will listen to, compared to the server
					 */
					switch(obj.getString("Function")){
					//Lecture Review Only
					case "lectureReview":
						((LectureReviewController) controller).recieveLectureReview(obj);
						break;
						
					case "addLectures":
						controller.recieveLectures(obj);
						break;
						
					case("addQuestions"):
						System.out.println(">>>>>>>>> SENDING QUESTIONS");
						controller.recieveQuestions(obj);
						break;
					}
				}catch(JSONException e){
					// data recieved wasn't a json object
					// close connection or ignore.
					e.printStackTrace();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			System.out.println("Review reciever is done!");
		}catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void interrupt(){
		interrupted = true;
		System.out.println("yikes");
	}

}
