/**
 *  Name:    Effects.java
 *  Created: 00:45:36 - 20 nov 2013
 *
 *  Author:  Lucas Arnstr�m - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
 *
 *
 *  Copyright 2013 Lucas Arnstr�m
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Effects {

    private final static HashMap<Integer, Double[]> lookup = new HashMap<Integer, Double[]>();

    private static void generateLookup() {
        if(lookup.size() != 0) {
            return;
        }

        for(int i = 0; i < 360; i++) {
            Double[] data = new Double[2];
            data[0] = Math.sin(Math.toRadians(i));
            data[1] = Math.cos(Math.toRadians(i));
            lookup.put(i, data);
        }
    }

    /**
     * Spawns a tornado at the given location l.
     *
     * @param plugin           - Plugin instance that spawns the tornado.
     * @param location         - Location to spawn the tornado.
     * @param material         - The base material for the tornado.
     * @param data             - Data for the block.
     * @param direction        - The direction the tornado should move in.
     * @param speed            - How fast it moves in the given direction. Warning! A number greater than 0.3 makes it look weird.
     * @param amount_of_blocks - The max amount of blocks that can exist in the tornado.
     * @param time             - The amount of ticks the tornado should be alive.
     * @param spew             - Defines if the tornado should remove or throw out any block it picks up.
     * @param explode          - This defines if the tornado should "explode" when it dies. Warning! Right now it only creates a huge mess.
     */
    public static void spawnTornado(
                                           final JavaPlugin plugin,
                                           final Location location,
                                           final Material material,
                                           final byte data,
                                           final Vector direction,
                                           final double speed,
                                           final int amount_of_blocks,
                                           final long time,
                                           final boolean spew,
                                           final boolean explode
    ) {

        generateLookup();

        class VortexBlock {

            private Entity entity;

            public boolean removable = true;

            private int ticker_vertical   = 0;
            private int ticker_horisontal = (int) Math.round((Math.random() * 360));

            @SuppressWarnings("deprecation")
            public VortexBlock(Location l, Material m, byte d) {

                if(l.getBlock().getType() != Material.AIR) {

                    Block b = l.getBlock();
                    entity = l.getWorld().spawnFallingBlock(l, b.getType(), b.getData());

                    if(b.getType() != Material.WATER)
                        b.setType(Material.AIR);

                    removable = !spew;
                }
                else {
                    entity = l.getWorld().spawnFallingBlock(l, m, d);
                    removable = true;
                }

                addMetadata();
            }

            public VortexBlock(Entity e) {
                entity = e;
                removable = false;
                addMetadata();
            }

            private void addMetadata() {
                entity.setMetadata("vortex", new FixedMetadataValue(plugin, "protected"));
            }

            public void remove(boolean explode) {
                if(removable && !explode) {
                    entity.remove();
                }
                entity.removeMetadata("vortex", plugin);
            }

            @SuppressWarnings("deprecation")
            public HashSet<VortexBlock> tick() {

                double radius = lookup.get(verticalTicker())[0] * 2;
                int horisontal = horisontalTicker();

                Vector v = new Vector(radius * lookup.get(horisontal)[1], 0.5D, radius * lookup.get(horisontal)[0]);

                HashSet<VortexBlock> new_blocks = new HashSet<VortexBlock>();

                // Pick up blocks
                Block b = entity.getLocation().add(v.clone().normalize()).getBlock();
                if(b.getType() != Material.AIR) {
                    new_blocks.add(new VortexBlock(b.getLocation(), b.getType(), b.getData()));
                }

                // Pick up other entities
                List<Entity> entities = entity.getNearbyEntities(1.0D, 1.0D, 1.0D);
                for(Entity e : entities) {
                    if(!e.hasMetadata("vortex")) {
                        new_blocks.add(new VortexBlock(e));
                    }
                }

                setVelocity(v);

                return new_blocks;
            }

            private void setVelocity(Vector v) {
                entity.setVelocity(v);
            }

            private int verticalTicker() {
                if(ticker_vertical < 90) {
                    ticker_vertical += 5;
                }
                return ticker_vertical;
            }

            private int horisontalTicker() {
                ticker_horisontal = (ticker_horisontal + 45) % 360;
                return ticker_horisontal;
            }
        }

        // Modify the direction vector using the speed argument.
        if(direction != null) {
            direction.normalize().multiply(speed);
        }

        // This set will contain every block created to make sure the metadata for each and everyone is removed.
        final HashSet<VortexBlock> clear = new HashSet<VortexBlock>();

        final int id = new BukkitRunnable() {

            private ArrayDeque<VortexBlock> blocks = new ArrayDeque<VortexBlock>();

            public void run() {

                if(direction != null) {
                    location.add(direction);
                }

                // Spawns 10 blocks at the time.
                for(int i = 0; i < 10; i++) {
                    checkListSize();
                    VortexBlock vb = new VortexBlock(location, material, data);
                    blocks.add(vb);
                    clear.add(vb);
                }

                // Make all blocks in the list spin, and pick up any blocks that get in the way.
                ArrayDeque<VortexBlock> que = new ArrayDeque<VortexBlock>();

                for(VortexBlock vb : blocks) {
                    HashSet<VortexBlock> new_blocks = vb.tick();
                    for(VortexBlock temp : new_blocks) {
                        que.add(temp);
                    }
                }

                // Add the new blocks
                for(VortexBlock vb : que) {
                    checkListSize();
                    blocks.add(vb);
                    clear.add(vb);
                }
            }

            // Removes the oldest block if the list goes over the limit.
            private void checkListSize() {
                while(blocks.size() >= amount_of_blocks) {
                    VortexBlock vb = blocks.getFirst();
                    vb.remove(false);
                    blocks.remove(vb);
                    clear.remove(vb);
                }
            }
        }.runTaskTimer(plugin, 5L, 5L).getTaskId();

        // Stop the "tornado" after the given time.
        new BukkitRunnable() {
            public void run() {
                for(VortexBlock vb : clear) {
                    vb.remove(explode);
                }
                plugin.getServer().getScheduler().cancelTask(id);
            }
        }.runTaskLater(plugin, time);
    }
}
