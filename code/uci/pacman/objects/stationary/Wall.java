package code.uci.pacman.objects.stationary;


import code.uci.pacman.objects.StationaryObject;


public class Wall extends StationaryObject {


	public Wall(int l, int x, int y, int width, int height) {
		super(width, height);
		String level = "level" + l + ".png";
		super.addFrame(level, x, y);
		super.position(x, y);
	}

}
