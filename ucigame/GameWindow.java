package ucigame;

import java.awt.Dimension;

public class GameWindow
{
	private Ucigame ucigame;

	GameWindow(Ucigame _u)
	{
		ucigame = _u;
	}


	int clientWidth, clientHeight;
	String title;
	boolean showfps = false;


	public void size(int _width, int _height)
	{
		if (ucigame.isApplet)
		{
			clientWidth = ucigame.getWidth();
			clientHeight = ucigame.getHeight();
		}
		else if (50 <= _width && _width <= 2000 &&
				 50 <= _height && _height <= 2000)
		{
			ucigame.gameCanvas.setPreferredSize(new Dimension(_width, _height));
			ucigame.gameCanvas.setMinimumSize(new Dimension(_width, _height));
			clientWidth = _width;
			clientHeight = _height;
		}
		else
			size(100, 100);  // bad parm values, use reasonable defaults
	}


	public void title(String _title)
	{
		title = _title;
		if (ucigame.frame != null)
			ucigame.frame.setTitle(_title);
	}


	public void showFPS() { showfps = true; }


	public void hideFPS()
	{
		showfps = false;
		if (ucigame.frame != null)
			ucigame.frame.setTitle(title);
	}


	public void setfps(int _f)
	{
		if (showfps && ucigame.frame != null)
			ucigame.frame.setTitle(title + " (" + _f + ")");
	}


	int clientWidth() { return clientWidth; }


	int clientHeight() { return clientHeight; }
}
