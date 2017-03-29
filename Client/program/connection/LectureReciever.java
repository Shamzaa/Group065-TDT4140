package program.connection;

import org.json.JSONObject;

public interface LectureReciever {
	public void fetchLectures();
	public void recieveLectures(JSONObject obj);
	public void recieveQuestions(JSONObject obj);
	public void recieveLectureReview(JSONObject obj);
}
