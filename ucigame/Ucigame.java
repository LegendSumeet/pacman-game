

package ucigame;


import org.jdesktop.swingworker.SwingWorker;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;
import java.applet.*;
import java.lang.reflect.Method;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Ucigame
	extends JApplet
	implements MouseMotionListener,
			   MouseListener,
			   MouseWheelListener,
	           KeyListener,
	           FocusListener
{
	private static String VERSION = "2007.11.13";
	static Ucigame gameObject = null;  			 // only used when not an applet 
	protected static String rootImagePath = ""; //root path for images
	private static Object lock1 = new Object();  // for synchronization
	private static Object lock2 = new Object();  // for synchronization
	private static Object lock3 = new Object();  // for synchronization  of keysThatAreDown

	private static UcigameWorker worker = null;
	private static StaticUcigameWorker staticworker = null;
	static Ucigame ucigameObject = null;  		// seems to overlap gameObject; need both?

	public static void main(String[] commandLineArgs)
	{
		System.out.println("Ucigame version " + VERSION);
		if (commandLineArgs.length < 1)
		{
			logError("Please repeat the program name.  For example, java MyGame MyGame");
			System.exit(0);
		}

		String className = commandLineArgs[0];
		Object object = null;
		try {
			// see www.javageeks.com/Papers/ClassForName/ClassForName.pdf
			// for info on the next line
			Class<?> classDefinition = Class.forName(className, true,
									ClassLoader.getSystemClassLoader());
			object = classDefinition.newInstance();
			if (object instanceof Ucigame)
				gameObject = (Ucigame)object;
			else
			{
				logError("Class " + className + " does not extend Ucigame.");
				System.exit(0);
			}
		}
		catch (InstantiationException e) {
        	System.out.println(e);
        	System.exit(0);

		}
		catch (IllegalAccessException e) {
        	System.out.println(e);
        	System.exit(0);
		}
		catch (ClassNotFoundException e) {
        	logError("No class found with name " + className);
        	System.exit(0);
		}
		catch (NoClassDefFoundError e) {
        	logError("No class definition found with name " + className);
        	System.exit(0);
		}

		gameObject.isApplet = false;

		// Execute a job on the event-dispatching thread:
		// creating this applet's GUI.
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					gameObject.setupGUI();
				}
			});
		} catch (Exception e) {
			System.err.println("setupGUI didn't successfully complete\n" + e);
			e.printStackTrace();
		}
		staticworker = new StaticUcigameWorker();
		staticworker.execute();   // returns immediately, background thread continues
	}



	private String appletID = "";
	static private int appCount = 0;

	// Initialize the game.
	final public void init()
	{
		appletID = "" + appCount + " " + this.hashCode();
		appCount++;
		System.out.println(appletID + " Ucigame version " + VERSION);
		System.out.println(appletID + " applet.init()");
		isApplet = true;

		// Execute a job on the event-dispatching thread to create the GUI.
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					setupGUI();
				}
			});
		} catch (Exception e) {
			System.err.println(appletID + " setupGUI didn't successfully complete\n" + e);
			e.printStackTrace();
		}
	}

	final public void start()
	{
		System.err.println(appletID + " applet.start()");
		worker = new UcigameWorker();
		worker.execute();           // runs in a different thread
		//System.err.println("applet.start() is done");
	}

	@SuppressWarnings("unused")
	private boolean workerIsDone;

	final public void stop()
	{
		System.err.println(appletID + " applet.stop()");
		for (Sound s : soundsPossiblyPlaying)
			s.stop();
		soundsPossiblyPlaying.clear();
		workerIsDone = false;
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					worker.cancel(true);  // send worker thread an interrupt
				}
			});
		} catch (Exception e) {
			System.err.println(appletID + " worker.cancel(true) didn't successfully complete\n" + e);
			e.printStackTrace();
		}

		// The following code is a work-around to address the problem
		// that stop() can complete, and destroy() can be run and complete,
		// before the UcigameWorker thread is done.  This generates the
		// following message in the Java Console:

		// Exception in thread "SwingWorker-pool-2-thread-1" java.lang.IllegalMonitorStateException


		//while (!workerIsDone)
		//{
		//	try {
		//		Thread.sleep(50);
		//	}
		//	catch (InterruptedException ie) {;}
		//}

		//System.err.println("applet.stop() is done");
	}

	final public void destroy()
	{
		System.err.println(appletID + " applet.destroy()");
	}

	////// Constants for use by game and intrapackage///////////
	public static final int FILL = 982;
	public static final int SET = 20001;
	public static final int ADD = 20002;
	public static final int ADDONCE = 20004;
	public static final int MULTIPLY = 20003;
	public static final int TOP = 21001;
	public static final int BOTTOM = 21002;
	public static final int LEFT = 21003;
	public static final int RIGHT = 21004;
	public static Sprite TOPEDGE;
	public static Sprite BOTTOMEDGE;
	public static Sprite LEFTEDGE;
	public static Sprite RIGHTEDGE;
	public static final Sprite PIXELPERFECT = new Sprite(1, 1);
	public static final int BOLD   = Font.BOLD;
	public static final int PLAIN  = Font.PLAIN;
	public static final int ITALIC = Font.ITALIC;
	public static final int BOLDITALIC = Font.BOLD | Font.ITALIC;


	////// accessible within the package
	boolean isApplet;
	int mouseX, mouseY;
	int mouseChangeX, mouseChangeY, mousePrevX, mousePrevY;
	int mouseButton;
	boolean mouseIsAltDown, mouseIsControlDown, mouseIsMetaDown, mouseIsShiftDown;
	int mouseWheelUnits;
	Sprite mouseSprite;
	Graphics2D offG;
	int goalFPS = 0;
	Vector<Sprite> spritesFromBottomToTopList = new Vector<Sprite>();
	Hashtable<String, Method> name2method = new Hashtable<String, Method>();
	Sprite edgeLeft, edgeRight, edgeTop, edgeBottom;
	HashMap<String,String> keysThatAreDown = new HashMap<String, String>();
	int lastKeyPressed;
	boolean shiftPressed, ctrlPressed, altPressed;
	boolean typematicIsOff = false;
	Vector<Sound> soundsPossiblyPlaying = new Vector<Sound>();

	JFrame frame;
	GameCanvas gameCanvas;
	java.awt.Image offscreen;
	Color bgColor;
	Image bgImage;
	Font windowFont = null;


	private Random rand = null;
	private Vector<Sprite> buttonList = new Vector<Sprite>();
	private Vector<Sprite> buttonList2 = new Vector<Sprite>(); // these get appended to buttonList
	private Method[] methods = null;
	private String currScene = null;
	private Method startSceneMethod = null;
	private Method sceneKeyPressMethod = null;

	private int delayTime = 0;		// means no refreshing
	private int fps = 0;
	//private Font fontFPS;

	private javax.swing.Timer fpsTimer = null;
	private int frames = 0;
	private boolean playing;
	private boolean suspended = false;
	private boolean oneStep = false;
	private Vector<Timer> timers = new Vector<Timer>();
	private Vector<Timer> timers2 = new Vector<Timer>();  // to add later
	private Vector<Timer> timers3 = new Vector<Timer>();  // to remove later

	// Informational variables accessible to the game.
	protected Mouse mouse = new Mouse(this);
	public GameWindow window = new GameWindow(this); // visible in package, too
	protected Canvas1 canvas = new Canvas1(this);
	protected Keyboard keyboard = new Keyboard(this);

	private void setupGUI()
	{
		ucigameObject = this;
		// Create the window.
		if (!isApplet)
		{
			frame = new JFrame("No Title");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
        gameCanvas = new GameCanvas(this);
        gameCanvas.setOpaque(true);
        if (isApplet)
        	this.getContentPane().add(gameCanvas, BorderLayout.CENTER);
        else
        	frame.setContentPane(gameCanvas);

        if (isApplet)
        {
        	this.addFocusListener(this);
		}
        else
        	frame.addFocusListener(this);

        gameCanvas.addMouseMotionListener(this);
        gameCanvas.addMouseListener(this);
        gameCanvas.addMouseWheelListener(this);
        gameCanvas.addKeyListener(this);
        gameCanvas.addFocusListener(gameCanvas);
        gameCanvas.requestFocusInWindow();

        // Let the game set up the window, especially its size.
        window.size(100, 100);		// in case setup() has no size()
  		canvas.background(255);		// in case background is not called, default is white
        setup();

        // set default framerate if none in setup
        if (goalFPS == 0)
        	framerate(10);

        // Display the window.
        if (isApplet)
			this.setVisible(true);
		else
        {
			frame.pack();
			frame.setVisible(true);
		}

		// Set up the offscreen buffer
		if (isApplet)
			offscreen = this.createImage(canvas.width(), canvas.height());
		else
			offscreen = frame.createImage(canvas.width(), canvas.height());
		System.out.flush();
		offG = (Graphics2D)offscreen.getGraphics();
		offG.setColor(bgColor);
		offG.fillRect(0, 0, canvas.width(), canvas.height());
		offG.dispose();
		offG = null;

		createEdgeSprites();

		playing = true;
	}

	int r(double _x) { return (int)(Math.round(_x)); }  // should get rid of this


	// The following methods are meant to be called by the game code.


	public final void framerate(double _d)
	{
		int fr = (int)_d;
		if (0 < fr && fr <= 1000)
		{
			goalFPS = fr;
			delayTime = 1000 / fr;
		}
		if (fpsTimer != null)
			fpsTimer.stop();
		fpsTimer = new javax.swing.Timer(1000, fpsChecker);
		fpsTimer.start();
	}


	public final int actualFPS()
	{
		return fps;
	}


	public final void randomSeed(int _seed)
	{
		rand = new Random(_seed);
	}


	public final double random(double _limit)
	{
		if (rand == null)
			rand = new Random();
		if (_limit > 0.0)
			return rand.nextDouble() * _limit;
		else
			return 0.0;
	}


	public final double random(double _lowerlimit, double _upperlimit)
	{
		if (rand == null)
			rand = new Random();
		if (_upperlimit > _lowerlimit)
			return (rand.nextDouble() * (_upperlimit - _lowerlimit)) + _lowerlimit;
		else
			return _lowerlimit;
	}

	public final int randomInt(int _limit)
	{
		if (rand == null)
			rand = new Random();
		if (_limit <= 0)
			return 0;
		return rand.nextInt(_limit);
	}


	public final int randomInt(int _lowerlimit, int _upperlimit)
	{
		if (rand == null)
			rand = new Random();
		if (_upperlimit > _lowerlimit)
			return rand.nextInt(_upperlimit-_lowerlimit) + _lowerlimit;
		return _lowerlimit;
	}

	public final String[] arrayOfAvailableFonts()
	{
		return GraphicsEnvironment.
					getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}

	public final boolean isAvailableFont(String _fontName)
	{
		String fonts[] = GraphicsEnvironment.
					getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String f : fonts)
		{
			if (f.equals(_fontName))
				return true;
		}
		return false;
	}
	

	public final Image getImage(String _filename)  // dangerous having same name as Applet.getImage()?
	{
		_filename = rootImagePath + _filename;
		java.awt.Image i;
		if (isApplet)
			i = getImage(getCodeBase(), _filename);
		else
			i = Toolkit.getDefaultToolkit().getImage(_filename);
		if (i == null)
			logError("getImage(" + _filename + ") failed [1].");
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(i, 0);
		try { mt.waitForAll(); }
		catch (InterruptedException ie) {}

		//System.err.println("Done with image in " + _filename +
		//	" width: " + i.getWidth(this) +
		//	" height: " + i.getHeight(this) +
		//	" is BufferedImage: " + (i instanceof BufferedImage) );
		//System.err.println(i);

		if (i.getWidth(this) == -1 || i.getHeight(this) == -1)
		{
			logError("getImage(" + _filename + ") failed.");
			return null;
		}
		return new Image(i, this);
	}


	public final Image getImage(String _filename, int _c)
	{
		Image gi = getImage(_filename);
		gi.transparent(_c);
		return gi;
	}


	public final Image getImage(String _filename, int _r, int _g, int _b)
	{
		Image gi = getImage(_filename);
		gi.transparent(_r, _g, _b);
		return gi;
	}

	public final Sound getSound(String _filename)
	{
		if (_filename.toLowerCase().endsWith(".mp3"))
		{
			// make sure the file can be opened, for an immediate fail if not.
			if (!isApplet)
			{
				try {
					new FileInputStream(_filename);
				}
				catch (FileNotFoundException fnf)
				{
					logError("getSound(" + _filename + ") failed [3].");
					return null;
				}
			}
			return new SoundMP3(_filename, this);
		}

		// not an mp3, use Applet's AudioClip functionality
		AudioClip clip;
		try {
			if (isApplet)
				clip = getAudioClip(getCodeBase(), _filename);
			else
				clip = Applet.newAudioClip(
							getClass().getResource(_filename));
		}
		catch (NullPointerException npe)
		{
			logError("getSound(" + _filename + ") failed [1].");
			return null;
		}
		if (clip == null)
		{
			logError("getSound(" + _filename + ") failed [2].");
			return null;
		}
		return new SoundAudioClip(clip, this);
	}


	public final Sprite makeSprite(Image _image)
	{
		if (_image == null ||
		    _image.width() < 1 ||
		    _image.height() < 1)
		{
			logError("makeSprite(image): image is invalid.");
			return null;
		}
		return new Sprite(_image);
	}


	public final Sprite makeSprite(int _width, int _height)
	{
		if (_width < 1 || _width > 1000 ||
		    _height < 1 || _height > 1000)
		{
			logError("makeSprite(image, " + _width + ", " + _height +
						") has an illegal parameter.");
			return null;
		}
		return new Sprite(_width, _height);
	}


	public final Sprite makeSprite(Image _image, int _width, int _height)
	{
		if (_width < 1 || _width > 1000 ||
		    _height < 1 || _height > 1000)
		{
			logError("makeSprite(image, " + _width + ", " + _height +
						") has an illegal parameter.");
			return new Sprite(_image);
		}
		BufferedImage newImage = new BufferedImage(_width, _height,
		                              BufferedImage.TYPE_INT_ARGB);
		// Copy image to buffered image
		Graphics g = newImage.createGraphics();
		for (int x=0; x < _width; x+=_image.iwidth)
		{
			for (int y=0;  y < _height; y+=_image.iheight)
				g.drawImage(_image.buffImage, x, y, this);
		}
		g.dispose();

		return new Sprite(new Image(newImage, this));
	}


	public final Sprite makeButton(String _name, Image _image, int _width, int _height)
	{
		if (_width < 1 || _width > 1000 ||
		    _height < 1 || _height > 1000)
		{
			logError("makeButton(" + _name + ", image, " + _width + ", " + _height +
						") has an illegal size parameter.");
			return new Sprite(5, 5);
		}
		if (_name == null || _name.length() == 0)
		{
			logError("makeButton(" + _name + ", image, " + _width + ", " + _height +
						") has an illegal name.");
			return new Sprite(_width, _height);
		}
		if (_image == null ||
		    _image.width() < 1 ||
		    _image.height() < 1)
		{
			logError("in makeButton(" + _name + ") the image (second parameter) " +
					"is not valid.");
			return new Sprite(_width, _height);
		}
		if ( (_image.width() == _width && _image.height() == _height) ||
		     (_image.width() == _width && _image.height() == 3*_height) ||
		     (_image.width() == 3*_width && _image.height() == _height))
		     ; // good
		else
		{
			logError("in makeButton(" + _name + ") the width and height of the " +
					"image must be (" + _width + "," + _height + ") or (" +
					(_width*3) + "," + _height + ") or (" +
					_width + "," + (_height*3) + ")\nFound width =" +
					_image.width() + " height =" + _image.height());
			return new Sprite(_width, _height);
		}
		Sprite s = new Sprite(_width, _height);
		s.addFrame(_image, 0, 0);
		if (_image.width() == _width && _image.height() == 3*_height)
		{
			s.addFrame(_image, 0, _height);
			s.addFrame(_image, 0, _height*2);
		}
		else if (_image.width() == 3*_width) // ordered horizontally
		{
			s.addFrame(_image, _width, 0);
			s.addFrame(_image, _width*2, 0);
		}
		else // just a single image, use for all three frames
		{
			s.addFrame(_image, 0, 0);
			s.addFrame(_image, 0, 0);
		}

		if (methods == null)
			methods = this.getClass().getDeclaredMethods();
		boolean ok = false;
		for (Method m : methods)
		{
			if (m.getName().equals("onClick" + _name) &&
				m.getReturnType().toString().equals("void") &&
				m.getParameterTypes().length == 0)
				{
					name2method.put(_name, m);
					ok = true;
					break;
				}
		}
		if (!ok)
		{
			logError("Required method void onClick" + _name + "() not found.");
			return s;
		}
		s.makeButton(_name);
		synchronized(lock2)
		{
			buttonList2.add(s);
		}

		return s;
	}


	public final Sprite makeTiledSprite(int _cols, int _rows, int _width, int _height)
	{
		if (_cols < 1 || _cols > 1000 ||
		    _rows < 1 || _rows > 1000)
		{
			logError("makeTiledSprite(" + _cols + ", " + _rows +
			                ", " + _width + ", " + _height +
						") has an illegal number of columns or rows.");
			return new Sprite(5, 5);
		}
		if (_width < 1 || _width > 1000 ||
		    _height < 1 || _height > 1000)
		{
			logError("makeTiledSprite(" + _cols + ", " + _rows +
			                ", " + _width + ", " + _height +
						") has an illegal width or height.");
			return new Sprite(5, 5);
		}
		return new Sprite(_cols, _rows, _width, _height);
	}


	private void createEdgeSprites()  // TODO: don't need big width & height
	{
		int edgeWidth = 1000;
		int overlap = 500;
		int halfOverlap = overlap / 2;
		TOPEDGE = new Sprite(canvas.width()+overlap, edgeWidth);
		TOPEDGE.position(-halfOverlap, -edgeWidth);
		TOPEDGE.hide();
		BOTTOMEDGE = new Sprite(canvas.width()+overlap, edgeWidth);
		BOTTOMEDGE.position(-halfOverlap, canvas.height());
		BOTTOMEDGE.hide();
		LEFTEDGE = new Sprite(edgeWidth, canvas.height()+overlap);
		LEFTEDGE.position(-edgeWidth, -halfOverlap);
		LEFTEDGE.hide();
		RIGHTEDGE = new Sprite(edgeWidth, canvas.height()+overlap);
		RIGHTEDGE.position(canvas.width(), -halfOverlap);
		RIGHTEDGE.hide();
	}


	public final void startScene(String _name)
	{
		if (name2method.get(_name) == null)
		{
			if (methods == null)
				methods = this.getClass().getDeclaredMethods();
			boolean ok = false;
			for (Method m : methods)
			{
				if (m.getName().equals("draw" + _name) &&
					m.getReturnType().toString().equals("void") &&
					m.getParameterTypes().length == 0)
					{
						name2method.put(_name, m);
						ok = true;
						break;
					}
			}
			if (!ok)
			{
				logError("Required method void draw" + _name + "() not found.");
				return;
			}
		}
		currScene = _name;
		startSceneMethod = null;
		for (Method m : methods)
		{
			if (m.getName().equals("start" + _name) &&
				m.getReturnType().toString().equals("void") &&
				m.getParameterTypes().length == 0)
				{
					startSceneMethod = m;
				}
			if (m.getName().equals("onKeyPress" + _name) &&
				m.getReturnType().toString().equals("void") &&
				m.getParameterTypes().length == 0)
				{
					sceneKeyPressMethod = m;
				}
		}
	}

	public final void startTimer(String _name, double _millisBetween)
	{
		//start again
		if (_millisBetween < 1)
		{
			logError("Invalid second parameter in startTimer(" + _name +
					 ", " + _millisBetween + ")");
			return;
		}
		if (methods == null)
			methods = this.getClass().getDeclaredMethods();
		for (Method m : methods)
		{
			if (m.getName().equals(_name + "Timer") &&
				m.getReturnType().toString().equals("void") &&
				m.getParameterTypes().length == 0)
				//*******NEED TO CHECK FOR PUBLIC********
				{
					timers2.add(new Timer( (long)_millisBetween, m));
					return;
				}
		}
		logError("Required method public void " + _name + "Timer() not found.");
		return;
	}

	public final void stopTimer(String _name)
	{
		for (Timer t : timers)
		{
			Method m = t.timerMethod;
			if (m.getName().equals(_name + "Timer"))
			{
				timers3.add(t);		// a delayed timers.remove(t);
				return;
			}
		}
		for (Timer t : timers2)   // just in case a startTimer and stopTimer call are
		{                         // made closely together
			Method m = t.timerMethod;
			if (m.getName().equals(_name + "Timer"))
			{
				timers2.remove(t);
				return;
			}
		}
		logError("No timer found with name " + _name);
		return;
	}
	
	public void restartTimer(String _name, double d) {
		//stop if already going
		if (doesTimerExist(_name)) {
			stopTimer(_name);
		}
		startTimer(_name, d);
	}
	
	private boolean doesTimerExist(String _name) {
		for (Timer t : timers)
		{
			Method m = t.timerMethod;
			if (m.getName().equals(_name + "Timer"))
			{
				return true;
			}
		}
		for (Timer t : timers2)   // just in case a startTimer and stopTimer call are
		{                         // made closely together
			Method m = t.timerMethod;
			if (m.getName().equals(_name + "Timer"))
			{
				return true;
			}
		}
		return false;
	}

	public final void print(String x)  { System.out.print(x); }
	public final void print(int x)     { System.out.print("" + x); }
	public final void print(short x)   { System.out.print("" + x); }
	public final void print(char x)    { System.out.print("" + x); }
	public final void print(double x)  { System.out.print("" + x); }
	public final void print(float x)   { System.out.print("" + x); }
	public final void print(long x)    { System.out.print("" + x); }
	public final void print(boolean x) { System.out.print("" + x); }
	public final void print(Object x)  { System.out.print(x); }
	public final void println(String x)  { System.out.println(x); }
	public final void println(int x)     { System.out.println("" + x); }
	public final void println(short x)   { System.out.println("" + x); }
	public final void println(char x)    { System.out.println("" + x); }
	public final void println(double x)  { System.out.println("" + x); }
	public final void println(float x)   { System.out.println("" + x); }
	public final void println(long x)    { System.out.println("" + x); }
	public final void println(boolean x) { System.out.println("" + x); }
	public final void println(Object x)  { System.out.println(x); }

