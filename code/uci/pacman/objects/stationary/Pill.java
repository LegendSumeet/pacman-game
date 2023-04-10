package code.uci.pacman.objects.stationary;

import code.uci.pacman.objects.Eatable;
import code.uci.pacman.objects.StationaryObject;

import code.uci.pacman.game.PacManGame;
import code.uci.pacman.multiplayer.*;



public class Pill extends StationaryObject implements Eatable {

	private static final String PILL_IMAGE_PATH = "pill.png";


	public static final int SCOREVALUE = 10;


	public Pill(int x, int y) {
		super(PILL_IMAGE_PATH, x, y);
		if( PacManGame.gameType==1)
		{
			Server.send( PType.PILLA, x(), y() );
		}
	}


	public void eaten() {
		if( PacManGame.gameType==1)
		{
			Server.send( PType.PILLD, x(), y() );
		}
		control.pillEaten(this);
	}

}
