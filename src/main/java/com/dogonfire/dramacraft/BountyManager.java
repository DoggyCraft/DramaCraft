package com.dogonfire.dramacraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;



public class BountyManager implements Listener
{
	ScoreboardManager 					manager;
	Scoreboard 							board;
	static private FileConfiguration	config		= null;
	static private File					configFile	= null;	
	static private BountyManager		instance;
	
	public BountyManager()
	{
		instance = this;

		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		
	}
	
	public void load()
	{
		try
		{
			configFile = new File(DramaCraft.instance().getDataFolder(), "bounties.yml");

			config = YamlConfiguration.loadConfiguration(configFile);

			DramaCraft.log("Loaded " + config.getConfigurationSection("transmitters").getKeys(false).size() + " transmitters.");				
		}
		catch(Exception ex)
		{
			DramaCraft.log("No bounties loaded.");			
		}
		
	}
	
	public void save()
	{
		if (config == null || configFile == null)
		{
			DramaCraft.log("Config: " + config);
			DramaCraft.log("Configfile: " + configFile);
			return;
		}
		
		try
		{
			config.save(configFile);
		}
		catch (Exception ex)
		{
			DramaCraft.log("Could not save config to " + configFile + ": " + ex.getMessage());
		}
		
		DramaCraft.log("Saved configuration.");
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(!isWantedSign(event.getBlock().getLocation()))
		{
			return;
		}
		
		this.removeWantedSign(event.getBlock().getLocation());

		if(event.getPlayer()!=null)
		{
			event.getPlayer().sendMessage("You destroyed a wanted sign");
		}
	}

