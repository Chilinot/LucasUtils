/**
 *  Author:  Lucas Arnström - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
 *
 *
 *  Copyright 2014 Lucas Arnström
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
 */
package se.lucasarnstrom.lucasutils;

import java.util.HashMap;

public class MathUtils {

    static {
        generateLookupTable();
    }

    public final static HashMap<Integer, Double[]> lookuptable = new HashMap<Integer, Double[]>();

    public static void generateLookupTable() {
        if(lookuptable.size() != 0) {
            return;
        }

        for(int i = 0; i < 360; i++) {
            Double[] data = new Double[2];
            data[0] = Math.sin(Math.toRadians(i));
            data[1] = Math.cos(Math.toRadians(i));
            lookuptable.put(i, data);
        }
    }
}
