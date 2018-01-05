package faceduck.ai;

import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * The AI for a Fox. This AI will observe fox's view range and pick proper
 * action(move, eat, breed)
 */
public class FoxAI extends AbstractAI implements AI {
	// if fox energy > STUFFED_ENERGY, fox is full and breed.
	// STUTTED_ENERGY is must bigger than FOX_BREED_LIMIT(120)
	private static final int STUFFED_ENERGY = 130;

	/**
	 * The Fox's ai action selector
	 * 
	 * @param world
	 *            The world containing this fox
	 * 
	 * @param actor
	 *            The fox object
	 */
	@Override
	public Command act(World world, Actor actor) {
		// load fox's current info
		int view_range = actor.getViewRange();
		Location fox_loc = world.getLocation(actor);
		int fox_x = fox_loc.getX();
		int fox_y = fox_loc.getY();
		int energy = ((Animal) actor).getEnergy();

		// initiate variables necessary for judge
		int distance = 100;
		Direction dir = Util.randomDir();
		Command cmd = new MoveCommand(dir);

		// if fox is full, only breed. so after it statement, fox is not full
		if (energy >= STUFFED_ENERGY) {
			cmd = new BreedCommand(dir);
			return cmd;
		}

		// observe fox's view range
		for (int y = fox_y - view_range; y <= fox_y + view_range; y++) {
			for (int x = fox_x - view_range; x <= fox_x + view_range; x++) {
				try {
					// get info about searching location
					Location search_loc = new Location(x, y);
					Object search_obj = world.getThing(search_loc);

					// find rabbit
					if (search_obj instanceof Rabbit) {
						int search_distance = fox_loc.distanceTo(search_loc);
						// fox is adjacent with rabbit, so eat it 
						if (search_distance == 1) {
							dir = fox_loc.dirTo(search_loc);
							cmd = new EatCommand(dir);
							return cmd;
						}

						// found rabbit is closer before that, set newly dir to rabbit 
						if (distance > search_distance) {
							distance = search_distance;
							dir = fox_loc.dirTo(search_loc);
						}
					}
					// if observe location is out of bound, ignore and continue next observe
				} catch (Exception e) {
					continue;
				}
			}
		}

		// find movable location in 4 times
		for (int i = 0; i < 4; i++) {
			Location mv_loc = new Location(fox_loc, dir);
			if (world.isValidLocation(mv_loc) != false && world.getThing(mv_loc) == null) {
				break;
			}
			
			dir = Util.randomDir();
		}

		// remained act is move
		cmd = new MoveCommand(dir);
		return cmd;
	}
}
