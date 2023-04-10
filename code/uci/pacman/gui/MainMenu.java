package code.uci.pacman.gui;

import code.uci.pacman.controllers.GameController;
import ucigame.Sound;
import ucigame.Sprite;


public class MainMenu{

	private Sprite singlePlayerButton;
	private Sprite multiplayerButton;
	private Sprite topScores;
	private Sprite quit;
	private int canvasWidth = 600;
	private int canvasHeight = 650;
	private Sound menuTheme;
	private int buttonStartHeight = 120;
	private int buttonHeight = 83;
	

	
	public MainMenu() {
		//single player button creation
		menuTheme = GameController.getInstance().getPacInstance().getSound("sounds/final/MainMenu.mp3");
		
		singlePlayerButton = GameController.getInstance().getPacInstance().makeButton("SinglePlay",GameController.getInstance().getPacInstance().getImage("singleplayerButton.png"),
                249, 76);
		singlePlayerButton.position(canvasWidth/2 - singlePlayerButton.width()/2, buttonStartHeight);
				
		//multiplayer button creation
		multiplayerButton = GameController.getInstance().getPacInstance().makeButton("MultiPlay",GameController.getInstance().getPacInstance().getImage("multiplayerButton.png"),
                249, 76);
		multiplayerButton.position(canvasWidth/2 - multiplayerButton.width()/2,	buttonStartHeight + buttonHeight);
		
		//topScore button creation
		topScores = GameController.getInstance().getPacInstance().makeButton("TopScores",GameController.getInstance().getPacInstance().getImage("topScoresButton.png"),
                249, 76);
		topScores.position(canvasWidth/2 - singlePlayerButton.width()/2, buttonStartHeight + buttonHeight*2);
		
		//quite button creation
		quit = GameController.getInstance().getPacInstance().makeButton("Quit",GameController.getInstance().getPacInstance().getImage("quitButton.png"),
                249, 76);
		quit.position(canvasWidth/2 - singlePlayerButton.width()/2, buttonStartHeight + buttonHeight*3);
		
		
	}


	public void draw(){
		singlePlayerButton.draw();
		multiplayerButton.draw();
		topScores.draw();
		quit.draw();
	}
	

	public void startMenuTheme()
	{
		menuTheme.loop();		
	}
	

	public void stopMenuTheme()
	{
		menuTheme.stop();
	}
		
}
