package se.lucasarnstrom.lucasutils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vectors {
	
	/**
	 * Returns a vector pointing from the "from" location to the "to" location.
	 * 
	 * @param from : From-location
	 * @param to   : To-location
	 * @return The vector.
	 */
	public static Vector fromTo(Location from, Location to) {
		return to.toVector().subtract(from.toVector());
	}
}
