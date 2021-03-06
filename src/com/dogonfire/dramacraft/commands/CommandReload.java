package com.dogonfire.dramacraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.PermissionsManager;
import dogonfire.DramaCraft.RankManager;



public class CommandReload extends DramaCraftCommand
{
	protected CommandReload()
	{
		super("reload");
		this.permission = "dramacraft.reload";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{		
		//DramaCraft.reload();
		//PermissionsManager.load();
		//RankManager.load();

		sender.sendMessage(ChatColor.YELLOW + DramaCraft.instance().getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
	}
}
