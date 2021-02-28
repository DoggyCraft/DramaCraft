package dogonfire.DramaCraft;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class DramaCraft extends JavaPlugin
{
	Commands					command;
	private Logger						log;
	private static VoteManager			voteManager;
	private ResourceManager				resourceManager;
	private BodyguardManager			bodyGuardManager;
	private BountyManager				bountyManager;
	private RevolutionManager			revolutionManager;
	private RebelTransmitterManager		rebelTransmitterManager;
	private RebelDetectorManager		rebelDetectorManager;
	private ImperialManager				imperialManager;
	private PermissionsManager			permissionsManager		= null;
	private DeathListener				deathListener;
	private static Server				server;
	private PluginManager				pluginmanager			= null;
	public static Economy				economy					= null;

	public long							voteTimeLength			= 800000000000L;
	public long							voteTimeLengthBetween	= 600000000L; // 60000000000L
	public double						requiredYesPercentage	= 66;
	public int							requiredVotes			= 20;
	public int							votePayment				= 10;
	public int							startVoteCost			= 10;
	
	private String 						pattern 				= "HH:mm:ss dd-MM-yyyy";
	DateFormat 							formatter 				= new SimpleDateFormat(this.pattern);

	private boolean						debug					= true;
	boolean								downloadLanguageFile	= true;
	String								serverName;
	String								languageIdentifier		= "english";
	private LanguageManager				languageManager;
	private FileConfiguration			config					= null;
	static private DramaCraft			instance;
	
	static public DramaCraft instance()
	{
		return instance;
	}
	
	
	public void log(String message)
	{
		log.info("[" + getDescription().getFullName() + "] " + message);
	}

	public void logDebug(Object object)
	{
		if (this.debug)
		{
			log.info("[" + getDescription().getFullName() + "] " + object);
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

	public PermissionsManager getPermissionsManager()
	{
		return permissionsManager;
	}
	
	public RebelTransmitterManager getTransmitterManager()
	{
		return rebelTransmitterManager;
	}

	public RebelDetectorManager getRebelDetectorManager()
	{
		return rebelDetectorManager;
	}

	public ImperialManager getImperialManager()
	{
		return imperialManager;
	}

	public BodyguardManager getBodyguardManager()
	{
		return bodyGuardManager;
	}

	public BountyManager getBountyManager()
	{
		return bountyManager;
	}

	public Economy getEconomyManager()
	{
		return economy;
	}

	public static void broadcastMessage(String message)
	{
		server.broadcastMessage(ChatColor.AQUA + message);
	}

	public boolean isRevolution()
	{		
		return false;
	}
	
	public static int getOnlinePlayers()
	{
		return server.getOnlinePlayers().size();
	}

	public int getNumberOfImperials()
	{	
		ConfigurationSection section =  config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}

		return section.getKeys(false).size();
	}

	public int getNumberOfRebels()
	{	
		ConfigurationSection section = config.getConfigurationSection("Rebels");	
		
		if(section==null)
		{
			return 0;
		}
		
		return section.getKeys(false).size();
	}

	public boolean isNeutral(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), worldName, "default");
	}

	public boolean isRebel(UUID playerId)
	{	
		return config.getString("Rebels." + playerId.toString()) != null;
	}

	public boolean isImperial(UUID playerId)
	{	
		return config.getString("Imperials." + playerId.toString()) != null;
	}

	public boolean isBasicRebel(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), worldName, "rebel");
	}

	public boolean isBasicImperial(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), worldName, "imperial");
	}

	public boolean isNoble(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), worldName, "noble");
	}

	public boolean isInnerCircle(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), worldName, "innercircle");
	}
	
	public boolean isRingLeader(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		if(player.isOp())
		{		
			return false;
		}

		if(isRingLeader1(playerId))
			return true;
		
		if(isRingLeader2(playerId))
			return true;

		return false;
	}

	public boolean isRoyal(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		if(player.isOp())
		{		
			return false;
		}

		if(isKing(playerId))
			return true;
		
		if(isQueen(playerId))
			return true;

		return false;
	}
	
	public boolean isKing(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		
		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, worldName, "king"))
		{
			return true;
		}

		return false;
	}

	public boolean isQueen(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, worldName, "queen"))
		{
			return true;
		}

		return false;
	}
	
	public boolean isRingLeader1(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, worldName, "boss1"))
		{
			return true;
		}

		return false;
	}

	public boolean isRingLeader2(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, worldName, "boss2"))
		{
			return true;
		}

		return false;
	}

	public int getOnlineRebels()
	{
		int numberOfRebels = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				numberOfRebels++;				
			}			
		}
		
		return numberOfRebels;
	}

	public int getOnlineImperials()
	{
		int numberOfImperials = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isImperial(player.getUniqueId()))
			{
				numberOfImperials++;				
			}			
		}
		
		return numberOfImperials;
	}
	
	public List<Player> getOnlineImperialPlayers()
	{
		List<Player> numberOfImperials = new ArrayList<Player>();
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isImperial(player.getUniqueId()))
			{
				numberOfImperials.add(player);				
			}			
		}
		
		return numberOfImperials;
	}

	public int getOnlineNobles()
	{
		int numberOfNobles = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isNoble(player.getUniqueId()))
			{
				numberOfNobles++;				
			}			
		}
		
		return numberOfNobles;
	}

	public List<Player> getOnlineNoblePlayers()
	{
		List<Player> numberOfImperials = new ArrayList<Player>();
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isNoble(player.getUniqueId()))
			{
				numberOfImperials.add(player);				
			}			
		}
		
		return numberOfImperials;
	}

	public List<Player> getOnlineRebelPlayers()
	{
		List<Player> rebels = new ArrayList<Player>();
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				rebels.add(player);				
			}			
		}
		
		return rebels;
	}

	public int getActiveImperials()
	{
		int numberOfActiveImperials = 0;
		
		ConfigurationSection section = config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			UUID playerId = UUID.fromString(playerIdString);

			if(isImperial(playerId))
			{
				if(this.getImperialLastOnlineDays(playerId) < 7)
				{
					numberOfActiveImperials++;
				}
			}			
		}
		
		return numberOfActiveImperials;
	}

	public int getActiveNobles()
	{
		int numberOfActiveNobles = 0;
		
		ConfigurationSection section = config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			UUID playerId = UUID.fromString(playerIdString);
			
			if(isNoble(playerId))
			{
				if(this.getImperialLastOnlineDays(playerId) < 7)
				{
					numberOfActiveNobles++;
				}
			}			
		}
		
		return numberOfActiveNobles;
	}

	public int getActiveInnerCircle()
	{
		int numberOfActiveInnerCircle = 0;
		
		ConfigurationSection section = config.getConfigurationSection("Rebels");	
		
		if(section==null)
		{
			return 0;
		}

		for(String playerIdString : section.getKeys(false))
		{
			UUID playerId = UUID.fromString(playerIdString);
			
			if(isInnerCircle(playerId))
			{
				if(this.getRebelLastOnlineDays(playerId) < 7)
				{
					numberOfActiveInnerCircle++;
				}
			}			
		}
		
		return numberOfActiveInnerCircle;
	}

	public int getOnlineInnerCircle()
	{
		int numberOfInnerCircle = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isInnerCircle(player.getUniqueId()))
			{
				numberOfInnerCircle++;				
			}			
		}
		
		return numberOfInnerCircle;
	}

	public static VoteManager getVoteManager()
	{
		return voteManager;
	}

	public RevolutionManager getRevolutionManager()
	{
		return revolutionManager;
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

	public UUID getKing()	
	{
		String king = config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		return UUID.fromString(king);
	}

	public String getKingName()
	{
		String king = config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		OfflinePlayer player = this.getServer().getOfflinePlayer(UUID.fromString(king));

		return player.getName();
	}

	public UUID getQueen()
	{
		String queen = config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		return UUID.fromString(queen);
	}

	public String getQueenName()
	{
		String queen = config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		OfflinePlayer player = this.getServer().getOfflinePlayer(UUID.fromString(queen));

		return player.getName();
	}

	public boolean setKingHead(Location location)
	{
		UUID ownerId = UUID.fromString(config.getString("King.Id"));

		setKingHead(ownerId, location);
		
		return true;
	}

	public void setKingHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		config.set("King.Head.World", location.getWorld().getName());
		config.set("King.Head.X", location.getBlockX());
		config.set("King.Head.Y", location.getBlockY());
		config.set("King.Head.Z", location.getBlockZ());
		
		saveConfig();				
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
		
		saveConfig();				
	}
		
	public void setHead(UUID ownerId, Location location)
	{		
		location.getBlock().setType(Material.PLAYER_HEAD);
	
		//location.getBlock().setData((byte) 3);
	
		OfflinePlayer player = this.getServer().getOfflinePlayer(ownerId);
		
		Skull s = (Skull)location.getBlock().getState();
		s.setOwningPlayer(player);
		//s.setSkullType(SkullType.PLAYER);
		//s.setRotation(arg0);
		s.update();		
	}

	public void setN00b(UUID playerId)
	{
		OfflinePlayer player = this.getServer().getOfflinePlayer(playerId);
		permissionsManager.setDramaCraftGroup(player, "default");
	}
	
	public void	clearImperial(Player player)
	{
		setRank(player, "neutral");
		clearImperialLastLogin(player.getUniqueId());

		saveSettings();								
	}

	public void	clearRebel(Player player)
	{
		setRank(player, "neutral");
		clearRebelLastLogin(player.getUniqueId());
		
		saveSettings();								
	}

	public void	clearNoble(Player player)
	{
		setRank(player, "neutral");
		clearRebelLastLogin(player.getUniqueId());
		
		saveSettings();								
	}

	public void	clearInnerCircle(Player player)
	{
		permissionsManager.setPrefix(player, "");		
		permissionsManager.setDramaCraftGroup(player, "neutral");		

		saveSettings();								
	}

	public void	setNobleClientRank(Player player, UUID clientId, String rankname)
	{
		String oldClientId = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.OldRank");
			
			//permissionsManager.setDramaCraftGroup(player, newGroupName);Group(oldClientName, oldClientRank);		
		}

		String playerName = getServer().getOfflinePlayer(clientId).getName();

		config.set("Nobles." + player.getUniqueId().toString() + ".Client.Id", clientId.toString());
		config.set("Nobles." + player.getUniqueId().toString() + ".Client.OldRank", permissionsManager.getDramaCraftGroup(player));
		config.set("Nobles." + player.getUniqueId().toString() + ".Client.Rank", rankname);
		
		//permissionsManager.setDramaCraftGroup(playerName, "DoggyCraft");
		
		saveSettings();								
	}

	public void	setInnerCircleClientRank(Player player, UUID clientId, String rankname)
	{
		String oldClientId = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank");
			
			//permissionsManager.setRankGroup(oldClientName, oldClientRank);		
		}

		String playerName = getServer().getOfflinePlayer(clientId).getName();

		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Id", clientId.toString());
		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank", permissionsManager.getDramaCraftGroup(player));
		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Rank", rankname);
		
		//permissionsManager.setDramaCraftGroup(playerName, "DoggyCraft");
		
		saveSettings();								
	}



	public void clearRingLeader1()
	{
		config.set("RingLeader1", null);
						
		saveSettings();
	}
	
	public void clearRingLeader2()
	{
		config.set("RingLeader2", null);
						
		saveSettings();
	}

	
	
	public void setImperialLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		config.set("Imperials." + playerId.toString() + ".LastLoginTime", formatter.format(thisDate));
		
		saveSettings();				
	}

	public void clearImperialLastLogin(UUID playerId)
	{
		config.set("Imperials." + playerId.toString(), null);
		
		saveSettings();				
	}

	public void setRebelLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		config.set("Rebels." + playerId.toString() + ".LastLoginTime", formatter.format(thisDate));
		
		saveSettings();				
	}

	public void clearRebelLastLogin(UUID playerId)
	{
		config.set("Rebels." + playerId.toString(), null);
		
		saveSettings();				
	}

	public long getRebelLastOnlineDays(UUID playerId)
	{
		String electionTime = config.getString("Rebels." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date electionDate = null;
		Date thisDate = new Date();
		try
		{
			electionDate = formatter.parse(electionTime);
		}
		catch (Exception ex)
		{
			electionDate = new Date();
		}
		
		long diff = thisDate.getTime() - electionDate.getTime();
		long diffMinutes = diff / 60000L;
		return diffMinutes / (24*60);		
	}	

	public long getImperialLastOnlineDays(UUID playerId)
	{
		String electionTime = config.getString("Imperials." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date electionDate = null;
		Date thisDate = new Date();
		try
		{
			electionDate = formatter.parse(electionTime);
		}
		catch (Exception ex)
		{
			electionDate = new Date();
		}
		
		long diff = thisDate.getTime() - electionDate.getTime();
		long diffMinutes = diff / 60000L;
		return diffMinutes / (24*60);		
	}	
	
	public long getNobleElectionDays(UUID playerId)
	{
		String electionTime = config.getString("Imperials." + playerId.toString() + ".Noble.JoinDate");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date electionDate = null;
		Date thisDate = new Date();
		try
		{
			electionDate = formatter.parse(electionTime);
		}
		catch (Exception ex)
		{
			electionDate = new Date();
		}
		
		long diff = thisDate.getTime() - electionDate.getTime();
		long diffMinutes = diff / 60000L;
		return diffMinutes / (24*60);		
	}
	
	public long getKingElectionDays()
	{
		String electionTime = config.getString("King.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date electionDate = null;
		Date thisDate = new Date();
		try
		{
			electionDate = formatter.parse(electionTime);
		}
		catch (Exception ex)
		{
			electionDate = new Date();
		}
		
		long diff = thisDate.getTime() - electionDate.getTime();
		long diffMinutes = diff / 60000L;
		return diffMinutes / (24*60);		
	}
	
	public long getQueenElectionDays()
	{
		String electionTime = config.getString("Queen.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date electionDate = null;
		Date thisDate = new Date();
		try
		{
			electionDate = formatter.parse(electionTime);
		}
		catch (Exception ex)
		{
			electionDate = new Date();
		}
		
		long diff = thisDate.getTime() - electionDate.getTime();
		long diffMinutes = diff / 60000L;
		return diffMinutes / (24*60);		
	}
	
	public int getNumberOfNobles()
	{
		return getNobles().size();
	}

	public int getNumberOfInnerCircle()
	{		
		return getInnerCircle().size();
	}

	public Set<UUID> getImperials()
	{
		ConfigurationSection section = config.getConfigurationSection("Imperials");
		
		if(section==null)
		{
			return null;
		}

		Set<UUID> imperials = new HashSet<UUID>();
				
		for(String playerId : section.getKeys(false))
		{
			imperials.add(UUID.fromString(playerId));
		}
								
		return imperials;
	}

	public Set<String> getNobles()
	{
		Set<String> nobles = new HashSet<String>();
		ConfigurationSection section = config.getConfigurationSection("Imperials");
		
		if(section==null)
		{
			return nobles;
		}

		Set<String> imperials = section.getKeys(false);
						
		for(String playerId : imperials)
		{
			OfflinePlayer player = this.getServer().getOfflinePlayer(UUID.fromString(playerId));

			if(isNoble(player.getUniqueId()))
			{
				nobles.add(playerId);
			}
		}
		
		return nobles;
	}
	
	public Set<String> getInnerCircle()
	{
		Set<String> innercircle = new HashSet<String>();
		ConfigurationSection section = config.getConfigurationSection("Rebels");

		if(section==null)
		{
			return innercircle;
		}

		Set<String> rebels = section.getKeys(false);
		
		for(String playerId : rebels)
		{
			OfflinePlayer player = this.getServer().getOfflinePlayer(UUID.fromString(playerId));
			
			if(isInnerCircle(player.getUniqueId()))
			{
				innercircle.add(playerId);
			}
		}
		
		return innercircle;
	}

	public void setNeutralPrefix(UUID playerId)
	{
		String title = "Neutral";
		
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		
		if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "Wizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "police"))
		{
			title = ChatColor.BLUE +  "Police ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
		{
			title = ChatColor.DARK_GREEN +  "Farmer ";			
		}			
			
		permissionsManager.setPrefix(player, title);		
	}

	public void setImperialPrefix(UUID playerId)
	{
		String title = "";
		
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(this.isKing(playerId))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.GOLD +  "WizardKing ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.GOLD +  "KnightKing ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GOLD +  "FarmerKing ";			
			}			
			else
			{
				title = ChatColor.GOLD +  "King ";			
			}			
		}

		else if(this.isQueen(playerId))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.GOLD +  "WizardQueen ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.GOLD +  "KnightQueen ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GOLD +  "FarmerQueen ";			
			}			
			else
			{
				title = ChatColor.GOLD +  "Queen ";			
			}			
		}

		else if(this.isNoble(player.getUniqueId()))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "NobleWizard ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.BLUE +  "NobleGuard ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GREEN +  "NobleFarmer ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Noble ";			
			}			
		}

		else if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "ImperialWizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "police"))
		{
			title = ChatColor.BLUE +  "ImperialGuard ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
		{
			title = ChatColor.GREEN +  "ImperialFarmer ";			
		}			
		else
		{
			title = ChatColor.GRAY +  "Imperial ";						
		}
			
		permissionsManager.setPrefix(player, title);		
	}
	
	public void setRebelPrefix(UUID playerId)
	{
		String title = "";

		OfflinePlayer player = getServer().getOfflinePlayer(playerId);
		/*
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(this.isRingLeader1(playerId))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "WizardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.BLUE +  "GuardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GREEN +  "WeedBoss ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Boss ";			
			}			
		}

		else if(this.isRingLeader2(playerId))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "WizardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.BLUE +  "GuardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GREEN +  "WeedBoss ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "RingLeader ";			
			}			
		}

		else if(this.isInnerCircle(playerId))
		{
			if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "ChaosWizard ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.BLUE +  "RogueGuard ";			
			}
			else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GREEN +  "WeedFarmer ";			
			}			
			else
			{
				title = "";			
			}			
		}

		else if(this.permissionsManager.isInGroup(player, worldName, "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "ChaosWizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "police"))
		{
			title = ChatColor.BLUE +  "RogueGuard ";			
		}
		else if(this.permissionsManager.isInGroup(player, worldName, "farmer"))
		{
			title = ChatColor.GREEN +  "WeedFarmer ";			
		}			
		else*/
		{
			title = ChatColor.RED +  "Rebel ";						
		}
		
			
		permissionsManager.setPrefix(player, title);		
	}

	public void updatePrefix(UUID playerId)
	{
		if(isRebel(playerId))
		{
			setRebelPrefix(playerId);
			return;
		}
		
		if(isImperial(playerId))
		{
			setImperialPrefix(playerId);
			return;
		}
		
		if(isNeutral(playerId))
		{
			setNeutralPrefix(playerId);
			return;
		}		
	}
	
	public void setKing(UUID playerId)
	{
		String currentKingId = config.getString("King.Id");
		String currentKingPreviousRank = config.getString("Players." + currentKingId + ".PreviousRank");
		String currentKingName = null;
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();

		if(currentKingId!=null)
		{
			currentKingName = this.getServer().getOfflinePlayer(UUID.fromString(currentKingId)).getName();
		}

		String kingHeadWorld = config.getString("King.Head.World");
		if(kingHeadWorld!=null)
		{
			try
			{
				String kingHeadX = config.getString("King.Head.X");
				String kingHeadY = config.getString("King.Head.Y");
				String kingHeadZ = config.getString("King.Head.Z");
				
				Location location = new Location(this.getServer().getWorld(kingHeadWorld), Integer.parseInt(kingHeadX), Integer.parseInt(kingHeadY), Integer.parseInt(kingHeadZ));
				
				setHead(playerId, location);
			}
			catch(Exception ex)
			{
				
			}
		}

		if (currentKingName != null)
		{
			languageManager.setPlayerName(currentKingName);
			String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_OVERTURNED, ChatColor.AQUA);
			broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = getServer().getOfflinePlayer(UUID.fromString(currentKingId));
			
			try
			{
				log("Setting current king '" + currentKingPlayer.getName() + "' to his previous rank '" + currentKingPreviousRank + "'");
				permissionsManager.setDramaCraftGroup(currentKingPlayer, currentKingPreviousRank);
			}
			catch (Exception ex)
			{
				log("Error while setting current king to his previous rank '" + currentKingPreviousRank + "'");
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + currentKingName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		log("Setting new king '" + playerName + "' previous rank to '" + currentKingPreviousRank + "'");
		config.set("Players." + playerId + ".PreviousRank", permissionsManager.getGroup(playerName));

		log("Setting new king '" + playerName + "' rank to 'king'");
		this.permissionsManager.setDramaCraftGroup(getServer().getOfflinePlayer(playerId), "king");
		
		//updatePrefix(playerId);
	
		Date thisDate = new Date();
		
		config.set("King.Id", playerId.toString());
		//config.set("King.JoinDate", formatter.format(thisDate));
		config.set("King.ElectionTime", formatter.format(thisDate));
				
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());
		
		saveSettings();
	}
	
	public void setQueen(UUID playerId)
	{
		String currentQueenId = config.getString("Queen.Id");
		String currentQueenPreviousRank = config.getString("Players." + currentQueenId + ".PreviousRank");
		String currentQueenName = null;
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();

		if(currentQueenId!=null)
		{
			currentQueenName = this.getServer().getOfflinePlayer(UUID.fromString(currentQueenId)).getName();
		}

		String queenHeadWorld = config.getString("Queen.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String headX = config.getString("Queen.Head.X");
				String headY = config.getString("Queen.Head.Y");
				String headZ = config.getString("Queen.Head.Z");
				
				Location location = new Location(this.getServer().getWorld(queenHeadWorld), Integer.parseInt(headX), Integer.parseInt(headY), Integer.parseInt(headZ));
				
				setHead(playerId, location);
			}
			catch(Exception ex)
			{
				
			}
		}

		if (currentQueenName != null)
		{
			languageManager.setPlayerName(currentQueenName);
			String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_OVERTURNED, ChatColor.AQUA);
			broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = getServer().getOfflinePlayer(UUID.fromString(currentQueenId));
			
			try
			{
				log("Setting current queen '" + currentKingPlayer.getName() + "' to his previous rank '" + currentQueenPreviousRank + "'");
				permissionsManager.setDramaCraftGroup(currentKingPlayer, currentQueenPreviousRank);
			}
			catch (Exception ex)
			{
				log("Error while setting current king to his previous rank '" + currentQueenPreviousRank + "'");
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + currentQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		log("Setting new queen '" + playerName + "' previous rank to '" + currentQueenPreviousRank + "'");
		config.set("Players." + playerId + ".PreviousRank", permissionsManager.getGroup(playerName));

		log("Setting new queen '" + playerName + "' rank to 'queen'");
		this.permissionsManager.setDramaCraftGroup(getServer().getOfflinePlayer(playerId), "queen");
		
		//updatePrefix(playerId);
	
		Date thisDate = new Date();
		
		config.set("Queen.Id", playerId.toString());
		//config.set("King.JoinDate", formatter.format(thisDate));
		config.set("Queen.ElectionTime", formatter.format(thisDate));
				
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());
		
		saveSettings();
	}

	public void setRank(OfflinePlayer player, String groupname)
	{
		Date thisDate = new Date();

		permissionsManager.setDramaCraftGroup(player, groupname);		

		config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", permissionsManager.getGroup(player.getName()));
		config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", groupname);
		config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", formatter.format(thisDate));

		String joinDate = config.getString("Players." + player.getUniqueId().toString() + ".JoinDate");
		if(joinDate == null)
		{
			config.set("Players." + player.getUniqueId().toString() + ".JoinDate", formatter.format(thisDate));			
		}
		
		updatePrefix(player.getUniqueId());
	}
	
	public void setImperial(UUID playerId)
	{							
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);

		setRank(player, "imperial");		
		setImperialLastLogin(playerId);
		clearRebelLastLogin(playerId);
		
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember imperials " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		this.log(player.getName() + " was set to be an imperial");

		saveSettings();
	}

	public void setRebel(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
		
		setRank(player, "rebel");
		setRebelLastLogin(playerId);
		clearImperialLastLogin(playerId);
		
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember rebels " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		this.log(player.getName() + " was set to be a rebel");

		saveSettings();
	}
		
	public void setNoble(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		setRank(player, "noble");		

		Date thisDate = new Date();
		
		config.set("Imperials." + player.getUniqueId().toString() + ".Noble.JoinDate", formatter.format(thisDate));

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember imperials " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember castle " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		
		this.log(player.getName() + " was set to be a noble");

		saveSettings();
	}

	public void setInnerCircle(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		setRank(player, "innercircle");		
	
		Date thisDate = new Date();

		config.set("Rebels." + playerId.toString() + ".InnerCircle.JoinDate", formatter.format(thisDate));
		
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember rebels " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember mansion " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		
		this.log(player.getName() + " was set to be in innercircle");

		saveSettings();
	}
	
	public void setNeutral(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		Date thisDate = new Date();

		permissionsManager.setDramaCraftGroup(player, "default");		

		config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", permissionsManager.getGroup(player.getName()));
		config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", "neutral");
		config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", formatter.format(thisDate));
		
		config.set("Rebels." + playerId.toString(), null);
		config.set("Imperials." + playerId.toString(), null);

		updatePrefix(player.getUniqueId());
						
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember mansion " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		
		this.log(player.getName() + " was set to be neutral");

		saveSettings();
	}
	
	public void clearKing()
	{				
		String kingId = config.getString("Queen.Id");

		if(kingId == null)
		{
			return;
		}
		
		UUID playerId = UUID.fromString(kingId);
		
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		config.set("King", null);
		
		saveSettings();
	}
	
	public void clearQueen()
	{
		String queenId = config.getString("Queen.Id");
		
		if(queenId == null)
		{
			return;
		}

		UUID playerId = UUID.fromString(queenId);

		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		config.set("Queen", null);
						
		saveSettings();
	}

	public Date getJoinDate(UUID playerId)
	{		
		String joinDate = config.getString("Players." + playerId.toString() + ".JoinDate");

		if(joinDate == null)
		{
			return null;
		}
		
		try
		{
			return formatter.parse(joinDate);
		}
		catch (ParseException e)
		{
			return null;
		}
	}
		
	public void endRevolution()
	{
		// Clear PvP in castle
		// Clear exit deny in castle
		
		// Add tpa and home commands
	}
	
	public void setRingLeader1(UUID playerId)
	{
		String oldQueenId = config.getString("RingLeader1.Id");
		String oldQueenDayjob = config.getString("RingLeader1.DayJob");
		String oldQueenName = null;
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();
		String worldName = this.getServer().getWorlds().get(0).getName();
		
		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}
		
		OfflinePlayer oldPlayer = getServer().getOfflinePlayer(UUID.fromString(oldQueenId));
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		String queenHeadWorld = config.getString("Boss1.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String queenHeadX = config.getString("Boss1.Head.X");
				String queenHeadY = config.getString("Boss1.Head.Y");
				String queenHeadZ = config.getString("Boss1.Head.Z");
				
				Location location = new Location(this.getServer().getWorld(queenHeadWorld), Integer.parseInt(queenHeadX), Integer.parseInt(queenHeadY), Integer.parseInt(queenHeadZ));
				
				setHead(playerId, location);
			}
			catch(Exception ex)
			{
				
			}
		}
	
		if (oldQueenName != null)
		{
			languageManager.setPlayerName(oldQueenName);
			String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_BOSS1_OVERTURNED, ChatColor.AQUA);
			broadcastMessage(broadcast);

			try
			{
				log("Setting old Boss1 '" + oldQueenName + "' to his dayjob '" + oldQueenDayjob + "'");
				permissionsManager.setRankGroup(oldPlayer, oldQueenDayjob);
			}
			catch (Exception ex)
			{
				System.out.println("[KingVote] Error while setting old boss1 to her dayjob '" + oldQueenDayjob + "'");
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner dannevirke " + oldQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		updatePrefix(playerId);

		Date thisDate = new Date();
		
		log("Setting new boss1 '" + playerName + "' dayjob to '" + permissionsManager.getDramaCraftGroup(player) + "'");
		config.set("Boss1.Id", playerId.toString());
		config.set("Rebels." + playerId.toString() + ".Boss1.JoinDate", formatter.format(thisDate));
		permissionsManager.setRankGroup(player, "boss1");

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner dannevirke " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		config.set("Boss1.ElectionTime", formatter.format(thisDate));
		
		saveSettings();
	}
	
	public void setRingLeader2(UUID playerId)
	{
	}
	
	public void sendToRebels(String message)
	{
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				player.sendMessage(message);				
			}			
		}		
	}
	
	public void downgradeRank(UUID playerId)
	{
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);		
		String previousRank = config.getString("Players." + playerId.toString() + ".PreviousRank");

		permissionsManager.setDramaCraftGroup(player, previousRank);
		
		updatePrefix(playerId);

		config.set("Players." + playerId.toString() + ".PreviousRank", null);				
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
		
		this.command = new Commands(this);

		this.pluginmanager = getServer().getPluginManager();

		permissionsManager = new PermissionsManager();
		permissionsManager.load();

		resourceManager = new ResourceManager();
		resourceManager.load();

		voteManager = new VoteManager();

		revolutionManager = new RevolutionManager(this);
		server.getPluginManager().registerEvents(revolutionManager, this);

		deathListener = new DeathListener(this);
		server.getPluginManager().registerEvents(deathListener, this);

		languageManager = new LanguageManager(this);
		languageManager.load();

		rebelTransmitterManager = new RebelTransmitterManager(this);
		getServer().getPluginManager().registerEvents(rebelTransmitterManager, this);
		rebelTransmitterManager.load();

		rebelDetectorManager = new RebelDetectorManager(this);
		getServer().getPluginManager().registerEvents(rebelDetectorManager, this);
		rebelDetectorManager.load();

		imperialManager = new ImperialManager();

		bountyManager = new BountyManager(this);
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
					DramaCraft.voteManager.checkVote(20);
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