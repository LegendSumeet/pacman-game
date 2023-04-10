package code.uci.pacman.controllers.utilities;

import java.util.*;
import java.util.concurrent.*;

import code.uci.pacman.game.GameState;

import ucigame.Sprite;


public abstract class SpriteController<T, S extends Sprite> {
	private static final long serialVersionUID = 1L;
	protected static GameState state = GameState.getInstance(); 
	//private HashMap<T, S> hash;
	private ConcurrentHashMap<T, S> hash;

	public SpriteController() {
       //hash = new HashMap<T, S>();
       hash = new ConcurrentHashMap<T, S>();
	}
	

	protected void addObject(T key, S object) {
		hash.put(key, object);
	}
	

	public S getObjectAt(T key) {
		return hash.get(key);
	}
	

	protected void destroyAt(T key) {
		if( hash.containsKey(key) )
		{
			hash.remove(key);
		}
	}
	
	

	public void drawObjects() {
		for (Sprite s : this.getObjects())
		{
			s.draw();
		}
	}
	

	public Collection<S> getObjects() {
		return hash.values();
	}
}
