package code.uci.pacman.controllers;

import code.uci.pacman.controllers.utilities.ArtifactController;
import code.uci.pacman.objects.controllable.PacMan;
import code.uci.pacman.objects.stationary.PowerPellet;


public class PowerPelletController extends ArtifactController<PowerPellet> {
	public PowerPelletController() {
		this.constructArtifactsFromFile("powerpellet");
	}


	public PowerPellet getCollidingPellet(PacMan pacMan) {
		return super.getCollidedWith(pacMan);
	}

	public void addArtifact(int x, int y) {
		super.addObject(x, y, new PowerPellet(x,y));
		
	}
}
