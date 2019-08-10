package dogonfire.DramaCraft;

import java.text.DateFormat;
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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class DramaCraft extends JavaPlugin
{
	VotePlayerListener					votePlayerListener;
	private Logger						log;
	private static VoteManager			voteManager;
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
	
	private String 						pattern = "HH:mm:ss dd-MM-yyyy";
	DateFormat 							formatter = new SimpleDateFormat(this.pattern);

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

	public void logDebug(String message)
	{
		if (this.debug)
		{
			log.info("[" + getDescription().getFullName() + "] " + message);
		}
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

	public boolean isCitizen(UUID playerId)
	{	
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "citizen");
		//return !isRebel(playerId) && !isImperial(playerId) && !isNoble(playerId) && !isInnerCircle(playerId);
	}

	public boolean isRebel(UUID playerId)
	{	
		return config.getString("Rebels." + playerId.toString()) != null;
	}

	public boolean isImperial(UUID playerId)
	{	
		//return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "imperial");
		return config.getString("Imperials." + playerId.toString()) != null;
	}

	public boolean isBasicRebel(UUID playerId)
	{	
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "rebel");
	}

	public boolean isBasicImperial(UUID playerId)
	{	
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "imperial");
	}

	public boolean isNoble(UUID playerId)
	{	
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "noble");
		//return config.getString("Imperials." + playerId.toString() + ".Noble") != null;
	}

	public boolean isInnerCircle(UUID playerId)
	{	
		return this.permissionsManager.isInGroup(this.getServer().getOfflinePlayer(playerId), "DoggyCraft", "innercircle");
		//return config.getString("Rebels." + playerId.toString() + ".InnerCircle") != null;
	}
	
	public boolean isBoss(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(isBoss1(player))
			return true;
		
		if(isBoss2(player))
			return true;

		return false;
	}

	public boolean isRoyal(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(isKing(player))
			return true;
		
		if(isQueen(player))
			return true;

		return false;
	}
	
	public boolean isKing(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, player.getWorld().getName(), "king"))
		{
			return true;
		}

		return false;
	}

	public boolean isQueen(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, player.getWorld().getName(), "queen"))
		{
			return true;
		}

		return false;
	}
	
	public boolean isBoss1(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, player.getWorld().getName(), "boss1"))
		{
			return true;
		}

		return false;
	}

	public boolean isBoss2(Player player)
	{
		if(player.isOp())
		{		
			return false;
		}

		if(getPermissionsManager().isInGroup(player, player.getWorld().getName(), "boss2"))
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
		int numberOfActiveNobles = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isImperial(player.getUniqueId()))
			{
				if(this.getImperialLastOnlineDays(player.getUniqueId()) < 7)
				{
					numberOfActiveNobles++;
				}
			}			
		}
		
		return numberOfActiveNobles;
	}

	public int getActiveNobles()
	{
		int numberOfActiveNobles = 0;
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isNoble(player.getUniqueId()))
			{
				if(this.getImperialLastOnlineDays(player.getUniqueId()) < 7)
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
		
		for(Player player : server.getOnlinePlayers())
		{
			if(isInnerCircle(player.getUniqueId()))
			{
				if(this.getRebelLastOnlineDays(player.getUniqueId()) < 7)
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

		if (player == null)
		{
			return null;
		}

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

	public String getQueenId()
	{
		String queen = config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		OfflinePlayer player = this.getServer().getOfflinePlayer(UUID.fromString(queen));

		if (player == null)
		{
			return null;
		}

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
		//s.setOwner(ownerName);
		s.update();		
	}

	public void setN00b(UUID playerId)
	{
		OfflinePlayer player = this.getServer().getOfflinePlayer(playerId);
		permissionsManager.setDramaCraftGroup(player, "default");
	}
	
	public void	clearImperial(Player player)
	{
		permissionsManager.setPrefix(player, "");		
		permissionsManager.setDramaCraftGroup(player, "citizen");		

		clearImperialLastLogin(player);

		saveSettings();								
	}

	public void	clearRebel(Player player)
	{
		permissionsManager.setPrefix(player, "");		
		permissionsManager.setDramaCraftGroup(player, "citizen");		
		
		clearRebelLastLogin(player);
		
		saveSettings();								
	}

	public void	clearNoble(Player player)
	{
		permissionsManager.setPrefix(player, "");		
		permissionsManager.setDramaCraftGroup(player, "citizen");		

		/*
		String oldClientId = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.OldRank");
			
			OfflinePlayer oldPlayer = DramaCraft.instance().getServer().getOfflinePlayer(UUID.fromString(oldClientId));
			permissionsManager.setRankGroup(oldPlayer, oldClientRank);		
		}

		//config.set("Nobles." + player.getUniqueId().toString(), null);
		config.set("Imperials." + player.getUniqueId().toString() + ".Noble", null);
	*/
		saveSettings();								
	}

	public void	clearInnerCircle(Player player)
	{
		permissionsManager.setPrefix(player, "");		
		permissionsManager.setDramaCraftGroup(player, "citizen");		

		/*
		String oldClientId = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank");
			
			permissionsManager.setRankGroup(oldClientName, oldClientRank);		
		}
		
		//config.set("InnerCircle." + player.getUniqueId().toString(), null);
		config.set("Rebels." + player.getUniqueId().toString() + ".InnerCircle", null);
	*/
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

	public void clearKing()
	{
		String oldKingId = config.getString("King.Id");
		String oldKingDayjob = config.getString("King.DayJob");
		String oldKingName = null;

		if(oldKingId!=null)
		{
			oldKingName = this.getServer().getOfflinePlayer(UUID.fromString(oldKingId)).getName();
		}

		if (oldKingName != null)
		{
			//languageManager.setPlayerName(oldKingName);
			//String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_KILLED, ChatColor.AQUA);
			//broadcastMessage(broadcast);

			/*			try
			{
				this.log("Setting old king '" + oldKingName + "' to his dayjob '" + oldKingDayjob + "'");
				permissionsManager.setRankGroup(this.getServer().getOfflinePlayer(UUID.fromString(oldKingId)), oldKingDayjob);
			}
			catch (Exception ex)
			{
				this.log("Error while setting old king to his dayjob '" + oldKingDayjob + "'");
			}
			*/

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldKingName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
						
		//config.set("Imperials." + oldKingId + ".King", null);
		config.set("King", null);
		
		saveSettings();
	}
	
	public void clearQueen()
	{
		String oldQueenId = config.getString("Queen.Id");
		String oldQueenDayjob = config.getString("Queen.DayJob");
		String oldQueenName = null;

		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}

		if (oldQueenName != null)
		{
			//languageManager.setPlayerName(oldQueenName);
			//String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_KILLED, ChatColor.AQUA);
			//broadcastMessage(broadcast);

			/*
			try
			{
				this.log("[KingVote] Setting old king '" + oldQueenName + "' to his dayjob '" + oldQueenDayjob + "'");
				permissionsManager.setRankGroup(this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)), oldQueenDayjob);
			}
			catch (Exception ex)
			{
				this.log("Error while setting old queen to her dayjob '" + oldQueenDayjob + "'");
			}*/

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		
		config.set("Imperials." + oldQueenId + ".Queen", null);
		config.set("Queen", null);
						
		saveSettings();
	}

	public void clearBoss1()
	{
		String oldQueenId = config.getString("Boss1.Id");
		String oldQueenDayjob = config.getString("Boss1.DayJob");
		String oldQueenName = null;

		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}

		if (oldQueenName != null)
		{
			//languageManager.setPlayerName(oldQueenName);
			//String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_KILLED, ChatColor.AQUA);
			//broadcastMessage(broadcast);

			try
			{
				System.out.println("[KingVote] Setting old boss1 '" + oldQueenName + "' to his dayjob '" + oldQueenDayjob + "'");
				permissionsManager.setRankGroup(this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)), oldQueenDayjob);
			}
			catch (Exception ex)
			{
				System.out.println("[KingVote] Error while setting old boss1 to his dayjob '" + oldQueenDayjob + "'");
			}

			//getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		config.set("Boss1", null);
						
		saveSettings();
	}
	
	public void clearBoss2()
	{
		String oldQueenId = config.getString("Boss2.Id");
		String oldQueenDayjob = config.getString("Boss2.DayJob");
		String oldQueenName = null;

		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}

		if (oldQueenName != null)
		{
			//languageManager.setPlayerName(oldQueenName);
			//String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_KILLED, ChatColor.AQUA);
			//broadcastMessage(broadcast);

			try
			{
				log("Setting old boss2 '" + oldQueenName + "' to his dayjob '" + oldQueenDayjob + "'");
				permissionsManager.setRankGroup(this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)), oldQueenDayjob);
			}
			catch (Exception ex)
			{
				log("Error while setting old boss2 to his dayjob '" + oldQueenDayjob + "'");
			}

			//getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
			
		config.set("Boss2", null);
						
		saveSettings();
	}

	public void setKing(UUID playerId)
	{
		String oldKingId = config.getString("King.Id");
		String oldKingDayjob = config.getString("Players." + oldKingId + ".PreviousRank");
		String oldKingName = null;
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();

		if(oldKingId!=null)
		{
			oldKingName = this.getServer().getOfflinePlayer(UUID.fromString(oldKingId)).getName();
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

		if (oldKingName != null)
		{
			languageManager.setPlayerName(oldKingName);
			String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_OVERTURNED, ChatColor.AQUA);
			broadcastMessage(broadcast);

			OfflinePlayer oldKingPlayer = getServer().getOfflinePlayer(UUID.fromString(oldKingId));
			
			try
			{
				log("Setting old king '" + oldKingPlayer.getName() + "' to his dayjob '" + oldKingDayjob + "'");
				permissionsManager.setDramaCraftGroup(oldKingPlayer, oldKingDayjob);
			}
			catch (Exception ex)
			{
				log("Error while setting old king to his dayjob '" + oldKingDayjob + "'");
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldKingName + " -w " + this.getServer().getWorlds().get(0).getName());
		}
		
		this.permissionsManager.setDramaCraftGroup(getServer().getOfflinePlayer(playerId), "king");
		
		setPrefix(playerId);
	
		Date thisDate = new Date();

		log("Setting new king '" + playerName + "' dayjob to '" + oldKingDayjob + "'");
		config.set("King.Id", playerId.toString());
		config.set("Players." + oldKingId + ".PreviousRank", permissionsManager.getGroup(playerName));
		config.set("Imperials." + playerId.toString() + ".King.JoinDate", formatter.format(thisDate));
		permissionsManager.setDramaCraftGroup(this.getServer().getOfflinePlayer(playerId), "king");
		
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());

		config.set("King.ElectionTime", formatter.format(thisDate));
		
		saveSettings();
	}
	
	public void setImperialLastLogin(Player player)
	{
		Date thisDate = new Date();
		config.set("Imperials." + player.getUniqueId().toString() + ".LastLoginTime", formatter.format(thisDate));
		
		saveSettings();				
	}

	public void clearImperialLastLogin(Player player)
	{
		config.set("Imperials." + player.getUniqueId().toString(), null);
		
		saveSettings();				
	}

	public void setRebelLastLogin(Player player)
	{
		Date thisDate = new Date();
		config.set("Rebels." + player.getUniqueId().toString() + ".LastLoginTime", formatter.format(thisDate));
		
		saveSettings();				
	}

	public void clearRebelLastLogin(Player player)
	{
		config.set("Rebels." + player.getUniqueId().toString(), null);
		
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
		List<String> nobles = config.getStringList("Nobles");
		
		if(nobles == null)
		{
			return 0;			
		}
		
		return nobles.size();
	}

	public int getNumberOfInnerCircle()
	{
		List<String> inner = config.getStringList("InnerCircle");
		
		if(inner == null)
		{
			return 0;			
		}
		
		return inner.size();
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
		ConfigurationSection section = config.getConfigurationSection("Imperials");

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

	public void setPrefix(UUID playerId)
	{
		this.setImperialPrefix(this.getServer().getPlayer(playerId));
		this.setRebelPrefix(this.getServer().getPlayer(playerId));
	}

	public void setNeutralPrefix(Player player)
	{
		String title = "";
		
		if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "Wizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
		{
			title = ChatColor.BLUE +  "Police ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
		{
			title = ChatColor.DARK_GREEN +  "Farmer ";			
		}			
			
		permissionsManager.setPrefix(player, title);		
	}

	public void setImperialPrefix(Player player)
	{
		String title = "";
		
		if(this.isKing(player))
		{
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.GOLD +  "WizardKing ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.GOLD +  "KnightKing ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
			{
				title = ChatColor.GOLD +  "FarmerKing ";			
			}			
			else
			{
				title = ChatColor.GOLD +  "King ";			
			}			
		}

		else if(this.isQueen(player))
		{
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.GOLD +  "WizardQueen ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.GOLD +  "KnightQueen ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
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
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "NobleWizard ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.BLUE +  "NobleGuard ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
			{
				title = ChatColor.GREEN +  "NobleFarmer ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Noble ";			
			}			
		}

		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "ImperialWizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
		{
			title = ChatColor.BLUE +  "ImperialGuard ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
		{
			title = ChatColor.GREEN +  "ImperialFarmer ";			
		}			
		else
		{
			title = ChatColor.GRAY +  "Imperial ";						
		}
			
		permissionsManager.setPrefix(player, title);		
	}
	
	public void setRebelPrefix(Player player)
	{
		String title = "";
		
		if(this.isBoss1(player))
		{
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "WizardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.BLUE +  "GuardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
			{
				title = ChatColor.GREEN +  "WeedBoss ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Boss ";			
			}			
		}

		else if(this.isBoss2(player))
		{
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "WizardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.BLUE +  "GuardBoss ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
			{
				title = ChatColor.GREEN +  "WeedBoss ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Boss ";			
			}			
		}

		else if(this.isInnerCircle(player.getUniqueId()))
		{
			if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "ChaosWizard ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
			{
				title = ChatColor.BLUE +  "RogueGuard ";			
			}
			else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
			{
				title = ChatColor.GREEN +  "WeedFarmer ";			
			}			
			else
			{
				title = "";			
			}			
		}

		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "ChaosWizard ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "police"))
		{
			title = ChatColor.BLUE +  "RogueGuard ";			
		}
		else if(this.permissionsManager.isInGroup(player, "DoggyCraft", "farmer"))
		{
			title = ChatColor.GREEN +  "WeedFarmer ";			
		}			
		else
		{
			title = ChatColor.GRAY +  "Rebel ";						
		}
			
		permissionsManager.setPrefix(player, title);		
	}

	public void setImperial(Player player)
	{							
		setRank(player, "imperial");		
		setImperialLastLogin(player);
		clearRebelLastLogin(player);
		
		this.log(player.getName() + " was set to be an imperial");
	}

	public void setRebel(Player player)
	{
		setRank(player, "rebel");
		setRebelLastLogin(player);
		clearImperialLastLogin(player);
		
		this.log(player.getName() + " was set to be a rebel");
	}

	public void setQueen(UUID playerId)
	{
		String oldQueenId = config.getString("Queen.Id");
		String oldQueenDayjob = config.getString("Players." + oldQueenId + ".PreviousRank");
		String oldQueenName = null;
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();
		String worldName = this.getServer().getWorlds().get(0).getName();
		
		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}

		OfflinePlayer oldQueenPlayer = getServer().getOfflinePlayer(UUID.fromString(oldQueenId));
		OfflinePlayer queenPlayer = getServer().getOfflinePlayer(playerId);

		String queenHeadWorld = config.getString("Queen.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String queenHeadX = config.getString("Queen.Head.X");
				String queenHeadY = config.getString("Queen.Head.Y");
				String queenHeadZ = config.getString("Queen.Head.Z");
				
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
			String broadcast = languageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_OVERTURNED, ChatColor.AQUA);
			broadcastMessage(broadcast);

			try
			{
				log("Setting old queen '" + oldQueenName + "' to her dayjob '" + oldQueenDayjob + "'");
				permissionsManager.setDramaCraftGroup(oldQueenPlayer, oldQueenDayjob);
			}
			catch (Exception ex)
			{
				System.out.println("[KingVote] Error while setting old queen to her dayjob '" + oldQueenDayjob + "'");
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + oldQueenName + " -w " + this.getServer().getWorlds().get(0).getName());
		}

		log("Setting new queen '" + playerName + "' dayjob to '" + permissionsManager.getDramaCraftGroup(queenPlayer) + "'");
		config.set("Queen.Id", playerId.toString());

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());

		Date thisDate = new Date();
		config.set("Queen.ElectionTime", formatter.format(thisDate));

		setRank(queenPlayer, "queen");
		
		saveSettings();
	}

	public void setRank(OfflinePlayer player, String groupname)
	{
		Date thisDate = new Date();

		config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", permissionsManager.getGroup(player.getName()));
		permissionsManager.setDramaCraftGroup(player, "queen");		
		config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", groupname);
		config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", formatter.format(thisDate));
		setPrefix(player.getUniqueId());
	}
	
	/*
	public void removeNoble(UUID playerId)
	{
		OfflinePlayer player = this.getServer().getOfflinePlayer(playerId);
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();

		permissionsManager.removePermission(player.getPlayer(), "kingvote.noble");
		//config.set("Nobles." + playerId.toString(), null);
		config.set("Imperials." + playerId.toString() + ".Noble", null);
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());
			
		saveSettings();
	}*/
	
	public void setNoble(UUID playerId)
	{
		/*
		List<String> nobles = config.getStringList("Nobles");
		
		if(nobles.contains(playerId) || nobles==null)
		{
			return;			
		}
		
		OfflinePlayer player = this.getServer().getOfflinePlayer(playerId);
		String playerName = player.getName();
		
		/*
		if(oldQueenId!=null)
		{
			oldQueenName = this.getServer().getOfflinePlayer(UUID.fromString(oldQueenId)).getName();
		}*/

		/*
		String queenHeadWorld = config.getString("Queen.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String queenHeadX = config.getString("Queen.Head.X");
				String queenHeadY = config.getString("Queen.Head.Y");
				String queenHeadZ = config.getString("Queen.Head.Z");
				
				Location location = new Location(this.getServer().getWorld(queenHeadWorld), Integer.parseInt(queenHeadX), Integer.parseInt(queenHeadY), Integer.parseInt(queenHeadZ));
				
				setHead(playerName, location);
			}
			catch(Exception ex)
			{
				
			}
		}
*/
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		permissionsManager.setDramaCraftGroup(player, "noble");

		setPrefix(playerId);

		Date thisDate = new Date();
		
		//System.out.println("[KingVote] Setting new noble '" + playerName + "' dayjob to '" + permissionsManager.getGroup(playerName, worldName) + "'");
		//config.set("Nobles." + playerId.toString() + ".Dayjob", permissionsManager.getGroup(playerName, worldName));
		//permissionsManager.addPermission(player.getPlayer(), "kingvote.noble");
		config.set("Imperials." + player.getUniqueId().toString() + ".Noble.JoinDate", formatter.format(thisDate));

		//config.set("Nobles." + playerId.toString() + ".ElectionTime", formatter.format(thisDate));
		//config.set("Rebels." + playerId.toString() + ".Noble", null);

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember castle " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		
		saveSettings();
	}

