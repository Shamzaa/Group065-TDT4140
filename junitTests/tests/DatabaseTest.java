package tests;

import java.util.ArrayList;
import java.util.Map;

import database.Database;

/**
 * For this integration testcase to succeed, the file DatabaseKey.java (not checked in to git)
 * has to be present. Access to database is also dependent on a VPN connection to
 * the NTNU network.
 */
public class DatabaseTest extends junit.framework.TestCase {
	Database db = null;
	
	
	@Override
	public void setUp() {
		db = new Database();
		db.connect();
		assertTrue(db instanceof Database);
	}
	
	public void testConnection() {
		assertTrue(db.connect()); 
	}
	
	public void testCreateNewSubject() {
		assertFalse(db.executeStatement("delete from subjects where code = 'TEST001'"));
		assertTrue(db.createNewSubject("TEST001", "TestName"));
		assertFalse(db.createNewSubject("TEST001", "TestName")); //Should rejects duplicte subjects
		assertFalse(db.createNewSubject("TEST0010000000", "TestName")); // Should reject too long codes
	}
	
	public void testCreateNewLecture() {
		assertFalse(db.executeStatement("delete from lectures where subject_code = 'TEST001'"));
		db.createNewLecture("TEST001");
		assertTrue(db.createNewLecture("NonExistingSubject") == 0); //Should reject nonexisting subjects
	}
	
	public void testPostNewQuestion() {
		db.postNewQuestion("#test", 1);
		ArrayList<Map<String, String>> question = db.getLastestQuestions(1, 1);
		assertEquals(question.size(), 1); // The retrieval should have a result present
		assertEquals("#test", question.get(0).get("question"));
		assertTrue(question.get(0).get("time") instanceof String);
		assertEquals("0", question.get(0).get("rating"));				
	}
	
	public void tearDown() {
		db.connect();
		db.executeStatement("delete from questions where question = '#test'");
		db.close();
	}
}
