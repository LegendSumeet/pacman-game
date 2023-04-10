// Sprite.java

package ucigame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.*;
import java.awt.geom.AffineTransform;


public class Sprite
{

	class PinnedSprite // inner class 
	{
		Sprite sprite;

		int x;

		int y;

		PinnedSprite(Sprite _s, int _x, int _y) {
			sprite = _s;
			x = _x;
			y = _y;
		}
	}

	private Ucigame ucigame;

	//TODO Nathan - changed add comments
	private SpriteAnimationController spriteImages;

	private Image[][] tiledImages = null;
	private Vector<int[][]> transparencyBuffer = null;
	private int tileWidth = 0, tileHeight = 0;
	private int tileCols, tileRows;
	private double deltaX, deltaY;
	private double addOnceDeltaX, addOnceDeltaY;
	private double nextX, nextY;
	private double rotationDegrees;
	private double rotCenterX, rotCenterY;
	private boolean flipH = false, flipV = false;
	private Vector<PinnedSprite> pinnedSprites = new Vector<PinnedSprite>();
	private boolean isButton = false;
	private String buttonName = null;
	private boolean isShown = true;
	private boolean Xcollision;
	private boolean Ycollision;
	private boolean contactOnThisBottom;
	private boolean contactOnThisRight;
	private Font spriteFont = null;
	private Color spriteFontColor = null;
	private double cumFrames = 0;
	private int spriteGoalFPS = 0;

	// package visible
	double currX, currY;

	public Sprite(Image _image) {
		spriteImages = new SpriteAnimationController();
		if (_image == null || _image.width() < 1 || _image.height() < 1) {
			Ucigame.logError("in Sprite constructor: image is invalid.");
		}
		ucigame = Ucigame.ucigameObject;
		getCurrentSpriteMode().addImage(_image); //adds image to the current sprite mode
		getCurrentSpriteMode().setWidth(_image.width());
		getCurrentSpriteMode().setHeight(_image.height());
		deltaX = deltaY = 0;
		rotationDegrees = 0;
		addOnceDeltaX = addOnceDeltaY = 0;
	}
	
	public Sprite(String _imagePath) {
		this(Ucigame.ucigameObject.getImage(_imagePath));
	}


	public Sprite(int _cols, int _rows, int _tileWidth, int _tileHeight) {
		if (_cols < 1 || _cols > 1000 || _rows < 1 || _rows > 1000) {
			Ucigame.logError("in Sprite constructor (" + _cols + ", " + _rows + ", " + _tileWidth + ", " + _tileHeight + ") found an illegal number of columns or rows.");
		}
		if (_tileWidth < 1 || _tileWidth > 1000 || _tileHeight < 1 || _tileHeight > 1000) {
			Ucigame.logError("in Sprite constructor (" + _cols + ", " + _rows + ", " + _tileWidth + ", " + _tileHeight + ") found an illegal width or height.");
		}
		ucigame = Ucigame.ucigameObject;
		tileCols = _cols;
		tileRows = _rows;
		tileWidth = _tileWidth;
		tileHeight = _tileHeight;
		tiledImages = new Image[_cols][];
		for (int col = 0; col < _cols; col++)
			tiledImages[col] = new Image[_rows];
		deltaX = deltaY = 0;
		addOnceDeltaX = addOnceDeltaY = 0;
	}



	public Sprite(int _w, int _h) {
		spriteImages = new SpriteAnimationController();
		if (_w < 1 || _w > 2000 || _h < 1 || _h > 2000) {
			Ucigame.logError("in Sprite constructor, the width or height is invalid");
		}
		ucigame = Ucigame.ucigameObject;
		getCurrentSpriteMode().setWidth(_w);
		getCurrentSpriteMode().setHeight(_h);
		deltaX = deltaY = 0;
		addOnceDeltaX = addOnceDeltaY = 0;
	}



	public void addFrame(Image imgObject, int _x, int _y) {
		addFrameToAnimation(getCurrentSpriteMode().getName(), imgObject, _x, _y);
	}
	
	public void addFrame(String imgPath, int _x, int _y) {
		addFrame(ucigame.getImage(imgPath), _x, _y);
	}

	//Add frame to mode same width as other frames in that mode
	public void addFrameToAnimation(String mode, Image _gameImage, int _x, int _y) {
		SpriteAnimationMode spriteMode = spriteImages.addNewMode(mode);
		int height = spriteMode.getHeight();
		int width = spriteMode.getWidth();
		if (height == -1 || width == -1) { //if no dimensions on frame
			spriteMode.setHeight(_gameImage.height());
			spriteMode.setWidth(_gameImage.width());
		}
		//TODO Nathan - document
		if (_gameImage == null) {
			Ucigame.logError("addFrame(Image, " + _x + ", " + _y + "): " + " first parameter (image) is null.");
			return;
		}
		if (_x < 0 || _y < 0) {
			Ucigame.logError("addFrame(Image, " + _x + ", " + _y + "): " + " invalid parameter (less than 0).");
			return;
		}

		BufferedImage newimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width * height + 1];
		BufferedImage source = _gameImage.getBufferedImage();
		if (_x >= source.getWidth() || _y >= source.getHeight() || _x + width > source.getWidth() || _y + height > source.getHeight()) {
			Ucigame.logError("addFrame(Image, " + _x + ", " + _y + "): " + " requested frame extends beyond image.");
			return;
		}
		source.getRGB(_x, _y, width, height, pixels, 0, width);
		for (int x = 0; x < width; x++) // maybe should use BufferedImage.
		{ // setData(Raster)
			for (int y = 0; y < height; y++) {
				newimage.setRGB(x, y, pixels[x + (y * width)]);
			}
		}

