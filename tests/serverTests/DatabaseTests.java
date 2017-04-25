package serverTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import database.Database;

/**
 * For this integration testcase to succeed, the file DatabaseKey.java (not checked in to git)
 * has to be present. Access to database is also dependent on a VPN connection to
 * the NTNU network.
 */
public class DatabaseTests {
	//--------------------------------------------------------------------------------------
	//Setup and Tear-down
	static Database db;
	/**
	 * Initializes values for ALL tests (runs once)
	 */
	@BeforeClass
	public static void setUpClass(){
		db = new Database();
		db.connect();
		db.createNewSubject("JUNIT_1", "TestName");
	}
	/**
	 * Initializes values before each test (thus runs once for each test)
	 */
	@Before
	public void setUp(){
		//Empty
	}
	
	/**
	 * This method runs after each test (thus runs once for EACH test)
	 */
	@After
	public void tearDown(){
		// Empty
	}
	
	/**
	 * This method runs when all tests have been run (Thus runs only once)
	 */
	@AfterClass
	public static void tearDownClass() {
		//db.connect();
		db.executeStatement("delete from questions where question = '#test'");
		db.executeStatement("delete from subjects where code = 'JUNIT_1'");
		db.executeStatement("delete from lecture where subject_code = 'JUNIT_1'");
		db.executeStatement("delete from lecture where subject_code = 'JUNIT_2'");
		db.close();
	}	
	
	//--------------------------------------------------------------------------------------
	//TEST CASES 
	@Test
	public void testConnection() throws Exception {
		assertTrue(db.connect()); 		
	}

	@Test
	public void testPostNewQuestion() throws Exception {
		db.createNewLecture("A lecture made for JUnit question testing", "JUNIT_1");
		int lectureID = db.getLiveLectureID("JUNIT_1");
		System.out.println("GOT ID: "+String.valueOf(lectureID));
		assertTrue(db.postNewQuestion("#test", lectureID));
		ArrayList<Map<String, String>> questions = db.getLastestQuestions(lectureID, 1);
		assertEquals(1, questions.size()); // The retrieval should have a result present
		assertEquals("#test", questions.get(0).get("question"));
		assertTrue(questions.get(0).get("time") instanceof String);
		assertEquals("0", questions.get(0).get("rating"));						
		
		assertFalse(db.postNewQuestion("#test2", 1)); //It should not be possible to post a question if the lecture isn't live;
	}
	
	@Test
	public void testCreateNewSubject() throws Exception {
		assertTrue(db.createNewSubject("JUNIT_2", "TestName"));
		assertFalse(db.createNewSubject("JUNIT_2", "TestName")); //Should rejects duplicte subjects
		assertTrue(db.executeStatement("delete from subjects where code = 'JUNIT_2'"));
		assertFalse(db.createNewSubject("TEST0010000000", "TestName")); // Should reject too long codes
	}
	
	@Test
	public void testCreateAndEndNewLecture() throws Exception {
		int lectureID = db.createNewLecture("A lecture only made for a JUnit test", "JUNIT_1");
		assertNotEquals(0, lectureID);
		assertEquals(0, db.createNewLecture("A lecture only made for a JUnit test", "gsnuweh")); //Should reject if subject doesn't exist
		assertEquals(1, db.setEndLecture(lectureID));
		assertEquals(0, db.setEndLecture(-1));
	}
	
	@Test
	public void testQuestionVotes() throws Exception {
		int lectureID = db.createNewLecture("A lecture for testing question voting", "JUNIT_1");
		db.postNewQuestion("A question that will be voted on", lectureID);
		db.postNewQuestion("A question that will NOT be voted on", lectureID);
		db.postNewQuestion("A question that will NOT be voted on", lectureID);
		
		ArrayList<Map<String, String>> questions = db.getLastestQuestions(lectureID, Integer.MAX_VALUE);
		ArrayList<Integer> ids = new ArrayList<>();
		for (Map<String, String> map : questions) {
			ids.add(Integer.valueOf(map.get("id")));
		}
		//System.out.println(questions);
		assertNotEquals(0, questions.size());
		assertEquals(1, db.voteQuestion(Integer.valueOf(questions.get(0).get("id")), 1));
		assertEquals(1, db.voteQuestion(Integer.valueOf(questions.get(0).get("id")), 1));
		assertEquals(1, db.voteQuestion(Integer.valueOf(questions.get(0).get("id")), 1));
		
		questions = db.getLastestQuestions(lectureID, Integer.MAX_VALUE);
		
		assertEquals(3, db.getScoreQuestion(ids.get(0)));
		assertEquals(0, db.getScoreQuestion(ids.get(1)));
		assertEquals(0, db.getScoreQuestion(ids.get(2)));
		
		assertEquals(1, db.voteQuestion(Integer.valueOf(questions.get(0).get("id")), -2));
		assertEquals(1, db.getScoreQuestion(ids.get(0)));	
	}
	
	@Test
	public void testGetSubjectCodes() throws Exception {
		assertNotEquals(0, db.getAllSubjectCodes().size());
	}
	
	@Test
	public void testGetLatestLectures() throws Exception {
		db.createNewSubject("JUNIT_2", "Used to test lecture fetching");
		ArrayList<Integer> ids = new ArrayList<>();
		for (int i = 1; i <= 5; i++) { //Creating 5 fake lectures
			int l_id = db.createNewLecture("Fake lecture #" + String.valueOf(i), "JUNIT_2");
			db.addStudentCountToLecture(l_id);
			db.setEndLecture(l_id);
			ids.add(l_id);
		}
		ArrayList<Map<String, String>> lectures = db.getLatestLectures("JUNIT_2");
		
		assertEquals(5, lectures.size());
		
		HashMap<String, String> stats = db.getLectureStats(ids.get(0));
		System.out.println(stats);
		assertEquals("Fake lecture #1", stats.get("name"));
		assertEquals("1", stats.get("studentsJoined"));
		
		// Remove the test lecture
		assertTrue(db.executeStatement("delete from subjects where code = 'JUNIT_2'"));
	}
	
	@Test
	public void testGetLostMeTimeStamps() throws Exception {
		int id = db.createNewLecture("A lecture to test lostMe timestamps", "JUNIT_1");
		assertEquals(1, db.createYouLostMe(id));
		db.setEndLecture(id);
		assertNotEquals(0, db.getLostMeTimestamps(id).size());
	}
}
