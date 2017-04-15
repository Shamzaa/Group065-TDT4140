package classes;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import program.speech.microphone.Microphone;

public class Recording {
	private String filePath;
	private Microphone mic;
	private File file;
	
	public Recording(String filePath) {
		this.filePath = filePath;
	}
	
	public void start() {
		mic = new Microphone(AudioFileFormat.Type.WAVE);
		file = new File(filePath);
		try {
			mic.captureAudioToFile (file);
		} catch (Exception ex) {
			//Microphone not available or some other error.
			ex.printStackTrace();
			throw new RuntimeException("ERROR: Microphone is not availible");
		}
	}
	
	public void stop() {
		mic.close ();
	}
	
	public void delete() {
		file.delete();
	}

	public File getRecording() {
		return file;
	}

	public File getClip(int startSeconds, int secondsToCopy) {
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		File destinationFile = null;
		try {
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			AudioFormat format = fileFormat.getFormat();
			inputStream = AudioSystem.getAudioInputStream(file);
			int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
			inputStream.skip(startSeconds * bytesPerSecond);
			long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
			shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
			destinationFile = new File(file +".destination.wav");
			AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (inputStream != null) try { inputStream.close(); } catch (Exception e) { System.out.println(e); }
			if (shortenedStream != null) try { shortenedStream.close(); } catch (Exception e) { System.out.println(e); }
		}
		return destinationFile;
	}
}
