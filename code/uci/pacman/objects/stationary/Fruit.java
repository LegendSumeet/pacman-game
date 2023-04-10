package code.uci.pacman.objects.stationary;

import java.util.ArrayList;
import java.util.Random;

import code.uci.pacman.objects.Eatable;
import code.uci.pacman.objects.StationaryObject;
import code.uci.pacman.game.*;
import code.uci.pacman.multiplayer.*;


public class Fruit extends StationaryObject implements Eatable {

	public static double SHOW_FRUIT_DURATION = 8000;

	private int score;
	private int fruitEaten;


	public Fruit(int x, int y, int initialScore) {
		this(randomFruitPath(), x, y, initialScore);
		super.hide();
	}

	public Fruit(String fruitImagePath, int x, int y, int initialScore) {
		super(fruitImagePath, x, y);
		score = initialScore;
		fruitEaten = 0;
	}

	private static String randomFruitPath() {
		ArrayList<String> fruits = new ArrayList<String>();
		fruits.add("cherry.png");
		fruits.add("lemon.png");
		fruits.add("peach.png");
		Random r = new Random();
		return fruits.get(r.nextInt(fruits.size()));
	}


	public void showWithTimer() {
		control.getPacInstance().startFruitTimer();
		if( PacManGame.gameType == 1)
		{
			Server.send(PType.AFRUIT, x(), y() );
		}
		super.show();
	}


	public void eaten() {
		fruitEaten++;
		score += 100; // increase score when eaten
		if(PacManGame.gameType==1)
		{
			Server.send( PType.DFRUIT, x(), y() );
		}
		control.fruitEaten(this);
	}


	public int getValue() {
		return score;
	}


	public int getFruitEaten() {
		return fruitEaten;
	}


	public void setValue(int scoreValue) {
		this.score = scoreValue;
	}

}
