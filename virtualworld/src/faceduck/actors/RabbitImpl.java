package faceduck.actors;

import faceduck.ai.RabbitAI;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.interfaces.Command;

/**
 * A Rabbit is an {@link Animal} that can move, eat or breed.
 */
public class RabbitImpl implements Rabbit {
	private static final int RABBIT_MAX_ENERGY = 20;
	private static final int RABBIT_VIEW_RANGE = 3;
	private static final int RABBIT_BREED_LIMIT = RABBIT_MAX_ENERGY * 2 / 4;
	private static final int RABBIT_ENERGY_VALUE = 20;
	private static final int RABBIT_COOL_DOWN = 4;
	private static final int RABBIT_INITIAL_ENERGY = RABBIT_MAX_ENERGY * 1 / 2;

	// rabbit's current energy
	private int energy;

	/**
	 * RabbitImpl class constructor Initiate energy to initial value
	 */
	public RabbitImpl() {
		energy = RABBIT_INITIAL_ENERGY;
	}

	/**
	 * RabbitImpl class constructor
	 *
	 * @param init_energy
	 *            set initial energy when it bred
	 */
	public RabbitImpl(int init_energy) {
		energy = init_energy;
	}

	/**
	 * Act by Command type(move, eat, breed)
	 * 
	 * @param world
	 *            The world containing this rabbit
	 */
	@Override
	public void act(World world) {
		// when it have act timing, first energy is decreased and if energy is 0, the
		// rabbit is dead and removed from the world
		energy--;
		if (energy == 0) {
			world.remove(this);
			return;
		}

		// get command from RabbitAI's act function and execute it
		try {
			RabbitAI rabbit_ai = new RabbitAI();
			Command cmd = rabbit_ai.act(world, this);
			cmd.execute(world, this);
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Move to dir in the world
	 * 
	 * @param world
	 *            The world containing this rabbit
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
			// only if new location is valid and has no other object, rabbit can move to
			// that location
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
	 * parant's energy in half
	 *
	 * @param world
	 *            The world containing this actor
	 * 
	 * @param dir
	 *            The direction in which the child will bred
	 */
	@Override
	public void breed(World world, Direction dir) {
		try {
			Location cur_loc = world.getLocation(this);
			Location child_loc = new Location(cur_loc, dir);
			// only if new location is valid and has no other object, actor can breed to
			// that location
			if (world.isValidLocation(child_loc) && world.getThing(child_loc) == null) {
				// decrease energy to half and add child rabbit object which has half remainder
				// of parant's energy to world
				energy /= 2;
				Rabbit child = new RabbitImpl(energy);
				world.add(child, child_loc);
			}
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * Consume grass located at the adjacent space(dir direction)
	 *
	 * @param world
	 *            The world containing this rabbit
	 * 
	 * @param dir
	 *            The direction of the grass to eat
	 */
	@Override
	public void eat(World world, Direction dir) {
		try {
			Location cur_loc = world.getLocation(this);
			Location grass_loc = new Location(cur_loc, dir);
			Object grass_obj = world.getThing(grass_loc);
			// only if new location is valid and grass_obj is real Grass, rabbit can eat
			// grass
			if (world.isValidLocation(grass_loc) == true && grass_obj instanceof Grass) {
				energy += ((Grass) grass_obj).getEnergyValue();
				if (energy > RABBIT_MAX_ENERGY)
					energy = RABBIT_MAX_ENERGY;

				world.remove(grass_obj);
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
	 * Return energy value when eaten
	 */
	@Override
	public int getEnergyValue() {
		return RABBIT_ENERGY_VALUE;
	}

	/**
	 * Return maximum view range value
	 */
	@Override
	public int getViewRange() {
		return RABBIT_VIEW_RANGE;
	}

	/**
	 * Return maximum energy value
	 */
	@Override
	public int getMaxEnergy() {
		return RABBIT_MAX_ENERGY;
	}

	/**
	 * Return cool down value
	 */
	@Override
	public int getCoolDown() {
		return RABBIT_COOL_DOWN;
	}

	/**
	 * Return critical point energy of breed
	 */
	@Override
	public int getBreedLimit() {
		return RABBIT_BREED_LIMIT;
	}
}
