// Sound.java

package ucigame;

import java.applet.AudioClip;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.*;
import java.net.URL;


public interface Sound
{

	public void play();

	public void loop();


	public void stop();
}

class SoundAudioClip implements Sound
{
	private Ucigame ucigame;
	private AudioClip clip;

	SoundAudioClip(AudioClip _clip, Ucigame _u)
	{
		ucigame = _u;
		clip = _clip;
		ucigame.soundsPossiblyPlaying.add(this);
	}

	public void play() { clip.play(); }

	public void loop() { clip.loop(); }

	public void stop() { clip.stop(); }
}

class SoundMP3 implements Sound {
	MP3PlayerThread thread;
	String filename;
	private Ucigame ucigame;

	SoundMP3(String _f, Ucigame _u) {
		ucigame = _u;
		filename = _f;
		ucigame.soundsPossiblyPlaying.add(this);
	}

	public void play() {
		stop();
		thread = new MP3PlayerThread(filename, ucigame, false);
		thread.start();
	}
	public void stop() {
		if (thread != null)
			thread.stopPlay();
		thread = null;
	}
	public void loop() {
		stop();
		thread = new MP3PlayerThread(filename, ucigame, true);
		thread.start();
	}
}

class MP3PlayerThread extends Thread {
	Ucigame ucigame;
	Player p;
	boolean running = false;
	String filename;
	boolean loop = false;
	boolean looping = false;
	boolean stopRequested;

	public MP3PlayerThread(String _f, Ucigame _u, boolean _t) {
		loop = _t;
		ucigame = _u;
		filename = _f;
	}

	public void run() {
		if (loop)
		{
			looping = true;
			while (looping) {
				playOnce();
			}
		}
		else
		{
			playOnce();
		}
	}

	public void playOnce() {
		stopRequested = false;
		try {
			InputStream inputStream = null;
			if (ucigame.isApplet)
			{
				URL url = new URL(ucigame.getCodeBase(), filename);
				inputStream = url.openStream();
			}
			else
				inputStream = new FileInputStream(filename);
			p = new Player(inputStream);
			while (!stopRequested)
			{
				boolean moreFrames = p.play(10);  // play 10 "frames"
				if (!moreFrames)
					break;
			}
			inputStream.close();
			p.close();
		} catch (FileNotFoundException fnf) {
			Ucigame.logError("getSound(" + filename + ") failed [3a].");
		} catch (JavaLayerException e) {
			Ucigame.logError("Internal error #N2 on sound file " + filename);
		} catch (IOException e) {
			Ucigame.logError("Internal error #B7 on sound file " + filename);
		}
	}

	public synchronized void stopPlay() {
		looping = false;
		stopRequested = true;
	}
}
