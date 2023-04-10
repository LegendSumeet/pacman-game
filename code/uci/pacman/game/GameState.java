package code.uci.pacman.game;

import code.uci.pacman.controllers.*;
import code.uci.pacman.objects.controllable.*;
import code.uci.pacman.objects.stationary.*;
import java.awt.Point;


public class GameState {

	private static final int INITIAL_LIVES = 3;
	private static GameState gameInstance;
	private PacMan pacMan;
	private int lives;
	private GhostController ghosts;
	private PillController pills;
	private PowerPelletController pellets;
	private WallController walls;
	private Fruit bonusItem;
	private int score;
	private int level;
	
	// Set the starting points of PacMan for each level.
	private static final Point lev1_start = new Point(290,440);
	private static final Point lev2_start = new Point(290,445);
	private static final Point lev3_start = new Point(290,445);

	public static GameState getInstance() {
		return gameInstance;
	}

	public static void setInstance(GameState gameState) {
		gameInstance = gameState;
	}

	public void initialize() {
		score = 0;
		lives = INITIAL_LIVES;
		level = 1;
		setupLevel();
	}


	public void setupLevel() {
		pacMan = getPacManForLevel(level);
		pills = new PillController();
		walls = new WallController();
		pellets = new PowerPelletController();
		ghosts = new GhostController();
		bonusItem = new Fruit(300, 330, 100);
		SoundController.stopAllSounds(); //stop all sound
	}
	
	public Point getPacManStartPosForLevel(int l)
	{
		if(l == 1)
			return lev1_start;
		else if(l == 2)
			return lev2_start;
		else if(l == 3)
			return lev3_start;
		else
			return lev1_start;
	}
	
	private PacMan getPacManForLevel(int l)
	{
		return new PacMan((int)getPacManStartPosForLevel(l).getX(),(int)getPacManStartPosForLevel(l).getY());
	}


	public void draw() {
		walls.drawObjects();
		pacMan.draw();
		pills.drawObjects();
		ghosts.drawObjects();
		pellets.drawObjects();
		bonusItem.draw();
	}


	public boolean stageHasBeenCleared() {
		return this.getPills().getPillCount() == 0;
	}


	public void addToScore(int score) {
		this.score += score;
	}


	public int getScore() {
		return score;
	}
	

	public void setScore(int sc){
		score = sc;
	}


	public void lifeLost() {
		lives--;
	}


	public int getLives() {
		return lives;
	}

	public void setLives(int lv)
	{
		lives = lv;
	}


	public GhostController getGhosts() {
		return ghosts;
	}


	public PillController getPills() {
		return pills;
	}


	public PowerPelletController getPellets() {
		return pellets;
	}


	public Fruit getFruit() {
		return bonusItem;
	}

	public void setFruit(Fruit fruit){
		this.bonusItem = fruit;
	}
	

	public PacMan getPacMan() {
		return pacMan;
	}


	public WallController getWalls() {
		return walls;
	}


	public void nextStage() {
		level++;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int lvl)
	{
		level = lvl;
	}
}
