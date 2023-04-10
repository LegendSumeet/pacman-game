package code.uci.pacman.controllers.utilities;

import java.util.ArrayList;
import java.util.Collection;

import ucigame.Sprite;
import code.uci.pacman.objects.ControllableObject;


public abstract class ActorController<T, S extends ControllableObject> extends SpriteController<T, S> {
	protected abstract void constructActors();


	public Collection<S> getCollidedWith(Sprite c) {
		Collection<S> collidedObjects = new ArrayList<S>();
		for (S actor : super.getObjects()) {
			if (actor.collidedWith(c))
				collidedObjects.add(actor);
		}
		return collidedObjects;
	}
}
