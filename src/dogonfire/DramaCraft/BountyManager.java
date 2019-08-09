package dogonfire.DramaCraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;



public class BountyManager implements Listener
{
	private DramaCraft plugin;
	private Random random = new Random();
	ScoreboardManager 					manager;
	Scoreboard 							board;
	private FileConfiguration			config		= null;
	private File						configFile	= null;	
	
	public BountyManager(DramaCraft plugin)
	{
		this.plugin = plugin;

		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(this.plugin.getDataFolder(), "bounties.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);

			this.plugin.log("Loaded " + this.config.getConfigurationSection("transmitters").getKeys(false).size() + " transmitters.");				
		}
		catch(Exception ex)
		{
			this.plugin.log("No bounties loaded.");			
		}
		
	}
	
	public void save()
	{
		if (this.config == null || this.configFile == null)
		{
			plugin.log("Config: " + this.config);
			plugin.log("Configfile: " + this.configFile);
			return;
		}
		
		try
		{
			this.config.save(this.configFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.configFile + ": " + ex.getMessage());
		}
		
		this.plugin.log("Saved configuration.");
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

	
	@EventHandler
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if(!plugin.isImperial(player.getUniqueId()))
		{
			return;
		}
		
		if(event.getBlock().getType() != Material.OAK_WALL_SIGN)
		{
			return;
		}
		
		Sign sign = (Sign) event.getBlock().getState();
			
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
		
		plugin.getBountyManager().setWantedSign(rank, event.getBlock().getLocation());
		
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
		{
			public void run()
			{
				plugin.getBountyManager().updateWantedSigns();
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

		int bounty = plugin.getBountyManager().getBounty(player.getUniqueId());
		
		
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
		
		
		for(Player onlinePlayer : plugin.getServer().getOnlinePlayers())
		{
			if(onlinePlayer==player)
			{
				continue;
			}
			
			if(plugin.isRebel(onlinePlayer.getUniqueId()))
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
	
		if(plugin.isRebel(killer.getUniqueId()))
		{
			return;			
		}
		
		int bounty = plugin.getBountyManager().getBounty(victim.getUniqueId());
		plugin.getEconomyManager().depositPlayer(killer.getName(), bounty);
		
		clearBounty(victim);
	
		plugin.getServer().broadcastMessage("" + ChatColor.GOLD + killer + ChatColor.AQUA + " claimed the bounty of " + ChatColor.GOLD + bounty + " wanks " + ChatColor.AQUA + " by killing " + ChatColor.GOLD + victim.getName() + ChatColor.AQUA + "!");

		updateWantedSigns();
	}

	private int hashVector(Location location)
	{
		return location.getBlockX() * 73856093 ^ location.getBlockY() * 19349663 ^ location.getBlockZ() * 83492791;
	}
	
	private boolean isWantedSign(Location location)
	{
		int hash = hashVector(location);

		return config.getString("Signs." + hash + ".WorldName") != null;		
	}

	private void setWantedSign(int rank, Location location)
	{		
		int hash = hashVector(location);
		
		config.set("Signs." + hash + ".Rank", rank);		
		config.set("Signs." + hash + ".X", location.getBlockX());		
		config.set("Signs." + hash + ".Y", location.getBlockY());		
		config.set("Signs." + hash + ".Z", location.getBlockZ());		
		config.set("Signs." + hash + ".WorldName", location.getWorld().getName());		
		
		save();		
	}

	public List<Bounty> getBounties()
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

	private UUID getRankedPlayer(int rank)
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

	private void updateWantedSigns()
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

			World world = plugin.getServer().getWorld(worldName);
			Location location = new Location(world, x, y, z);
			try
			{
				Sign sign = (Sign) world.getBlockAt(location).getState();

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
					sign.setLine(2, plugin.getServer().getPlayer(playerId).getName());
					sign.setLine(3, bounty + " wanks");
				}

				sign.update();
			}
			catch (Exception ex)
			{
				plugin.log("ERROR: Not a sign at " + location.hashCode() + ". Deleted from config.");
				plugin.log("ERROR: " + ex.getMessage());
				config.set("Signs." + locationHash, null);
				save();
			}
		}
	}
	
	private boolean isWanted(Player player)
	{
		return config.getString("Bounties." + player.getUniqueId().toString() + ".Bounty") != null;		
	}
	
	public int getBounty(UUID playerId)
	{
		return config.getInt("Bounties." + playerId.toString() + ".Bounty");	
	}

	public void addBounty(Player player, int bounty)
	{
		int currentBounty = config.getInt("Bounties." + player.getUniqueId().toString() + ".Bounty");
		
		currentBounty += bounty;
		
		config.set("Bounties." + player.getUniqueId().toString() + ".Bounty", currentBounty);

		save();
		
		this.updateWantedSigns();
	}

	public void clearBounty(Player player)
	{	
		config.set("Bounties." + player.getUniqueId().toString(), null);

		save();
		
		this.updateWantedSigns();
	}
}