package ucigame;

import java.awt.Font;
import java.awt.Color;

/**
 * The canvas object on which the game is drawn. 
 */
public class Canvas1
{
	private Ucigame ucigame;

	Canvas1(Ucigame _u)
	{
		ucigame = _u;
	}

	public int width() { return ucigame.window.clientWidth(); }

	public int height() { return ucigame.window.clientHeight(); }


	public void background(int _c)
	{
		if (0 <= _c && _c <= 255)
		{
			ucigame.bgColor = new Color(_c, _c, _c);
			ucigame.bgImage = null;
		}
	}


	public void background(int _r, int _g, int _b)
	{
		if (0 <= _r && _r <= 255 &&
			0 <= _g && _g <= 255 &&
			0 <= _b && _b <= 255)
		{
			ucigame.bgColor = new Color(_r, _g, _b);
			ucigame.bgImage = null;
		}
	}


	public void background(Image _image)
	{
		ucigame.bgImage = _image;
		ucigame.bgColor = null;

	}


	public void color(int _c)
	{
		if (0 <= _c && _c <= 255)
			if (ucigame.offG != null)
				ucigame.offG.setColor(new Color(_c, _c, _c));
			else
				Ucigame.logError("canvas.color(" + _c + ") used outside of draw()");
	}


	public void color(int _r, int _g, int _b)
	{
		if (0 <= _r && _r <= 255 &&
			0 <= _g && _g <= 255 &&
			0 <= _b && _b <= 255)
			if (ucigame.offG != null)
				ucigame.offG.setColor(new Color(_r, _g, _b));
			else
				Ucigame.logError("canvas.color(" + _r + ", " + _g + ", " + _b + ") used outside of draw()");
	}


	public void clear()
	{
		if (ucigame.offG != null)
		{
			if (ucigame.bgColor != null)
			{
				Color c = ucigame.offG.getColor();
				ucigame.offG.setColor(ucigame.bgColor);
				ucigame.offG.fillRect(0, 0, ucigame.canvas.width(), ucigame.canvas.height());
				ucigame.offG.setColor(c);
			}
			else if (ucigame.bgImage != null)
			{
				ucigame.bgImage.draw(0, 0);
			}
		}
		else
			Ucigame.logError("canvas.clear() used outside of draw()");
	}



	public void line(double _x1, double _y1, double _x2, double _y2)
	{
		if (ucigame.offG != null)
			ucigame.offG.drawLine(ucigame.r(_x1), ucigame.r(_y1),
								ucigame.r(_x2), ucigame.r(_y2));
		else
			Ucigame.logError("canvas.line(" + _x1 + ", " + _y1 + ", " + _x2 + ", " + _y2 +
					 ") used outside of draw()");
	}


	public void oval(int _x1, int _y1, int _w, int _h)
	{
		if (ucigame.offG != null)
			ucigame.offG.drawOval(_x1, _y1, _w, _h);
		else
			Ucigame.logError("canvas.oval(" + _x1 + ", " + _y1 + ", " + _w + ", " + _h +
					 ") used outside of draw()");
	}

	public void oval(int _x1, int _y1, int _w, int _h, int _option)
	{
		if (_option != Ucigame.FILL)
			Ucigame.logError("canvas.oval used with last parameter not FILL");
		else if (ucigame.offG != null)
			ucigame.offG.fillOval(_x1, _y1, _w, _h);
		else
			Ucigame.logError("canvas.oval(" + _x1 + ", " + _y1 + ", " + _w + ", " + _h +
					 ", FILL) used outside of draw()");
	}


	public void font(String _name, int _style, int _size)
	{
		if (_style == Ucigame.BOLD || _style == Ucigame.PLAIN ||
			_style == Ucigame.ITALIC ||	_style == Ucigame.BOLDITALIC)
			;
		else
		{
			Ucigame.logError("Invalid style parameter in canvas.font()");
			_style = Ucigame.PLAIN;
		}
		ucigame.windowFont = new Font(_name, _style, _size);
		//System.out.println("Font: " + spriteFont);
		if (ucigame.windowFont.getFamily().equalsIgnoreCase(_name) ||
			ucigame.windowFont.getFontName().equalsIgnoreCase(_name))
			;
		else
			Ucigame.logWarning("Could not create font with name " + _name +
					". Using font " + ucigame.windowFont.getFontName() + " instead.");

	}
	
	public void font(String _name, int _style, int _size, int _r, int _g, int _b){
		this.font(_name, _style, _size);
		this.color(_r, _g, _b);
	}


	public void putText(int _n, double _x, double _y)
	{
		putText(""+_n, _x, _y);
	}


	public void putText(String _string, double _x, double _y)
	{
		if (ucigame.offG == null)
		{
			Ucigame.logError("canvas.putText(" + _string + "," + _x + ", " + _y +
					 ") used outside of draw()");
			return;
		}
		if (ucigame.windowFont != null)
			ucigame.offG.setFont(ucigame.windowFont);
		ucigame.offG.drawString(_string, (int)_x, (int)_y);
	}
}

