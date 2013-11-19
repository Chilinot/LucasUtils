/**
 *  Name:    Vectors.java
 *  Created: 00:45:36 - 20 nov 2013
 * 
 *  Author:  Lucas Arnström - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
 *  
 *
 *  Copyright 2013 Lucas Arnström
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 *
 *
 *  Filedescription:
 * 
 */

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
