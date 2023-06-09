package code.uci.pacman.gui;

import code.uci.pacman.controllers.GameController;
import code.uci.pacman.game.GameState;
import code.uci.pacman.game.PacManGame;
import code.uci.pacman.objects.stationary.Fruit;
import ucigame.*;
import java.util.*;



public class ScoreBoard{

	private int remainingLives = 0;
	private Sprite score;
	private Sprite level;
	private Sprite lives; 
	private Fruit fruit;
	private int scorePosX = 10;
	private int scorePosY = 610;
	private int levelPosX = 235;
	private int levelPosY = 608;
	private int livesPosX = 355;
	private int livesPosY = 610;
	private int lifePosX = 410;
	private int lifePosY = 610;
	private int lifePosAdj = 30;
	private int fruitPosX = 550;
	private int fruitPosY = 610;
	ArrayList<Sprite> lifeList = new ArrayList<Sprite>();

	public ScoreBoard() {
		//create score sprite
		score = GameController.getInstance().getPacInstance().makeSpriteFromPath("score.png");
		score.position(scorePosX, scorePosY);
		score.font(PacManGame.font, PacManGame.BOLD, 24, 255, 255, 255);
		
		//create levels sprite
		level = GameController.getInstance().getPacInstance().makeSprite(GameController.getInstance().getPacInstance().getImage("levels_LVL.png", 255,0,0));
		level.position(levelPosX, levelPosY);
		level.font(PacManGame.font, PacManGame.BOLD, 26, 255, 255, 255);
		
		//create lives sprite
		lives = GameController.getInstance().getPacInstance().makeSpriteFromPath("lives.png");
		lives.position(livesPosX, livesPosY);
		
		//set remainLives
		remainingLives = GameState.getInstance().getLives();
		for(int x = 1; x <= remainingLives; x++){
			lifeList.add(GameController.getInstance().getPacInstance().makeSpriteFromPath("life.png"));
		}
		
		// set fruit
		String fruitImagePath = GameState.getInstance().getFruit().getGraphicPath();
		fruit = new Fruit(fruitImagePath, fruitPosX, fruitPosY, 0);
	}
	

	public void draw(){
		

		score.putText(GameState.getInstance().getScore() + "", 110, 24);
		score.draw();
		

		level.putText(GameState.getInstance().getLevel(), 56, 27);
		level.draw();
		
		

		lives.draw();
		remainingLives = GameState.getInstance().getLives();
		for(int x = 1; x <= remainingLives; x++){
			lifeList.get(x-1).position(lifePosX + x*lifePosAdj, lifePosY);
			lifeList.get(x-1).draw();
		}
		

		String fruitImagePath = GameState.getInstance().getFruit().getGraphicPath();
		fruit = new Fruit(fruitImagePath, fruitPosX, fruitPosY, 0);
		fruit.draw();
	}
}
