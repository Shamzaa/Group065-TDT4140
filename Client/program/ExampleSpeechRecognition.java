package program;

import java.io.File;

import classes.Recording;
import classes.Transcribe;

public class ExampleSpeechRecognition {
	public static void main(String[] args) throws InterruptedException {
		Recording rec = new Recording("/tmp/temporary_recording");
		rec.start();
		System.out.println("recording...");
		Thread.sleep(5000);
		System.out.println("5 sec");
		Thread.sleep(10000);
		System.out.println("15 sec");
		Thread.sleep(5000);
		rec.stop();
		System.out.println("recording stopped.");
		
		
		File recordedClip = rec.getClip(5, 10);  // fetches the recording between sec 5 and sec 15.
		
		String transcribedText;
		
		transcribedText = Transcribe.transcribeWav(recordedClip);
		
		System.out.println(transcribedText);
		
		rec.delete();
		
	}
}
