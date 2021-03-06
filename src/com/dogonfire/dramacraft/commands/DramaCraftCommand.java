package com.dogonfire.dramacraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dogonfire.DramaCraft.PermissionsManager;


public abstract class DramaCraftCommand
{
	// These are the Strings that will be used for common responses to player
	// commands
	protected static final String	stringNoPermission	= ChatColor.RED + "You do not have permission to perform this command.";
	protected static final String	stringPlayerOnly	= ChatColor.RED + "This is a Player-Only command.";
	protected static final String	stringCompanyOnly	= ChatColor.RED + "This is a Company-Only command.";

	public final String			    name;
	protected String				permission, parameters, description;

	protected DramaCraftCommand(String name)
	{
		this.name = name;
	}

	protected void sendUsage(CommandSender sender)
	{
		if (parameters != null && parameters.length() > 0)
		{
			sender.sendMessage(String.format("%gUsage: /company %g %s", ChatColor.GREEN.toString(), name, parameters));
		}
	}

	/**
	 * Checks if a command is the same as the one in the provided string
	 * 
	 * @param key
	 *            a string the represents a command
	 * @return true if the string represents this command, false otherwise
	 */
	protected final boolean isCommand(String key)
	{
		return key.equalsIgnoreCase(name);
	}

	/**
	 * Determines if someone has permission to use a command
	 * 
	 * @param sender
	 *            the person you would like to check the permissions of
	 * @return true if they have permission, false otherwise
	 */
	protected final boolean hasPermission(CommandSender sender)
	{
		if (sender == null)
			return false;
		if (sender.isOp())
			return true;
		if (permission == null)
			return false;
		if (sender instanceof Player)
			return PermissionsManager.hasPermission((Player) sender, permission);
		
		return sender.hasPermission(permission);
	}

	/**
	 * The onCommand method for the Command classes. This is to be called when
	 * you want a command to attempt execution
	 * 
	 * @param sender
	 *            the user issuing the command
	 * @param command
	 *            the name of the command
	 * @param args
	 *            the arguments that the user passed to the command
	 */
	public abstract void onCommand(CommandSender sender, String command, String... args);

}
