package dogonfire.DramaCraft;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;



// Events encourages roleplay within their factions
public class EventManager implements Listener
{
	private Random 						random = new Random();
	private FileConfiguration			config		= null;
	private File						configFile	= null;
	private HashMap<Long, Location>  	transmitters = new HashMap<Long, Location>(); 
	private WorldGuardPlugin 			worldGuard;
	private long 						lastRebelHelpTime;
	private long 						lastImperialHelpTime;
	
	public EventManager()
	{
		
	}
	
		
	//
	public void generateRebelEvent()
	{
		// Top moonlight spider killers this week
		// Top moonlight spider killers this week
	}
}