package faceduck.ai;

import faceduck.actors.Grass;
import faceduck.actors.MagicianImpl;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.commands.MagicianCommand;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;
import java.util.Set;

/**
 * The AI for a Magician. This AI will observe fox's view range and construct
 * necessary info. The action is only magic
 */
public class MagicianAI extends AbstractAI implements AI {

	/**
	 * The Magician's ai action constructor
	 * 
	 * @param world
	 *            The world containing this magician
	 * 
	 * @param actor
	 *            The magician object
	 */
	@Override
	public Command act(World world, Actor actor) {
		// initiate magician's info
		((MagicianImpl) actor).clear();

		// set teleport location
		Location random_loc = Util.randomEmptyLoc(world);
		if (random_loc == null)
			return null;

		// conduct only if empty location exist
		if (random_loc != null) {
			// get set of all objects in world, seek and sum rabbit and fox
			Set<Object> all_objs = world.getAllObjects();
			for (Object obj : all_objs) {
				if (obj instanceof Rabbit)
					((MagicianImpl) actor).addRabbitNum();
				else if (obj instanceof Fox)
					((MagicianImpl) actor).addFoxNum();
			}

			// get magician's teleport location info
			int magician_x = random_loc.getX();
			int magician_y = random_loc.getY();
			int view_range = actor.getViewRange();

			// set magician's teleport coordinate to class variable
			((MagicianImpl) actor).setMoveLocation(magician_x, magician_y);

			// observe view range
			for (int y = magician_y - view_range; y <= magician_y + view_range; y++) {
				for (int x = magician_x - view_range; x <= magician_x + view_range; x++) {
					try {
						Location search_loc = new Location(x, y);
						Object search_obj = world.getThing(search_loc);

						// find and add to vector
						if (search_obj instanceof Fox)
							((MagicianImpl) actor).addFoxObj(search_obj);
						else if (search_obj instanceof Grass)
							((MagicianImpl) actor).addGrassObj(search_obj);

						// if observe location is out of bound, ignore and continue next observe
					} catch (Exception e) {
						continue;
					}
				}
			}
		}

		Command cmd = new MagicianCommand();
		return cmd;
	}
}
