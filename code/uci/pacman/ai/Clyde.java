package code.uci.pacman.ai;

import java.util.Random;

import code.uci.pacman.game.Direction;
import code.uci.pacman.game.GameState;
import code.uci.pacman.objects.controllable.Ghost;
import code.uci.pacman.objects.controllable.PacMan;


public class Clyde extends Ghost{

	private final int COUNTDOWN = 150;
	private int cageTimer = 0;
	private boolean directionUP = false;
	private final static int SPEED = 5;
	private int mod = 7;
	private int deathTimer = 40;
	private int minDistance = 100;
	private int pacLives = 5;
	private Random numGen = new Random();
	private boolean moveOpposite = false;
	private int oppCounter = 0;

	public Clyde(int x, int y, boolean isPlayer) {
		super("pac-man ghost images/clydeFINAL.png", x, y, SPEED, isPlayer);
	}



	protected Direction getAIMove()
	{


		int curX = this.x();
		int curY = this.y();

		GameState state = GameState.getInstance();
		int currentLives = state.getLives();
		if(currentLives != pacLives){
			pacLives = currentLives;
			cageTimer = COUNTDOWN;
			isAttacking = false;
			countdownTimer = SCATTER;
		}
		if(cageTimer > 0){
			if(cageTimer%mod==0){
				if(directionUP){
					curDirection = Direction.UP;
				}
				else{
					curDirection = Direction.DOWN;
				}
				directionUP = !directionUP;
			}
			cageTimer --;
			if(cageTimer <= 0){
				lastDirection = Direction.LEFT;
				this.position(getInitialOutOfCagePos());
			}
		} else {
			if ((curY > 249 && curY <= 310) && (curX >= 230 && curX <= 370)) {
				cageTimer = deathTimer;
			}
			PacMan pm = state.getPacMan();
			int targetX = 250, targetY = 350;
			if(this.isScattered()){
				targetX = 558 - pm.x();
				targetY = 551 - pm.y();
			}  else if(!isAttacking){
				targetX = 558 - pm.x();
				targetY = 551 - pm.y();
				countdownTimer --;
			}else {
				targetX = pm.x();
				targetY = pm.y();
				if (getDistanceToPacman(curX, curY, targetX, targetY) > minDistance){
					try{
						curDirection = getPacmanDirection(pm.xspeed(), pm.yspeed());
					}
					catch(NullPointerException NPE){
						curDirection = lastDirection;
					}
					if (moveOpposite) {
						if (oppCounter++ == 50) {
							moveOpposite = false;
							oppCounter = 0;
						}
						targetX = 558 - targetX;
						targetY = 551 - targetY;
					} else {
						if (numGen.nextInt(40) == 1) { // 1 in 40 chance
							moveOpposite = true;
						}
						if(curDirection == Direction.UP){
							targetY -= minDistance/2;
						}
						else if(curDirection == Direction.DOWN){
							targetY += minDistance/2;
						}
						else if(curDirection == Direction.LEFT){
							targetX -= minDistance/2;
						}
						else{
							targetX += minDistance/2;
						}
					}
				}
				countdownTimer --;
			}			
			if(countdownTimer <= 0){
				flipAttack();
			}
			tryMove(curX, curY, targetX, targetY);

		}
		lastDirection = curDirection;
		return curDirection;

	}

	private double getDistanceToPacman(int Gx, int Gy, int Px, int Py){
		double distance = 0;
		distance = Math.sqrt(Math.pow((Px - Gx), 2) + Math.pow((Py - Gy), 2));
		return distance;
	}

	private Direction getPacmanDirection(double XSpeed, double YSpeed){
		Direction direction = Direction.LEFT;
		if(XSpeed == 0 ){
			if(YSpeed > 0 ){
				direction = Direction.DOWN;
			}
			else if(YSpeed < 0 ){
				direction = Direction.UP;
			}
			else{
				direction = null;
			}
		}
		else{
			if(XSpeed > 0 ){
				direction = Direction.RIGHT;
			}
			else if(XSpeed < 0 ){
				direction = Direction.LEFT;;
			}
			else{
				direction = null;
			}
		}
		return direction;
	}

}
