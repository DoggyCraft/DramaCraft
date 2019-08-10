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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;


// Because the hierachy and ranks of imperials and rebels can be hard to understand for players, automated advice is very important to guide the player to what they can do with their rank
// Also to encourage them to use their powers and maintain a level of tension (drama) among the factions, thereby creating a lore and culture. 
public class AdviceManager implements Listener
{
	private DramaCraft 					plugin;
	private Random 						random = new Random();
	private FileConfiguration			config		= null;
	private File						configFile	= null;
	private HashMap<Long, Location>  	transmitters = new HashMap<Long, Location>(); 
	private WorldGuardPlugin 			worldGuard;
	private long 						lastRebelHelpTime;
	private long 						lastImperialHelpTime;
	
	public AdviceManager(DramaCraft plugin)
	{
		worldGuard = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		this.plugin = plugin;	
	}
	
		
	// King/Queen : Check time since last treasure hunt + Check numbers of online players > 3 -> Advice to start treasure hunt
	// King/Queen : Check time since last bounty set + Rebel online -> Advice to set a bounty
	// King/Queen : Check time since last donation -> Encourage players to donate to the empire
	//
	public void sendMessage()
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
			int n = random.nextInt(keys.size());
			String hash = (String) keys.toArray()[n];
			String path = "transmitters." + hash;

			String message = config.getString(path + ".Message");

			plugin.getServer().broadcastMessage(ChatColor.RED + "Rebel Message >> " + message);
	}
}