// GameCanvas.java

package ucigame;

import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;


class GameCanvas extends JComponent   // maybe should be JPanel 
                 implements FocusListener
{

	private static final long serialVersionUID = -210505118730338434L;
	private Ucigame ucigame;


	GameCanvas(Ucigame _u)
	{
		ucigame = _u;
	}


	public void update(Graphics g)
	{
		paintComponent(g);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (ucigame.offscreen != null)
			g.drawImage(ucigame.offscreen, 0, 0, this);
	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e)
	{
		// all keys go "up" when focus is released
		ucigame.keysThatAreDown = new HashMap<String, String>();
	}
}

