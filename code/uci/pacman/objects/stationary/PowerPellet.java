package code.uci.pacman.objects.stationary;

import code.uci.pacman.objects.Eatable;
import code.uci.pacman.objects.StationaryObject;

import code.uci.pacman.game.PacManGame;
import code.uci.pacman.multiplayer.*;



public class PowerPellet extends StationaryObject implements Eatable {
	
	private static final String PELLET_IMAGE_PATH = "pellet.png";
	

	public PowerPellet(int x, int y) {
		super(PELLET_IMAGE_PATH, x, y);
		if( PacManGame.gameType==1)
		{
			Server.send( PType.PPILLA, x(), y() );
		}

	}

	public static final int SCOREVALUE = 50;


	public void eaten() {
		if( PacManGame.gameType==1)
		{
			Server.send( PType.PPILLD, x(), y() );
		}

		control.pelletEaten(this);
	}

	
}
