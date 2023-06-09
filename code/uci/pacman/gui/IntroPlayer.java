package code.uci.pacman.gui;

import ucigame.*;
import java.util.ArrayList;
import code.uci.pacman.game.PacManGame;
import code.uci.pacman.controllers.GameController;
//import java.io.FileNotFoundException;
//import java.io.IOException;



public class IntroPlayer{	
	
	private ArrayList<String> frames;
	private Sound introSound;
	private int drawCounter;
	private Sprite menuButton; 
		

	public IntroPlayer() {
		drawCounter = 1;
		introSound = GameController.getInstance().getPacInstance().getSound("sounds/final/IntroTheme.mp3");
		frames = new ArrayList<String>();
		
		menuButton = GameController.getInstance().getPacInstance().makeButton("MenuStart",GameController.getInstance().getPacInstance().getImage("startbutton.png"),
                249, 76);
		menuButton.position(175, 450);
		
		for (int currentImage = 1; currentImage <= 30; currentImage++)
		{
			String workingString = "0";
			if (currentImage < 10)
			{
				workingString += ("0" + currentImage);
			}
			if (currentImage >= 10)
			{
				workingString += currentImage;
			}
			workingString += ".png";
			frames.add("intro/" + workingString);
		}


	}

	public void draw(){

			if (drawCounter < 30)
			{
				try
				{
					String frameLocation = frames.get(drawCounter);
					Sprite currentFrame =  GameController.getInstance().getPacInstance().makeSpriteFromPath(frameLocation);
					Thread.sleep(1520);	//orig 1525  
					currentFrame.draw();
					drawCounter ++;
				}
				catch (InterruptedException e)
				{
					System.out.println("Intro Thread was Interrupted!");
					e.printStackTrace();
				}
			}
			if (drawCounter == 30)
			{
				menuButton.draw();		
			}
	}
	

	public void playIntroTheme()
	{
		introSound.play();
	}
	

	public void stopIntroTheme()
	{
		introSound.stop();
	}
}
