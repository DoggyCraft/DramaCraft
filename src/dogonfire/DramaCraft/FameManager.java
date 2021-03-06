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
import org.bukkit.block.Sign;
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



public class FameManager implements Listener
{	
	enum FameType
	{
		FAME_MOST_CUTE,
		FAME_MOST_NICE,
		FAME_MOST_NASTY,
		FAME_MOST_FUNNY,
		FAME_MOST_FRIENDLY,
		FAME_MOST_INTELLIGENT,		
	}
	
	private Random 							random = new Random();
	private FileConfiguration				config		= null;
	private File							configFile	= null;
	private HashMap<FameType, List<Long>> 	fameBlockLocations = new HashMap<FameType, List<Long>>();
		
	public FameManager()
	{
		
	}
	
	public void load()
	{
		try
		{
			this.configFile = new File(DramaCraft.instance().getDataFolder(), "fame.yml");

			this.config = YamlConfiguration.loadConfiguration(this.configFile);

			DramaCraft.instance().log("Loaded " + this.config.getConfigurationSection("Blocks").getKeys(false).size() + " fame blocks.");				
		}
		catch(Exception ex)
		{
			DramaCraft.instance().log("No fame blocks loaded.");			
		}
		
	}
	
	public void save()
	{
		if (this.config == null || this.configFile == null)
		{
			DramaCraft.instance().log("Config: " + this.config);
			DramaCraft.instance().log("Configfile: " + this.configFile);
			return;
		}
		
		try
		{
			this.config.save(this.configFile);
		}
		catch (Exception ex)
		{
			DramaCraft.instance().log("Could not save config to " + this.configFile + ": " + ex.getMessage());
		}
		
		DramaCraft.instance().log("Saved configuration.");
	}
	
	public boolean setFameHead(Location location)
	{
		UUID ownerId = UUID.fromString(config.getString("Fame.Id"));
		
		setFameHead(ownerId, location);
		
		return true;
	}

	public void setFameHead(UUID ownerId, Location location)
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
	
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(ownerId);
		
		//Skull s = (Skull)location.getBlock().getState();
		//s.setOwningPlayer(player);
		//s.setRotation(BlockFace.SOUTH);
		//s.update();		
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(player);
		skull.setItemMeta(meta);
	}
	
	private boolean handleNewFameBlock(Block signBlock, Block fameBlock)
	{
		FameType fameType = getFameTypeFromBlock(signBlock);
		
		long hash = 0;
		
		fameBlockLocations.get(fameType).add(hash);
		
		//setFameHead();
		
		return true;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		
	}
	
	private FameType getFameTypeFromBlock(Block block)
	{			
		Sign sign = (Sign) block.getBlockData();
		switch(sign.getLine(0))
		{
			case "Cute" :  return FameType.FAME_MOST_CUTE;
		}
		
		return null;
	}
		
	public boolean isFameBlock(Block block)
	{
		if (block == null || false)
		{
			return false;
		}

		if (block.getRelative(BlockFace.UP).getType() != Material.PLAYER_HEAD)
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

		DramaCraft.instance().logDebug("isFameSign(): FameSignBlock block is " + block.getType().name());
		
		if (getFameTypeFromBlock(block) == null)
		{
			return false;
		}
				
		return true;
	}
	
	private Block getFameBlockFromSign(Block block)
	{
		WallSign wallSign = (org.bukkit.block.data.type.WallSign) block.getBlockData();
		Block fameBlock = block.getRelative(wallSign.getFacing().getOppositeFace());
		return fameBlock;
	}
	
	@EventHandler
	public void OnSignChange(SignChangeEvent event)
	{
		
		if (!isFameSign(event.getBlock()))
		{
			return;
		}
		
		Block fameBlock = getFameBlockFromSign(event.getBlock());

		if (!isFameBlock(fameBlock))
		{
			return;
		}
				
		//if (!handleNewFameBlock(event.getBlock(), fameBlock))
		//{
		// TODO add this block to list of blocks for this fametype	
		//}
	}
}