package com.dogonfire.dramacraft;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.dogonfire.dramacraft.LanguageManager.LANGUAGESTRING;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;




public class RebelTransmitterManager implements Listener
{
	private Random 						random 			= new Random();
	private FileConfiguration			config			= null;
	private File						configFile		= null;
	private HashMap<Long, Location>  	transmitters 	= new HashMap<Long, Location>(); 
	private WorldGuardPlugin 			worldGuard	 	= null;
	//private GriefPrevention 			griefPrevention;
	private long 						lastRebelHelpTime;
	private long 						lastImperialHelpTime;
	static RebelTransmitterManager 		instance;
	
	public RebelTransmitterManager()
	{
		instance = this;
		worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		//this.griefPrevention = GriefPrevention.instance;
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(DramaCraft.instance().getDataFolder(), "transmitters.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);

			DramaCraft.log("Loaded " + this.config.getConfigurationSection("Transmitters").getKeys(false).size() + " transmitters.");

			for (String hash : this.config.getConfigurationSection("Transmitters").getKeys(false))
			{
				String key = "Transmitters." + hash;
				int x = config.getInt(key + ".X");
				int y = config.getInt(key + ".Y");
				int z = config.getInt(key + ".Z");
				int yaw = config.getInt(key + ".Yaw");
				String worldName = config.getString(key + ".World");

				World world = Bukkit.getServer().getWorld(worldName);

				transmitters.put(Long.parseLong(hash), new Location(world, x, y, z, yaw, 0));
			}
		}
		catch (Exception ex)
		{
			DramaCraft.log("No Transmitters loaded.");
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
	
	public boolean isTransmitterSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			//this.plugin.log("Not isTransmitterSign OAK_WALL_SIGN");
			return false;
		}		
		
		BlockData bd = block.getBlockData();
		if (!(bd instanceof Directional)) 
		{
			return false;
		}
		
		Directional directional = (Directional) bd;

		Block connected = block.getRelative(directional.getFacing().getOppositeFace());
        
		if (connected.getType() != Material.STONE)
		{
			DramaCraft.log("Not isTransmitterSign STONE");
			return false;
		}

		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.TORCH))
		{
			DramaCraft.log("Not isTransmitterSign TORCH");
			return false;
		}
		
		return true;
	}
	
	static public double getClosestDistanceToTransmitter(Location location)
	{
		double distance = 999999;
		
		for(Location tlocation : instance.transmitters.values())
		{
			if(tlocation.distance(location) < distance)
			{
				distance = tlocation.distance(location);
			}			
		}
		
		return distance;
	}
	
	public boolean handleNewTransmitter(SignChangeEvent event)
	{
		Player player = event.getPlayer();

		if(!RankManager.isRebel(player.getUniqueId()))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Only rebels can build a Rebel Transmitter!");
			return false;					
		}
		
		String message = "";
		int line = 0;

		while (line < 4)
		{
			message += event.getLine(line++).trim() + " ";
		}

		if (message.length() <= 3)
		{
			//this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InvalidGodName, ChatColor.DARK_RED, 0, "", 20);
			event.getPlayer().sendMessage(ChatColor.DARK_RED + "That message is too short");
			DramaCraft.log("message is too short");
			return false;
		}

		message = message.trim();

		Block altarBlock = this.getTransmitterBlockFromSign(player.getWorld().getBlockAt(event.getBlock().getLocation()));

		if (altarBlock == null)
		{
			DramaCraft.log("transmitter is not valid");
			return false;
		}
		
		// Make sure there is x distance between this and nearest other transmitter
		double length = getClosestDistanceToTransmitter(event.getBlock().getLocation());
		
		if(length < 100)
		{
			event.getPlayer().sendMessage(ChatColor.DARK_RED + "That is too close to another transmitter");
			DramaCraft.log("Transmitter is too close to another transmitter");
			return false;
		}
		
		// Make sure this is not inside a worldguard region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));
		
		if(set.size() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.DARK_RED + "Transmitter cannot be inside an region");
			DramaCraft.log("transmitter is inside an region");
			return false;			
		}		

		// Make sure this is not inside a grief prevention region
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getBlock().getLocation(), false, null);

		if (claim != null)
		{
			return false;
		}				
		
		this.addTransmitter(event.getPlayer(), message, event.getBlock().getLocation());

		event.setLine(0, event.getLine(0).trim());
		event.setLine(1, event.getLine(1).trim());
		event.setLine(2, event.getLine(2).trim());
		event.setLine(3, event.getLine(3).trim());

		DramaCraft.log(event.getPlayer().getName() + " placed a rebel transmitter at " + event.getBlock().getLocation());
		event.getPlayer().sendMessage(ChatColor.GREEN + "Rebel Transmitter placed!");
		Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "The Rebels placed a new Transmitter!");

		return true;
	}
	
	private void addTransmitter(Player player, String message, Location location)
	{
		long hash = hashLocation(location);
		//config.set(hash + ".CreatedTime", hash);
		
		String key = "Transmitters." + hash;
		
		config.set(key + ".PlayerId", player.getUniqueId().toString());
		config.set(key + ".Message", message);
		config.set(key + ".X", location.getBlockX());
		config.set(key + ".Y", location.getBlockY());
		config.set(key + ".Z", location.getBlockZ());
		config.set(key + ".World", location.getWorld().getName());
		
		save();

		transmitters.put(hash, location); 			
	}
	
	private void removeTransmitter(Location location)
	{
		long hash = hashLocation(location);
		String key = "Transmitters." + hash;
		config.set(key, null);
		
		save();

		transmitters.remove(hash); 			
	}
	
	static public int getTransmitters()
	{
		return instance.transmitters.size();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if(!RankManager.isRebel(player.getUniqueId()) && !player.isOp())
		{
			return;
		}
				
		if (this.isTransmitterSign(event.getBlock()))
		{
			if (!this.handleNewTransmitter(event))
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
			}
			return;
		}
	}

	@EventHandler
	public void OnBlockBreak(BlockBreakEvent event)
	{
		if(isTransmitterSign(event.getBlock()))
		{
			Block block = getTransmitterBlockFromSign(event.getBlock());
			
			destroyTransmitter(event.getPlayer(), block.getLocation());
		}				
		else if(isTransmitterBlock(event.getBlock().getLocation()))
		{
			destroyTransmitter(event.getPlayer(), event.getBlock().getLocation());
		}
	}

	private void destroyTransmitter(Player player, Location location)
	{
		removeTransmitter(location);
		DramaCraft.log(player.getName() + " removed a rebel transmitter at " + location);
		
		if(RankManager.isImperial(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.GREEN + "You received " + ChatColor.GOLD + "100 wanks" + ChatColor.GREEN + " for destroying that rebel transmitter!");
			DramaCraft.instance().getEconomyManager().depositPlayer(player.getName(), 100);
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.GRAY + " destroyed a Rebel Transmitter!");
		}		
	}
	
	public long hashLocation(Location location)
	{
	     int result = 373; // Constant can vary, but should be prime
	     result = 37 * result + location.getBlockX();
	     result = 37 * result + location.getBlockY();
	     result = 37 * result + location.getBlockZ();
	     
	     return location.hashCode();
	}

	boolean isTransmitterBlock(Location location)
	{		
		return config.getString("Transmitters." + hashLocation(location) + ".PlayerId") != null;
	}
	
	public Block getTransmitterBlockFromSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return null;
		}
        
        BlockData bd = block.getBlockData();
		if (!(bd instanceof Directional)) 
		{
			return null;
		}
		
		Directional directional = (Directional) bd;

		Block connected = block.getRelative(directional.getFacing().getOppositeFace());
                
		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.TORCH))
		{
			return null;
		}

		return connected;
	
	}
	
	public void transmitMessage()
	{
		if (transmitters.size() < 3)
		{
			if (System.currentTimeMillis() > lastRebelHelpTime + (10 * 60 * 1000 + 10 * 60 * 1000 * transmitters.size()))
			{
				for (Player rebelPlayer : RankManager.getOnlineRebelPlayers())
				{
					DramaCraft.instance().sendInfo(rebelPlayer.getUniqueId(), LANGUAGESTRING.INFO_REBEL_BUILD_TRANSMITTERS, ChatColor.AQUA, 0, 120);

					DramaCraft.log("Send INFO_REBEL_BUILD_TRANSMITTERS to " + rebelPlayer.getName());

				}

				// plugin.log("transmitMessage rebel CHECK");

				lastRebelHelpTime = System.currentTimeMillis();
			}
		}		
		
		if(transmitters.size()==0)
		{
			//plugin.log("transmitMessage NO MESSAGE");
			return;
		}
		
		Set<String> keys = config.getConfigurationSection("Transmitters").getKeys(false);
				
		if (keys.size() > 0)
		{
			int n = random.nextInt(keys.size());
			String hash = (String) keys.toArray()[n];
			String path = "Transmitters." + hash;

			String message = config.getString(path + ".Message");

			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Rebel Message >> " + message);

			// Check for invalid placement
			Location location = (Location) (transmitters.values().toArray()[random.nextInt(transmitters.keySet().size())]);

			// Make sure this is not inside a region
			RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = container.createQuery();
			ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
			
			//TODO: Check for GriefPrevention regions as well
			//ClaimBlockSystem system = griefPrevention.
			
			if (set.size() > 0)
			{
				removeTransmitter(location);
				DramaCraft.log("Transmitter was located inside an region. Destroyed the transmitter.");
				return;
			}
			
			if (location.getBlock().getType()!=Material.OAK_SIGN)
			{
				removeTransmitter(location);
				DramaCraft.log("Transmitter was air. Destroyed the transmitter.");
				return;
			}
		}
	}
}