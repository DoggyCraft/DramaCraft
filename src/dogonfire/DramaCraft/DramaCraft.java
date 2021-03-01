package dogonfire.DramaCraft;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class DramaCraft extends JavaPlugin
{
	private Logger						log;
	private	Commands					command;
	private VoteManager					voteManager;
	private RankManager					rankManager;
	private ResourceManager				resourceManager;
	private BodyguardManager			bodyGuardManager;
	private BountyManager				bountyManager;
	private RevolutionManager			revolutionManager;
	private RebelTransmitterManager		rebelTransmitterManager;
	private RebelDetectorManager		rebelDetectorManager;
	private ImperialManager				imperialManager;
	private PermissionsManager			permissionsManager			= null;
	private RevolutionPlayerListener	revolutionPlayerListener	= null;
	private static Server				server;
	private PluginManager				pluginmanager				= null;
	public static Economy				economy						= null;

	public long							voteTimeLength				= 800000000000L;
	public long							voteTimeLengthBetween		= 600000000L; // 60000000000L
	public double						requiredYesPercentage		= 66;
	public int							requiredVotes				= 20;
	public int							votePayment					= 10;
	public int							startVoteCost				= 10;
	
	private String 						pattern 					= "HH:mm:ss dd-MM-yyyy";
	DateFormat 							formatter 					= new SimpleDateFormat(this.pattern);

	private boolean						debug						= true;
	String								serverName;
	private LanguageManager				languageManager;
	private FileConfiguration			config						= null;
	static private DramaCraft			instance;
	
	static public DramaCraft instance()
	{
		return instance;
	}
	
	public static void setDay(String worldName)
	{
		server.getWorld(worldName).setTime(600L);
	}

	public static void setNight(String worldName)
	{
		server.getWorld(worldName).setTime(20000L);
	}

	public static void setSun(String worldName)
	{
		server.getWorld(worldName).setStorm(false);
	}

	public static void setStorm(String worldName)
	{
		server.getWorld(worldName).setStorm(true);
	}
	
	private boolean isDay(long currenttime, int offset)
	{
		return (currenttime < 12000 + offset) && (currenttime > offset);
	}

	private boolean isSun(World world)
	{
		if ((world.hasStorm()) || (world.isThundering()))
		{
			return false;
		}
		return true;
	}
	
	static public void log(String message)
	{
		instance.log.info("[" + instance.getDescription().getFullName() + "] " + message);
	}

	static public void logDebug(Object object)
	{
		if (instance.debug)
		{
			instance.log.info("[" + instance.getDescription().getFullName() + "] " + object);
		}
	}

	public ResourceManager getResourceManager()
	{
		return resourceManager;
	}

	public LanguageManager getLanguageManager()
	{
		return languageManager;
	}

	public Economy getEconomyManager()
	{
		return economy;
	}

	public static void broadcastMessage(String message)
	{
		server.broadcastMessage(ChatColor.AQUA + message);
	}
	
	public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, int amount, int delay)
	{
		Player player = getServer().getPlayer(playerId);

		if (player == null)
		{
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		
		getServer().getScheduler().runTaskLater(this, new InfoTask(this, color, playerId, message, amount), delay);
	}
	
	public void startTreasureHunt()
	{
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "starthunt" + this.getServer().getWorlds().get(0).getName());		
	}
	
	public void onDisable()
	{
	}

	public void onEnable()
	{
		server = getServer();
		instance = this;
		
		loadSettings();
		saveSettings();

		this.log = Logger.getLogger("Minecraft");

		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

		if (economyProvider != null)
		{
			economy = (Economy) economyProvider.getProvider();
			log("Using Vault.");
		}
		else
		{
			log("Vault not found.  No money will used.");
		}
		
		this.command = new Commands();

		this.pluginmanager = getServer().getPluginManager();

		permissionsManager = new PermissionsManager();
		permissionsManager.load();

		resourceManager = new ResourceManager();
		resourceManager.load();

		voteManager = new VoteManager();

		revolutionManager = new RevolutionManager();
		server.getPluginManager().registerEvents(revolutionManager, this);

		revolutionPlayerListener = new RevolutionPlayerListener();
		server.getPluginManager().registerEvents(revolutionPlayerListener, this);

		languageManager = new LanguageManager();
		languageManager.load();

		rebelTransmitterManager = new RebelTransmitterManager();
		getServer().getPluginManager().registerEvents(rebelTransmitterManager, this);
		rebelTransmitterManager.load();

		rebelDetectorManager = new RebelDetectorManager();
		getServer().getPluginManager().registerEvents(rebelDetectorManager, this);
		rebelDetectorManager.load();

		imperialManager = new ImperialManager();

		bountyManager = new BountyManager();
		bountyManager.load();
		getServer().getPluginManager().registerEvents(bountyManager, this);
		
		bodyGuardManager = new BodyguardManager(this);
		getServer().getPluginManager().registerEvents(bodyGuardManager, this);

		server.getScheduler().runTaskTimerAsynchronously(this, new Runnable()
		{
			public void run()
			{
				imperialManager.update();
			}
		}, 30L, 100*20L);

		server.getScheduler().runTaskTimer(this, new Runnable()
		{
			public void run()
			{
				if(!revolutionManager.enforceRevolution())
				{
					VoteManager.checkVote(20);
				}
			}
		}, 20L, 100*20L);
		
		server.getScheduler().runTaskTimer(this, new Runnable()
		{
			public void run()
			{
				rebelTransmitterManager.transmitMessage();
			}
		}, 40L, 5*60*20L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return this.command.onCommand(sender, cmd, label, args);
	}	
	
	public void printlog(String message)
	{
		PluginDescriptionFile pdfFile = getDescription();
		this.log.info("[" + pdfFile.getName() + " Version: " + pdfFile.getVersion() + "] " + message);
	}

	public void loadSettings()
	{
		this.config = getConfig();
		
		serverName = this.config.getString("ServerName", "Your Server");
		voteTimeLength = this.config.getLong("VoteTimeLength", 800000000000L);
		//voteTimeLengthBetween = this.config.getLong("VoteTimeLengthBetween", 60000000000L);
		requiredYesPercentage = this.config.getInt("RequiredYesPercentage", 66);
		requiredVotes = this.config.getInt("RequiredVotes", 7);
		votePayment = this.config.getInt("VotePayment", 10);
		startVoteCost = this.config.getInt("StartVoteCost", 200);		
	}

	public void saveSettings()
	{
		this.config.set("ServerName", serverName);
		this.config.set("VoteTimeLength", voteTimeLength);
		this.config.set("VoteTimeLengthBetween", voteTimeLengthBetween);
		this.config.set("RequiredYesPercentage", requiredYesPercentage);
		this.config.set("RequiredVotes", requiredVotes);
		this.config.set("VotePayment", votePayment);
		this.config.set("StartVoteCost", startVoteCost);
				
		saveConfig();
	}	
}