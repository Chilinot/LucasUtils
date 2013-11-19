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
	 * @param amount_of_blocks - The max amount of blocks that can exist in the tornado.
	 * @param time - The amount of seconds the tornado should be alive.
	 */
	public static void spawnTornado(final JavaPlugin plugin, final Location location, final int amount_of_blocks, int time) {
		   
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
