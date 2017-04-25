package lectureTests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import program.ClientMain;
import program.ui.controllers.lecturer.LectureReviewController;

public class LectureReviewTest extends GuiTest {
	static ClientMain main;
	static LectureReviewController c;
	
	@Override
	protected Parent getRootNode() {
		try {
			//return 
			main = new ClientMain();
			FXMLLoader l = new FXMLLoader(main.getClass().getResource("ui/LectureReview.fxml"));
			
			main.startConnection("");
			//c.setMainApp(main);
			Parent p = l.load();
			c = l.getController();
			c.setMainApp(main);
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void recieveQuestions() throws Exception {
		String recievedString = "{\"Function\":\"addQuestions\",\"QuestionAmount\":2147483647,\"List\":[{\"question\":\"dsa\",\"rating\":\"0\",\"time\":\"2017-04-21 02:37:37.0\",\"id\":\"158\"},{\"question\":\"asd\",\"rating\":\"0\",\"time\":\"2017-04-21 02:37:31.0\",\"id\":\"157\"}]}";
		recievedString.replaceAll("\\/","");
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveQuestions(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VBox vb = (VBox) find("#QuestionContainer");
		
		sleep(200);
		assertEquals(2, vb.getChildren().size());
	}
	
	/**
	 * Checks the handling of lectures without an end time-stamp,
	 * and with no lostMe stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_1() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "stop:\"\","
					+ "name:A lecture lacking a stop timestamp and no lostMe stamps,"
					+ "start:\"14:56:56\"},"
				+ "Function:lectureReview,"
				+ "StampList:[]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("00:00", ((Text) find("#stopTimeText")).getText());
	}
	
	/**
	 * Checks the handling of lectures without a start time-stamp,
	 * and with no lostMe time-stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_2() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "name:A lecture lacking a start timestamp and no lostMe stamps,"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "stop:\"14:56:56\","
					+ "start:\"\"},"
				+ "Function:lectureReview,"
				+ "StampList:[]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("00:00", ((Text) find("#startTimeText")).getText());
	}
	
	/**
	 * Checks the handling of lectures without a start OR end time-stamp,
	 * and without lostMe time-stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_3() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "name:A lecture lacking a start timestamp and no lostMe stamps,"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "stop:\"\","
					+ "start:\"\"},"
				+ "Function:lectureReview,"
				+ "StampList:[]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("00:00", ((Text) find("#startTimeText")).getText());
		assertEquals("00:00", ((Text) find("#stopTimeText")).getText());
	}
	/**
	 * Checks the handling of lectures without an end time-stamp,
	 * but WITH lostMe stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_4() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "name:A lecture lacking a stop timestamp and no lostMe stamps,"
					+ "start:\"14:56:56\","
					+ "stop:\"\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 15:00:00.0\","
					+ "\"1970-01-01 16:30:00.0\""
			+ "]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("16:30", ((Text) find("#stopTimeText")).getText());
	}
	
	/**
	 * Checks the handling of lectures without a start time-stamp,
	 * and with no lostMe time-stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_5() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "name:A lecture lacking a start timestamp and no lostMe stamps,"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "start:\"\","
					+ "stop:\"14:56:56\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 14:00:00.0\","
					+ "\"1970-01-01 14:30:00.0\""
				+ "]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("14:00", ((Text) find("#startTimeText")).getText());
	}
	
	/**
	 * Checks the handling of lectures without a start OR end time-stamp,
	 * and without lostMe time-stamps
	 * @throws Exception
	 */
	@Test
	public void recieveLackingInfo_6() throws Exception {
		String recievedString = 
				"{JSONStats:{"
					+ "name:A lecture lacking a start timestamp and no lostMe stamps,"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "start:\"\","
					+ "stop:\"\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 14:00:00.0\","
					+ "\"1970-01-01 14:30:00.0\""
				+ "]}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("14:00", ((Text) find("#startTimeText")).getText());
		assertEquals("14:30", ((Text) find("#stopTimeText")).getText());
	}
	
	/**
	 * This gives a lecture where the charts format is HH
	 * Unsure about if there is any way to check if its correct programmatically, for now it will just show,
	 * and we just have to see if it looks right
	 * @throws Exception
	 */
	@Test
	public void recieveAllReviewInfo_1() throws Exception{
		String recievedString = 
			 "{"
				+ "JSONStats:{"
					+ "studentsJoined:0,"
					+ "name:\"A Lecture with all info, and bar-format HH\","
					+ "date:2017-04-19,"
					+ "start:\"10:12:00\","
					+ "stop:\"15:35:00\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 10:00:00.0\","
					+ "\"1970-01-01 10:10:00.0\","
					+ "\"1970-01-01 10:20:00.0\","
					+ "\"1970-01-01 10:30:00.0\","
					+ "\"1970-01-01 10:40:00.0\","
					+ "\"1970-01-01 10:50:00.0\","
					+ "\"1970-01-01 10:59:00.0\","
					+ "\"1970-01-01 13:10:00.0\","
					+ "\"1970-01-01 13:10:00.0\","
					+ "\"1970-01-01 14:30:00.0\","
					+ "\"1970-01-01 14:30:00.0\","
					+ "\"1970-01-01 15:30:00.0\","
					+ "\"1970-01-01 15:40:00.0\","
					+ "\"1970-01-01 15:50:00.0\""
				+ "]"
			+ "}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(5000);
	}
	
	
	/**
	 * This gives a lecture where the charts format is hh:mm
	 * Unsure about if there is any way to check if its correct programmatically, for now it will just show,
	 * and we just have to see if it looks right
	 * @throws Exception
	 */
	@Test
	public void recieveAllReviewInfo_2() throws Exception {
		String recievedString = 
			 "{"
				+ "JSONStats:{"
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "stop:\"15:56:56\","
					+ "name:\"A Lecture with all info, and bar-format hh:mm\","
					+ "start:\"14:56:56\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 15:00:00.0\","
					+ "\"1970-01-01 15:10:00.0\","
					+ "\"1970-01-01 15:10:00.0\","
					+ "\"1970-01-01 15:30:00.0\","
					+ "\"1970-01-01 15:30:00.0\","
					+ "\"1970-01-01 15:30:00.0\","
					+ "\"1970-01-01 15:40:00.0\","
					+ "\"1970-01-01 15:50:00.0\""
				+ "]"
			+ "}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(5000);
	}
	
	/**
	 * This gives a lecture where the charts format is mm:ss
	 * Unsure about if there is any way to check if its correct programmatically, for now it will just show,
	 * and we just have to see if it looks right
	 * @throws Exception
	 */
	@Test
	public void recieveAllReviewInfo_3() throws Exception {
		String recievedString = 
			 "{"
				+ "JSONStats:{"
					+ "name:\"A Lecture with all info, and bar-format mm:ss\","
					+ "studentsJoined:0,"
					+ "date:2017-04-19,"
					+ "start:\"15:00:00\","
					+ "stop:\"15:05:40\""
				+ "},"
				+ "Function:lectureReview,"
				+ "StampList:["
					+ "\"1970-01-01 15:00:00.0\","
					+ "\"1970-01-01 15:00:10.0\","
					+ "\"1970-01-01 15:01:23.0\","
					+ "\"1970-01-01 15:02:57.0\","
					+ "\"1970-01-01 15:02:16.0\","
					+ "\"1970-01-01 15:03:47.0\","
					+ "\"1970-01-01 15:05:25.0\","
					+ "\"1970-01-01 15:05:24.0\""
				+ "]"
			+ "}";
		JSONObject obj;
		try {
			obj = new JSONObject(recievedString);
			c.recieveLectureReview(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(5000);
	}
}
