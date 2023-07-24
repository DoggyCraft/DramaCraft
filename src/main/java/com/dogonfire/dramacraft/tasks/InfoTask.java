package com.dogonfire.dramacraft.tasks;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;


public class InfoTask implements Runnable
{
	private UUID							playerId	= null;
	private String							name1		= null;
	private String							name2		= null;
	private LanguageManager.LANGUAGESTRING	message		= null;
	private String							rawMessage	= null;
	private int								amount		= 0;
	private ChatColor						color;

	public InfoTask(UUID playerId, String message)
	{
		this.playerId = playerId;
		this.rawMessage = message;
	}

	public InfoTask(ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, int amount, String name1)
	{
		this.playerId = playerId;
		this.message = m;
		this.name1 = name1;
		this.amount = amount;
		this.color = color;
	}

	public InfoTask(ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, String name1, String name2)
	{
		this.playerId = playerId;
		this.name1 = name1;
		this.name2 = name2;
		this.message = m;
		this.color = color;
	}

	public InfoTask(ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, int amount1)
	{
		this.playerId = playerId;
		this.name1 = String.valueOf(amount1);
		this.message = m;
		this.amount = amount1;
		this.color = color;
	}

	public void run()
	{		
		Player player = Bukkit.getServer().getPlayer(this.playerId);

		if (player == null)
		{
			return;
		}

		if(rawMessage != null)
		{
			player.sendMessage(rawMessage);
			return;
		}		

		LanguageManager.setPlayerName(this.name1);

		try
		{
			LanguageManager.setType(this.name2);
		}
		catch (Exception ex)
		{
			DramaCraft.logDebug(Arrays.toString(ex.getStackTrace()));
		}

		LanguageManager.setAmount1(this.amount);

		String questionMessage = LanguageManager.getLanguageString(message, color);

		player.sendMessage(this.color + questionMessage);
	}
}