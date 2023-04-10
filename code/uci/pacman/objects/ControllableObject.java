package code.uci.pacman.objects;

import java.awt.Point;
import code.uci.pacman.controllers.*;
import code.uci.pacman.game.Direction;
import ucigame.*;


public abstract class ControllableObject extends Sprite implements Eatable {
	protected double speed;
	protected GameController control;
	protected Direction currentDirection;

	public ControllableObject(String imgPath, int[] frames, int width, int height, int framerate, int x, int y) {
		super(width, height);
		position(x, y);
		addFrames(imgPath, frames);
		framerate(framerate);
		control = GameController.getInstance();
	}


	public boolean collidedWith(Sprite a) {
		super.checkIfCollidesWith(a);
		return super.collided();
	}


	public abstract void eaten();

	

	public void step(Direction d) {
		if (moveIsAllowed(d))
			currentDirection = d;

		spriteForDirection(currentDirection);

		if (currentDirection == Direction.UP) {
			motion(0, 0 - speed);
		} else if (currentDirection == Direction.DOWN) {
			motion(0, speed);
		} else if (currentDirection == Direction.LEFT) {
			motion(0 - speed, 0);
		} else if (currentDirection == Direction.RIGHT) {
			motion(speed, 0);
		}
	}


	public void adjustSpeed(int speedAdjust) {
		this.speed += speedAdjust;
	}


	public void position(Point p) {
		super.position(p.x, p.y);
	}
	


	protected abstract void spriteForDirection(Direction d);


	public abstract boolean moveIsAllowed(Direction d);

}
