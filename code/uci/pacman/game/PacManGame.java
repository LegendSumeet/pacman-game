package code.uci.pacman.game;

import java.io.IOException;

import code.uci.pacman.controllers.*;
import code.uci.pacman.gui.*;
import code.uci.pacman.objects.stationary.*;
import code.uci.pacman.multiplayer.*;
import ucigame.*;

public class PacManGame extends Ucigame {

	private static final long serialVersionUID = -917916728311505169L;
	private GameController control;
	private ScoreBoard scoreBoard;
	private TopScores topScores;
	private MainMenu mainMenu;
	private MultiplayerMenu multiMenu;
	private IntroPlayer introPlayer; // for playing the intro
	private CreditsScreen creditsScreen;
	private ScreenMode currentScene; // stores the current scene displayed
	public static String font = "Dialog.bold";
	public static int multiplayerType = 1;
	public static int gameType = 0;
	
	private static boolean quickStart = false;


	public static void main(String[] args)
	{
		String[] args2 = new String[1];
		args2[0] = "code.uci.pacman.game.PacManGame";
		System.out.println("Current User: " + System.getProperty("user.name") );
		Ucigame.main(args2);
	}


	public void setup() {
		generatePositions(false);
		// creates the window, sets the title, initialize
		initializeWindow();
		// starts the intro for the game
		showIntroScreen();
	}

	private void initializeWindow() {
		Ucigame.rootImagePath = "images/final/";
		// set global game control 
		control = GameController.setInstance(this);

		this.framerate(30);

		window.size(600, 650);

		window.title("Pac Man ");
	}
	
	private void showIntroScreen() {
		try
		{
			System.out.println("Game starting");
			// initialize screens
			introPlayer = new IntroPlayer();
			// show intro screen
			canvas.background(0, 0, 0);
			Thread.sleep(3000);
			showScene(ScreenMode.INTRO);
			if(!quickStart)
				showMenuScreen();
		}
		catch(InterruptedException e)
		{
			System.out.println("Intro thread interrupted before start!");
			e.printStackTrace();
		}
	}
	

	

	private void showMenuScreen() {
		// stop intro theme
		introPlayer.stopIntroTheme();
		// initialize menu
		mainMenu = new MainMenu();
		// show menu
		canvas.background(getImage("mainMenuBackGroundDim2.png"));
		mainMenu.startMenuTheme();
		showScene(ScreenMode.MENU);
	}


	public void showGameScreen() {
		// stop menu theme
		mainMenu.stopMenuTheme();
		introPlayer.stopIntroTheme();
		// show game
		canvas.background(0, 0, 0);
		control.startGame(); // start the game
		scoreBoard = new ScoreBoard();
		showScene(ScreenMode.GAME);
	}


	private void showMultiGameScreen() {

		multiMenu = new MultiplayerMenu();

		canvas.background(getImage("multiplayermenu.png"));

		showScene(ScreenMode.MULTIGAME);
	}
	

	public void showScoresScreen() {
		// initialize top scores
		SoundController.stopAllSounds();
		topScores = new TopScores();
		mainMenu.stopMenuTheme();		
		canvas.background(getImage( "topscores.png"));
		topScores.startTopScoresTheme();
		showScene(ScreenMode.SCORES);
	}
	

	public void showGameOverScreen() {
		boolean f = true;
		canvas.background(0, 0, 0);
		//stop all gameplay sounds
		SoundController.stopAllSounds();
		showScene(ScreenMode.GAMEOVER);
	}


	private void showMpwaitingScreen()
	{
		mainMenu.stopMenuTheme();
		introPlayer.stopIntroTheme();
		canvas.background(0, 0, 0);
		showScene(ScreenMode.MPWAITING);
	}
	
	private void startPacManServer()
	{
		try
		{
			new Server().start();
		}
		catch (Exception e){}
	}

	private void startPacManClient()
	{
		try
		{
			new Client().start();
		}
		catch (Exception e){}
		showMpwaitingScreen();
	}


	public void drawMpwaiting() {
		canvas.clear();
		canvas.font(PacManGame.font, PacManGame.BOLD, 40, 255, 255, 255);
		canvas.putText("WAITING FOR SERVER", 100, 300);
		canvas.font(PacManGame.font, PacManGame.BOLD, 20, 255, 255, 255);
		canvas.putText("PacMan is currently listening for open games", 80, 340);
	}

