package code.uci.pacman.controllers;

import ucigame.Sound;


public class SoundController {
	
	private static Sound ambient = makeSound("gs_siren_soft2.mp3");
	private static Sound pellet = makeSound("gs_chomp.mp3");
	private static Sound pill = makeSound("gs_chomp.mp3");
	private static Sound fruit = makeSound("gs_eatfruit.mp3");
	private static Sound ghost = makeSound("gs_eatghost.mp3");
	private static Sound pacman = makeSound("gs_pacmandies.mp3");
	private static Sound start = makeSound("gs_start.mp3");
	private static Sound fever = makeSound("fever_clip.mp3");
	
	

	public static void pelletEaten(){
		pellet.play();
	}
	

	public static void pillEaten(){
		pill.play();
	}
	

	public static void fruitEaten(){
		fruit.play();
	}
	

	public static void ghostEaten(){
		ghost.play();
	}
	

	public static void pacmanEaten(){
		feverSwitch(false); //stop fever
		pacman.play();
	}
	

	public static void gameStarted(){
		start.play();
	}
	

	public static void feverSwitch(boolean play){
		if (play) 
		{ 
			//stopAllSounds(); // see Google Docs as to why this was changed. 
			fever.play();
		} 
		else 
		{ 
			fever.stop();
			//startAmbient(); // see Google Docs as to why this was changed.
		}
	}
	

	public static void startAmbient(){
		ambient.loop();
	}
	
	public static void stopAllSounds(){
		pellet.stop();
		pill.stop();
		ambient.stop();
		fever.stop();
		fruit.stop();
		ghost.stop();
		pacman.stop();
		start.stop();
	}
	
	private static Sound makeSound(String file){
		return GameController.getInstance().getPacInstance().getSound("sounds/final/"+file);
	}

}
