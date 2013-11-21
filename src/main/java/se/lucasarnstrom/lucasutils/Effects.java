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

import java.util.ArrayDeque;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Effects {

	/**
	 * Spawns a tornado at the given location l.
	 * 
	 * @param plugin
	 *            - Plugin instance that spawns the tornado.
	 * @param location
	 *            - Location to spawn the tornado.
	 * @param material
	 *            - The base material for the tornado.
	 * @param data
	 *            - Data for the block.
	 * @param direction
	 *            - The direction the tornado should move in.
	 * @param speed
	 *            - How fast it moves in the given direction. Warning! A number greater than 0.3 makes it look weird.
	 * @param amount_of_blocks
	 *            - The max amount of blocks that can exist in the tornado.
	 * @param time
	 *            - The amount of ticks the tornado should be alive.
	 * @param spew
	 *            - Defines if the tornado should remove or throw out any block it picks up.
	 */
	public static void spawnTornado(
			final JavaPlugin plugin, 
			final Location   location, 
			final Material   material, 
			final byte       data,
			final Vector     direction, 
			final double     speed, 
			final int        amount_of_blocks, 
			final long       time,
			final boolean    spew
	) {
		// Modify the direction vector using the speed argument.
		if (direction != null) {
			direction.normalize().multiply(speed);
		}

		class VortexBlock {

			Entity entity;
			
			private boolean removable = true;

			private float ticker_vertical = 0.0f;
			private float ticker_horisontal = (float) (Math.random() * 2 * Math.PI);

			@SuppressWarnings("deprecation")
			public VortexBlock(Location l, Material m, byte d) {

				if (l.getBlock().getType() != Material.AIR) {

					Block b = l.getBlock();
					entity = l.getWorld().spawnFallingBlock(l, b.getType(), b.getData());

					if (b.getType() != Material.WATER)
						b.setType(Material.AIR);
					
					removable = false;
				}
				else
					entity = l.getWorld().spawnFallingBlock(l, m, d);
				
				addMetadata();
				tick();
			}
			
			public VortexBlock(Entity e) {
				entity    = e;
				removable = false;
				addMetadata();
				tick();
			}
			
			private void addMetadata() {
				entity.setMetadata("vortex", new FixedMetadataValue(plugin, "protected"));
			}
			
			public void remove() {
				if(removable || (!spew && (entity instanceof FallingBlock))) {
					entity.remove();
				}
				entity.removeMetadata("vortex", plugin);
			}

			@SuppressWarnings("deprecation")
			public VortexBlock tick() {
				
				double radius     = Math.sin(verticalTicker()) * 2;
				float  horisontal = horisontalTicker();
				
				Vector v = new Vector(radius * Math.cos(horisontal), 0.5D, radius * Math.sin(horisontal));
				
				setVelocity(v);
				
				// Pick up blocks
				Block b = entity.getLocation().add(v).getBlock();
				if(b.getType() != Material.AIR) {
					return new VortexBlock(b.getLocation(), b.getType(), b.getData());
				}
				
				// Pick up other entities
				List<Entity> entities = entity.getNearbyEntities(1.0D, 1.0D, 1.0D);
				for(Entity e : entities) {
					if(!e.hasMetadata("vortex")) {
						return new VortexBlock(e);
					}
				}
				
				return null;
			}

			private void setVelocity(Vector v) {
				entity.setVelocity(v);
			}

			private float verticalTicker() {
				if (ticker_vertical < 1.0f) {
					ticker_vertical += 0.05f;
				}
				return ticker_vertical;
			}

			private float horisontalTicker() {
//				ticker_horisontal = (float) ((ticker_horisontal + 0.8f) % 2*Math.PI);
				return (ticker_horisontal += 0.8f);
			}
		}

		final int id = new BukkitRunnable() {

			private ArrayDeque<VortexBlock> blocks = new ArrayDeque<VortexBlock>();

			public void run() {
				
				if (direction != null) {
					location.add(direction);
				}

				// Spawns 10 blocks at the time.
				for (int i = 0; i < 10; i++) {
					checkListSize();
					blocks.add(new VortexBlock(location, material, data));
				}
				
				
				// Make all blocks in the list spin, and pick up any blocks that get in the way.
				ArrayDeque<VortexBlock> que = new ArrayDeque<VortexBlock>();

				for (VortexBlock vb : blocks) {
					VortexBlock temp = vb.tick();
					if(temp != null) {
						que.add(temp);
					}
				}
				
				for(VortexBlock vb : que) {
					checkListSize();
					blocks.add(vb);
				}
			}
			
			// Removes the oldest block if the list goes over the limit.
			private void checkListSize() {
				while(blocks.size() >= amount_of_blocks) {
					VortexBlock vb = blocks.getFirst();
					vb.remove();
					blocks.remove(vb);
				}
			}
			
		}.runTaskTimer(plugin, 5L, 5L).getTaskId();

		// Stop the "tornado" after the given time.
		new BukkitRunnable() {
			public void run() {
				plugin.getServer().getScheduler().cancelTask(id);
			}
		}.runTaskLater(plugin, time);
	}
}
