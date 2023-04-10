package code.uci.pacman.objects.controllable;


import java.util.Random;

import code.uci.pacman.ai.AI;
import code.uci.pacman.controllers.WallController;
import code.uci.pacman.game.*;
import code.uci.pacman.objects.ControllableObject;
import java.awt.Point;

import code.uci.pacman.multiplayer.*;




public abstract class Ghost extends ControllableObject implements AI {

	private static final int GHOST_WIDTH = 22;
	private static final int GHOST_HEIGHT = 22;
	public static final int GHOST_FRAMERATE = 5;
	
	private static final int CAGE_POS = 250;
	private int startX;
	private int startY;

	protected Direction lastDirection = Direction.LEFT;
	protected Direction curDirection;
	
	protected boolean isPlayer = false;
	protected final int ATTACK = 280;
	protected final int SCATTER = 100;
	protected int countdownTimer = 0;
	protected boolean isAttacking = false;
	


	public Ghost(String imgPath, int x, int y, int speed, boolean ip) {
		super(imgPath, new int[] {0,0}, GHOST_WIDTH, GHOST_HEIGHT, GHOST_FRAMERATE, x, y);
		super.addFramesForAnimation("scatter", "ghost_scatter.png", 0, 0, 22,0);
		super.speed = speed;
		startX = x;
		startY = y;
		scoreValue = 200;
		// DO NOT SET isPlayer anywhere in this constructor
	}


	private int scoreValue;
	private boolean scatter;
	



	public void returnAI()
	{
		isPlayer = false;
	}


	public void setDirection(Direction dir)
	{
		isPlayer = true;
		lastDirection = curDirection;
		curDirection = dir;
	}


	public void eaten(){
		control.ghostEaten(this);
	}
	

	public boolean isScattered() {
		return scatter;
	}
	

	public void scatter(){
		scatter = true;
		if( PacManGame.gameType==1)
		{
			Server.send(PType.DGHOST);
		}
		super.setAnimationMode("scatter");
	}
	

	public void unScatter() {
		scatter = false;
		if( PacManGame.gameType==1)
		{
			Server.send(PType.AGHOST);
		}

		setAnimationMode("default"); //this will tell the ghost to switch back to his regular animations
		//see above in scatter for explanation or just ask me --> nathan
	}

	public void respawnInCage() {
		this.position(startX,startY);
		this.unScatter();
	}
	

	public boolean isPlayer(){
		return isPlayer;
	}


	public int getValue() {
		return scoreValue;
	}


	@Override
	//this is for changing the sprite based on direction
	protected void spriteForDirection(Direction d) {
		// TODO Auto-generated method stub
		
	}


	protected abstract Direction getAIMove();
	
	

	public Direction getMove()
	{
		if(isPlayer)
		{
			int curX = this.x();
			int curY = this.y();
			if ((curY > 215 && curY <= 250) && (curX >= 250 && curX <= 325))
			{
				this.position(this.x(), 205);
				lastDirection = Direction.LEFT;
				curDirection = Direction.UP;
			}
			return curDirection;
		}
		else
		{
			if(PacManGame.gameType == 2)
			{
				return curDirection;
			}
			else
			{
				//return curDirection;
				return getAIMove();
			}
		}
	}


	public boolean moveIsAllowed(Direction d) 
	{			
		int hCheck = 8;
		int wCheck = 6;
	
		WallController walls = GameState.getInstance().getWalls();
		if (d == Direction.UP && walls.willCollideAtPos(this, 0, -hCheck))
			return true;
		if (d == Direction.DOWN && walls.willCollideAtPos(this, 0, hCheck ))
			return true;
		if (d == Direction.LEFT && walls.willCollideAtPos(this, -wCheck, 0))
			return true;
		if (d == Direction.RIGHT && walls.willCollideAtPos(this, wCheck, 0))
			return true;
		else
			return false;
	}
	
	public Point getInitialOutOfCagePos()
	{
		if(GameState.getInstance().getLevel() == 1)
			return new Point(this.x(),205);
		else if(GameState.getInstance().getLevel() == 2)
			return new Point(this.x(),185);
		else
			return new Point(this.x(),210);
	}

	protected void flipAttack(){
		if(isAttacking){
			countdownTimer = SCATTER;
		} else{
			countdownTimer = ATTACK;
		}
		isAttacking = !isAttacking;
	}
	
	protected void tryMove(int curX, int curY, int targetX, int targetY){
		int horizontalDifference = curX - targetX;
		int verticalDifference = curY - targetY;
		Direction preferredHorizontal = horizontalDifference > 0 ? Direction.LEFT : Direction.RIGHT;
		Direction preferredVertical = verticalDifference > 0 ? Direction.UP : Direction.DOWN;
		boolean verticalMoreImportant = Math.abs(verticalDifference) > Math.abs(horizontalDifference);
		if (verticalMoreImportant)
			curDirection = preferredVertical;
		else
			curDirection = preferredHorizontal;
		if (!this.moveIsAllowed(curDirection)) {
			if (verticalMoreImportant) {
				if (lastDirection == Direction.LEFT || lastDirection == Direction.RIGHT) {
					curDirection = lastDirection;
					if (!this.moveIsAllowed(curDirection))
						curDirection = curDirection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
				} else {
					curDirection = preferredHorizontal;
					if (!this.moveIsAllowed(curDirection)) {
						curDirection = preferredHorizontal == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
						if (!this.moveIsAllowed(curDirection))
							curDirection = preferredVertical == Direction.UP ? Direction.DOWN : Direction.UP;
					}
				}
			} else {
				if (lastDirection == Direction.UP || lastDirection == Direction.DOWN) {
					curDirection = lastDirection;
					if (!this.moveIsAllowed(curDirection))
						curDirection = curDirection == Direction.UP ? Direction.DOWN : Direction.UP;
				} else {
					curDirection = preferredVertical;
					if (!this.moveIsAllowed(curDirection)) {
						curDirection = preferredVertical == Direction.UP ? Direction.DOWN : Direction.UP;
						if (!this.moveIsAllowed(curDirection))
							curDirection = preferredHorizontal == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
					}
				}
			}
		}
	}
}
