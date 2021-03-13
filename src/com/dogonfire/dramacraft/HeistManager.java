package com.dogonfire.dramacraft;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import com.dogonfire.dramacraft.LanguageManager.LANGUAGESTRING;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;


public class HeistManager implements Listener
{
	static private HeistManager instance;
	
	private UUID rebelLooterPlayerId;
	private int rebelLootAmount;
	
	private UUID imperialLooterPlayerId;
	private int imperialLootAmount;
	
	private FileConfiguration			config			= null;
	private File						configFile		= null;

	private String 						pattern 	= "HH:mm:ss dd-MM-yyyy";
	private DateFormat 					formatter 	= new SimpleDateFormat(pattern);

	public HeistManager()
	{		
		instance = this;		
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(DramaCraft.instance().getDataFolder(), "treasuries.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);
		}
		catch (Exception ex)
		{
			DramaCraft.log("No treasuries loaded.");
		}		
	}
	
	public void save()
	{
		if (this.config == null || this.configFile == null)
		{
			DramaCraft.log("Config: " + this.config);
			DramaCraft.log("Configfile: " + this.configFile);
			return;
		}
		
		try
		{
			this.config.save(this.configFile);
		}
		catch (Exception ex)
		{
			DramaCraft.log("Could not save config to " + this.configFile + ": " + ex.getMessage());
		}
		
		DramaCraft.log("Saved configuration.");
	}
	
	public long hashLocation(Location location)
	{
	     int result = 373; // Constant can vary, but should be prime
	     result = 37 * result + location.getBlockX();
	     result = 37 * result + location.getBlockY();
	     result = 37 * result + location.getBlockZ();
	     
	     return location.hashCode();
	}

	public void setRebelStashLocation(Location location)
	{		
		Date date = new Date();
		config.set("Rebels." + hashLocation(location) + ".LastSetTime", formatter.format(date));
	}

	public void setImperialStashLocation(Location location)
	{		
		Date date = new Date();
		config.set("Imperials." + hashLocation(location) + ".LastSetTime", formatter.format(date));
	}
	
	boolean isRebelStashLocation(Location location)
	{		
		return config.getString("Rebels." + hashLocation(location)) != null;
	}
	
	private boolean isRebelStashSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}		
						
		return isRebelStashLocation(block.getLocation());
	}
	
	boolean isImperialBankLocation(Location location)
	{		
		return config.getString("Imperials." + hashLocation(location)) != null;
	}
	
	private boolean isImperialBankSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}		
						
		return isImperialBankLocation(block.getLocation());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerSignInteract(PlayerInteractEvent event)
	{		
		if(RankManager.isRebel(event.getPlayer().getUniqueId()))
		{
			if (isImperialBankSign(event.getClickedBlock()))
			{
				if (rebelLooterPlayerId == null)
				{
					int amount = TreasuryManager.getImperialBalance() / 5;
					if (amount > 0)
					{
						if (RankManager.getOnlineImperials() > 2)
						{
							DramaCraft.broadcastMessage("The Imperial Bank was just looted for " + ChatColor.GOLD + amount + " wanks!");
							rebelLooterPlayerId = event.getPlayer().getUniqueId();
							rebelLootAmount = amount;
							return;
						}
					}
					else
					{
						DramaCraft.broadcastMessage(ChatColor.DARK_RED + "The Imperial Bank contains 0 wanks.");
					}
				}
			}
		}

		if (RankManager.isImperial(event.getPlayer().getUniqueId()))
		{
			if (isRebelStashSign(event.getClickedBlock()))
			{
				if (imperialLooterPlayerId == null)
				{
					if (RankManager.getOnlineRebels() > 2)
					{
						int amount = TreasuryManager.getRebelsBalance() / 5;
						if(amount > 0)
						{
							DramaCraft.broadcastMessage("" + ChatColor.GOLD + amount + " wanks " + ChatColor.GRAY + " was just confiscated from the Rebel Stash!");
							imperialLooterPlayerId = event.getPlayer().getUniqueId();
							imperialLootAmount = amount;
							return;
						}
						else
						{
							DramaCraft.broadcastMessage(ChatColor.DARK_RED + "The Rebel stash contains 0 wanks.");
						}
					}
				}
			}
		}

		if (isRebelStashSign(event.getClickedBlock()))
		{
			if (rebelLooterPlayerId != null)
			{
				if (rebelLooterPlayerId.equals(event.getPlayer().getUniqueId()))
				{
					DramaCraft.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " deposited the imperial loot worth " + ChatColor.GOLD + rebelLootAmount + " wanks" + ChatColor.GRAY + " to the Rebel Stash!");
					rebelLooterPlayerId = null;
					rebelLootAmount = 0;
					return;
				}
			}
		}

		if (isImperialBankSign(event.getClickedBlock()))
		{
			if (imperialLooterPlayerId != null)
			{
				if (imperialLooterPlayerId.equals(event.getPlayer().getUniqueId()))
				{
					DramaCraft.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " deposited the confiscated rebel loot worth " + ChatColor.GOLD + imperialLootAmount + " wanks" + ChatColor.GRAY + " to the Imperial Treasury!");
					imperialLooterPlayerId = null;
					imperialLootAmount = 0;
					return;
				}
			}
		}			
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{		
		if(rebelLooterPlayerId != null)
		{
			if(rebelLooterPlayerId.equals(event.getPlayer().getUniqueId()))
			{
				DramaCraft.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " logged off while attemptiong to loot the Imperials.");
				DramaCraft.broadcastMessage("" + ChatColor.GOLD + rebelLootAmount +  " wanks " + ChatColor.GRAY + " was returned to the Imperial Bank!");
				rebelLooterPlayerId = null;
				rebelLootAmount = 0;				
			}
		}
		
		if(imperialLooterPlayerId != null)
		{
			if(imperialLooterPlayerId.equals(event.getPlayer().getUniqueId()))
			{
				DramaCraft.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.GRAY + " logged off while attempting to raid the Rebels.");
				DramaCraft.broadcastMessage("" + ChatColor.GOLD + imperialLootAmount +  " wanks " + ChatColor.GRAY + " was returned to the Rebel stash!");
				imperialLooterPlayerId = null;
				imperialLootAmount = 0;				
			}
		}
	}	
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{		
		if(rebelLooterPlayerId != null)
		{
			if(event.getEntity().getUniqueId().equals(rebelLooterPlayerId))
			{
				//DramaCraft.broadcastMessage(" was slain before develivering the loot to the Rebel Stash!");
				DramaCraft.broadcastMessage("" + ChatColor.GOLD + rebelLootAmount +  " wanks " + ChatColor.GRAY + " was returned to the Imperial Bank!");
				rebelLooterPlayerId = null;
				rebelLootAmount = 0;
			}
		}
		
		if(imperialLooterPlayerId != null)
		{
			if(event.getEntity().getUniqueId().equals(imperialLooterPlayerId))
			{
				//DramaCraft.broadcastMessage(" was slain before develivering the loot to the Rebel Stash!");
				DramaCraft.broadcastMessage("" + ChatColor.GOLD + rebelLootAmount +  " wanks " + ChatColor.GRAY + " was returned to the Rebel stash!");
				imperialLooterPlayerId = null;
				imperialLootAmount = 0;
			}
		}
	}
	
	public boolean isWithinRegion(Player player, String regionName)
	{
		// Check for worldguard region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
											
		for(ProtectedRegion region : set)
		{
			if(region.getId().equals(regionName))
			{
				return true;
			}						
		}
	    
	    return false;
	}
}