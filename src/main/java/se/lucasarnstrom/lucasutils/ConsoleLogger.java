/**
 *  Author: Lucas Arnström
 *  Contact: Lucasarnstrom@gmail.com
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
 *
 *
 *  Filedescription:
 *
 *  Takes care of all the logging to the console.
 *
 */
package se.lucasarnstrom.lucasutils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class ConsoleLogger {

    enum LogLevel {
        INFO, WARNING, SEVERE, DEBUG
    }

    private static JavaPlugin plugin = null;
    private static Logger     logger = null;

    private static String  template;
    private static boolean debug = false;

    private final String name;
    private final String info;

    private static Set<UUID>          listeners = new HashSet<UUID>();
    private static Set<ConsoleLogger> loggers   = new HashSet<ConsoleLogger>();

    /**
     * Constructor for the ConsoleLogger.
     *
     * @param logger_name - Name of the logger.
     */
    public ConsoleLogger(String logger_name) {
        this.name = logger_name;
        this.info = ConsoleLogger.template + "[" + logger_name + "] - ";

        loggers.add(this);
    }

    /**
     * Returns the name of this object.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Outputs normal info to the console with a green color.
     *
     * @param msg - Info message
     */
    public void info(String msg) {
        if(isInitiated()) {
            ConsoleLogger.logger.info(colorizeLevel(LogLevel.INFO, msg));
            broadcastToListeners(LogLevel.INFO, msg);
        }
        else {
            errorNotInitiated();
        }
    }

    /**
     * Outputs warnings to the console with a yellow color.
     *
     * @param msg - Warning message
     */
    public void warning(String msg) {
        if(isInitiated()) {
            ConsoleLogger.logger.warning(colorizeLevel(LogLevel.WARNING, msg));
            broadcastToListeners(LogLevel.WARNING, msg);
        }
        else {
            errorNotInitiated();
        }
    }

    /**
     * Outputs severe messages to the console with a red color.
     *
     * @param msg - Severe message
     */
    public void severe(String msg) {
        if(isInitiated()) {
            ConsoleLogger.logger.severe(colorizeLevel(LogLevel.SEVERE, msg));
            broadcastToListeners(LogLevel.SEVERE, msg);
        }
        else {
            errorNotInitiated();
        }
    }

    /**
     * This will only output if the debug is set to true. Outputs with a cyan
     * color.
     *
     * @param msg - Debug message
     */
    public void debug(String msg) {
        if(debug) {
            ConsoleLogger.logger.info(colorizeLevel(LogLevel.DEBUG, msg));
            broadcastToListeners(LogLevel.DEBUG, msg);
        }
    }

    /**
     * Takes an array of StackTraceElement objects and prints them using the debug function.
     *
     * @param sa - The stack trace to print to console.
     */
    public void debug(StackTraceElement[] sa) {
        for(StackTraceElement s : sa) {
            debug(s.toString());
        }
    }

    /**
     * Outputs true if the init() method has been called.
     *
     * @return true if init() has been called
     */
    public boolean isInitiated() {
        return ConsoleLogger.logger != null && ConsoleLogger.plugin != null;
    }

    private void errorNotInitiated() {
        System.out.println(colorizeLevel(LogLevel.SEVERE, "ConsoleLogger has not been initiated!"));
    }

    private String colorizeLevel(LogLevel level, String msg) {
        StringBuilder sb = new StringBuilder();

        switch(level) {
            case INFO:
                sb.append(Ansi.ansi().fg(Ansi.Color.GREEN));
                break;

            case WARNING:
                sb.append(Ansi.ansi().fg(Ansi.Color.YELLOW));
                break;

            case SEVERE:
                sb.append(Ansi.ansi().fg(Ansi.Color.RED));
                break;

            case DEBUG:
                sb.append(Ansi.ansi().fg(Ansi.Color.CYAN));
                break;

            default:
                // Nothing
        }

        sb.append(this.info);
        sb.append(msg);
        sb.append(Ansi.ansi().fg(Ansi.Color.WHITE));

        return sb.toString();
    }

    private void broadcastToListeners(LogLevel level, String msg) {

        if(ConsoleLogger.plugin == null) {
            System.out.println(colorizeLevel(LogLevel.SEVERE, "CONSOLELOGGER HAS NOT BEEN INITIATED WITH PLUGIN INSTANCE!"));
            return;
        }

        StringBuilder label = new StringBuilder();

        switch(level) {
            case INFO:
                label.append(ChatColor.GREEN);
                break;
            case WARNING:
                label.append(ChatColor.YELLOW);
                break;
            case SEVERE:
                label.append(ChatColor.DARK_RED);
                break;
            case DEBUG:
                label.append(ChatColor.BLUE);
                break;
            default:
                // Nothing
        }

        label.append(level.name());
        label.append(" [");
        label.append(this.name);
        label.append("] - ");
        label.append(ChatColor.WHITE);
        label.append(msg);

        String message = label.toString();

        Iterator<UUID> i = listeners.iterator();
        while(i.hasNext()) {
            Player player = plugin.getServer().getPlayer(i.next());
            if(player != null && player.isOnline()) { // server.getPlayer(UUID) will only return online players, player.isOnline() is only there as failsafe if that would change.
                player.sendMessage(message);
            }
            else {
                i.remove();
            }
        }
    }

    // ---------- Static methods ----------

    /**
     * Has to be called before any ConsoleLogger can be initialized.
     *
     * @param instance - Plugin instance.
     */
    public static void init(JavaPlugin instance) {
        ConsoleLogger.plugin = instance;
        ConsoleLogger.logger = instance.getLogger();
        ConsoleLogger.template = "v" + instance.getDescription().getVersion() + ": ";

        // the config.get() methods are case sensitive, and we need to make sure we get the debug value if there is one
        for(String key : instance.getConfig().getKeys(false)) {
            if(key.equalsIgnoreCase("debug")) {
                ConsoleLogger.debug = instance.getConfig().getBoolean(key);
                break;
            }
        }
    }

    /**
     * Set whether or not to output debug information to the console.
     *
     * @param newstate - True to output, otherwise false.
     */
    public static void setDebug(boolean newstate) {
        ConsoleLogger.debug = newstate;
        ConsoleLogger.plugin.getConfig().set("debug", newstate);
        ConsoleLogger.plugin.saveConfig();
    }

    /**
     * Add a player to the list of players listening for debug info.
     *
     * @param id - Name of the player
     */
    public static void addListener(UUID id) {
        ConsoleLogger.listeners.add(id);
    }

    /**
     * Remove a listening player from the list of listeners.
     *
     * @param id - Name of the player to remove
     */
    public static void removeListener(UUID id) {
        ConsoleLogger.listeners.remove(id);
    }

    /**
     * Retrieve the logger with the given name.
     *
     * @param name - Name of the ConsoleLogger to retrieve.
     * @return ConsoleLogger with given name, returns null if none was found.
     */
    public static ConsoleLogger getLogger(String name) {
        for(ConsoleLogger logger : ConsoleLogger.loggers) {
            if(logger.getName().equals(name))
                return logger;
        }
        return null;
    }
}
