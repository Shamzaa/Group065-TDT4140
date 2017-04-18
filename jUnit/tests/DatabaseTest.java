package tests;

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
public class DatabaseTest {
	//--------------------------------------------------------------------------------------
	//Setup and Teardown
	static Database db;
	/**
	 * Initializes values for ALL tests (runs once)
	 */
	@BeforeClass
	public static void setUpClass(){
		db = new Database();
		db.connect();
		
		assertTrue(db instanceof Database);		
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
		db.connect();
		db.executeStatement("delete from questions where question = '#test'");
		db.close();
	}	
	
	//--------------------------------------------------------------------------------------
	//TEST CASES 
	@Test
	public void testConnection() throws Exception {
		assertTrue(db.connect()); 		
	}
	
	@Test
	public void testCreateNewSubject() throws Exception {
		assertFalse(db.executeStatement("delete from subjects where code = 'TEST001'"));
		assertTrue(db.createNewSubject("TEST001", "TestName"));
		assertFalse(db.createNewSubject("TEST001", "TestName")); //Should rejects duplicte subjects
		assertFalse(db.createNewSubject("TEST0010000000", "TestName")); // Should reject too long codes		
	}
	
	@Test
	public void testCreateNewLecture() throws Exception {
		
		/* Not sure what's being tested here, TODO later
		assertFalse(db.executeStatement("delete from lectures where subject_code = 'TEST001'"));
		db.createNewLecture("TEST001");
		assertTrue(db.createNewLecture("NonExistingSubject") == 0); //Should reject nonexisting subjects*/		
	}
	
	@Test
	public void testPostNewQuestion() throws Exception {
		db.postNewQuestion("#test", 1);
		ArrayList<Map<String, String>> question = db.getLastestQuestions(1, 1);
		assertEquals(question.size(), 1); // The retrieval should have a result present
		assertEquals("#test", question.get(0).get("question"));
		assertTrue(question.get(0).get("time") instanceof String);
		assertEquals("0", question.get(0).get("rating"));						
	}
}
