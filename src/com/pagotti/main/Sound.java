package com.pagotti.main;

import java.applet.Applet;
import java.applet.AudioClip;

@SuppressWarnings("deprecation")
public class Sound {
	
	@SuppressWarnings("unused")
	private AudioClip clip;
	
	public static final Sound musicBackGround = new Sound("/music.wav");
	public static final Sound hurtEffect = new Sound("/hurt.wav");
	
	private Sound(String name) {
		try {
			this.clip = Applet.newAudioClip(Sound.class.getResource(name));
		} catch(Throwable e) {
			
		}
	}

	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.play();
				}
			}.start();
		} catch(Throwable e) {
			
		}
	}
	
	public void loop() {
		try {
			new Thread() {
				public void run() {
					clip.loop();
				}
			}.start();
		} catch(Throwable e) {
			
		}
	}
}
