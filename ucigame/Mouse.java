// Mouse.java

package ucigame;

import java.awt.*;
import java.awt.event.MouseEvent;


public final class Mouse
{
	public final int CROSSHAIR = Cursor.CROSSHAIR_CURSOR;
	public final int DEFAULT   = Cursor.DEFAULT_CURSOR; 
	public final int HAND      = Cursor.HAND_CURSOR;
	public final int MOVE      = Cursor.MOVE_CURSOR;
	public final int TEXT      = Cursor.TEXT_CURSOR;
	public final int WAIT      = Cursor.WAIT_CURSOR;
	public final int N_RESIZE  = Cursor.N_RESIZE_CURSOR;
	public final int E_RESIZE  = Cursor.E_RESIZE_CURSOR;
	public final int S_RESIZE  = Cursor.S_RESIZE_CURSOR;
	public final int W_RESIZE  = Cursor.W_RESIZE_CURSOR;
	public final int NE_RESIZE = Cursor.NE_RESIZE_CURSOR;
	public final int NW_RESIZE = Cursor.NW_RESIZE_CURSOR;
	public final int SE_RESIZE = Cursor.SE_RESIZE_CURSOR;
	public final int SW_RESIZE = Cursor.SW_RESIZE_CURSOR;

	public final int NONE   = MouseEvent.NOBUTTON;
	public final int LEFT   = MouseEvent.BUTTON1;
	public final int MIDDLE = MouseEvent.BUTTON2;
	public final int RIGHT  = MouseEvent.BUTTON3;

	private Ucigame ucigame;


	Mouse(Ucigame _u)
	{
		ucigame = _u;
	}


	public int x() { return ucigame.mouseX; }


	public int y() { return ucigame.mouseY; }

	public int Xchange() { return ucigame.mouseChangeX; }

	public int Ychange() { return ucigame.mouseChangeY; }

	public int button() { return ucigame.mouseButton; }

	public boolean isAltDown() { return ucigame.mouseIsAltDown; }

	public boolean isControlDown() { return ucigame.mouseIsControlDown; }

	public boolean isMetaDown() { return ucigame.mouseIsMetaDown; }

	public boolean isShiftDown() { return ucigame.mouseIsShiftDown; }

	public int wheelClicks() { return ucigame.mouseWheelUnits; }


	public Sprite sprite() { return ucigame.mouseSprite; }


	public void setCursor(int _c)
	{
		ucigame.setCursor(_c);
	}


	public void setCursor(Image _image, int _x, int _y)
	{
		ucigame.setCursor(_image, _x, _y);
	}

}
