package classes;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import program.speech.recognizer.FlacEncoder;
import program.speech.recognizer.Recognizer;

public class Transcribe {
	public static String transcribeWav(File inputFile) {
		AudioFileFormat fileFormat = null;
		AudioFormat format = null;
		String response = null;
		Recognizer recognizer = new Recognizer (Recognizer.Languages.ENGLISH_US, System.getProperty("google-api-key"));

		try {
			fileFormat = AudioSystem.getAudioFileFormat(inputFile);
			format = fileFormat.getFormat();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Audio file inputFile not supported.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("File input not supported.");
		}
		
		FlacEncoder flacEncoder = new FlacEncoder();
        File flacFile = new File(inputFile + ".flac");
        flacEncoder.convertWaveToFlac(inputFile, flacFile);

		try {
			response = recognizer.getRecognizedDataForFlac(flacFile, 1, (int) format.getSampleRate()).getResponse();
		}
		catch (Exception ex) {
			ex.printStackTrace ();
			System.err.println("ERROR: Google cannot be contacted");
		}
		
		return response;
	}
}
