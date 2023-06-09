package code.uci.pacman.objects.controllable;

import code.uci.pacman.controllers.WallController;
import code.uci.pacman.game.Direction;
import code.uci.pacman.game.GameState;
import code.uci.pacman.objects.ControllableObject;


public class PacMan extends ControllableObject {

	private static final String pacImagePath = "pac-chomp.png";
	private static final int PACWIDTH = 22;
	private static final int PACHEIGHT = 22;
	private static final int PACFRAMERATE = 15;
	private static final int PACSPEED = 7;
	private double angle; // 0,90,180,270


	public PacMan(int x, int y) {
		super(pacImagePath, new int[] { 0, 0 }, PACWIDTH, PACHEIGHT, PACFRAMERATE, x, y);
		super.addFramesForAnimation("chomp", pacImagePath, 0, 0, 22, 0, 45, 0, 67, 0);
		super.addFramesForAnimation("chomp-left", "pac-chomp-left.png", 0, 0, 22, 0, 45, 0, 68, 0);
		super.addFramesForAnimation("death", "pac-death.png", 0, 0, 26, 0, 51, 0, 74, 0, 100, 0, 125, 0, 152, 0, 175, 0);
		// add
		// death
		// animation
		// frames
		super.speed = PACSPEED;
		angle = 0;
	}


	public void eaten() {
		control.pacManEaten(this);
	}


	public void draw() {
		super.rotate(angle);
		super.draw();
	}


	protected void spriteForDirection(Direction d) {
		if (getAnimationMode() != "death" && getAnimationMode() != "default" ) {
			if (d == Direction.UP) {
				setAnimationMode("chomp-left");
				angle = 90;
			} else if (d == Direction.DOWN) {
				setAnimationMode("chomp");
				angle = 90;
			} else if (d == Direction.LEFT) {
				angle = 0;
				setAnimationMode("chomp-left");
			} else if (d == Direction.RIGHT) {
				setAnimationMode("chomp");
				angle = 0;
			}
		}
	}


	public boolean moveIsAllowed(Direction d) {
		
		int hCheck = 13;
		int wCheck = 13;
	
		WallController walls = GameState.getInstance().getWalls();
		if (d == Direction.UP && walls.willCollideAtPos(this, 0, -hCheck))
			return true;
		if (d == Direction.DOWN && walls.willCollideAtPos(this, 0, wCheck))
			return true;
		if (d == Direction.LEFT && walls.willCollideAtPos(this, -hCheck, 0))
			return true;
		if (d == Direction.RIGHT && walls.willCollideAtPos(this, wCheck, 0))
			return true;
		else
			return false;
	}
}
