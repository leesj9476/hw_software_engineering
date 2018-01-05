package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

/**
 * This is a simple implementation of a Gnat. It never loses energy and moves in
 * random directions.
 */
public class Gnat implements Animal {
	private static final int MAX_ENERGY = 10;
	private static final int VIEW_RANGE = 1;
	private static final int BREED_LIMIT = 0;
	private static final int COOL_DOWN = 0;

	// gnat's current energy
	private int energy;

	// gnat's ai object
	private GnatAI gnat_ai;

	/**
	 * Gnat class constructor. Initiate energy to num
	 * 
	 * @param num
	 *            initial energy when it created
	 */
	public Gnat(int num) {
		energy = num;
		gnat_ai = new GnatAI();
	}

	/**
	 * Act by Command type. It just act move
	 * 
	 * @param world
	 *            The world containing this gnat
	 */
	@Override
	public void act(World world) {
		try {
			Command cmd = gnat_ai.act(world, this);
			cmd.execute(world, this);
			// if cause NullPointerException, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Move to dir in the world
	 * 
	 * @param world
	 *            The world containing this gnat
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
			// only if new location is valid and has no other object, gnat can move to
			// that location
			if (world.isValidLocation(new_loc) == true && world.getThing(new_loc) == null) {
				world.remove(this);
				world.add(this, new_loc);
			}
			// if cause NullPointerException, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Do nothing(gnat does not breed)
	 *
	 * @param world
	 *            The world containing this gnat
	 * 
	 * @param dir
	 *            The direction to breed
	 */
	@Override
	public void breed(World world, Direction dir) {
	}

	/**
	 * Do nothing(gnat does not eat)
	 *
	 * @param world
	 *            The world containing this gnat
	 * 
	 * @param dir
	 *            The direction to eat
	 */
	@Override
	public void eat(World world, Direction dir) {
	}

	/**
	 * Return current energy value
	 */
	@Override
	public int getEnergy() {
		return energy;
	}

	/**
	 * Return maximum energy value
	 */
	@Override
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	/**
	 * Return critical point energy of breed
	 */
	@Override
	public int getBreedLimit() {
		return BREED_LIMIT;
	}

	/**
	 * Return maximum view range value
	 */
	@Override
	public int getViewRange() {
		return VIEW_RANGE;
	}

	/**
	 * Return cool down value
	 */
	@Override
	public int getCoolDown() {
		return COOL_DOWN;
	}
}
