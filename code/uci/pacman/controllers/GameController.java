package code.uci.pacman.controllers;

import java.awt.Point;
import java.util.Collection;

import code.uci.pacman.game.Direction;
import code.uci.pacman.game.GameState;
import code.uci.pacman.game.PacManGame;
import code.uci.pacman.objects.controllable.Ghost;
import code.uci.pacman.objects.controllable.PacMan;
import code.uci.pacman.objects.stationary.*;


public class GameController {

	private static GameController gControl;
	private boolean performNextMove;
	private int numberOfEatenGhostsInScatter;
	private boolean scatterMode;

	public static GameController getInstance() {
		return gControl;
	}

	public static GameController setInstance(PacManGame pacManGame) {
		gControl = new GameController(pacManGame);
		return gControl;
	}



	private GameState state;
	private PacManGame game;



	private GameController(PacManGame pacManGame) {
		GameState.setInstance(new GameState());
		state = GameState.getInstance();
		this.game = pacManGame;
		scatterMode = false;
	}


	public void drawState() {
		state.draw();
	}


	public void fruitEaten(Fruit fruit) {
		SoundController.fruitEaten();
		state.addToScore(fruit.getValue()); // update score
		fruit.hide();
	}


	public void ghostEaten(Ghost ghost) {
		numberOfEatenGhostsInScatter++;
		SoundController.ghostEaten();
		ghost.respawnInCage(); // restart in cage
		state.addToScore(ghost.getValue()*numberOfEatenGhostsInScatter);
	}


	public void hideFruit() {
		state.getFruit().hide();
	}


	public void nextMove() {
		if (performNextMove) {
			moveActors(); // moves the actors for tick
			handleActorCollisions(); // handles the actors colliding
			handleItemCollisions(); // handle item collisions
			checkStageClear(); // handle stage being clear (loading next stage)
		}
		else{
			state.getPacMan().motion(0, 0); 
		}
	}


	public void pacManEaten(PacMan pacMan) {

		SoundController.stopAllSounds();
		SoundController.pacmanEaten();
		if (state.getLives() > 0) {
			performNextMove = false;			 
			game.restartTimer("pacManDeath", 2000);

			state.getPacMan().framerate(4);
			state.getPacMan().setAnimationMode("death", true); //set pacman to "death" frame mode
			
		} else {

			game.showGameOverScreen();
		}
	}

	public void pacManRevive() {
		state.lifeLost();
		state.getGhosts().respawn();
		state.getFruit().hide();
		state.getPacMan().position(GameState.getInstance().getPacManStartPosForLevel(GameState.getInstance().getLevel()));
		state.getPacMan().framerate(15);  
		state.getPacMan().setAnimationMode("chomp", true); //restores PacMan to chomp animations
		state.getPacMan().step(Direction.RIGHT);
		SoundController.startAmbient(); // starts the ambient noise again
		performNextMove = true;
	};


	public void pelletEaten(PowerPellet powerPellet) {
		numberOfEatenGhostsInScatter = 0;
		scatterMode = true;
		state.addToScore(PowerPellet.SCOREVALUE);
		state.getPellets().destroy(powerPellet);
		state.getGhosts().scatter();
		game.startScatterTimer();
		SoundController.stopAllSounds();
		SoundController.pelletEaten();
		SoundController.feverSwitch(true);
	}


	public void pillEaten(Pill pill) {
		state.addToScore(Pill.SCOREVALUE);
		state.getPills().destroy(pill);
		if(!scatterMode)
			SoundController.pillEaten();
	}


	public void setPacManDirection(Direction d) {
		state.getPacMan().step(d);
	}

	public void startGame() {
		state.initialize();
		performNextMove = false;
		game.startInitialWaitTimer();
		SoundController.gameStarted();
	}
	
	public void initialWaitOver(){
		SoundController.startAmbient();
		state.getPacMan().setAnimationMode("chomp");
		performNextMove = true;
	}


	public void unscatterGhosts() {
		SoundController.feverSwitch(false);
		SoundController.startAmbient();
		state.getGhosts().unscatter();
		scatterMode = false;
	}


	public PacManGame getPacInstance() {
		return game;
	}

	/* Private Methods */

	private void checkStageClear() {
		if (state.stageHasBeenCleared()) {
			state.nextStage();
			SoundController.stopAllSounds();
			if (state.getLevel() <= 3) {
				state.setupLevel();
				state.getPacMan().setAnimationMode("chomp");
				SoundController.startAmbient();
			} else {
				game.showScoresScreen();
			}
		}
	}

	private void handleActorCollisions() {
		GhostController ghosts = state.getGhosts();
		PacMan pac = state.getPacMan();
		Collection<Ghost> collidingGhosts = ghosts.getCollidedWith(pac);

		if (ghosts.haveCollidedWithPacMan(pac)) {

			for (Ghost ghost : collidingGhosts) {

				if (ghost.isScattered()) {
					ghost.eaten();
				} else {
					pac.eaten();
					return; //
				}
			}
		}
	}

	private void handleItemCollisions() {

		Pill pill = state.getPills().getCollidingPill(state.getPacMan());
		if (pill != null) {
			pill.eaten();
		}
		// handle pellet collisions
		PowerPellet pellet = state.getPellets().getCollidingPellet(state.getPacMan());
		if (pellet != null) {
			pellet.eaten();
		}
		// handle fruit collisions and visibility
		Fruit fruit = state.getFruit();
		if (fruit.collidedWith(state.getPacMan())) {
			fruit.eaten();
		}
		if (shouldShowFruit()) {
			fruit.showWithTimer();
		}
	}

	private void moveActors() {
		// move the actors
		state.getGhosts().moveAIGhosts();
		state.getPacMan().move();

		state.getWalls().stopCollisions(state.getPacMan());
		state.getGhosts().stopWallCollisions(state.getWalls());
	}

	private boolean shouldShowFruit() {
		int initialPills = state.getPills().getInitialCount();
		if (initialPills - state.getPills().getPillCount() == initialPills / 3 && state.getFruit().getFruitEaten() == 0) {
			return true;
		} else if (initialPills - state.getPills().getPillCount() == (initialPills / 3) * 2 && state.getFruit().getFruitEaten() <= 1) {
			return true;
		} else
			return false;
	}

}