		spriteImages.addNewMode(mode).addImage(new Image(newimage, ucigame));
	}


	public void addFrames(Image _gameImage, int... _locations) {
		addFramesForAnimation(getCurrentSpriteMode().getName(), getCurrentSpriteMode().getWidth(), 
				getCurrentSpriteMode().getHeight(), 
				_gameImage, _locations);
	}
	

	public void addFrames(String _gameImagePathPath, int... _locations) {
		addFrames(ucigame.getImage(_gameImagePathPath), _locations);
	}
	

	//clears the current sprite mode and sets it to this
	public void replaceFrames(Image _gameImage, int... _locations) {
		//TODO Nathan - document
		getCurrentSpriteMode().clearFrames(); //clear current frames
		addFrames(_gameImage, _locations); //adds images back to current frame
	}


	public void addFramesForAnimation(String mode, int frameWidth, int frameHeight, Image _modeImage, int... _locations) {
		//TODO nathan - document
		if (_locations.length % 2 != 0) {
			Ucigame.logError("addFrames() does not have an even number of x and y's.");
			return;
		}
		
		SpriteAnimationMode spriteMode = spriteImages.addNewMode(mode); 
		spriteMode.setWidth(frameWidth);
		spriteMode.setHeight(frameHeight);
		
		for (int p = 0; p < _locations.length; p += 2)
			addFrameToAnimation(mode, _modeImage, _locations[p], _locations[p + 1]);

	}
	

	public void addFramesForAnimation(String mode, Image _modeImage, int... _locations) {
		//TODO nathan - document
		if (_locations.length % 2 != 0) {
			Ucigame.logError("addFrames() does not have an even number of x and y's.");
			return;
		}
		
		for (int p = 0; p < _locations.length; p += 2)
			addFrameToAnimation(mode, _modeImage, _locations[p], _locations[p + 1]);

	}
	
	public void addFramesForAnimation(String mode, int width, int height, String imagePath, int... locations) {
		addFramesForAnimation(mode, width, height, ucigame.getImage(imagePath), locations);
	}
	
	public void addFramesForAnimation(String mode, String imagePath, int... locations) {
		addFramesForAnimation(mode, getCurrentSize().x, getCurrentSize().y, ucigame.getImage(imagePath), locations);
	}

	//switches to a defined framemode
	public void setAnimationMode(String mode, boolean resetToInitial) {
		//TODO nathan - document
		//TODO nathan - add error handling
		if (spriteImages.hasMode(mode)) {
			spriteImages.switchToMode(mode);
			if(resetToInitial)
				getCurrentSpriteMode().setCurrFrame(0); //reset to first sprite
		}
	}
	
	//switches to a defined framemode
	public void setAnimationMode(String mode) {
		setAnimationMode(mode, false);
	}
	
	public String getAnimationMode(){
		return getCurrentSpriteMode().getName();
	}

	//switches to the default frame mode
	public void resetAnimationMode() {
		//TODO nathan - document
		//TODO nathan - add error handling

		spriteImages.switchToMode(SpriteAnimationController.DEFAULT_MODE);
	}


	public void setTiles(Image _gameImage, int _x, int _y, int... _locations) {
		if (tileWidth == 0 || tileHeight == 0 || tiledImages == null) {
			Ucigame.logError("Cannot call setTiles() unless the sprite was\n" + "created with makeTiledSprite().");
			return;
		}
		if (_gameImage == null) {
			Ucigame.logError("setTiles(Image, " + _x + ", " + _y + ", ...): " + " first parameter (image) is null.");
			return;
		}
		if (_x < 0 || _y < 0) {
			Ucigame.logError("setTiles(Image, " + _x + ", " + _y + ", ...): " + " invalid parameter (less than 0).");
			return;
		}
		if (_locations.length % 2 != 0) {
			Ucigame.logError("setTiles(Image, " + _x + ", " + _y + ", ...): " + "not an even number of cols and rows.");
			return;
		}
		if (_locations.length == 0) {
			Ucigame.logError("setTiles(Image, " + _x + ", " + _y + ", ...): " + "no cols and rows specified.");
			return;
		}

		BufferedImage newimage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[tileWidth * tileHeight + 1];
		BufferedImage source = _gameImage.getBufferedImage();
		if (_x >= source.getWidth() || _y >= source.getHeight() || _x + tileWidth > source.getWidth() || _y + tileHeight > source.getHeight()) {
			Ucigame.logError("setTiles(Image, " + _x + ", " + _y + "): " + " requested frame extends beyond image.");
			return;
		}
		source.getRGB(_x, _y, tileWidth, tileHeight, pixels, 0, tileWidth);
		for (int x = 0; x < tileWidth; x++) {
			for (int y = 0; y < tileHeight; y++) {
				newimage.setRGB(x, y, pixels[x + (y * tileWidth)]);
			}
		}
		Image tileImage = new Image(newimage, ucigame);
		for (int p = 0; p < _locations.length; p += 2) {
			int col = _locations[p];
			int row = _locations[p + 1];
			if (col < 0 || col >= tileCols || row < 0 || row >= tileRows) {
				Ucigame.logError("setTiles(Image, " + _x + ", " + _y + ", ...): " + " col " + col + " or row " + row + " is invalid");
				return;
			}
			tiledImages[col][row] = tileImage;
		}
	}



	public void pin(Sprite _sprite, int _x, int _y) {
		if (this != _sprite)
			pinnedSprites.add(new PinnedSprite(_sprite, _x, _y));
	}


	public void framerate(int _d) {
		if (_d == 0) {
			spriteGoalFPS = 0; // turn it off
			return;
		} else if (0 < _d && _d <= 1000) {
			spriteGoalFPS = _d;
			cumFrames = 0;
		} else
			Ucigame.logError("sprite.framerate(" + _d + ") has an invalid parameter.");
	}


	public void rotate(double degrees) {
		int height = getCurrentSize().y;
		int width = getCurrentSize().x;
		rotationDegrees = degrees;
		rotCenterX = (double) width / 2.0;
		rotCenterY = (double) height / 2.0;
	}


	public void rotate(double degrees, double _rotCenterX, double _rotCenterY) {
		rotationDegrees = degrees;
		rotCenterX = _rotCenterX;
		rotCenterY = _rotCenterY;
	}


	public void flipHorizontal() {
		flipH = true;
	}


	public void flipVertical() {
		flipV = true;
	}


	public void draw() {
		draw(new AffineTransform()); // call with identity
	}


	public void draw(AffineTransform _Tx) {
		int height = getCurrentSize().y;
		int width = getCurrentSize().x;
		if (!isShown)
			return;
		if (getCurrentSpriteMode().getNumFrames() == 0)
			return;
		currX = nextX;
		currY = nextY;

		AffineTransform at = new AffineTransform(_Tx);
		at.translate(currX + rotCenterX, currY + rotCenterY); // third
		at.rotate(rotationDegrees * Math.PI / 180.0); // second
		at.translate(-rotCenterX, -rotCenterY); // first
		if (flipH || flipV) // flips happen, logically, before rotations
		{
			at.translate((double) width / 2.0, (double) height / 2.0); // third
			at.scale(flipH ? -1 : 1, flipV ? -1 : 1); // second
			at.translate(-(double) width / 2.0, -(double) height / 2.0);// first
			flipH = flipV = false;
		}
		if (tiledImages == null)
			getCurrentSpriteMode().getCurrImage().draw(at);
		else {
			for (int col = 0; col < tileCols; col++)
				for (int row = 0; row < tileRows; row++) {
					if (tiledImages[col][row] != null) {
						AffineTransform at2 = new AffineTransform(_Tx);
						at2.translate(currX + rotCenterX + (col * tileWidth), currY + rotCenterY + (row * tileHeight));
						at2.rotate(rotationDegrees * Math.PI / 180.0);
						at2.translate(-rotCenterX, -rotCenterY);
						tiledImages[col][row].draw(at2);
					}
				}
		}

		int currFrame = getCurrentSpriteMode().getCurrFrame();
		int numFrames = getCurrentSpriteMode().getNumFrames();
		ucigame.addSpriteToList(this);
		if (!isButton) {
			if (spriteGoalFPS == 0)
				currFrame = (currFrame + 1) % numFrames;
			else {
				cumFrames += spriteGoalFPS;
				if (cumFrames >= ucigame.goalFPS) {
					cumFrames -= ucigame.goalFPS;
					currFrame = (currFrame + 1) % numFrames;
				}
			}
			getCurrentSpriteMode().setCurrFrame(currFrame);
		}
		for (PinnedSprite ps : pinnedSprites) {
			ps.sprite.nextX = ps.x;
			ps.sprite.nextY = ps.y;
			// ps.sprite.nextX = this.currX + ps.x;
			// ps.sprite.nextY = this.currY + ps.y;
			ps.sprite.draw(at);
			ucigame.addSpriteToList(ps.sprite);
		}
		rotationDegrees = rotCenterX = rotCenterY = 0;
	}

	private int getCurrentFrame() {
		return getCurrentSpriteMode().getCurrFrame();
	}

	public Point getCurrentSize() {
		return new Point(getCurrentSpriteMode().getWidth(), getCurrentSpriteMode().getHeight());
	}

	private SpriteAnimationMode getCurrentSpriteMode() {
		return spriteImages.getCurrentMode();
	}


	public void font(String _name, int _style, int _size) {
		font(_name, _style, _size, 0, 0, 0);
	}

	public void font(String _name, int _style, int _size, int _r, int _g, int _b) {
		if (_style == Ucigame.BOLD || _style == Ucigame.PLAIN || _style == Ucigame.ITALIC || _style == Ucigame.BOLDITALIC)
			;
		else {
			Ucigame.logError("Invalid style parameter in Sprite.font()");
			_style = Ucigame.PLAIN;
		}
		spriteFont = new Font(_name, _style, _size);
		// System.out.println("Font: " + spriteFont);
		if (spriteFont.getFamily().equalsIgnoreCase(_name) || spriteFont.getFontName().equalsIgnoreCase(_name))
			;
		else
			Ucigame.logWarning("Could not create font with name " + _name + ". Using font " + spriteFont.getFontName() + " instead.");
		if (0 <= _r && _r <= 255 && 0 <= _g && _g <= 255 && 0 <= _b && _b <= 255)
			spriteFontColor = new Color(_r, _g, _b);
		else
			spriteFontColor = Color.BLACK;
	}


	public void putText(int _n, double _x, double _y) {
		putText("" + _n, _x, _y);
	}

	public void putText(double _d, double _x, double _y) {
		putText("" + _d, _x, _y);
	}


	public void putText(String _string, double _x, double _y) {
		if (ucigame.offG == null) {
			Ucigame.logError("Sprite.putText(" + _string + "," + _x + ", " + _y + ") used outside of draw()");
			return;
		}
		if (isShown) {
			ucigame.offG.setClip((int) currX, (int) currY, getCurrentSize().x, getCurrentSize().y);
			if (spriteFont != null)
				ucigame.offG.setFont(spriteFont);
			Color prevColor = ucigame.offG.getColor();
			if (spriteFontColor != null)
				ucigame.offG.setColor(spriteFontColor);
			ucigame.offG.drawString(_string, (int) (currX + _x), (int) (currY + _y));
			ucigame.offG.setClip(null);
			ucigame.offG.setColor(prevColor);
		}
	}


	public int width() {
		return getCurrentSize().x;
	}


	public int height() {
		return getCurrentSize().y;
	}


	public void hide() {
		isShown = false;
	}


	public void show() {
		isShown = true;
	}


	public boolean isShown() {
		return isShown;
	}


	public void position(double _x, double _y) {
		currX = _x;
		currY = _y;
		nextX = _x;
		nextY = _y;
	}


	public void motion(double _x, double _y) {
		deltaX = _x;
		deltaY = _y;
	}


	public void motion(double _x, double _y, int _COMMAND) {
		if (_COMMAND == Ucigame.SET) {
			deltaX = _x;
			deltaY = _y;
		} else if (_COMMAND == Ucigame.ADD) {
			deltaX += _x;
			deltaY += _y;
		} else if (_COMMAND == Ucigame.ADDONCE) {
			addOnceDeltaX += _x;
			addOnceDeltaY += _y;
		} else if (_COMMAND == Ucigame.MULTIPLY) {
			deltaX *= _x;
			deltaY *= _y;
		} else
			Ucigame.logError("motion(" + _x + ", " + _y + ", ???) -- last parameter not valid.");
	}


	public void move() {
		nextX = currX + deltaX + addOnceDeltaX;
		//HLOC: check for negative value
		///if(nextX < 0){
		///	nextX = -1;
		///}
		nextY = currY + deltaY + addOnceDeltaY;
		//HLOC: check for negative value
		///if(nextY < 0){
		///	nextY = -1;
		///}
		addOnceDeltaX = addOnceDeltaY = 0;
	}


	public int x() {
		return (int) (Math.round(currX));
	}

	public int y() {
		return (int) (Math.round(currY));
	}


	public void nextX(double _nextX) {
		nextX = _nextX;
		deltaX = 0;
	}


	public void nextY(double _nextY) {
		nextY = _nextY;
		deltaY = 0;
	}


	public double xspeed() {
		return deltaX;
	}


	public double yspeed() {
		return deltaY;
	}


	public boolean collided(int... sides) {
		if (sides.length == 0) // any side
			return Xcollision || Ycollision;
		for (int side : sides) {
			if (side == Ucigame.LEFT && Xcollision && !contactOnThisRight)
				return true;
			else if (side == Ucigame.RIGHT && Xcollision && contactOnThisRight)
				return true;
			else if (side == Ucigame.TOP && Ycollision && !contactOnThisBottom)
				return true;
			else if (side == Ucigame.BOTTOM && Ycollision && contactOnThisBottom)
				return true;
			if (side != Ucigame.LEFT && side != Ucigame.RIGHT && side != Ucigame.TOP && side != Ucigame.BOTTOM)
				Ucigame.logError("collided() called with illegal value");
		}
		return false;
	}


	public void makeButton(String _name) {
		isButton = true;
		buttonName = _name;
	}

	// d drag, m move, p press r release, x, y);
	public final void buttonAction(char _action, int _x, int _y) {
		int height = getCurrentSize().y;
		int width = getCurrentSize().x;
		if (!(isShown && isButton))
			return;
		boolean over = _x >= currX && _x < currX + width && _y >= currY && _y < currY + height;
		// System.out.println("action: " + _action + " over: " + over);
		int tempCurrentFrame = getCurrentFrame();
		if (_action == 'M' && over) // mouseMove event
		{
			if (tempCurrentFrame == 0)
				tempCurrentFrame = 1;
		} else if (_action == 'M' && !over) // mouseMove event
		{
			if (tempCurrentFrame == 1 || tempCurrentFrame == 2)
				tempCurrentFrame = 0;
		} else if (_action == 'D' && over) // mouseDrag event
		{
			;
		} else if (_action == 'D' && !over) // mouseDrag event
		{
			;
		} else if (_action == 'P' && over) // mousePressed event
		{
			if (tempCurrentFrame == 0 || tempCurrentFrame == 1)
				tempCurrentFrame = 2;
		} else if (_action == 'P' && !over) // mousePressed event
		{
			;
		} else if (_action == 'R' && over) // mouseReleased event
		{
			if (tempCurrentFrame == 2) {
				tempCurrentFrame = 1;
				Method m = ucigame.name2method.get(buttonName);
				try {
					m.invoke(ucigame.isApplet ? ucigame : Ucigame.gameObject);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					Ucigame.logError("Exception4 while invoking " + m.getName() + "\n" + e + "\n" + e.getCause());
				}
			}
		} else if (_action == 'R' && !over) // mouseReleased event
		{
			if (tempCurrentFrame == 2)
				tempCurrentFrame = 0;
		}
		getCurrentSpriteMode().setCurrFrame(tempCurrentFrame);
	}

	private final int BOUNCE = 1234321;
	private final int STOP = 1234322;
	private final int CHECK = 1234567;
	private final int PAUSE = 1234568;


	public void bounceIfCollidesWith(Sprite... _sprite) {
		somethingIfCollidesWith("bounce", BOUNCE, _sprite);
	}


	public void stopIfCollidesWith(Sprite... _sprite) {
		somethingIfCollidesWith("stop", STOP, _sprite);
	}


	public void checkIfCollidesWith(Sprite... _sprite) {
		somethingIfCollidesWith("check", CHECK, _sprite);
	}


	public void pauseIfCollidesWith(Sprite... _sprite) {
		somethingIfCollidesWith("pause", PAUSE, _sprite);
	}


	private void somethingIfCollidesWith(String _name, int _action, Sprite... _sprite) {
		if (_sprite.length == 0 || (_sprite.length == 1 && _sprite[0] == Ucigame.PIXELPERFECT))
			Ucigame.logError(_name + "IfCollidesWith called with no Sprite specified.");

		boolean pixelPerfect = (_sprite[_sprite.length - 1] == Ucigame.PIXELPERFECT);

		// check each sprite listed, after initializing flags to false
		Xcollision = false;
		Ycollision = false;
		contactOnThisBottom = false;
		contactOnThisRight = false;
		for (Sprite s : _sprite) {
			if (s == Ucigame.PIXELPERFECT) // skip this flag
				continue;
			if (pixelPerfect)
				ifCollidesWithPixelPerfect(s, _action);
			else
				ifCollidesWith(s, _action);
		}
	}


	// for right now, just based on final (next) positions, not on entire trajectory
	private void ifCollidesWith(Sprite _sprite, int _action) {
		int height = getCurrentSize().y;
		int spriteHeight = _sprite.getCurrentSize().y;
		int width = getCurrentSize().x;
		int spriteWidth = _sprite.getCurrentSize().x;
		// System.out.println("in ifCollidesWith, position:" +
		// nextX + ", " + nextY);
		double XcollisionTime = 0;
		double YcollisionTime = 0;
		boolean XcollisionThisSprite = false; // based on _sprite
		boolean YcollisionThisSprite = false;

		if (_sprite == null)
			return;
		if (!overlapsWith(_sprite)) {
			return;
		}
		if (_action == CHECK) {
			Xcollision = Ycollision = true;
			return;
		}

		if (_sprite == Ucigame.BOTTOMEDGE) {
			if (nextY + height >= ucigame.window.clientHeight()) {
				if (currY + height > ucigame.window.clientHeight()) // already out of screen
					YcollisionTime = 0;
				else
					YcollisionTime = (ucigame.window.clientHeight() - (currY + height)) / (nextY - currY);
				Ycollision = true;
				YcollisionThisSprite = true;
				contactOnThisBottom = true;
			}
		} else if (_sprite == Ucigame.TOPEDGE) {
			if (nextY < 0) {
				if (currY < 0) // already out of screen
					YcollisionTime = 0;
				else
					YcollisionTime = -currY / (nextY - currY);
				Ycollision = true;
				YcollisionThisSprite = true;
				contactOnThisBottom = false;
			}
		}

		// System.out.println("\n_sprite: " + _sprite);
		// System.out.println("currY+ht: " + (currY + height) +
		// " nextY+ht: " + (nextY + height) +
		// " _sprite.currY:" + _sprite.currY +
		// " _sprite.nextY:" + _sprite.nextY);
		// this's bottom hit other's top
		else if (currY + height <= _sprite.currY && nextY + height > _sprite.nextY) {
			//HLOC: Account for the possibility that the next position of the other sprite will
			//overlap the current position of this sprite
			if ((_sprite.nextY - (currY + height)) < 0)
				YcollisionTime = 0;
			else
				YcollisionTime = (_sprite.nextY - (currY + height)) / (nextY - currY);
			Ycollision = true;
			YcollisionThisSprite = true;
			contactOnThisBottom = true;
			// System.out.println(".-Y collision at: " + YcollisionTime);
		}
		// this's top hit other's bottom
		else if (currY >= _sprite.currY + spriteHeight && nextY < _sprite.nextY + spriteHeight) {
			//HLOC: Account for negative value
			if ((currY - (_sprite.nextY + spriteHeight)) < 0)
				YcollisionTime = 0;
			else
				YcollisionTime = (currY - (_sprite.nextY + spriteHeight)) / (currY - nextY);
			Ycollision = true;
			YcollisionThisSprite = true;
			contactOnThisBottom = false;
			// System.out.println("--Y collision at: " + YcollisionTime);
		}

		if (_sprite == Ucigame.RIGHTEDGE) {
			if (nextX + width >= ucigame.window.clientWidth()) {
				if (currX + width > ucigame.window.clientWidth()) // already out of screen
					XcollisionTime = 0;
				else
					XcollisionTime = (ucigame.window.clientWidth() - (currX + width)) / (nextX - currX);
				Xcollision = true;
				XcollisionThisSprite = true;
				contactOnThisRight = true;
			}
		} else if (_sprite == Ucigame.LEFTEDGE) {
			if (nextX < 0) {
				if (currX < 0) // already out of screen
					XcollisionTime = 0;
				else
					XcollisionTime = -currX / (nextX - currX);
				Xcollision = true;
				XcollisionThisSprite = true;
				contactOnThisRight = false;
			}
		}

		// System.out.println("currX+wd: " + (currX + width) +
		// " nextX+wd: " + (nextX + width) +
		// " _sprite.currX:" + _sprite.currX +
		// " _sprite.nextX:" + _sprite.nextX);
		// System.out.println("currX: " + currX +
		// " nextX: " + nextX +
		// " _sprite.currX+wd:" + (_sprite.currX + spriteWidth) +
		// " _sprite.nextX+wd:" + (_sprite.nextX + spriteWidth));
		// this's right hit other's left
		else if (currX + width <= _sprite.currX && nextX + width > _sprite.nextX) {
			//HLOC: Account for negative value
			if ((_sprite.nextX - (currX + width)) < 0)
				XcollisionTime = 0;
			else
				XcollisionTime = (_sprite.nextX - (currX + width)) / (nextX - currX);
			contactOnThisRight = true;
			Xcollision = true;
			XcollisionThisSprite = true;
			// System.out.println(".-X collision at: " + XcollisionTime);
		}
		// this's left hit other's right
		else if (currX >= _sprite.currX + spriteWidth && nextX < _sprite.nextX + spriteWidth) {
			//HLOC: Account for negative value
			if ((currX - (_sprite.nextX + spriteWidth)) < 0)
				XcollisionTime = 0;
			else
				XcollisionTime = (currX - (_sprite.nextX + spriteWidth)) / (currX - nextX);
			contactOnThisRight = false;
			Xcollision = true;
			XcollisionThisSprite = true;
			// System.out.println("--X collision at: " + XcollisionTime);
		}
		if (XcollisionTime == -1 || (XcollisionTime >= 0 && XcollisionTime <= 1))
			;
		else
			System.out.println("**Unexpected XcollisionTime: " + XcollisionTime);
		if (YcollisionTime == -1 || (YcollisionTime >= 0 && YcollisionTime <= 1))
			; // nop
		else
			System.out.println("**Unexpected YcollisionTime: " + YcollisionTime);

		if (!XcollisionThisSprite && !YcollisionThisSprite)
			;
		else if ((!XcollisionThisSprite && YcollisionThisSprite) || // collision in Y only or Y first
				(XcollisionThisSprite && YcollisionThisSprite && YcollisionTime < XcollisionTime)) {
			if (contactOnThisBottom) // **MORE COMPLEX IF _SPRITE IS MOVING**
			{
				if (_action == BOUNCE) {
					double overlap = (nextY + height) - _sprite.nextY;
					// System.out.println("\tC " + overlap);
					nextY = nextY - overlap; // bounce
					//HLOC: check for negative value
					if (nextY < 0)
						nextY = -1;
					deltaY = -deltaY;
				} else if (_action == STOP) {
					nextY = _sprite.nextY - height - 1;
					deltaY = 0;
				} else if (_action == PAUSE)
					nextY = _sprite.nextY - height - 1;
			} else {
				if (_action == BOUNCE) {
					double overlap = (_sprite.nextY + spriteHeight) - nextY;
					// System.out.println("\tD " + overlap);
					nextY = nextY + overlap; // bounce
					//HLOC: check for negative value
					if (nextY < 0)
						nextY = -1;
					deltaY = -deltaY;
				} else if (_action == STOP) {
					nextY = _sprite.nextY + spriteHeight + 1;
					deltaY = 0;
				} else if (_action == PAUSE)
					nextY = _sprite.nextY + spriteHeight + 1;
			}
		} else if ((XcollisionThisSprite && !YcollisionThisSprite) || // collision in X only or X first
				(XcollisionThisSprite && YcollisionThisSprite && XcollisionTime < YcollisionTime)) {
			if (contactOnThisRight) // **MORE COMPLEX IF _SPRITE IS MOVING**
			{
				if (_action == BOUNCE) {
					double overlap = (nextX + width) - _sprite.nextX;
					// System.out.println("\tA " + overlap);
					nextX = nextX - overlap; // bounce
					//HLOC: check for negative value
					if (nextX < 0)
						nextX = -1;
					deltaX = -deltaX;
				} else if (_action == STOP) {
					nextX = _sprite.nextX - width - 1;
					deltaX = 0;
				} else if (_action == PAUSE)
					nextX = _sprite.nextX - width - 1;
			} else {
				if (_action == BOUNCE) {
					double overlap = (_sprite.nextX + spriteWidth) - nextX;
					// System.out.println("\tB " + overlap);
					nextX = nextX + overlap; // bounce
					//HLOC: check for negative value
					if (nextX < 0)
						nextX = -1;
					deltaX = -deltaX;
				} else if (_action == STOP) {
					nextX = _sprite.nextX + spriteWidth + 1;
					deltaX = 0;
				} else if (_action == PAUSE)
					nextX = _sprite.nextX + spriteWidth + 1;
			}
		} else
			// collision is X and Y simultaneously
			; // System.out.println("***Unexpected ELSE***");
	}

	/**
	 * Pixel Perfect check for collision and perform appropriate action.
	 */
	private void ifCollidesWithPixelPerfect(Sprite _sprite, int _action) {
		// this version assumes _sprite is not moving, and does
		// go pixel by pixel with this.
		// System.out.println("in ifCollidesWith, position:" +
		// nextX + ", " + nextY);

		if (_sprite == null)
			return;
		if (!overlapsWith(_sprite))
			return;

		//Return a number if pixels overlap;
		int ppCollisionCount = pixelPerfectOverlap(_sprite, this.nextX, this.nextY);

		if (ppCollisionCount <= 0)
			return;
		Xcollision = Ycollision = true; // make collided() work

		//Normal vector
		int dx = pixelPerfectOverlap(_sprite, this.nextX + 1, this.nextY) - pixelPerfectOverlap(_sprite, this.nextX - 1, this.nextY);
		int dy = pixelPerfectOverlap(_sprite, this.nextX, this.nextY + 1) - pixelPerfectOverlap(_sprite, this.nextX, this.nextY - 1);

		//Convert the vector to positive values to calculate the angle
		//between the normal vector and the horizontal line
		int tempDx, tempDy;

		if (dx < 0)
			tempDx = -dx;
		else
			tempDx = dx;

		if (dy < 0)
			tempDy = -dy;
		else
			tempDy = dy;

		double angleVal;
		if (tempDx != 0)
			angleVal = Math.atan2(tempDy, tempDx);
		else
			angleVal = Math.PI / 2;

		double minThreshold = (Math.PI) / 32;
		double maxThreshold = (15 * Math.PI) / 32;

		//Assume to be a 45 degree angle collision
		if (angleVal >= minThreshold && angleVal <= maxThreshold) {

			if ((dx > 0 && dy < 0) || dx < 0 && dy > 0) {

				double temp = this.deltaX;
				this.deltaX = this.deltaY;
				this.deltaY = temp;
			}
			//the normal vector have the same signs
			else {
				double temp = this.deltaX;
				this.deltaX = -this.deltaY;
				this.deltaY = -temp;
			}
		}
		//Normal vector leaning toward vertical
		else if (angleVal > maxThreshold && angleVal <= Math.PI / 2) {
			//Assume vertical, and reset dx to 0
			dx = 0;
		} else if (angleVal >= 0 && angleVal < minThreshold) {
			//Assume horizontal, reset dy to 0
			dy = 0;
		}

		//Assume vertical or horizontal collision
		{
			//If the x component for the normal vector is positive
			if (dx > 0) {

				//if the current deltaX is positive, make it negative
				if (this.deltaX > 0)
					this.deltaX = -this.deltaX;

			} else if (dx < 0) { //x component for the normal vector is negative

				//if the current delta is negative as well
				if (this.deltaX < 0)
					this.deltaX = -this.deltaX;

			}

			//If the x component for the normal vector is positive
			if (dy > 0) {

				//if the current deltaX is positive, make it negative
				if (this.deltaY > 0)
					this.deltaY = -this.deltaY;

			} else if (dy < 0) { //x component for the normal vector is negative

				//if the current delta is negative as well
				if (this.deltaY < 0)
					this.deltaY = -this.deltaY;

			}
		}

		//Set new location
		this.nextX = this.currX + this.deltaX;
		if (this.nextX < 0) {
			this.nextX = -1;
			if (this.deltaX < 0)
				this.deltaX = -this.deltaX;
		}
		this.nextY = this.currY + this.deltaY;
		if (this.nextY < 0) {
			this.nextY = -1;
			if (this.deltaY < 0)
				this.deltaY = -this.deltaY;
		}

	}

	/**
	 * Pixel perfect check for overlap.
	 */
	private int pixelPerfectOverlap(Sprite _sprite, double thisX, double thisY) {
		int height = getCurrentSize().y;
		int spriteHeight = _sprite.getCurrentSize().y;
		int width = getCurrentSize().x;
		int spriteWidth = _sprite.getCurrentSize().x;

		if (this.transparencyBuffer == null)
			this.setUpTransparencyBuffer();
		if (_sprite.transparencyBuffer == null)
			_sprite.setUpTransparencyBuffer();

		//The width and height of the over lap
		double overlapWidth = 0;
		double overlapHeight = 0;

		double overlapX = 0;
		double overlapY = 0;

		//The larger of the two next X values
		if (thisX > _sprite.nextX)
			overlapX = thisX;
		else
			overlapX = _sprite.nextX;

		//The larger of the two next Y values
		if (thisY > _sprite.nextY)
			overlapY = thisY;
		else
			overlapY = _sprite.nextY;

		//this sprite's left side overlaps with the other sprite's right side
		if ((thisX < _sprite.nextX + spriteWidth) && (thisX + width > _sprite.nextX + spriteWidth)) {

			overlapWidth = _sprite.nextX + spriteWidth - thisX;

			//Check to see if the overlap area is greater than the width of either sprite
			if (overlapWidth >= width)
				overlapWidth = width - 1;

			if (overlapWidth >= spriteWidth)
				overlapWidth = spriteWidth - 1;

		}
		//this sprite's right side overlaps with the other sprite's left side
		else if ((_sprite.nextX < thisX + width) && (thisX < _sprite.nextX)) {

			overlapWidth = thisX + width - _sprite.nextX;

			//Check to see if the overlap area is greater than the width of either sprite
			if (overlapWidth >= width)
				overlapWidth = width - 1;

			if (overlapWidth >= spriteWidth)
				overlapWidth = spriteWidth - 1;

		}
		//this sprite's x-values is completely within the other sprite in term of width
		else if ((thisX >= _sprite.nextX) && ((thisX + width) <= (_sprite.nextX + spriteWidth))) {

			overlapWidth = width;

		}

		//this sprite's top overlaps with the other's bottom
		if ((thisY < _sprite.nextY + spriteHeight) && (thisY + height > _sprite.nextY + spriteHeight)) {

			overlapHeight = (_sprite.nextY + spriteHeight) - thisY;

			//Check to see if the overlap area is larger than the height of either sprite
			if (overlapHeight >= height)
				overlapHeight = height - 1;

			if (overlapHeight >= spriteHeight)
				overlapHeight = spriteHeight - 1;

		}
		//this sprite's bottom overlaps with the other's top
		else if ((_sprite.nextY < thisY + height) && (thisY < _sprite.nextY)) {

			overlapHeight = (thisY + height) - _sprite.nextY;

			//Check to see if the overlap area is larger than the height of either sprite
			if (overlapHeight >= height)
				overlapHeight = height - 1;

			if (overlapHeight >= spriteHeight)
				overlapHeight = spriteHeight - 1;

		}
		//this sprite's x-values is completely withing the other sprite in term of height
		else if ((thisY >= _sprite.nextY) && ((thisY + height) <= (_sprite.nextY + spriteHeight))) {

			overlapHeight = height;

		}

		//The value to offset in each individual sprite
		int thisOffsetX = 0, thisOffsetY = 0;
		int spriteOffsetX = 0, spriteOffsetY = 0;

		//Calculate the offset for this sprite
		if (thisX < overlapX)
			thisOffsetX = (int) (overlapX - thisX);
		else
			//Also include the case where the values thisX == overlapX
			thisOffsetX = (int) (thisX - overlapX);

		if (thisY < overlapY)
			thisOffsetY = (int) (overlapY - thisY);
		else
			//Also include the case where the values this.nextY == overlapY
			thisOffsetY = (int) (thisY - overlapY);

		//Calulate the offset for the other sprite
		if (_sprite.nextX < overlapX)
			spriteOffsetX = (int) (overlapX - _sprite.nextX);
		else
			//Also include the case where the values _sprite.nextX == overlapX
			spriteOffsetX = (int) (_sprite.nextX - overlapX);

		if (_sprite.nextY < overlapY)
			spriteOffsetY = (int) (overlapY - _sprite.nextY);
		else
			//Also include the case where the values _sprite.nextY == overlapY
			spriteOffsetY = (int) (_sprite.nextY - overlapY);

		//boolean collisionDetected = false;

		int ppCollisionCount = 0;

		//loop through the overlapped area to see if there is any pixel collision
		if (this.transparencyBuffer.size() <= getCurrentFrame() || 
				_sprite.transparencyBuffer.size() <= _sprite.getCurrentFrame()) {
			System.err.println("Sprite Problem: Pixel Perfect Collision Failed 1");
			return ppCollisionCount; //stop right now
		}
		
		int[][] thisSprite = this.transparencyBuffer.get(getCurrentFrame());
		int[][] otherSprite = _sprite.transparencyBuffer.get(_sprite.getCurrentFrame());

		for (int x = 0; x < overlapWidth; ++x) {
			for (int y = 0; y < overlapHeight; ++y) {
				int xComputed = x + thisOffsetX;
				int yComputed = y + thisOffsetY;
				int spriteComputedX = x + spriteOffsetX;
				int spriteComputedY = y + spriteOffsetY;
				//there is a collision
				if (	thisSprite.length >  xComputed && 
						thisSprite[xComputed].length > yComputed &&
						otherSprite.length > spriteComputedX &&
						otherSprite[spriteComputedX].length > spriteComputedY) {
					
					if (thisSprite[xComputed][yComputed] != 0 && 
							otherSprite[spriteComputedX][spriteComputedY] != 0) {
						
						++ppCollisionCount;
					
					}	
					
				}
				else
				{
					System.err.println("Sprite Problem: Pixel Perfect Collision Failed 2");
				}
			}

		}

		return ppCollisionCount;

	}

	private void setUpTransparencyBuffer() {
		//HLOC: change to 2-D array
		transparencyBuffer = new Vector<int[][]>();
		int numFrames = getCurrentSpriteMode().getNumFrames();
		for (int frame = 0; frame < numFrames; frame++) {
			transparencyBuffer.add(frame, getCurrentSpriteMode().getFrameImage(frame).getTransparencyBuffer());
		}
	}

	private boolean overlapsWith(Sprite _sprite) {
		int height = getCurrentSize().y;
		int spriteHeight = _sprite.getCurrentSize().y;
		int width = getCurrentSize().x;
		int spriteWidth = _sprite.getCurrentSize().x;
		// System.out.println(
		// "**this x: " + nextX + " to " + (nextX + width) +
		// " y: " + nextY + " to " + (nextY + height) +
		// " other x: " + _sprite.nextX + " to " + (_sprite.nextX +
		// spriteWidth) +
		// " y: " + _sprite.nextY + " to " + (_sprite.nextY + spriteHeight)
		// );
		if (this.nextX > _sprite.nextX + spriteWidth || this.nextX + width < _sprite.nextX || this.nextY > _sprite.nextY + spriteHeight || this.nextY + height < _sprite.nextY)
			return false;
		else
			return true;
	}

	public String toString() {
		int height = getCurrentSize().y;
		int width = getCurrentSize().x;
		if (this == Ucigame.TOPEDGE)
			return "<TOPEDGE>";
		if (this == Ucigame.BOTTOMEDGE)
			return "<BOTTOMEDGE>";
		if (this == Ucigame.LEFTEDGE)
			return "<LEFTEDGE>";
		if (this == Ucigame.RIGHTEDGE)
			return "<RIGHTEDGE>";
		if (this == Ucigame.PIXELPERFECT)
			return "<PIXELPERFECT>";
		return "<Sprite [" + width + "," + height + "]#" + (hashCode() % 10000) + ">";
	}
}
