package com.dogonfire.dramacraft;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import com.dogonfire.dramacraft.tasks.InfoTask;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class DramaCraft extends JavaPlugin
{
	private Logger						log;
	private	Commands					command;
	private VoteManager					voteManager;
	private HeistManager				heistManager;
	private RankManager					rankManager;
	private TreasuryManager				resourceManager;
	private BodyguardManager			bodyGuardManager;
	private BountyManager				bountyManager;
	private RevolutionManager			revolutionManager;
	private RebelTransmitterManager		rebelTransmitterManager;
	private RebelDetectorManager		rebelDetectorManager;
	private ImperialManager				imperialManager;
	private PermissionsManager			permissionsManager			= null;
	private RevolutionPlayerListener	revolutionPlayerListener	= null;
	private TeleportPreventer			teleportPreventer			= null;
	private PhantomPreventer			phantomPreventer			= null;
	private static Server				server;
	private PluginManager				pluginmanager				= null;
	public static Economy				economy						= null;

	public long							voteLengthSeconds				= 800000000000L;
	public int							voteSecondsBetween			= 1; // 60000000000L
	public int 							voteBroadcastSeconds		= 20;
	public double						requiredYesPercentage		= 60;
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
	
	public static void disablePhantoms()
	{	
		PhantomPreventer.disablePhantoms();
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

	public TreasuryManager getResourceManager()
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
		server.broadcastMessage(ChatColor.GRAY + message);
	}
	
	public static void broadcastToRebels(String message)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(RankManager.isRebel(player.getUniqueId()))
			{		
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance, new InfoTask(player.getUniqueId(), message), 2);
			}
		}
	}

	public static void broadcastToImperials(String message)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(RankManager.isImperial(player.getUniqueId()))
			{		
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance, new InfoTask(player.getUniqueId(), message), 2);
			}
		}
	}

	public static void broadcastToRebels(LanguageManager.LANGUAGESTRING message, ChatColor color, String playerName)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(RankManager.isRebel(player.getUniqueId()))
			{		
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance, new InfoTask(color, player.getUniqueId(), message, playerName, null), 2);
			}
		}
	}

	public static void broadcastToImperials(LanguageManager.LANGUAGESTRING message, ChatColor color, String playerName)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(RankManager.isImperial(player.getUniqueId()))
			{		
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance, new InfoTask(color, player.getUniqueId(), message, playerName, null), 2);
			}
		}
	}

	public static void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, int amount, int delay)
	{
		Player player = Bukkit.getServer().getPlayer(playerId);

		if (player == null)
		{
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance, new InfoTask(color, playerId, message, amount), delay);
	}
	
	public void startTreasureHunt()
	{
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "starthunt" + this.getServer().getWorlds().get(0).getName());		
	}
	
	static public boolean isWorldGuardLocation(Location location)
	{
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));		
		return set.size() > 0;
	}
	
	static public boolean isGriefPreventionLocation(Location location)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);

		return claim != null;
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
		
		//getCommand("king").setExecutor(KingCommandExecutor.instance());
		//getCommand("queen").setExecutor(KingCommandExecutor.instance());

		this.pluginmanager = getServer().getPluginManager();

		permissionsManager = new PermissionsManager();
		permissionsManager.load();

		rankManager = new RankManager();
		rankManager.load();

		resourceManager = new TreasuryManager();
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

		heistManager = new HeistManager();
		heistManager.load();
		getServer().getPluginManager().registerEvents(heistManager, this);

		bountyManager = new BountyManager();
		bountyManager.load();
		getServer().getPluginManager().registerEvents(bountyManager, this);
		
		bodyGuardManager = new BodyguardManager(this);
		getServer().getPluginManager().registerEvents(bodyGuardManager, this);

	    teleportPreventer = new TeleportPreventer();
		getServer().getPluginManager().registerEvents(teleportPreventer, this);

		phantomPreventer = new PhantomPreventer();
		getServer().getPluginManager().registerEvents(phantomPreventer, this);

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
				rebelTransmitterManager.transmitMessage();
			}
		}, 40L, 5*60*20L);

		server.getScheduler().runTaskTimer(this, new Runnable()
		{
			public void run()
			{
				PhantomPreventer.evaluate();

				if(!revolutionManager.enforceRevolution())
				{
					VoteManager.checkVote();
				}
			}
		}, 20L, 100*20L);		
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
		voteLengthSeconds = this.config.getLong("VoteLengthSeconds", 240L); // 800 seconds ~ 13.3 minutes, 20 seconds
		voteSecondsBetween = this.config.getInt("VoteSecondsBetween", 300);
		voteBroadcastSeconds = this.config.getInt("VoteBroadcastSeconds", 20);
		requiredYesPercentage = this.config.getInt("RequiredYesPercentage", 66);
		requiredVotes = this.config.getInt("RequiredVotes", 7);
		votePayment = this.config.getInt("VotePayment", 10);
		startVoteCost = this.config.getInt("StartVoteCost", 200);		
	}

	public void saveSettings()
	{
		this.config.set("ServerName", serverName);
		this.config.set("VoteSecondsBetween", voteSecondsBetween);
		this.config.set("VoteLengthSeconds", voteLengthSeconds);
		this.config.set("VoteSecondsBetween", voteSecondsBetween);
		this.config.set("RequiredYesPercentage", requiredYesPercentage);
		this.config.set("RequiredVotes", requiredVotes);
		this.config.set("VotePayment", votePayment);
		this.config.set("StartVoteCost", startVoteCost);
				
		saveConfig();
	}	
}