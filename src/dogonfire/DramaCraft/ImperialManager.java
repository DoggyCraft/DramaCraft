package dogonfire.DramaCraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class ImperialManager implements Listener
{
	private Random random = new Random();
	private long 						lastImperialHelpTime;
	
	//private List<String>[] imperialTitles = new String[] { "Imperial", "Master", "Over" };
	//private List<String>[] nobleTitles = new String[] { "High", "Master", "Royal", "Court", "" };
	//private List<String>[] wizardTitles = new String[] { "Wizard", "Mage", "Magic" };
	//private List<String>[] policeTitles = new String[] { "Guard", "Police", "Magic" };
	//private List<String>[] farmerTitles = new String[] { "Wizard", "Mage", "Magic" };
	
	public ImperialManager()
	{
	}
	
	public List<String> generateRandomTitles(Player player, int amount)
	{
		// Mix up 4 suggestions
		List<String> possibleTitles = new ArrayList<String>();
		List<String> customTitles = new ArrayList<String>();
		
		/*
		if(DramaCraft.instance().isImperial(player.getUniqueId()))
		{
			possibleTitles.addAll(imperialTitles);
		}
		
		if(DramaCraft.instance().isNoble(player.getUniqueId()))
		{
			possibleTitles.addAll(imperialTitles);
		}
		
		if(DramaCraft.instance().isRoyal(player.getUniqueId()))
		{
			possibleTitles.addAll(imperialTitles);
		}

		for(int i=0; i<amount; i++)
		{
			String p1 = possibleTitles.get(random.nextInt(possibleTitles.size()));
			String p2 = possibleTitles.get(random.nextInt(possibleTitles.size()));
			
			customTitles.add(p1 + "" + p2);
		}
		*/
		return null;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if (RankManager.isKing(event.getPlayer().getUniqueId()))
		{
			for(Player player : DramaCraft.instance().getServer().getOnlinePlayers())
			{
				if(player.getUniqueId().equals(event.getPlayer().getUniqueId()))
				{
					continue;
				}
				
				TitleManager.sendTitle(player, 1*20, 3*20, 1*20, ChatColor.GOLD + "The King has arrived", ChatColor.GREEN + "Please greet his majesty " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + "!");
			}
		}		
		
		if (RankManager.isKing(event.getPlayer().getUniqueId()))
		{
			for(Player player : DramaCraft.instance().getServer().getOnlinePlayers())
			{
				if(player.getUniqueId().equals(event.getPlayer().getUniqueId()))
				{
					continue;
				}
				
				TitleManager.sendTitle(event.getPlayer(), 1*20, 3*20, 1*20, ChatColor.GOLD + "The Queen has arrived", ChatColor.GREEN + "Please greet her majesty " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GREEN + "!");
			}
		}		
	}


	public void update()
	{
		if(System.currentTimeMillis() > lastImperialHelpTime + 10*60*1000)
		{
			// Tell imperials about transmitters and how to smash them
			{
				List<Player> players = RankManager.getOnlineImperialPlayers();
				if (players.size() > 0 && RebelTransmitterManager.getTransmitters() > 0)
				{
					if (System.currentTimeMillis() > lastImperialHelpTime + (10 * 60 * 1000))
					{
						LanguageManager.setAmount1(RebelTransmitterManager.getTransmitters());

						for (Player imperialPlayer : RankManager.getOnlineImperialPlayers())
						{
							DramaCraft.instance().sendInfo(imperialPlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_ACTIVE_TRANSMITTERS, ChatColor.AQUA, 0, 120);
						}

						lastImperialHelpTime = System.currentTimeMillis();
					}
				}
			}
			
			// Remove inactive nobles
			{
				Set<UUID> players = RankManager.getImperials();
				
				for(UUID playerId : players)
				{
					if(RankManager.getImperialLastOnlineDays(playerId) > 30)
					{
						RankManager.setImperial(playerId);
						break;
					}
				}
			}			
			
			if (RankManager.getActiveNobles() < 3)
			{
				for (Player rebelPlayer : RankManager.getOnlineImperialPlayers())
				{
					DramaCraft.instance().sendInfo(rebelPlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_VOTE_NOBLES, ChatColor.AQUA, 0, 120);
				}
			}
			else if (RankManager.getKing() == null)
			{
				for (Player rebelPlayer : RankManager.getOnlineNoblePlayers())
				{
					DramaCraft.instance().sendInfo(rebelPlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_VOTE_KING, ChatColor.AQUA, 0, 120);
				}
			}
			else if (RankManager.getQueen() == null)
			{
				for (Player rebelPlayer : RankManager.getOnlineNoblePlayers())
				{
					DramaCraft.instance().sendInfo(rebelPlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_VOTE_QUEEN, ChatColor.AQUA, 0, 120);
				}
			}
			else
			{
				switch(random.nextInt(2))
				{
					case 0: 
					for (Player noblePlayer : RankManager.getOnlineNoblePlayers())
					{
						DramaCraft.instance().sendInfo(noblePlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_VOTE_KICK_NOBLE, ChatColor.AQUA, 0, 120);
					} break;
					
					case 1: 
					for (Player noblePlayer : RankManager.getOnlineNoblePlayers())
					{
						DramaCraft.instance().sendInfo(noblePlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_ADDBOUNTY, ChatColor.AQUA, 0, 120);
					} break;
					
					case 2: 
					for (Player noblePlayer : RankManager.getOnlineNoblePlayers())
					{
						DramaCraft.instance().sendInfo(noblePlayer.getUniqueId(), LANGUAGESTRING.INFO_IMPERIAL_CLAIMBOUNTY, ChatColor.AQUA, 0, 120);
					} break;
				}
			}
			
			lastImperialHelpTime = System.currentTimeMillis();
		}				
	}
}