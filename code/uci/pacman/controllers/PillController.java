package code.uci.pacman.controllers;

import code.uci.pacman.controllers.utilities.ArtifactController;
import code.uci.pacman.objects.controllable.PacMan;
import code.uci.pacman.objects.stationary.*;


public class PillController extends ArtifactController<Pill> {
	private int totalPills;

	public PillController() {
		totalPills = 0;
		this.constructArtifactsFromFile("pill");
	}


	public Pill getCollidingPill(PacMan p) {
		return super.getCollidedWith(p);
	}


	public int getInitialCount() {
		return totalPills;
	}


	public int getPillCount() {
		return super.getObjects().size();
	}
	
	

	public void addArtifact(int x, int y) {
		super.addObject(x, y, new Pill(x, y));
		totalPills++;
	}

}
