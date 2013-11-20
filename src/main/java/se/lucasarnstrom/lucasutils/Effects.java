/**
 *  Name:    Effects.java
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

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Effects {
	
	/**
	 * Spawns a tornado at the given location l.
	 * 
	 * @param plugin - Plugin instance that spawns the tornado.
	 * @param location - Location to spawn the tornado.
	 * @param direction - The direction the tornade should move in.
	 * @param speed - How fast it moves in the given direction. Warning! A number greater than 0.3 makes it look wierd.
	 * @param amount_of_blocks - The max amount of blocks that can exist in the tornado.
	 * @param time - The amount of seconds the tornado should be alive.
	 */
	public static void spawnTornado(final JavaPlugin plugin, final Location location, final Vector direction, final double speed, final int amount_of_blocks, int time) {
		
		// Modify the direction vector using the speed argument.
		if(direction != null) {
			direction.normalize().multiply(speed);
		}
		   
	    class VortexBlock {
	       
	        FallingBlock entity;
	       
	        private float ticker_vertical   = 0.0f;
	        private float ticker_horisontal = (float) (Math.random() * 2 * Math.PI);
	       
	        public VortexBlock(Location l) {
	            entity = l.getWorld().spawnFallingBlock(l, Material.DIRT, (byte) 0);
	        }
	       
	        public void setVelocity(Vector v) {
	            entity.setVelocity(v);
	        }
	       
	        public float verticalTicker() {
	            if(ticker_vertical < 1.0f) {
	                ticker_vertical += 0.05f;
	            }
	           
	            return ticker_vertical;
	        }
	       
	        public float horisontalTicker() {
	            return (ticker_horisontal += 0.8f);
	        }
	    }
	   
	    final int id = new BukkitRunnable() {
	       
	        private ArrayList<VortexBlock> blocks = new ArrayList<VortexBlock>();
	       
	        public void run() {
	           
	            // Spawns 10 blocks at the time, with a maximum of 200 blocks at the same time.
	            for(int i = 0 ; i < 10 ; i++) {
	            	
	            	// Remove the oldest block if the list goes over the limit.
	                if(blocks.size() >= amount_of_blocks) {
	                    VortexBlock vb = blocks.get(0);
	                    vb.entity.remove();
	                    blocks.remove(vb);
	                }
	                
	                if(direction != null) {
	                	location.add(direction);
	                }
	               
	                blocks.add(new VortexBlock(location));
	            }
	           
	            // Makes all of the blocks in the list spin.
	            for(VortexBlock vb : blocks) {
	            	
	                double radius    = Math.sin(vb.verticalTicker()) * 2;
	                float horisontal = vb.horisontalTicker();
	                
	                vb.setVelocity(new Vector(radius * Math.cos(horisontal), 0.5D, radius * Math.sin(horisontal)));
	            }
	        }
	    }.runTaskTimer(plugin, 5L, 5L).getTaskId();
	   
	    // Stop the "tornado" after 30 "seconds".
	    new BukkitRunnable() {
	        public void run() {
	            plugin.getServer().getScheduler().cancelTask(id);
	        }
	    }.runTaskLater(plugin, 20L * time);
	}
}