	public boolean isBountySign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}		
				
		return true;
	}
	
	@EventHandler
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if(!RankManager.isImperial(player.getUniqueId()))
		{
			return;
		}
		
		if(isBountySign(event.getBlock()))
		{
			return;
		}
					
		if(!event.getLine(0).trim().equalsIgnoreCase("wanted"))
		{
			return;
		}

		if(!event.getLine(1).trim().equalsIgnoreCase("rebel"))
		{
			return;
		}

		int rank = Integer.parseInt(event.getLine(2));
			
		//event.setLine(0, ChatColor.DARK_RED + "WANTED");
		//event.setLine(1, ChatColor.DARK_RED + "For being a Rebel");
		//event.setLine(2, "Nr. " + rank);
		
		BountyManager.setWantedSign(rank, event.getBlock().getLocation());
		
		Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance(), new Runnable()
		{
			public void run()
			{
				BountyManager.updateWantedSigns();
			}
		}, 2);


		player.sendMessage("You placed a wanted sign for the " + rank + ". most wanted rebel");
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event)
	{	
		Player player = event.getPlayer();
			
		if(!isWanted(player))
		{		
			return;
		}

		int bounty = BountyManager.getBounty(player.getUniqueId());
		
		
		//Objective objective = board.registerNewObjective("showhealth", "dummy");
		//objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		//objective.setDisplayName("WANTED " + bounty + " wanks");
		 
		
		/*
		Team team = board.getTeam(player.getName());
		
        if (team == null) 
        {
            team = board.registerNewTeam(player.getName());
        }
        
		team.setPrefix(ChatColor.DARK_RED + "WANTED 1230 wanks");
		team.addPlayer(player);		
		team.addEntry(player.getName());
		*/
		//player.setScoreboard(board);
		
		
		for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers())
		{
			if(onlinePlayer==player)
			{
				continue;
			}
			
			if(RankManager.isRebel(onlinePlayer.getUniqueId()))
			{
				continue;
			}
			
			onlinePlayer.sendMessage(ChatColor.GOLD + player.getName() + " has a bounty of " + ChatColor.GOLD + bounty + " wanks" + ChatColor.AQUA + " on him! Kill him to claim the bounty!" );
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{		
		Player victim = event.getEntity();
		Player killer = event.getEntity().getKiller();

		if(killer==null || !(killer instanceof Player))
		{
			return;
		}
		
		if(!isWanted(victim))
		{
			return;
		}
	
		if(RankManager.isRebel(killer.getUniqueId()))
		{
			return;			
		}
		
		int bounty = BountyManager.getBounty(victim.getUniqueId());
		DramaCraft.instance().getEconomyManager().depositPlayer(DramaCraft.instance().getServer().getOfflinePlayer(killer.getUniqueId()), bounty);
		
		clearBounty(victim);
	
		Bukkit.getServer().broadcastMessage("" + ChatColor.GOLD + killer + ChatColor.AQUA + " claimed the bounty of " + ChatColor.GOLD + bounty + " wanks " + ChatColor.AQUA + " by killing " + ChatColor.GOLD + victim.getName() + ChatColor.AQUA + "!");

		updateWantedSigns();
	}

	static private int hashVector(Location location)
	{
		return location.getBlockX() * 73856093 ^ location.getBlockY() * 19349663 ^ location.getBlockZ() * 83492791;
	}
	
	private boolean isWantedSign(Location location)
	{
		int hash = hashVector(location);

		return config.getString("Signs." + hash + ".WorldName") != null;		
	}

	static private void setWantedSign(int rank, Location location)
	{		
		int hash = hashVector(location);
		
		config.set("Signs." + hash + ".Rank", rank);		
		config.set("Signs." + hash + ".X", location.getBlockX());		
		config.set("Signs." + hash + ".Y", location.getBlockY());		
		config.set("Signs." + hash + ".Z", location.getBlockZ());		
		config.set("Signs." + hash + ".WorldName", location.getWorld().getName());		
		
		instance.save();		
	}

	static public List<Bounty> getBounties()
	{
		ConfigurationSection section = config.getConfigurationSection("Bounties");
		List<Bounty> bounties = new ArrayList<Bounty>();
		
		if(section == null)
		{
			return null;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			int bounty = config.getInt("Bounties." + playerIdString + ".Bounty");
			UUID playerId = UUID.fromString(playerIdString);
			
			bounties.add(new Bounty(playerId, bounty));			
		}
		
		Collections.sort(bounties, new BountyComparator());
			
		return bounties;
	}

	static private UUID getRankedPlayer(int rank)
	{
		ConfigurationSection section = config.getConfigurationSection("Bounties");
		List<Bounty> bounties = new ArrayList<Bounty>();
		
		if(section == null)
		{
			return null;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			int bounty = config.getInt("Bounties." + playerIdString + ".Bounty");
			UUID playerId = UUID.fromString(playerIdString);
			
			bounties.add(new Bounty(playerId, bounty));			
		}
		
		Collections.sort(bounties, new BountyComparator());
		
		if(bounties.size() < rank)
		{
			return null;			
		}
		
		return bounties.get(rank - 1).PlayerId;
	}
	
	private void removeWantedSign(Location location)
	{
		config.set("Signs." + location.hashCode(), null);
		
		save();
	}

	static private void updateWantedSigns()
	{
		ConfigurationSection section = config.getConfigurationSection("Signs");

		if (section == null)
		{
			return;
		}

		for (String locationHash : section.getKeys(false))
		{
			int x = config.getInt("Signs." + locationHash + ".X");
			int y = config.getInt("Signs." + locationHash + ".Y");
			int z = config.getInt("Signs." + locationHash + ".Z");
					
			String worldName = config.getString("Signs." + locationHash + ".WorldName");
			int rank = config.getInt("Signs." + locationHash + ".Rank");

			World world = Bukkit.getServer().getWorld(worldName);
			Location location = new Location(world, x, y, z);
			try
			{
				org.bukkit.block.Sign sign = (Sign) world.getBlockAt(location).getState();

				UUID playerId = getRankedPlayer(rank);

				if (playerId == null)
				{
					sign.setLine(0, ChatColor.DARK_RED + "WANTED");
					sign.setLine(1, ChatColor.DARK_RED + "For being a Rebel");
					sign.setLine(2, "Nr. " + rank);
					sign.setLine(3, "");
				}
				else
				{
					int bounty = getBounty(playerId);

					sign.setLine(0, ChatColor.DARK_RED + "WANTED");
					sign.setLine(1, ChatColor.DARK_RED + "For being a Rebel");
					sign.setLine(2, Bukkit.getServer().getPlayer(playerId).getName());
					sign.setLine(3, bounty + " wanks");
				}

				sign.update();
			}
			catch (Exception ex)
			{
				DramaCraft.log("ERROR: Not a sign at " + location.hashCode() + ". Deleted from config.");
				DramaCraft.log("ERROR: " + ex.getMessage());
				config.set("Signs." + locationHash, null);
				instance.save();
			}
		}
	}
	
	static private boolean isWanted(Player player)
	{
		return config.getString("Bounties." + player.getUniqueId().toString() + ".Bounty") != null;		
	}
	
	static public int getBounty(UUID playerId)
	{
		return config.getInt("Bounties." + playerId.toString() + ".Bounty");	
	}

	static public void addBounty(Player player, int bounty)
	{
		int currentBounty = config.getInt("Bounties." + player.getUniqueId().toString() + ".Bounty");
		
		currentBounty += bounty;
		
		config.set("Bounties." + player.getUniqueId().toString() + ".Bounty", currentBounty);

		instance.save();
		
		updateWantedSigns();
	}

	public void clearBounty(Player player)
	{	
		config.set("Bounties." + player.getUniqueId().toString(), null);

		save();
		
		updateWantedSigns();
	}
}