//	 The following methods are often overridden by the game code.

	public void setup() {
		window.size(100, 100);
		canvas.background(220);
	}


	public void draw() {}


	public void onKeyPress() {}

	//**
	// * This method should be coded if the program needs to be alerted when the user holds a key down
	// * on the keyboard.
	// *
	// */
	//public void onKeyDown() {}


	public void onMousePressed() {}


	public void onMouseMoved() {}


	public void onMouseDragged() {}


	public void onMouseReleased() {}


	public void onMouseWheelMoved() {}


	public final void mouseDragged(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseChangeX = mouseX - mousePrevX;
		mouseChangeY = mouseY - mousePrevY;
		mousePrevX = mouseX;
		mousePrevY = mouseY;
		if (e.getButton() != MouseEvent.NOBUTTON)	// I'm getting NOBUTTON with drag,
			mouseButton = e.getButton();	// so leave mouseButton from Pressed.
		mouseIsAltDown = e.isAltDown();
		mouseIsControlDown = e.isControlDown();
		mouseIsMetaDown = e.isMetaDown();
		mouseIsShiftDown = e.isShiftDown();
		synchronized(lock2)
		{
			buttonList.addAll(buttonList2);
			buttonList2.clear();
			for (Sprite s : buttonList)
				s.buttonAction('D', mouseX, mouseY);
		}
		checkSpritesAndMouse();
		onMouseDragged();
	}

	/**
	 * Called when the mouse is moved and no button is being held down.
	 */
	public final void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseChangeX = 0;
		mouseChangeY = 0;
		mouseButton = e.getButton();
		mouseIsAltDown = e.isAltDown();
		mouseIsControlDown = e.isControlDown();
		mouseIsMetaDown = e.isMetaDown();
		mouseIsShiftDown = e.isShiftDown();
		//System.out.println("mouse x: " + mouseX + " mouse y: " + mouseY);
		synchronized(lock2)
		{
			buttonList.addAll(buttonList2);
			buttonList2.clear();
			for (Sprite s : buttonList)
				s.buttonAction('M', mouseX, mouseY);
		}
		checkSpritesAndMouse();
		onMouseMoved();
	}


	public final void mousePressed(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseChangeX = 0;
		mouseChangeY = 0;
		mousePrevX = mouseX;
		mousePrevY = mouseY;
		mouseButton = e.getButton();
		mouseIsAltDown = e.isAltDown();
		mouseIsControlDown = e.isControlDown();
		mouseIsMetaDown = e.isMetaDown();
		mouseIsShiftDown = e.isShiftDown();
		synchronized(lock2)
		{
			buttonList.addAll(buttonList2);
			buttonList2.clear();
			for (Sprite s : buttonList)
				s.buttonAction('P', mouseX, mouseY);
		}
		checkSpritesAndMouse();
		onMousePressed();
	}

	public final void mouseReleased(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
		mouseChangeX = 0;
		mouseChangeY = 0;
		mouseButton = e.getButton();
		mouseIsAltDown = e.isAltDown();
		mouseIsControlDown = e.isControlDown();
		mouseIsMetaDown = e.isMetaDown();
		mouseIsShiftDown = e.isShiftDown();
		synchronized(lock2)
		{
			buttonList.addAll(buttonList2);
			buttonList2.clear();
			for (Sprite s : buttonList)
				s.buttonAction('R', mouseX, mouseY);
			buttonList.addAll(buttonList2);
			buttonList2.clear();
		}
		checkSpritesAndMouse();
		onMouseReleased();
	}


	public final void mouseWheelMoved(MouseWheelEvent e)
	{
		mouseWheelUnits = e.getWheelRotation();
		onMouseWheelMoved();
	}



	private void checkSpritesAndMouse()
	{
		mouseSprite = null;
		synchronized(lock1)  // prevent it from getting nulled out
		{
			for (int i=spritesFromBottomToTopList.size()-1; i >= 0; i--)
			{
				Sprite s = spritesFromBottomToTopList.get(i);
				if ( mouseX >= s.currX &&
					 mouseX <  (s.currX + s.getCurrentSize().x) &&
					 mouseY >= s.currY &&
					 mouseY <  (s.currY + s.getCurrentSize().y)
				   )
				{
					mouseSprite = s;
					break;
				}
			}
		}
	}

	final void addSpriteToList(Sprite _sprite)
	{
		synchronized(lock1)
		{
			spritesFromBottomToTopList.add(_sprite);
		}
	}

	public final void mouseClicked(MouseEvent e) {}

	public final void mouseEntered(MouseEvent e) {}

	public final void mouseExited(MouseEvent e) {}


	public final void keyPressed(KeyEvent e)
	{
		lastKeyPressed = e.getKeyCode();		// returned by keyboard.key()
		//System.out.println("lastKeyPressed: " + lastKeyPressed);

       //System.out.print(" ID=" + e.getID());
       //System.out.print(" KeyCode=" + e.getKeyCode());
       //System.out.print("KeyChar=\"" + e.getKeyChar() + "\" " + (int)e.getKeyChar());
       //System.out.println();

		shiftPressed = e.isShiftDown();
		ctrlPressed  = e.isControlDown();
		altPressed   = e.isAltDown();
		if (lastKeyPressed == KeyEvent.VK_ESCAPE)
		{
			if (shiftPressed)
				suspended = !suspended;
			else
				playing = false;
		}
		else if (suspended && lastKeyPressed == KeyEvent.VK_F1)
		{
			suspended = false;
			oneStep = true;
		}
		else
		{
			String loc = "X";
			//if (e.getKeyLocation() == e.KEY_LOCATION_LEFT) loc = "L";
			//else if (e.getKeyLocation() == e.KEY_LOCATION_RIGHT) loc = "R";
			//else if (e.getKeyLocation() == e.KEY_LOCATION_NUMPAD) loc = "N";
			String hashkey = loc + e.getKeyCode();
			synchronized(lock3)
			{
				if (typematicIsOff &&
				    keysThatAreDown.get("Y" + e.getKeyCode()) != null)
				    ; // do nothing, we are supressing multiple keyPress events
				else
					keysThatAreDown.put(hashkey, "*");		// note that key is down
			}
		}
	}

	public final void keyReleased(KeyEvent e)
	{
		String loc = "X";
		//if (e.getKeyLocation() == e.KEY_LOCATION_LEFT) loc = "L";
		//else if (e.getKeyLocation() == e.KEY_LOCATION_RIGHT) loc = "R";
		//else if (e.getKeyLocation() == e.KEY_LOCATION_NUMPAD) loc = "N";
		String hashkey = loc + e.getKeyCode();
		synchronized(lock3)
		{
			keysThatAreDown.remove(hashkey);
			keysThatAreDown.remove("Y" + e.getKeyCode());
		}
	}

	public final void keyTyped(KeyEvent e) {}


	public void focusGained(FocusEvent e) {
		gameCanvas.requestFocus();
	}

	public void focusLost(FocusEvent e)
	{
		// all keys go "up" when focus is lost
		synchronized(lock3)
		{
			keysThatAreDown = new HashMap<String, String>();
		}
	}

	// returns a new HashMap that is identical to the HashMap
	// passed in, except that any keys starting with "X" are
	// modified to start with "Y".  A "Y" start is used when
	// typematic is turned off, to indicate that the key was
	// pressed down, was handled once by the game's onKeyDown(),
	// and that further keyPressed events from Java should be ignored.
	private HashMap<String, String> flagKeysThatAreDown(HashMap<String, String> _hm)
	{
		HashMap<String, String> newHM = new HashMap<String, String>();
		for (String s : _hm.keySet())
		{
			if (s.startsWith("X"))
				newHM.put("Y" + s.substring(1), "*");
			else
				newHM.put(s, "*");
		}
		return newHM;
	}

	/////// protected methods called from within the package
	protected final void setCursor(int _c)  // called by Mouse.setCursor()
	{
		Cursor cursor = null;
		try {
			cursor = new Cursor(_c);
		}
		catch (IllegalArgumentException iae)
		{
			logError("mouse.setCursor() called with invalid argument.");
			return;
		}
		if (isApplet)
			Ucigame.this.setCursor(cursor);
		else
			gameCanvas.setCursor(cursor);
	}

	protected final void setCursor(Image _image, int _x, int _y)
	{								 // called by Mouse.setCursor()
		Cursor cursor = null;
		Toolkit tk = Toolkit.getDefaultToolkit();
		cursor = tk.createCustomCursor(_image.getBufferedImage(),
								new Point(_x,_y),"customCursor");
		if (isApplet)
			Ucigame.this.setCursor(cursor);
		else
			gameCanvas.setCursor(cursor);

	}


	static String version() { return VERSION; }



	static void logError(String _s)
	{
		JOptionPane.showMessageDialog(null, _s, "Ucigame error", JOptionPane.ERROR_MESSAGE);
		if (gameObject != null)
			System.exit(0);
	}

	static void logWarning(String _s)
	{
		JOptionPane.showMessageDialog(null, _s, "Ucigame error", JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings("unused")
	private Graphics2D getOffG() { return offG; }

	ActionListener fpsChecker = new ActionListener() {
		int prevFrames = 0;
		public void actionPerformed(ActionEvent event) {
			fps = frames - prevFrames;
			window.setfps(fps);
			if (isApplet && window.showfps)
				showStatus("Ucigame fps: " + fps);
			if (fps > 0 & !suspended)
			{
				//System.out.print("goalFPS: " + goalFPS + " fps: " +
				//		fps + " old delayTime: " + delayTime);
				if (fps < goalFPS - 5)
					delayTime = delayTime - 3;
				else if (fps < goalFPS)
					delayTime = delayTime - 1;
				else if (fps > goalFPS + 5)
					delayTime = delayTime + 3;
				else if (fps > goalFPS)
					delayTime = delayTime + 1;
				// else fps == delayFPS
				if (delayTime < 1)
					delayTime = 1;
				//System.out.println(" new delayTime: " + delayTime);
			}
			prevFrames = frames;
		}
	};

	//inner class
	class Timer
	{
		private long blastOffTime;   // when the timer method should next be called
		private long pauseLength;    // interval
		private Method timerMethod;

		Timer(long _pause, Method _m)
		{
			blastOffTime = System.currentTimeMillis() + _pause;
			pauseLength = _pause;
			timerMethod = _m;
		}
	}

	// this non-static version is used by Applets
	class UcigameWorker extends SwingWorker<String, Void>
	{
		@Override
		protected void done()
		{
			workerIsDone = true;		// communicate with Applet.stop()
			//System.err.println("worker is done");
		}

		@Override
		public String doInBackground()
		{
        // Redraw the window periodically.
        while (playing && !isCancelled())
        {
			if (!suspended)
			{
				long now = System.currentTimeMillis();
				for (Timer t : timers)
				{
					if (t.blastOffTime < now)
					{
						t.blastOffTime += t.pauseLength;
						try {
							t.timerMethod.invoke(Ucigame.this);
						}
						catch (Exception ex) {
							ex.printStackTrace(System.err);
							logError("Exception3ta while invoking " + t.timerMethod.getName()
									+ "\n" + ex + "\n" + ex.getCause());
						}
					}
				}
				timers.addAll(timers2);		// append timers2 to the end of timers
				timers.removeAll(timers3);	// zap those in timers3
				timers2.clear();			// and clear timers2
				timers3.clear();			// likewise

				offG = (Graphics2D)offscreen.getGraphics();
				if (offG == null)
					logError("Internal error: null offG in doInBackground()");
				else
				{
					synchronized(lock1)
					{
						spritesFromBottomToTopList.removeAllElements();
					}
					if (currScene == null)
					{
						synchronized(lock3)
						{
							if (!keysThatAreDown.isEmpty())
								onKeyPress();
							if (typematicIsOff)
								keysThatAreDown = flagKeysThatAreDown(keysThatAreDown);
						}
						draw();
					}
					else
					{
						if (startSceneMethod != null)
						{
							try { startSceneMethod.invoke(
										isApplet? this : gameObject); }
							catch (Exception ex) {
								ex.printStackTrace(System.err);
								logError("Exception1 while invoking " + startSceneMethod.getName()
										+ "\n" + ex + "\n" + ex.getCause());
							}
							startSceneMethod = null;
						}
						if (!keysThatAreDown.isEmpty())
						{
							synchronized(lock3)
							{
								if (sceneKeyPressMethod == null)
									onKeyPress();
								else
								{
									try {
										sceneKeyPressMethod.invoke(isApplet? this : gameObject);
									}
									catch (Exception ex) {
										ex.printStackTrace(System.err);
										logError("Exception3a while invoking " +
												sceneKeyPressMethod.getName() +
												"\n" + ex + "\n" + ex.getCause());
									}
								}
								if (typematicIsOff)
									keysThatAreDown = flagKeysThatAreDown(keysThatAreDown);
							}
						}
						Method m = name2method.get(currScene);  // get drawForScene
						try {
							m.invoke(isApplet? this : gameObject);
						}
						catch (Exception ex) {
							ex.printStackTrace(System.err);
							logError("Exception2 while invoking " + m.getName()
									+ "\n" + ex + "\n" + ex.getCause());
						}
					}
					frames++;
					offG.dispose();
					offG = null;
				}
				if (isApplet)
					repaint();
				else
					gameCanvas.repaint();
				if (oneStep)
				{
					oneStep = false;
					suspended = true;
				}
			}
			if (goalFPS == 0)
				break;
			else
			{
				try { Thread.sleep(delayTime); }
				catch (InterruptedException ie) { break; }  // we're done
			}
		}
		//System.err.println("playing: " + playing + " isCancelled(): " + isCancelled());
		return "";
		}

	}


	static class StaticUcigameWorker extends SwingWorker<String, Void>
	{
		@Override
		protected void done()
		{
			//workerIsDone = true;		// communicate with Applet.stop()
			//System.err.println("worker is done");
		}

		@Override
		public String doInBackground()
		{
        // Redraw the window periodically.
        while (gameObject.playing && !isCancelled())
        {
			if (!gameObject.suspended)
			{
				long now = System.currentTimeMillis();
				for (Timer t : gameObject.timers)
				{
					if (t.blastOffTime < now)
					{
						t.blastOffTime += t.pauseLength;
						try {
							t.timerMethod.invoke(gameObject.isApplet? this : gameObject);
						}
						catch (Exception ex) {
							ex.printStackTrace(System.err);
							logError("Exception3t while invoking " + t.timerMethod.getName()
									+ "\n" + ex + "\n" + ex.getCause());
						}
					}
				}
				gameObject.timers.addAll(gameObject.timers2);		// append timers2 to the end of timers
				gameObject.timers.removeAll(gameObject.timers3);	// zap those in timers3
				gameObject.timers2.clear();			// and clear timers2
				gameObject.timers3.clear();			// likewise

				gameObject.offG = (Graphics2D)gameObject.offscreen.getGraphics();
				if (gameObject.offG == null)
					logError("Internal error: null offG in doInBackground()");
				else
				{
					synchronized(lock1)
					{
						gameObject.spritesFromBottomToTopList.removeAllElements();
					}
					if (gameObject.currScene == null)
					{
						synchronized(lock3)
						{
							if (!gameObject.keysThatAreDown.isEmpty())
								gameObject.onKeyPress();
							if (gameObject.typematicIsOff)
								gameObject.keysThatAreDown = gameObject.flagKeysThatAreDown(gameObject.keysThatAreDown);
						}
						gameObject.draw();
					}
					else
					{
						if (gameObject.startSceneMethod != null)
						{
							try { gameObject.startSceneMethod.invoke(
										gameObject.isApplet? this : gameObject); }
							catch (Exception ex) {
								ex.printStackTrace(System.err);
								logError("Exception1 while invoking " + gameObject.startSceneMethod.getName()
										+ "\n" + ex + "\n" + ex.getCause());
							}
							gameObject.startSceneMethod = null;
						}
						if (!gameObject.keysThatAreDown.isEmpty())
						{
							synchronized(lock3)
							{
								if (gameObject.sceneKeyPressMethod == null)
									gameObject.onKeyPress();
								else
								{
									try {
										gameObject.sceneKeyPressMethod.invoke(gameObject.isApplet? this : gameObject);
									}
									catch (Exception ex) {
										ex.printStackTrace(System.err);
										logError("Exception3a while invoking " +
												gameObject.sceneKeyPressMethod.getName() +
												"\n" + ex + "\n" + ex.getCause());
									}
								}
								if (gameObject.typematicIsOff)
									gameObject.keysThatAreDown = gameObject.flagKeysThatAreDown(gameObject.keysThatAreDown);
							}
						}
						Method m = gameObject.name2method.get(gameObject.currScene);  // get drawForScene
						try {
							m.invoke(gameObject.isApplet? this : gameObject);
						}
						catch (Exception ex) {
							ex.printStackTrace(System.err);
							logError("Exception2 while invoking " + m.getName()
									+ "\n" + ex + "\n" + ex.getCause());
						}
					}
					gameObject.frames++;
					gameObject.offG.dispose();
					gameObject.offG = null;
				}
				if (gameObject.isApplet)
					gameObject.repaint();
				else
					gameObject.gameCanvas.repaint();
				if (gameObject.oneStep)
				{
					gameObject.oneStep = false;
					gameObject.suspended = true;
				}
			}
			if (gameObject.goalFPS == 0)
				break;
			else
			{
				try { Thread.sleep(gameObject.delayTime); }
				catch (InterruptedException ie) { break; }  // we're done
			}
		}
		System.exit(0);
		return "";
		}

	}

}
