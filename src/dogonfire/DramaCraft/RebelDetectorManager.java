package dogonfire.DramaCraft;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;


public class RebelDetectorManager implements Listener
{
	private DramaCraft plugin;
	private Random random = new Random();
	private FileConfiguration			config		= null;
	private File						configFile	= null;
	private HashMap<Long, Location>  	detectors = new HashMap<Long, Location>(); 
	private WorldGuardPlugin 			worldGuard;
	
	public RebelDetectorManager(DramaCraft plugin)
	{
		worldGuard = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		this.plugin = plugin;	
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(this.plugin.getDataFolder(), "detectors.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);

			this.plugin.log("Loaded " + this.config.getConfigurationSection("detectors").getKeys(false).size() + " detectors.");

			for (String hash : this.config.getConfigurationSection("detectors").getKeys(false))
			{
				String key = "detectors." + hash;
				int x = config.getInt(key + ".X");
				int y = config.getInt(key + ".Y");
				int z = config.getInt(key + ".Z");
				String worldName = config.getString(key + ".World");

				World world = plugin.getServer().getWorld(worldName);

				detectors.put(Long.parseLong(hash), new Location(world, x, y, z));
			}
		}
		catch(Exception ex)
		{
			this.plugin.log("No detectors loaded.");			
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
		
	public boolean isStatueSign(Block block)
	{
		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}		
		
		WallSign signData  = (WallSign) block.getState().getBlockData();
        BlockFace attached  = signData.getFacing().getOppositeFace();
        
        Block connected = block.getRelative(attached);
		        
		if (connected.getType() != Material.DIAMOND_BLOCK)
		{
			return false;
		}

		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.GOLD_BLOCK))
		{
			return false;
		}
		
		return true;
	}

	public double getClosestDistanceToStatue(Location location)
	{
		double distance = 999999;
		
		for(Location tlocation : detectors.values())
		{
			if(tlocation.distance(location) < distance)
			{
				distance = tlocation.distance(location);
			}			
		}
		
		return distance;
	}
	
	public boolean handleNewStatue(SignChangeEvent event)
	{
		Player player = event.getPlayer();

		if(!RankManager.isImperial(player.getUniqueId()))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Only imperials can build an Imperial rebel detectors!");
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

		Block statueBlock = this.getStatueBlockFromSign(player.getWorld().getBlockAt(event.getBlock().getLocation()));

		if (statueBlock == null)
		{
			plugin.log("transmitter is not valid");
			return false;
		}
		
		// Make sure there is x distance between this and nearest other transmitter
		double length = getClosestDistanceToStatue(event.getBlock().getLocation());
		
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
			plugin.log("statue is inside an region");
			return false;			
		}		
		
		if(random.nextInt(2)==0)
		{
			RankManager.setKingHead(statueBlock.getRelative(BlockFace.UP).getLocation());
		}
		else
		{
			RankManager.setQueenHead(statueBlock.getRelative(BlockFace.UP).getLocation());
		}

		int detectorId = this.addDetector(event.getPlayer(), message, event.getBlock().getLocation());

		event.setLine(0, event.getLine(0).trim());
		event.setLine(1, event.getLine(1).trim());
		event.setLine(2, event.getLine(2).trim());
		event.setLine(3, event.getLine(3).trim());

		plugin.log(event.getPlayer().getName() + " placed an imperial detector at " + event.getBlock().getLocation());
		event.getPlayer().sendMessage(ChatColor.GREEN + "Imperial rebel detector placed!");
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The Imperials placed a new Rebel Detector!");
		event.getPlayer().sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/detector " + detectorId + ChatColor.AQUA + " to go here when a rebel is detected!");

		return true;
	}
	
	private int addDetector(Player player, String message, Location location)
	{
		long hash = hashLocation(location);
		//config.set(hash + ".CreatedTime", hash);
		
		String key = "detectors." + hash;
		
		config.set(key + ".PlayerId", player.getUniqueId().toString());
		config.set(key + ".Message", message);
		config.set(key + ".X", location.getBlockX());
		config.set(key + ".Y", location.getBlockY());
		config.set(key + ".Z", location.getBlockZ());
		config.set(key + ".World", location.getWorld().getName());
		
		save();

		detectors.put(hash, location); 	
		
		return (int) (hash % 100);
	}
	
	private void removeDetector(Location location)
	{
		long hash = hashLocation(location);
		String key = "statues." + hash;
		config.set(key, null);
		
		save();

		detectors.remove(hash); 			
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if(!RankManager.isImperial(player.getUniqueId()) && !player.isOp())
		{
			return;
		}
				
		if (this.isStatueSign(event.getBlock()))
		{
			if (!this.handleNewStatue(event))
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
		if(isStatueBlock(event.getBlock().getLocation()))
		{
			removeDetector(event.getBlock().getLocation());
			plugin.log(event.getPlayer().getName() + " removed a rebel detector at " + event.getBlock().getLocation());
			
			if(RankManager.isRebel(event.getPlayer().getUniqueId()))
			{
				plugin.getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.AQUA + " destroyed a rebel detector!");
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

	boolean isStatueBlock(Location location)
	{		
		return config.getString("detectors." + hashLocation(location) + ".PlayerId") != null;
	}
	
	public Block getStatueBlockFromSign(Block block)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return null;
		}

		if (block == null || block.getType() != Material.OAK_WALL_SIGN)
		{
			return null;
		}		
		
		WallSign signData  = (WallSign) block.getState().getBlockData();
        BlockFace attached  = signData.getFacing().getOppositeFace();
        
        Block connected = block.getRelative(attached);
        
		if (!connected.getRelative(BlockFace.UP).getType().equals(Material.GOLD_BLOCK))
		{
			return null;
		}

		return connected;
	
	}
	
	public int getStatues()
	{
		return detectors.size();
	}

	public int getImperialIncome()
	{
		ConfigurationSection section = config.getConfigurationSection("statues");
		
		if(section==null)
		{
			return 0;
		}
		
		Set<String> keys = section.getKeys(false);
		int income = keys.size() * 10;		
		
		int n = RankManager.getActiveImperials();
		
		if(n==0)
		{
			return 0;
		}
		
		income = income / n;
		
		return income;
	}
	
	public void updateDetectors()
	{
		if(RankManager.getOnlineImperials()==0 || detectors.size()==0)
		{
			return;			
		}
		
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The Imperials earned " + ChatColor.GOLD + getImperialIncome() + " wanks" + ChatColor.AQUA + " from statues last hour.");
			
		// Check for invalid placement	
		Location location = (Location)(detectors.values().toArray()[random.nextInt(detectors.keySet().size())]);

		// Make sure this is not inside a region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
		
		if(set.size() > 0)
		{
			removeDetector(location);
			plugin.log("Statue was inside an region. Destroyed the statue.");
			return;			
		}							
	}
}