package code.uci.pacman.gui;
import code.uci.pacman.controllers.GameController;
import ucigame.*;


public class MultiplayerMenu{

	private Sprite hostPacMan;
	private Sprite joinGhost;
	private Sprite backToMainMenu;
	private int canvasWidth = 600;
	private int canvasHeight = 650;
	private Sound multiTheme;
	private int buttonStartHeight = 228;
	
	

	public MultiplayerMenu() {
		
		//make host pac-man button
		hostPacMan = GameController.getInstance().getPacInstance().makeButton("PacManPlayer",GameController.getInstance().getPacInstance().getImage("pacbutton.png"),
                281, 250);
		//set pac-man button position
		hostPacMan.position(canvasWidth/2 - (hostPacMan.width() + 7), buttonStartHeight + 1);
		
		//make join ghost button
		joinGhost = GameController.getInstance().getPacInstance().makeButton("GhostPlayer",GameController.getInstance().getPacInstance().getImage("ghostbutton.png"),
                281, 250);
		//set ghost button position
		joinGhost.position(canvasWidth/2 + 7,	buttonStartHeight + 1);
		
		//make MainMenu button
		backToMainMenu = GameController.getInstance().getPacInstance().makeButton("MultiPlayerBackToMainMenu",GameController.getInstance().getPacInstance().getImage("mainmenubutton.png"),
                249, 76);
		//set backToMenu button position
		backToMainMenu.position(canvasWidth/2 - backToMainMenu.width()/2, canvasHeight - backToMainMenu.width()/2);
		
	}


	public void draw(){
		hostPacMan.draw();
		joinGhost.draw();
		backToMainMenu.draw();
	}
	

	public void hideMultiPlayerMenuButtons()
	{
		hostPacMan.hide();
		joinGhost.hide();
		//backToMainMenu.draw();
	}
}
