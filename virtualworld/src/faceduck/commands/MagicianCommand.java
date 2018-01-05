package faceduck.commands;

import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.actors.MagicianImpl;

public class MagicianCommand implements Command {

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException
	 *             If actor is not an instance of Magician.
	 */
	@Override
	public void execute(World world, Actor actor) {
		if (actor == null) {
			throw new NullPointerException("Actor cannot be null");
		} else if (world == null) {
			throw new NullPointerException("World cannot be null");
		} else if (!(actor instanceof MagicianImpl)) {
			throw new IllegalArgumentException("actor must be an instance of Actor.");
		}

		// call magician's magic function
		((MagicianImpl) actor).magic(world);
	}
}
