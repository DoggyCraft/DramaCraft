package dogonfire.DramaCraft;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


// This is where the custom items for DramaCraft gets spawned and maintained
public class ArtifactManager
{
	static enum ArtifactType
	{
		Vodka,
		Weed,
	}

	private Random				random				= new Random();
	private FileConfiguration	artifactsConfig		= null;
	private File				artifactsConfigFile	= null;
	static private ArtifactManager instance;
	
	ArtifactManager()
	{
		instance = this;
	}
	
	static public ArtifactManager instance()
	{
		return instance;
	}

	public void load()
	{
		if (this.artifactsConfigFile == null)
		{
			this.artifactsConfigFile = new File(DramaCraft.instance().getDataFolder(), "artifacts.yml");
		}
		this.artifactsConfig = YamlConfiguration.loadConfiguration(this.artifactsConfigFile);

		DramaCraft.instance().log("Loaded " + this.artifactsConfig.getKeys(false).size() + " artifacts.");
	}

	public void save()
	{
		if ((this.artifactsConfig == null) || (this.artifactsConfigFile == null))
		{
			return;
		}
		try
		{
			this.artifactsConfig.save(this.artifactsConfigFile);
		}
		catch (Exception ex)
		{
			DramaCraft.instance().log("Could not save config to " + this.artifactsConfig + ": " + ex.getMessage());
		}
	}

	public ArtifactType getArtifactTypeFromItem(ItemStack item)
	{
		if (!item.hasItemMeta() || item.getItemMeta().getDisplayName()== null || item.getItemMeta().getDisplayName().length() < 3)
		{
			return null;
		}
		
		String name = item.getItemMeta().getDisplayName().substring(2);

		return getArtifactTypeFromName(name);
	}

	public ArtifactType getArtifactTypeFromName(String name)
	{
		if (name == null)
		{
			return null;
		}
				
		return null;// artifactTypes.get(name);
	}

	public boolean isArtifact(ItemStack item)
	{
		if (!item.hasItemMeta() || item.getItemMeta().getDisplayName() == null)
		{
			return false;
		}
			
		if(item.getItemMeta().getLore() == null || item.getItemMeta().getLore().size() < 2)
		{
			return false;			
		}
				
		return getArtifactTypeFromName(item.getItemMeta().getDisplayName().substring(2)) != null;
	}
	
	// This event will fire when a player is finishing consuming an item (food, potion, milk bucket). 
	// If the ItemStack is modified the server will use the effects of the new item and not remove the original one from the player's inventory. 
	// If the event is cancelled the effect will not be applied and the item will not be removed from the player's inventory.
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event)
	{
		
	}

	public ItemStack createArtifact(String creatorName, ArtifactType artifactType)
	{
		Material itemType = Material.AIR;
		String itemName = null;
		String lorePage = null;

		ItemStack item = null;
		switch (artifactType)
		{
			case Vodka:
				itemName = "Vodka";
				lorePage = ChatColor.DARK_PURPLE + "The good stuff!";
				itemType = Material.BONE;
				break;
			case Weed:
				itemName = "Weed";
				lorePage = ChatColor.DARK_PURPLE + "Blaze it!";
				itemType = Material.DIAMOND;
				break;
			default:
				DramaCraft.instance().log("createArtifact() : Unknown type " + artifactType);
				return null;
		}
		
		if (item == null)
		{
			item = new ItemStack(itemType);
		}
		
		ItemMeta itemMeta = item.getItemMeta();
		try
		{
			if (itemName != null)
			{
				itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(ChatColor.GOLD + itemName);
			}		
		}
		catch (Exception ex)
		{
			DramaCraft.instance().logDebug("createArtifact(): Could not get or set item meta");
		}
		
		if (creatorName != null)
		{
			try
			{
				if (lorePage != null)
				{
					List<String> lorePages = new ArrayList<String>();
					lorePages.add(lorePage);
					lorePages.add(ChatColor.WHITE + "Created by " + creatorName);
					itemMeta.setLore(lorePages);
				}
			}
			catch (Exception ex)
			{
				DramaCraft.instance().logDebug("createArtifact(): Could not set meta lore pages");
				return null;
			}
		}
								
		item.setItemMeta(itemMeta);

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.artifactsConfig.set("Location." + item.hashCode() + ".Type", artifactType.toString());

		save();

		return item;
	}
}