/*	
	public void removeInnerCircle(UUID playerId)
	{
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();

		config.set("Rebels." + playerId.toString() + ".InnerCircle", null);
		//config.set("InnerCircle." + playerId.toString(), null);
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember dannevirke " + playerName + " -w " + this.getServer().getWorlds().get(0).getName());
		
		setRebelPrefix(playerId);

		saveSettings();
	}
*/	
	public void setInnerCircle(UUID playerId)
	{
		/*
		List<String> nobles = config.getStringList("InnerCircle");
		//String oldQueenDayjob = config.getString("Queen.DayJob");
		
		if(nobles.contains(playerId) || nobles==null)
		{
			return;			
		}
		
		String playerName = this.getServer().getOfflinePlayer(playerId).getName();
	*/				
		OfflinePlayer player = getServer().getOfflinePlayer(playerId);

		permissionsManager.setDramaCraftGroup(player, "innercircle");

		setPrefix(playerId);
	
		Date thisDate = new Date();

		//log("Setting new innercircle '" + player.getName() + "' dayjob to '" + permissionsManager.getGroup(player) + "'");
		//permissionsManager.setRankGroup(playerName, "kingvote.innercircle");
		config.set("Rebels." + playerId.toString() + ".InnerCircle.JoinDate", formatter.format(thisDate));
		//config.set("InnerCircle." + playerId.toString() + ".ElectionTime", formatter.format(thisDate));
		
		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember dannevirke " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());
		
		saveSettings();
	}
	
	public void endRevolution()
	{
		// Clear PvP in castle
		// Clear exit deny in castle
		
		// Add tpa and home commands
	}
	
	public void setBoss1(UUID playerId)
	{
		String oldQueenId = config.getString("Boss1.Id");
		String oldQueenDayjob = config.getString("Boss1.DayJob");
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
		
		setPrefix(playerId);

		Date thisDate = new Date();
		
		log("Setting new boss1 '" + playerName + "' dayjob to '" + permissionsManager.getDramaCraftGroup(player) + "'");
		config.set("Boss1.Id", playerId.toString());
		config.set("Rebels." + playerId.toString() + ".Boss1.JoinDate", formatter.format(thisDate));
		permissionsManager.setRankGroup(player, "boss1");

		getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner dannevirke " + player.getName() + " -w " + this.getServer().getWorlds().get(0).getName());

		config.set("Boss1.ElectionTime", formatter.format(thisDate));
		
		saveSettings();
	}
	
	public void setBoss2(UUID playerId)
	{
		setBoss1(playerId);
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
		
		setPrefix(playerId);

		config.set("Players." + playerId.toString() + ".PreviousRank", null);
		
		Date thisDate = new Date();
		
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

		RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

		if (economyProvider != null)
		{
			economy = (Economy) economyProvider.getProvider();
			log("Using Vault.");
		}
		else
		{
			log("Vault not found.  No money will used.");
		}

		this.votePlayerListener = new VotePlayerListener(this);

		this.pluginmanager = getServer().getPluginManager();

		permissionsManager = new PermissionsManager();
		permissionsManager.load();

		voteManager = new VoteManager(this);

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

		imperialManager = new ImperialManager(this);

		bountyManager = new BountyManager(this);
		bountyManager.load();
		getServer().getPluginManager().registerEvents(bountyManager, this);
		
		bodyGuardManager = new BodyguardManager(this);
		getServer().getPluginManager().registerEvents(bodyGuardManager, this);

		server.getScheduler().runTaskTimer(this, new Runnable()
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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return this.votePlayerListener.onPlayerCommand(sender, command, label, args);
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