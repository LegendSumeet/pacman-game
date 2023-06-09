package code.uci.pacman.objects;

import java.awt.Point;

import code.uci.pacman.controllers.GameController;
import code.uci.pacman.game.PacManGame;
import ucigame.Image;
import ucigame.Sprite;


public abstract class StationaryObject extends Sprite {
	
    protected GameController control;
    protected String objectGraphicPath;
    

    private StationaryObject(Image img, int x, int y) {
		super(img.width(), img.height());
		super.addFrame(img, 0, 0);
		this.position(x, y);
		control = GameController.getInstance();
	}

	public StationaryObject(String path, int x, int y) {
		this(getImage(path), x, y);
		this.objectGraphicPath = path;
	}
	

	public StationaryObject(int width, int height) {
		super(width, height);
		
	}
	

	public String getGraphicPath() {
		return objectGraphicPath;
	}
	

	public boolean collidedWith(ControllableObject c){
		super.checkIfCollidesWith(c, PacManGame.PIXELPERFECT);
		return super.collided() && super.isShown();
	}

	public void position(Point p) {
		super.position(p.x, p.y);
	}
	

	public void addFrame(String imgPath, int x, int y){
		super.addFrame(getImage(imgPath), x, y);
	}
	

	private static Image getImage(String stringPath){
		return GameController.getInstance().getPacInstance().getImage(stringPath, 77,77,77);
	}
}
