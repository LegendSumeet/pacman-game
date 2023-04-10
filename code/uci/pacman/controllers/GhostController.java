package code.uci.pacman.controllers;

import code.uci.pacman.ai.*;
import code.uci.pacman.controllers.utilities.ActorController;
import code.uci.pacman.game.Direction;
import code.uci.pacman.objects.controllable.Ghost;
import code.uci.pacman.objects.controllable.PacMan;
import code.uci.pacman.game.GameState;
import java.awt.Point;


public class GhostController extends ActorController<String, Ghost> {

	public static final double SCATTERSECONDS = 8000;

	public GhostController() {
		this.constructActors();
	}


	protected void constructActors() {
		addObject("Blinky", new Blinky(getStartPos(1).x, getStartPos(1).y, false));
		addObject("Pinky", new Pinky(getStartPos(2).x, getStartPos(2).y, false));
		addObject("Inky", new Inky(getStartPos(3).x, getStartPos(3).y, false));
		addObject("Clyde", new Clyde(getStartPos(4).x, getStartPos(4).y, false));

	}



	public boolean haveCollidedWithPacMan(PacMan p) {
		return super.getCollidedWith(p).size() > 0;
	}

	public void moveAIGhosts() {
		for (Ghost g : getObjects()) {
			// DO NOT DO if( !g.isPlayer) - you will mess up the game
			Direction nextMove = g.getMove();
			g.step(nextMove);
			g.move();
		}
	}


	public void respawn() {
		unscatter();
		getObjectAt("Blinky").position(getStartPos(1));
		getObjectAt("Pinky").position(getStartPos(2));
		getObjectAt("Inky").position(getStartPos(3));
		getObjectAt("Clyde").position(getStartPos(4));
	}


	public void scatter() {
		for (Ghost g : getObjects()) {
			g.scatter();
		}
	}


	public void stopWallCollisions(WallController walls) {
		walls.stopCollisions(super.getObjects());
	}


	public void unscatter() {
		for (Ghost g : getObjects()) {
			g.unScatter();
		}
	}
	
	private Point getStartPos(int ghost)
	{
		if(GameState.getInstance().getLevel() < 3)
		{
			if(ghost == 1)
				return new Point(250,250);
			else if(ghost == 2)
				return new Point(275,250);
			else if(ghost == 3)
				return new Point (300,250);
			else
				return new Point(325,250);
		}
		else
		{
			if(ghost == 1)
				return new Point(265,310);
			else if(ghost == 2)
				return new Point(275,270);
			else if(ghost == 3)
				return new Point (300,310);
			else
				return new Point(310,270);
		}
	}
}
