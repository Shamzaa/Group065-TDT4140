package program.connection.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import program.ClientMain;
import program.ui.controllers.lecturer.LectureReviewController;


/**
 * 
 * Listener that sends info to controllers implementing the LectureReciever interface
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public class LectureStatListener implements Runnable{
	private LectureReciever controller;
	private int listenerCounter;
	// connection atributes
	private Socket client;
	
	// in and out data channels
	private BufferedReader in;
	
	/**
	 * @param main reference to the Main class
	 * @param controller the controller of the active window.
	 * @param listenerCounter a counter used to count how many times it will recieve information
	 * before the active view doesn't need more info, and closes the listener
	 */
	public LectureStatListener(ClientMain main, LectureReciever controller, int listenerCounter){
		this.controller = controller;
		this.listenerCounter = listenerCounter;
		client = main.getServerManager().getSocket();
	}

		
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
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

}