	private void generatePositions(boolean run) {
		try {
			if (run) {
				ItemGenerator.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void drawIntro() {		
		introPlayer.draw();
	}
	
	public void drawCredits() {
		if(creditsScreen.canClear())
		{
			canvas.clear();
		}
		creditsScreen.draw();
	}


	public void drawMenu() {
		canvas.clear();
		mainMenu.draw();
	}


	public void drawScores() {
		canvas.clear();
		topScores.draw();
	}


	public void drawGame() {
		canvas.clear();
		control.nextMove();
		control.drawState();
		scoreBoard.draw();
	}


	public void drawMultigame() {
		canvas.clear();
		multiMenu.draw();
	}
	

	public void drawGameover() {

		canvas.clear();
		canvas.font(PacManGame.font, PacManGame.BOLD, 40, 255, 255, 255);
		canvas.putText("GAME OVER", 180, 300);
		canvas.font(PacManGame.font, PacManGame.BOLD, 20, 255, 255, 255);
		canvas.putText("Press R to Try Again or T to see top scores", 100, 340);

	}


	public void startFruitTimer() {
		if( PacManGame.gameType == 1 )
		{
			startTimer("removeFruit", Fruit.SHOW_FRUIT_DURATION);
		}
	}
	

	public void pacManDeathTimer(){
		stopTimer("pacManDeath");
		if(isShowingScene(ScreenMode.GAME))
			control.pacManRevive();
	}


	public void removeFruitTimer() {
		if( PacManGame.gameType == 1 )
		{
			stopTimer("removeFruit");
			control.hideFruit();
		}
	}


	public void startScatterTimer() {
		restartTimer("unScatterGhosts", GhostController.SCATTERSECONDS);
	}
	
	public void startInitialWaitTimer(){
		restartTimer("initialWait", 5000);
	}
	
	public void initialWaitTimer(){
		stopTimer("initialWait");
		if(isShowingScene(ScreenMode.GAME))
			control.initialWaitOver();
	}


	public void unScatterGhostsTimer() {
		stopTimer("unScatterGhosts");
		if(isShowingScene(ScreenMode.GAME))
			control.unscatterGhosts();
	}


	public void onClickMenuStart() {
		if (isShowingScene(ScreenMode.INTRO)) {
			showMenuScreen();
		}
	}


	public void onClickSinglePlay() {
		if (isShowingScene(ScreenMode.MENU)) {
			PacManGame.gameType = 0;
			System.out.println("single player click");
			//startPacManServer();
			showGameScreen();
		}
	}


	public void onClickMultiPlay() {
		if (isShowingScene(ScreenMode.MENU)) {
			System.out.println("load multiplayer screen..");
			showMultiGameScreen();
		}
	}
	

	public void onClickPacManPlayer() {
		if (isShowingScene(ScreenMode.MULTIGAME)){
			PacManGame.gameType = 1;
			System.out.println("pac-man player game");
			startPacManServer();
			showGameScreen();
		}
	}
	

	public void onClickGhostPlayer() {
		if (isShowingScene(ScreenMode.MULTIGAME)){
			PacManGame.gameType = 2;
			System.out.println("ghost player game");
			startPacManClient();
		}
	}


	public void onClickTopScores() {
		if (isShowingScene(ScreenMode.MENU)) {
			System.out.println("topScore click");
			//mainMenu.stopMenuTheme();
			showScoresScreen();			
			// beginGame();
		}
	}


	public void onClickTopScoresMainMenu(){
		if(isShowingScene(ScreenMode.SCORES))
		{
			mainMenu.stopMenuTheme();
			topScores.stopTopScoresTheme();
			showMenuScreen();
		}
	}//multiMenu.hideButtons();
	
	public void onClickCreditsFromTopScores(){
		if(isShowingScene(ScreenMode.SCORES))
		{
			mainMenu.stopMenuTheme();
			topScores.stopTopScoresTheme();
			topScores.hideButtonsAndScores();
			topScores.writeScores();			

		}
	}


	

	public void onClickMultiPlayerBackToMainMenu(){
		if(isShowingScene(ScreenMode.MULTIGAME))
		{
			mainMenu.stopMenuTheme();
			multiMenu.hideMultiPlayerMenuButtons();
			showMenuScreen();
		}
	}


	public void onClickQuit() {
		if (isShowingScene(ScreenMode.MENU)) {

			if( PacManGame.gameType == 1 )
			{
				// server
				Server.send(PType.GAMEOVER);
			}
			else if(PacManGame.gameType==2)
			{
				Client.send(PType.LEAVE);
			}

			System.out.println("quit click");
			System.exit(0);
			// beginGame();
		}
	}


	public void onKeyPressIntro() {
		if (keyboard.isDown(keyboard.S) && isShowingScene(ScreenMode.INTRO)) {
			showMenuScreen();
		}
		if (keyboard.isDown(keyboard.SPACE) && isShowingScene(ScreenMode.INTRO)) {
			showMenuScreen();
		}
	}
	

	public void onKeyPressScores(){
		if(isShowingScene(ScreenMode.SCORES))
		{
			try
			{
				if(keyboard.isDown(keyboard.BACKSPACE))
				{
					topScores.removeFromName();
				}
				else
				{
					topScores.addToName(keyboard.getKeyChar());
				}
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
				System.err.println("Typing Name into Top Scores thread interrupted!");
			}
		}
	}
	
	public void onMousePressed() {
		System.out.println("Mouse Clicked: " + mouse.x() + "," + mouse.y());
	}


	public void onKeyPressGame() {

		if( PacManGame.gameType == 1 )
		{
			if (keyboard.isDown(keyboard.UP, keyboard.W))
			{
				Server.send(Direction.UP);
				control.setPacManDirection(Direction.UP);
			}
			else if (keyboard.isDown(keyboard.DOWN, keyboard.S))
			{
				Server.send(Direction.DOWN);
				control.setPacManDirection(Direction.DOWN);
			}
			else if (keyboard.isDown(keyboard.LEFT, keyboard.A))
			{
				Server.send(Direction.LEFT);
				control.setPacManDirection(Direction.LEFT);
			}
			else if (keyboard.isDown(keyboard.RIGHT, keyboard.D))
			{
				Server.send(Direction.RIGHT);
				control.setPacManDirection(Direction.RIGHT);
			}
		}
		else if(PacManGame.gameType==2)
		{
			// FOR MUTLIPLAYER
			String gname = capitalize(Client.getGhostType().name());

			if (keyboard.isDown(keyboard.UP, keyboard.W))
			{
				Client.send(Direction.UP);
				GameState.getInstance().getGhosts().getObjectAt(gname).setDirection(Direction.UP);
			}
			else if (keyboard.isDown(keyboard.DOWN, keyboard.S))
			{
				Client.send(Direction.DOWN);
				GameState.getInstance().getGhosts().getObjectAt(gname).setDirection(Direction.DOWN);
			}
			else if (keyboard.isDown(keyboard.LEFT, keyboard.A))
			{
				Client.send(Direction.LEFT);
				GameState.getInstance().getGhosts().getObjectAt(gname).setDirection(Direction.LEFT);
			}
			else if (keyboard.isDown(keyboard.RIGHT, keyboard.D))
			{
				Client.send(Direction.RIGHT);
				GameState.getInstance().getGhosts().getObjectAt(gname).setDirection(Direction.RIGHT);
			}

		}
		else
		{
			// this is for single player, do not send any keystrokes to the server
			if (keyboard.isDown(keyboard.UP, keyboard.W))
			{
				control.setPacManDirection(Direction.UP);
			}
			else if (keyboard.isDown(keyboard.DOWN, keyboard.S))
			{
				control.setPacManDirection(Direction.DOWN);
			}
			else if (keyboard.isDown(keyboard.LEFT, keyboard.A))
			{
				control.setPacManDirection(Direction.LEFT);
			}
			else if (keyboard.isDown(keyboard.RIGHT, keyboard.D))
			{
				control.setPacManDirection(Direction.RIGHT);
			}
		}
		
	}


	public void onKeyPressGameover() {
		if (keyboard.isDown(keyboard.R) && isShowingScene(ScreenMode.GAMEOVER)) 
		{
			showGameScreen();
		}
		
		else if (keyboard.isDown(keyboard.T) && isShowingScene(ScreenMode.GAMEOVER)) 
		{
			//System.out.println("Pressed T");
			showScoresScreen();
			//showMenuScreen();
		}
	}


	public Sprite makeSpriteFromPath(String filename) {
		return makeSprite(getImage(filename));
	}


	public void showScene(ScreenMode scene) {
		this.currentScene = scene;
		String sceneName = capitalize(scene.toString().toLowerCase());
		super.startScene(sceneName);
	}


	public boolean isShowingScene(ScreenMode scene) {
		return currentScene.equals(scene);
	}

	private String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}
