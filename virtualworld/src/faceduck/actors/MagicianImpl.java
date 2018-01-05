package faceduck.actors;

import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Magician;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;
import faceduck.ai.MagicianAI;
import java.util.Vector;

/**
 * A Magician is an {@link Actor} that can move(teleport). Magician is
 * peace-maker, so it can remove grass and fox and add rabbit, too.
 */
public class MagicianImpl implements Magician {
	private static final int COOL_DOWN = 4;
	private static final int VIEW_RANGE = 2;
	private static final int ADD_RABBIT_NUM = 15;
	private static final int ADD_FOX_NUM = 2;

	// save grass and fox Object near Magician
	private Vector<Object> grass;
	private Vector<Object> fox;

	// teleport location
	private Location mv_loc;

	// save rabbit and fox number all over the world
	int rabbit_num;
	int fox_num;

	/**
	 * RabbitImpl class constructor. Initiate grass and fox vector and rabbit_num
	 */
	public MagicianImpl() {
		grass = new Vector<Object>(0);
		fox = new Vector<Object>(0);
		rabbit_num = 0;
		fox_num = 0;
	}

	/**
	 * Act by Command type(magic)
	 * 
	 * @param world
	 *            The world containing this Magician
	 */
	@Override
	public void act(World world) {
		// get command from MagicianAI's act function and execute it
		try {
			MagicianAI magician_ai = new MagicianAI();
			Command cmd = magician_ai.act(world, this);
			cmd.execute(world, this);
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
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

	/**
	 * set teleport location
	 * 
	 * @param x
	 *            the x coordinate from teleport location
	 * @param y
	 *            the y coordinate from teleport location
	 */
	public void setMoveLocation(int x, int y) {
		mv_loc = new Location(x, y);
	}

	/**
	 * teleport to mv_loc location(random location)
	 * 
	 * @param world
	 *            The world containing this magician
	 */
	public void move(World world) {
		// teleport
		try {
			world.remove(this);
			world.add(this, mv_loc);
			// if cause Exception, we can catch that and do nothing
		} catch (Exception e) {
		}
	}

	/**
	 * At the start of ai function, call this function to initiate vector objects,
	 * location and rabbit num variables
	 */
	public void clear() {
		grass.clear();
		fox.clear();
		mv_loc = null;
		rabbit_num = 0;
		fox_num = 0;
	}

	/**
	 * If find fox, call this function to save grass object
	 * 
	 * @param obj
	 *            found grass object
	 */
	public void addGrassObj(Object obj) {
		grass.addElement(obj);
	}

	/**
	 * If find fox, call this function to save fox object
	 * 
	 * @param obj
	 *            found fox object
	 */
	public void addFoxObj(Object obj) {
		fox.addElement(obj);
	}

	/**
	 * If find rabbit from world, call this function to increase rabbit num
	 */
	public void addRabbitNum() {
		rabbit_num++;
	}

	/**
	 * If find fox from world, call this function to increase fox num
	 */
	public void addFoxNum() {
		fox_num++;
	}

	/**
	 * The magician's main acting function. Magician can move, remove grass or fox
	 * and add rabbit
	 * 
	 * @param world
	 *            The world containing this rabbit
	 */
	public void magic(World world) {
		// if empty random location exist, teleport to that location
		if (mv_loc != null)
			move(world);

		// priority: remove grass > remove fox
		// if grass exist over 10 in magician's view range, remove all in grass vector
		if (grass.size() > 10) {
			for (int i = 0; i < grass.size(); i++) {
				try {
					world.remove(grass.elementAt(i));
					// if grass is already removed, ignore and continue
				} catch (Exception e) {
					continue;
				}
			}
			// if fox exist in magician's view range, remove all in fox vector
		} else if (fox.size() > 0 && fox_num > 3) {
			for (int i = 0; i < fox.size(); i++) {
				try {
					world.remove(fox.elementAt(i));
					// if fox is already removed, ignore and continue
				} catch (Exception e) {
					continue;
				}
			}
		}

		// if rabbit_num is under 20, add ADD_RABBIT_NUM(15) rabbit to random location
		if (rabbit_num < 20) {
			for (int i = 0; i < ADD_RABBIT_NUM; i++) {
				Location loc = Util.randomEmptyLoc(world);
				if (loc != null) {
					world.add(new RabbitImpl(), loc);
				}
			}
		}
		
		// if rabbit_num is under 3, add ADD_FOX_NUM(2) rabbit to random location
		if (fox_num < 3) {
			for (int i = 0; i < ADD_FOX_NUM; i++) {
				Location loc = Util.randomEmptyLoc(world);
				if (loc != null)
					world.add(new FoxImpl(), loc);
			}
		}
	}
}
