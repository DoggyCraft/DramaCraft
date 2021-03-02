package dogonfire.DramaCraft.tasks;

import java.util.Arrays;
import java.util.UUID;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.LanguageManager;
import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class InfoTask implements Runnable
{
	private DramaCraft						plugin;
	private UUID							playerId	= null;
	private String							name1		= null;
	private String							name2		= null;
	private LanguageManager.LANGUAGESTRING	message		= null;
	private int								amount		= 0;
	private ChatColor						color;

	public InfoTask(DramaCraft instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, int amount, String name1)
	{
		this.plugin = instance;
		this.playerId = playerId;
		this.message = m;
		this.name1 = name1;
		this.amount = amount;
		this.color = color;
	}

	public InfoTask(DramaCraft instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, String name1, String name2)
	{
		this.plugin = instance;
		this.playerId = playerId;
		this.name1 = name1;
		this.name2 = name2;
		this.message = m;
		this.color = color;
	}

	public InfoTask(DramaCraft instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, int amount1)
	{
		this.plugin = instance;
		this.playerId = playerId;
		this.name1 = String.valueOf(amount1);
		this.message = m;
		this.amount = amount1;
		this.color = color;
	}

	public void run()
	{
		Player player = this.plugin.getServer().getPlayer(this.playerId);

		if (player == null)
		{
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