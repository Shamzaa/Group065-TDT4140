package serverTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

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
		db.executeStatement("delete from subjects where code = 'JUNIT_2'");
		assertFalse(db.createNewSubject("TEST0010000000", "TestName")); // Should reject too long codes
	}
	
	@Test
	public void testCreateNewLecture() throws Exception {
		//TODO I think this one returns an incorrect value.
		//     if you look at the coverage report, it thinks that the query was unsuccessful, even though it actually succeeded (Remove the tear-downs to see that this is true). 
		assertEquals(1, db.createNewLecture("A lecture only made for a JUnit test", "JUNIT_1"));
		//assertEquals(0, db.createNewLecture("A lecture only made for a JUnit test", "gsnuweh")); //Should reject if subject doesn't exist
		
		//db.setEndLecture(db.getLiveLectureID("JUNIT_1"));
		
		
		/* Not sure what's being tested here, TODO later
		assertFalse(db.executeStatement("delete from lectures where subject_code = 'TEST001'"));
		db.createNewLecture("TEST001");
		assertTrue(db.createNewLecture("NonExistingSubject") == 0); //Should reject nonexisting subjects*/
		
		
	}
	
}
