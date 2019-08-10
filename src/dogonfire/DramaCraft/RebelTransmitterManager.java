package dogonfire.DramaCraft;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;



public class RebelTransmitterManager implements Listener
{
	private DramaCraft plugin;
	private Random random = new Random();
	private FileConfiguration			config		= null;
	private File						configFile	= null;
	private HashMap<Long, Location>  	transmitters = new HashMap<Long, Location>(); 
	private WorldGuardPlugin 			worldGuard;
	private long 						lastRebelHelpTime;
	private long 						lastImperialHelpTime;
	
	public RebelTransmitterManager(DramaCraft plugin)
	{
		worldGuard = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		this.plugin = plugin;	
	}
	
	public void load()
	{
		try
		{
		this.configFile = new File(this.plugin.getDataFolder(), "transmitters.yml");

		this.config = YamlConfiguration.loadConfiguration(this.configFile);

		this.plugin.log("Loaded " + this.config.getConfigurationSection("transmitters").getKeys(false).size() + " transmitters.");
				
		for(String hash : this.config.getConfigurationSection("transmitters").getKeys(false))
		{
			String key = "transmitters." + hash;
			int x = config.getInt(key + ".X");
			int y = config.getInt(key + ".Y");
			int z = config.getInt(key + ".Z");
			int yaw = config.getInt(key + ".Yaw");
			String worldName = config.getString(key + ".World");
					
			World world = plugin.getServer().getWorld(worldName);
			
			transmitters.put(Long.parseLong(hash), new Location(world,x,y,z,yaw,0)); 			
		}
		}
		catch(Exception ex)
		{
			this.plugin.log("No Transmitters loaded.");			
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
	
	public boolean isTransmitterSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			this.plugin.log("Not isTransmitterSign");
			return false;
		}		
		
		Attachable s = (Attachable) block.getState().getData();
        Block connected = block.getRelative(s.getAttachedFace());
        
		if (connected.getType() != Material.STONE)
		{
			this.plugin.log("Not isTransmitterSign STONE");
			return false;
		}

		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.TORCH))
		{
			this.plugin.log("Not isTransmitterSign TORCH");
			return false;
		}
		
		return true;
	}
	
	public double getClosestDistanceToTransmitter(Location location)
	{
		double distance = 999999;
		
		for(Location tlocation : transmitters.values())
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

		if(!plugin.isRebel(player.getUniqueId()))
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
			event.getPlayer().sendMessage(ChatColor.RED + "That message is too short");
			plugin.log("message is too short");
			return false;
		}

		message = message.trim();

		Block altarBlock = this.getTransmitterBlockFromSign(player.getWorld().getBlockAt(event.getBlock().getLocation()));

		if (altarBlock == null)
		{
			plugin.log("transmitter is not valid");
			return false;
		}
		
		// Make sure there is x distance between this and nearest other transmitter
		double length = getClosestDistanceToTransmitter(event.getBlock().getLocation());
		
		if(length < 100)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "That is too close to another transmitter");
			plugin.log("transmitter is too close to another transmitter");
			return false;
		}
		
		// Make sure this is not inside a region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getBlock().getLocation()));
		
		if(set.size() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Transmitter cannot be inside an region");
			plugin.log("transmitter is inside an region");
			return false;			
		}		
		
		this.addTransmitter(event.getPlayer(), message, event.getBlock().getLocation());

		event.setLine(0, event.getLine(0).trim());
		event.setLine(1, event.getLine(1).trim());
		event.setLine(2, event.getLine(2).trim());
		event.setLine(3, event.getLine(3).trim());

		plugin.log(event.getPlayer().getName() + " placed a rebel transmitter at " + event.getBlock().getLocation());
		event.getPlayer().sendMessage(ChatColor.GREEN + "Rebel Transmitter placed!");
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The Rebels placed a new Transmitter!");

		return true;
	}
	
	private void addTransmitter(Player player, String message, Location location)
	{
		long hash = hashLocation(location);
		//config.set(hash + ".CreatedTime", hash);
		
		String key = "transmitters." + hash;
		
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
		String key = "transmitters." + hash;
		config.set(key, null);
		
		save();

		transmitters.remove(hash); 			
	}
	
	public int getTransmitters()
	{
		return transmitters.size();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if(!plugin.isRebel(player.getUniqueId()) && !player.isOp())
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

		if(isTransmitterBlock(event.getBlock().getLocation()))
		{
			removeTransmitter(event.getBlock().getLocation());
			plugin.log(event.getPlayer().getName() + " removed a rebel transmitter at " + event.getBlock().getLocation());
			
			if(plugin.isImperial(event.getPlayer().getUniqueId()))
			{
				event.getPlayer().sendMessage(ChatColor.GREEN + "You received " + ChatColor.GOLD + "100 wanks" + ChatColor.AQUA + " for destroying that transmitter!");
				plugin.getEconomyManager().depositPlayer(event.getPlayer().getName(), 100);
				plugin.getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.AQUA + " destroyed a Rebel Transmitter!");
			}
		}
	}
	
	public long hashLocation(Location location)
	{
	     int result = 373; // Constant can vary, but should be prime
	     result = 37 * result + location.getBlockX();
	     result = 37 * result + location.getBlockY();
	     result = 37 * result + location.getBlockZ();
	     
	     return result;
	}

	boolean isTransmitterBlock(Location location)
	{		
		return config.getString("transmitters." + hashLocation(location) + ".PlayerId") != null;
	}
	
	public Block getTransmitterBlockFromSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return null;
		}

		Attachable s = (Attachable) block.getState().getData();
        Block connected = block.getRelative(s.getAttachedFace());
        
		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.TORCH))
		{
			return null;
		}

		return connected;
	
	}
	
	public void transmitMessage()
	{
		if(System.currentTimeMillis() > lastRebelHelpTime + (5*60*1000 + 7*60*1000*transmitters.size()))
		{
			for(Player rebelPlayer : plugin.getOnlineRebelPlayers())
			{
				plugin.sendInfo(
					rebelPlayer.getUniqueId(), 
					LANGUAGESTRING.INFO_REBEL_BUILD_TRANSMITTERS, 
					ChatColor.AQUA,
					0,
					120
					);			
			}
			
			//plugin.log("transmitMessage rebel CHECK");

			lastRebelHelpTime = System.currentTimeMillis();
		}
		
		
		if(transmitters.size()==0)
		{
			//plugin.log("transmitMessage NO MESSAGE");
			return;
		}

		
		Set<String> keys = config.getConfigurationSection("transmitters").getKeys(false);
				
		if (keys.size() > 0)
		{
			int n = random.nextInt(keys.size());
			String hash = (String) keys.toArray()[n];
			String path = "transmitters." + hash;

			String message = config.getString(path + ".Message");

			plugin.getServer().broadcastMessage(ChatColor.RED + "Rebel Message >> " + message);

			// Check for invalid placement
			Location location = (Location) (transmitters.values().toArray()[random.nextInt(transmitters.keySet().size())]);

			// Make sure this is not inside a region
			RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = container.createQuery();
			ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
			
			//TODO: Check for GriefPrevention regions as well
			
			if (set.size() > 0)
			{
				removeTransmitter(location);
				plugin.log("Transmitter was located inside an region. Destroyed the transmitter.");
				return;
			}
		}
	}
}