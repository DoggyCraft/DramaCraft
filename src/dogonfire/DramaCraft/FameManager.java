package dogonfire.DramaCraft;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Sign;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;


public class FameManager implements Listener
{
	
	enum FameType
	{
		FAME_MOST_CUTES,
		FAME_MOST_NICE,
		FAME_MOST_NASTY,
		FAME_MOST_FUNNY,
		FAME_MOST_FRIENDLY,
		FAME_MOST_INTELLIGENT,
		
	}
	
	private DramaCraft plugin;
	private Random random = new Random();
	ScoreboardManager 					manager;
	Scoreboard 							board;
	private FileConfiguration			config		= null;
	private File						configFile	= null;	
	
	public FameManager()
	{
		
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(this.plugin.getDataFolder(), "fame.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);

			this.plugin.log("Loaded " + this.config.getConfigurationSection("Players").getKeys(false).size() + " fame players.");				
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
	
	public boolean setQueenHead(Location location)
	{
		UUID ownerId = UUID.fromString(config.getString("Queen.Id"));
		
		setQueenHead(ownerId, location);
		
		return true;
	}

	public void setQueenHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		config.set("Queen.Head.World", location.getWorld().getName());
		config.set("Queen.Head.X", location.getBlockX());
		config.set("Queen.Head.Y", location.getBlockY());
		config.set("Queen.Head.Z", location.getBlockZ());
		
		save();				
	}
	
	
	
	public void setHead(UUID ownerId, Location location)
	{		
		location.getBlock().setType(Material.PLAYER_HEAD);
	
		//location.getBlock().setData((byte) 3);
	
		OfflinePlayer player = DramaCraft.instance().getServer().getOfflinePlayer(ownerId);
		
		Skull s = (Skull)location.getBlock().getState();
		s.setOwningPlayer(player);
		s.setRotation(BlockFace.SOUTH);
		s.update();		
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(player);
		skull.setItemMeta(meta);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		
	}
		
	public boolean isFameBlock(Block block)
	{
		if (block == null || false)
		{
			return false;
		}
		if ((block.getRelative(BlockFace.UP).getType() != Material.TORCH) && (block.getRelative(BlockFace.UP).getType() != Material.REDSTONE_TORCH))
		{
			return false;
		}
		
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST })
		{
			if (block.getRelative(face).getType() == Material.OAK_WALL_SIGN)
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean isFameSign(Block block)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return false;
		}
			
		WallSign wallSign = (org.bukkit.block.data.type.WallSign) block.getBlockData();
		Block altarBlock = block.getRelative(wallSign.getFacing().getOppositeFace());

		DramaCraft.instance().logDebug("isFameSign(): FameBlock block is " + altarBlock.getType().name());
		
		//if (getFameSignType == null)
		//{
		//	return false;
		//}
		
		if (!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.PLAYER_HEAD))
		{
			return false;
		}
		
		return true;
	}
	
	@EventHandler
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();

		if (player == null)
		{
			return;
		}
		
		if (isFameSign(event.getBlock()))
		{
			//if (!handleNewFameBlock(event))
			//{
			// TODO add this block to list of blocks for this fametype	
			//}
		}
	}
}