package faceduck.actors;

import faceduck.ai.FoxAI;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

/**
 * A Fox is an {@link Animal} that can move, eat or breed.
 */
public class FoxImpl implements Fox {
	private static final int FOX_MAX_ENERGY = 160;
	private static final int FOX_VIEW_RANGE = 5;
	private static final int FOX_BREED_LIMIT = FOX_MAX_ENERGY * 3 / 4;
	private static final int FOX_COOL_DOWN = 2;
	private static final int FOX_INITIAL_ENERGY = FOX_MAX_ENERGY * 1 / 2;

	// fox's current energy
	private int energy;

	/**
	 * FoxImpl class constructor. Initiate energy to initial value
	 */
	public FoxImpl() {
		energy = FOX_INITIAL_ENERGY;
	}

	/**
	 * FoxImpl class constructor
	 *
	 * @param init_energy
	 *            initial energy when it bred
	 */
	public FoxImpl(int init_energy) {
		energy = init_energy;
	}

	/**
	 * Act by Command type(move, eat, breed)
	 * 
	 * @param world
	 *            The world containing this fox
	 */
	@Override
	public void act(World world) {
		// when it have act timing, first energy is decreased and if energy is 0, the
		// fox is dead and removed from the world
		energy--;
		if (energy == 0) {
			world.remove(this);
			return;
		}

		// get command from FoxAI's act function and execute it
		try {
			FoxAI fox_ai = new FoxAI();
			Command cmd = fox_ai.act(world, this);
			cmd.execute(world, this);
		} catch (Exception e) {
		}
	}

	/**
	 * Move to dir in the world
	 * 
	 * @param world
	 *            The world containing this fox
	 * 
	 * @param dir
	 *            The direction to move in
	 * 
	 */
	@Override
	public void move(World world, Direction dir) {
		try {
			Location cur_loc = world.getLocation(this);
			Location new_loc = new Location(cur_loc, dir);
			// only if new location is valid and has no other object, fox can move to that
			// location
			if (world.isValidLocation(new_loc) == true && world.getThing(new_loc) == null) {
				world.remove(this);
				world.add(this, new_loc);
			}
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Breed a child animal at adjacent location. When animal breeds, they share
	 * parant's energy in half.
	 *
	 * @param world
	 *            The world containing this fox
	 * 
	 * @param dir
	 *            The direction in which the child will bred
	 */
	@Override
	public void breed(World world, Direction dir) {
		try {
			Location cur_loc = world.getLocation(this);
			Location child_loc = new Location(cur_loc, dir);
			// only if new location is valid and has no other object, fox can breed to
			// that location
			if (world.isValidLocation(child_loc) && world.getThing(child_loc) == null) {
				// decrease energy to half and add child fox object which has half remainder of
				// parant's energy to world
				energy /= 2;
				Fox child = new FoxImpl(energy);
				world.add(child, child_loc);
			}
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Consume rabbit located at the adjacent space(dir direction)
	 *
	 * @param world
	 *            The world containing this fox
	 * 
	 * @param dir
	 *            The direction of the rabbit to eat
	 */
	@Override
	public void eat(World world, Direction dir) {
		try {
			Location cur_loc = world.getLocation(this);
			Location rabbit_loc = new Location(cur_loc, dir);
			Object rabbit_obj = world.getThing(rabbit_loc);
			// only if new location is valid and rabbit_obj is real Rabbit, fox can eat
			// rabbit
			if (world.isValidLocation(rabbit_loc) == true && rabbit_obj instanceof Rabbit) {
				energy += ((Rabbit) rabbit_obj).getEnergyValue();
				if (energy > FOX_MAX_ENERGY)
					energy = FOX_MAX_ENERGY;

				world.remove(rabbit_obj);
			}
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Return current energy value
	 */
	@Override
	public int getEnergy() {
		return energy;
	}

	/**
	 * Return maximum view range value
	 */
	@Override
	public int getViewRange() {
		return FOX_VIEW_RANGE;
	}

	/**
	 * Return maximum energy value
	 */
	@Override
	public int getMaxEnergy() {
		return FOX_MAX_ENERGY;
	}

	/**
	 * Return cool down value
	 */
	@Override
	public int getCoolDown() {
		return FOX_COOL_DOWN;
	}

	/**
	 * Return critical point energy of breed
	 */
	@Override
	public int getBreedLimit() {
		return FOX_BREED_LIMIT;
	}
}
