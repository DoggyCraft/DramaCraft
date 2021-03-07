package com.dogonfire.dramacraft.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dogonfire.DramaCraft.DramaCraft;



public class ImperialCommandExecutor implements CommandExecutor
{
	private static ImperialCommandExecutor instance;

	public static ImperialCommandExecutor instance()
	{
		if (instance == null)
			instance = new ImperialCommandExecutor();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, DramaCraftCommand> commandList;

	private ImperialCommandExecutor()
	{
		commandList = new TreeMap<String, DramaCraftCommand>();
		//registerCommand(new CommandHelp());
		//registerCommand(new CommandPlayerInfo());
	}

	protected Collection<DramaCraftCommand> getCommands()
	{
		return Collections.unmodifiableCollection(commandList.values());
	}

	protected void registerCommand(DramaCraftCommand command)
	{
		if (commandList.containsKey(command.name))
			return;
		
		commandList.put(command.name.toLowerCase(), command);
	}

	private void CommandHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "Some kind of help goes here");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandHelp(sender);
			DramaCraft.log(sender.getName() + " /imperial");
			return true;
		}

		DramaCraftCommand dcCmd = commandList.get(args[0].toLowerCase());
		
		if (dcCmd == null)
		{
			sender.sendMessage(ChatColor.RED + "Invalid DramaCraft command!");
		}
		else
		{
			dcCmd.onCommand(sender, label, args);
		}
		
		return true;
	}
}