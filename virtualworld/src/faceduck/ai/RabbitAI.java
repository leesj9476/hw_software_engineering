package faceduck.ai;

import faceduck.actors.Grass;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * The AI for a Rabbit. This AI will observe rabbit's view range and pick proper
 * action(move, eat, breed)
 */
public class RabbitAI extends AbstractAI implements AI {
	// if fox energy > STUFFED_ENERGY, fox is full and don't eat
	private static final int STUFFED_ENERGY = 18;

	// if found fox's distance with rabbit is DANGER_DISTANCE, rabbit run away from
	// fox
	private static final int DANGER_DISTANCE = 1;

	/**
	 * The rabbit's ai action selector
	 * 
	 * @param world
	 *            The world containing this rabbit
	 * 
	 * @param actor
	 *            The rabbit object
	 */
	@Override
	public Command act(World world, Actor actor) {
		// load fox's current info
		int view_range = actor.getViewRange();
		Location rabbit_loc = world.getLocation(actor);
		int rabbit_x = rabbit_loc.getX();
		int rabbit_y = rabbit_loc.getY();
		int energy = ((Animal) actor).getEnergy();
		int breed_limit = ((Animal) actor).getBreedLimit();

		// initiate variables necessary for judge
		// 1: eat, 2: move, 3: runaway
		int action = 2;
		int distance_from_fox = 100;
		Direction dir = Util.randomDir();
		Command cmd = null;

		// observe rabbit's view range
		for (int y = rabbit_y - view_range; y <= rabbit_y + view_range; y++) {
			for (int x = rabbit_x - view_range; x <= rabbit_x + view_range; x++) {
				try {
					// get info about searching location
					Location search_loc = new Location(x, y);
					Object search_obj = world.getThing(search_loc);
					int search_distance = rabbit_loc.distanceTo(search_loc);

					// find fox
					if (search_obj instanceof Fox) {
						// if fox's distance is over DANGER_DISTANCE and found grass already,
						// ignore fox and observe next location
						if (search_distance > DANGER_DISTANCE && action == 1)
							continue;

						// found fox is closer that before
						if (distance_from_fox > search_distance) {
							distance_from_fox = search_distance;

							// set flee direction(the opposite direction to fox)
							dir = rabbit_loc.dirTo(search_loc);
							if (dir == Direction.EAST)
								dir = Direction.WEST;
							else if (dir == Direction.WEST)
								dir = Direction.EAST;
							else if (dir == Direction.NORTH)
								dir = Direction.SOUTH;
							else
								dir = Direction.NORTH;

							// set action flag
							action = 3;
						}
						// find grass
					} else if (search_obj instanceof Grass) {
						// rabbit is full
						if (energy >= STUFFED_ENERGY)
							continue;

						// fox is far away and cannot found adjacent grass before
						if (distance_from_fox > DANGER_DISTANCE && action != 1) {
							dir = rabbit_loc.dirTo(search_loc);

							// eat
							if (search_distance == 1)
								action = 1;
							// move to grass
							else
								action = 2;
						}
					}
					// if observe location is out of bound, ignore and continue next observe
				} catch (Exception e) {
					continue;
				}
			}
		}

		// pick action through action flag
		if (action == 1)
			cmd = new EatCommand(dir);
		// check fox's distance and current energy for breeding
		else if (distance_from_fox > DANGER_DISTANCE && energy >= breed_limit)
			cmd = new BreedCommand(dir);
		// if rabbit runaway from fox, find movable direction
		else if (action == 3) {
			for (int i = 0; i < 4; i++) {
				Location mv_loc = new Location(rabbit_loc, dir);
				if (world.isValidLocation(mv_loc) != false && world.getThing(mv_loc) == null) {
					break;
				}
				dir = Util.randomDir();
			}
			cmd = new MoveCommand(dir);
		// if found object is none, just move
		} else
			cmd = new MoveCommand(dir);

		return cmd;
	}
}
