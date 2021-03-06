package program.connection.listeners;

import org.json.JSONObject;

/**
 * Interface for controllers that are supposed to recieve information about lectures
 * @author Erling Ihlen
 * @author Anders Hunderi
 * @version "%I%, %G%"
 * @since 1.0
 *
 */
public interface LectureReciever {
	/** 
	 * Requests all lectures from the database
	 */
	public void fetchLectures();
	
	/**
	 * Receives and displays information about lectures
	 * @param obj A JSONObject containing a list of all lectures and various meta-data
	 */
	public void recieveLectures(JSONObject obj);
	/**
	 * Receives and displays questions related to a specific lecture
	 * @param obj A JSONObject containing a list of questions and various meta-data
	 */
	public void recieveQuestions(JSONObject obj);
	/**
	 * Receives  and displays meta-data about a specific lecture
	 * @param obj A JSONObject
	 */
	public void recieveLectureReview(JSONObject obj);
}
