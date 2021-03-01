package dogonfire.DramaCraft;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.bukkit.configuration.ConfigurationSection;
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

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;



public class RankManager implements Listener
{		
	private Random 							random = new Random();
	static private FileConfiguration		config		= null;
	static private File						configFile	= null;
	static private RankManager				instance	= null;
		
	static private String 					pattern 	= "HH:mm:ss dd-MM-yyyy";
	static private DateFormat 				formatter 	= new SimpleDateFormat(pattern);

	public RankManager()
	{
		instance = this;
	}
	
	public void load()
	{
		try
		{
			configFile = new File(DramaCraft.instance().getDataFolder(), "players.yml");

			config = YamlConfiguration.loadConfiguration(configFile);

			DramaCraft.log("Loaded players.yml.");				
		}
		catch(Exception ex)
		{
			DramaCraft.log("No players.yml file found.");			
		}
		
	}
	
	public void save()
	{
		if (config == null || configFile == null)
		{
			DramaCraft.log("Config: " + config);
			DramaCraft.log("Configfile: " + configFile);
			return;
		}
		
		try
		{
			this.config.save(this.configFile);
		}
		catch (Exception ex)
		{
			DramaCraft.log("Could not save config to " + this.configFile + ": " + ex.getMessage());
		}
		
		DramaCraft.log("Saved configuration.");
	}
	
	static public int getNumberOfImperials()
	{	
		ConfigurationSection section = config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}

		return section.getKeys(false).size();
	}

	static public int getNumberOfRebels()
	{	
		ConfigurationSection section = config.getConfigurationSection("Rebels");	
		
		if(section==null)
		{
			return 0;
		}
		
		return section.getKeys(false).size();
	}

	static public boolean isNeutral(UUID playerId)
	{	
		return !isImperial(playerId) && !isRebel(playerId);
	}

	static public boolean isRebel(UUID playerId)
	{	
		return config.getString("Rebels." + playerId.toString()) != null;
	}

	static public boolean isImperial(UUID playerId)
	{	
		return config.getString("Imperials." + playerId.toString()) != null;
	}

	static public boolean isBasicRebel(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return PermissionsManager.isInGroup(Bukkit.getServer().getOfflinePlayer(playerId), worldName, "rebel");
	}

	static public boolean isBasicImperial(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return PermissionsManager.isInGroup(Bukkit.getServer().getOfflinePlayer(playerId), worldName, "imperial");
	}

	static public boolean isNoble(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return PermissionsManager.isInGroup(Bukkit.getServer().getOfflinePlayer(playerId), worldName, "noble");
	}

	static public boolean isInnerCircle(UUID playerId)
	{	
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		return PermissionsManager.isInGroup(Bukkit.getServer().getOfflinePlayer(playerId), worldName, "innercircle");
	}
	
	static public boolean isRingLeader(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

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

	static public boolean isRoyal(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

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
	
	static public boolean isKing(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		
		if(player.isOp())
		{		
			return false;
		}

		if(PermissionsManager.isInGroup(player, worldName, "king"))
		{
			return true;
		}

		return false;
	}

	static public boolean isQueen(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(PermissionsManager.isInGroup(player, worldName, "queen"))
		{
			return true;
		}

		return false;
	}
	
	static public boolean isRingLeader1(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(PermissionsManager.isInGroup(player, worldName, "ringleader1"))
		{
			return true;
		}

		return false;
	}

	static public boolean isRingLeader2(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(player.isOp())
		{		
			return false;
		}

		if(PermissionsManager.isInGroup(player, worldName, "ringleader2"))
		{
			return true;
		}

		return false;
	}

	static public int getOnlineRebels()
	{
		int numberOfRebels = 0;
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				numberOfRebels++;				
			}			
		}
		
		return numberOfRebels;
	}

	static public int getOnlineImperials()
	{
		int numberOfImperials = 0;
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isImperial(player.getUniqueId()))
			{
				numberOfImperials++;				
			}			
		}
		
		return numberOfImperials;
	}
	
	static public List<Player> getOnlineImperialPlayers()
	{
		List<Player> numberOfImperials = new ArrayList<Player>();
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isImperial(player.getUniqueId()))
			{
				numberOfImperials.add(player);				
			}			
		}
		
		return numberOfImperials;
	}

	static public int getOnlineNobles()
	{
		int numberOfNobles = 0;
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isNoble(player.getUniqueId()))
			{
				numberOfNobles++;				
			}			
		}
		
		return numberOfNobles;
	}

	static public List<Player> getOnlineNoblePlayers()
	{
		List<Player> numberOfImperials = new ArrayList<Player>();
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isNoble(player.getUniqueId()))
			{
				numberOfImperials.add(player);				
			}			
		}
		
		return numberOfImperials;
	}

	static public List<Player> getOnlineRebelPlayers()
	{
		List<Player> rebels = new ArrayList<Player>();
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				rebels.add(player);				
			}			
		}
		
		return rebels;
	}

	static public int getActiveImperials()
	{
		int numberOfActiveImperials = 0;
		
		ConfigurationSection section = instance.config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			UUID playerId = UUID.fromString(playerIdString);

			if(isImperial(playerId))
			{
				if(getImperialLastOnlineDays(playerId) < 7)
				{
					numberOfActiveImperials++;
				}
			}			
		}
		
		return numberOfActiveImperials;
	}

	static public int getActiveNobles()
	{
		int numberOfActiveNobles = 0;
		
		ConfigurationSection section = instance.config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}
		
		for(String playerIdString : section.getKeys(false))
		{
			UUID playerId = UUID.fromString(playerIdString);
			
			if(isNoble(playerId))
			{
				if(getImperialLastOnlineDays(playerId) < 7)
				{
					numberOfActiveNobles++;
				}
			}			
		}
		
		return numberOfActiveNobles;
	}

	static public int getActiveInnerCircle()
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
				if(RankManager.getRebelLastOnlineDays(playerId) < 7)
				{
					numberOfActiveInnerCircle++;
				}
			}			
		}
		
		return numberOfActiveInnerCircle;
	}

	static public int getOnlineInnerCircle()
	{
		int numberOfInnerCircle = 0;
		
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isInnerCircle(player.getUniqueId()))
			{
				numberOfInnerCircle++;				
			}			
		}
		
		return numberOfInnerCircle;
	}
	
	static public UUID getKing()	
	{
		String king = config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		return UUID.fromString(king);
	}

	static public String getKingName()
	{
		String king = config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(king));

		return player.getName();
	}

	static public UUID getQueen()
	{
		String queen = config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		return UUID.fromString(queen);
	}

	static public String getQueenName()
	{
		String queen = config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(queen));

		return player.getName();
	}

	static public boolean setKingHead(Location location)
	{
		UUID ownerId = UUID.fromString(config.getString("King.Id"));

		setKingHead(ownerId, location);
		
		return true;
	}

	static public void setKingHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		config.set("King.Head.World", location.getWorld().getName());
		config.set("King.Head.X", location.getBlockX());
		config.set("King.Head.Y", location.getBlockY());
		config.set("King.Head.Z", location.getBlockZ());
		
		instance.save();				
	}

	static public boolean setQueenHead(Location location)
	{
		UUID ownerId = UUID.fromString(config.getString("Queen.Id"));
		
		setQueenHead(ownerId, location);
		
		return true;
	}

	static public void setQueenHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		config.set("Queen.Head.World", location.getWorld().getName());
		config.set("Queen.Head.X", location.getBlockX());
		config.set("Queen.Head.Y", location.getBlockY());
		config.set("Queen.Head.Z", location.getBlockZ());
		
		instance.save();				
	}
		
	static public void setHead(UUID ownerId, Location location)
	{		
		location.getBlock().setType(Material.PLAYER_HEAD);
	
		//location.getBlock().setData((byte) 3);
	
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(ownerId);
		
		Skull s = (Skull)location.getBlock().getState();
		s.setOwningPlayer(player);
		//s.setSkullType(SkullType.PLAYER);
		//s.setRotation(arg0);
		s.update();		
	}

	static public void setN00b(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		PermissionsManager.setDramaCraftGroup(player, "default");
	}
	
	static public void clearImperial(Player player)
	{
		setRank(player, "neutral");
		clearImperialLastLogin(player.getUniqueId());

		instance.save();								
	}

	static public void	clearRebel(Player player)
	{
		setRank(player, "neutral");
		clearRebelLastLogin(player.getUniqueId());
		
		instance.save();								
	}

	static public void	clearNoble(Player player)
	{
		setRank(player, "neutral");
		clearRebelLastLogin(player.getUniqueId());
		
		instance.save();								
	}

	static public void	clearInnerCircle(Player player)
	{
		//PermissionsManager.setPrefix(player, "");		
		PermissionsManager.setDramaCraftGroup(player, "neutral");		

		instance.save();								
	}

	static public void	setNobleClientRank(Player player, UUID clientId, String rankname)
	{
		String oldClientId = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("Nobles." + player.getUniqueId().toString() + ".Client.OldRank");
			
			//permissionsManager.setDramaCraftGroup(player, newGroupName);Group(oldClientName, oldClientRank);		
		}

		String playerName = Bukkit.getServer().getOfflinePlayer(clientId).getName();

		instance.config.set("Nobles." + player.getUniqueId().toString() + ".Client.Id", clientId.toString());
		instance.config.set("Nobles." + player.getUniqueId().toString() + ".Client.OldRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("Nobles." + player.getUniqueId().toString() + ".Client.Rank", rankname);
		
		//permissionsManager.setDramaCraftGroup(playerName, "DoggyCraft");
		
		instance.save();								
	}

	static public void setInnerCircleClientRank(Player player, UUID clientId, String rankname)
	{
		String oldClientId = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank");
			
			//permissionsManager.setRankGroup(oldClientName, oldClientRank);		
		}

		String playerName = Bukkit.getServer().getOfflinePlayer(clientId).getName();

		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Id", clientId.toString());
		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank", PermissionsManager.getDramaCraftGroup(player));
		config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Rank", rankname);
		
		//permissionsManager.setDramaCraftGroup(playerName, "DoggyCraft");
		
		instance.save();								
	}

	static public void clearRingLeader1()
	{
		config.set("RingLeader1", null);
						
		instance.save();
	}
	
	public void clearRingLeader2()
	{
		config.set("RingLeader2", null);
						
		instance.save();
	}
	
	static public void setImperialLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		config.set("Imperials." + playerId.toString() + ".LastLoginTime", formatter.format(thisDate));
		
		instance.save();				
	}

	static public void clearImperialLastLogin(UUID playerId)
	{
		config.set("Imperials." + playerId.toString(), null);
		
		instance.save();				
	}

	static public void setRebelLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		config.set("Rebels." + playerId.toString() + ".LastLoginTime", formatter.format(thisDate));
		
		instance.save();				
	}

	static public void clearRebelLastLogin(UUID playerId)
	{
		config.set("Rebels." + playerId.toString(), null);
		
		instance.save();				
	}

	static public long getRebelLastOnlineDays(UUID playerId)
	{
		String electionTime = config.getString("Rebels." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(pattern);
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

	static public long getImperialLastOnlineDays(UUID playerId)
	{
		String electionTime = config.getString("Imperials." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(pattern);
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
	
	static public long getNobleElectionDays(UUID playerId)
	{
		String electionTime = config.getString("Imperials." + playerId.toString() + ".Noble.JoinDate");

		DateFormat formatter = new SimpleDateFormat(pattern);
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
	
	static public long getKingElectionDays()
	{
		String electionTime = config.getString("King.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(pattern);
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
	
	static public long getQueenElectionDays()
	{
		String electionTime = config.getString("Queen.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(pattern);
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
	
	static public int getNumberOfNobles()
	{
		return getNobles().size();
	}

	static public int getNumberOfInnerCircle()
	{		
		return getInnerCircle().size();
	}

	static public Set<UUID> getImperials()
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

	static public Set<String> getNobles()
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
			OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerId));

			if(isNoble(player.getUniqueId()))
			{
				nobles.add(playerId);
			}
		}
		
		return nobles;
	}
	
	static public Set<String> getInnerCircle()
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
			OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerId));
			
			if(isInnerCircle(player.getUniqueId()))
			{
				innercircle.add(playerId);
			}
		}
		
		return innercircle;
	}

	static public void setNeutralPrefix(UUID playerId)
	{
		String title = "Neutral";
		
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		
		if(PermissionsManager.isInGroup(player, worldName, "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "Wizard ";			
		}
		else if(PermissionsManager.isInGroup(player, worldName, "police"))
		{
			title = ChatColor.BLUE +  "Police ";			
		}
		else if(PermissionsManager.isInGroup(player, worldName, "farmer"))
		{
			title = ChatColor.DARK_GREEN +  "Farmer ";			
		}			
			
		//PermissionsManager.setPrefix(player, title);		
	}

	static public void setImperialPrefix(UUID playerId)
	{
		String title = "";
		
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		if(isKing(playerId))
		{
			if(PermissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.GOLD +  "WizardKing ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.GOLD +  "KnightKing ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GOLD +  "FarmerKing ";			
			}			
			else
			{
				title = ChatColor.GOLD +  "King ";			
			}			
		}

		else if(isQueen(playerId))
		{
			if(PermissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.GOLD +  "WizardQueen ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.GOLD +  "KnightQueen ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GOLD +  "FarmerQueen ";			
			}			
			else
			{
				title = ChatColor.GOLD +  "Queen ";			
			}			
		}

		else if(isNoble(player.getUniqueId()))
		{
			if(PermissionsManager.isInGroup(player, worldName, "wizard"))
			{
				title = ChatColor.DARK_BLUE +  "NobleWizard ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "police"))
			{
				title = ChatColor.BLUE +  "NobleGuard ";			
			}
			else if(PermissionsManager.isInGroup(player, worldName, "farmer"))
			{
				title = ChatColor.GREEN +  "NobleFarmer ";			
			}			
			else
			{
				title = ChatColor.GRAY +  "Noble ";			
			}			
		}

		else if(PermissionsManager.isInGroup(player, worldName, "wizard"))
		{
			title = ChatColor.DARK_BLUE +  "ImperialWizard ";			
		}
		else if(PermissionsManager.isInGroup(player, worldName, "police"))
		{
			title = ChatColor.BLUE +  "ImperialGuard ";			
		}
		else if(PermissionsManager.isInGroup(player, worldName, "farmer"))
		{
			title = ChatColor.GREEN +  "ImperialFarmer ";			
		}			
		else
		{
			title = ChatColor.GRAY +  "Imperial ";						
		}
			
		//PermissionsManager.setPrefix(player, title);		
	}
	
	static public void setRebelPrefix(UUID playerId)
	{
		String title = "";

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
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
					
		//PermissionsManager.setPrefix(player, title);		
	}

	static public void updatePrefix(UUID playerId)
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
	
	static public void setKing(UUID playerId)
	{
		String currentKingId = config.getString("King.Id");
		String currentKingPreviousRank = config.getString("Players." + currentKingId + ".PreviousRank");
		String currentKingName = null;
		String playerName = Bukkit.getServer().getOfflinePlayer(playerId).getName();

		if(currentKingId!=null)
		{
			currentKingName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentKingId)).getName();
		}

		String kingHeadWorld = config.getString("King.Head.World");
		if(kingHeadWorld!=null)
		{
			try
			{
				String kingHeadX = config.getString("King.Head.X");
				String kingHeadY = config.getString("King.Head.Y");
				String kingHeadZ = config.getString("King.Head.Z");
				
				Location location = new Location(Bukkit.getServer().getWorld(kingHeadWorld), Integer.parseInt(kingHeadX), Integer.parseInt(kingHeadY), Integer.parseInt(kingHeadZ));
				
				setHead(playerId, location);
			}
			catch(Exception ex)
			{
				
			}
		}

		if (currentKingName != null)
		{
			LanguageManager.setPlayerName(currentKingName);
			String broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_OVERTURNED, ChatColor.AQUA);
			DramaCraft.broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentKingId));
			
			try
			{
				DramaCraft.log("Setting current king '" + currentKingPlayer.getName() + "' to his previous rank '" + currentKingPreviousRank + "'");
				PermissionsManager.setDramaCraftGroup(currentKingPlayer, currentKingPreviousRank);
			}
			catch (Exception ex)
			{
				DramaCraft.log("Error while setting current king to his previous rank '" + currentKingPreviousRank + "'");
			}

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + currentKingName + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		}
		
		DramaCraft.log("Setting new king '" + playerName + "' previous rank to '" + PermissionsManager.getGroup(playerName) + "'");
		config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getGroup(playerName));
		config.set("Players." + playerId + ".CurrentRank", "king");

		DramaCraft.log("Setting new king '" + playerName + "' rank to 'king'");
		PermissionsManager.setDramaCraftGroup(Bukkit.getServer().getOfflinePlayer(playerId), "king");
		
		//updatePrefix(playerId);
	
		Date thisDate = new Date();
		
		config.set("King.Id", playerId.toString());
		//config.set("King.JoinDate", formatter.format(thisDate));
		config.set("King.ElectionTime", formatter.format(thisDate));
				
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		
		instance.save();
	}
	
	static public void setQueen(UUID playerId)
	{
		String currentQueenId = config.getString("Queen.Id");
		String currentQueenPreviousRank = config.getString("Players." + currentQueenId + ".PreviousRank");
		String currentQueenName = null;
		String playerName = Bukkit.getServer().getOfflinePlayer(playerId).getName();

		if(currentQueenId!=null)
		{
			currentQueenName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentQueenId)).getName();
		}

		String queenHeadWorld = config.getString("Queen.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String headX = config.getString("Queen.Head.X");
				String headY = config.getString("Queen.Head.Y");
				String headZ = config.getString("Queen.Head.Z");
				
				Location location = new Location(Bukkit.getServer().getWorld(queenHeadWorld), Integer.parseInt(headX), Integer.parseInt(headY), Integer.parseInt(headZ));
				
				setHead(playerId, location);
			}
			catch(Exception ex)
			{
				
			}
		}

		if (currentQueenName != null)
		{
			LanguageManager.setPlayerName(currentQueenName);
			String broadcast = LanguageManager.getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_OVERTURNED, ChatColor.AQUA);
			DramaCraft.broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentQueenId));
			
			try
			{
				DramaCraft.log("Setting current queen '" + currentKingPlayer.getName() + "' to his previous rank '" + currentQueenPreviousRank + "'");
				PermissionsManager.setDramaCraftGroup(currentKingPlayer, currentQueenPreviousRank);
			}
			catch (Exception ex)
			{
				DramaCraft.log("Error while setting current king to his previous rank '" + currentQueenPreviousRank + "'");
			}

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removeowner castle " + currentQueenName + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		}
		
		DramaCraft.log("Setting new queen '" + playerName + "' previous rank to '" + PermissionsManager.getGroup(playerName) + "'");
		config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getGroup(playerName));
		config.set("Players." + playerId + ".CurrentRank", "queen");

		DramaCraft.log("Setting new queen '" + playerName + "' rank to 'queen'");
		PermissionsManager.setDramaCraftGroup(Bukkit.getServer().getOfflinePlayer(playerId), "queen");
		
		//updatePrefix(playerId);
	
		Date thisDate = new Date();
		
		config.set("Queen.Id", playerId.toString());
		//config.set("King.JoinDate", formatter.format(thisDate));
		config.set("Queen.ElectionTime", formatter.format(thisDate));
				
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addowner castle " + playerName + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		
		instance.save();
	}

	static public void setRank(OfflinePlayer player, String groupname)
	{
		Date thisDate = new Date();

		PermissionsManager.setDramaCraftGroup(player, groupname);		

		config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", PermissionsManager.getGroup(player.getName()));
		config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", groupname);
		config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", formatter.format(thisDate));

		String joinDate = config.getString("Players." + player.getUniqueId().toString() + ".JoinDate");
		if(joinDate == null)
		{
			config.set("Players." + player.getUniqueId().toString() + ".JoinDate", formatter.format(thisDate));			
		}
		
		updatePrefix(player.getUniqueId());
	}
	
	static public void setImperial(UUID playerId)
	{							
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);

		setRank(player, "imperial");		
		setImperialLastLogin(playerId);
		clearRebelLastLogin(playerId);
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember mansion " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember imperials " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());

		DramaCraft.log(player.getName() + " was set to be an imperial");

		instance.save();
	}

	static public void setRebel(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
		
		setRank(player, "rebel");
		setRebelLastLogin(playerId);
		clearImperialLastLogin(playerId);
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember rebels " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());

		DramaCraft.log(player.getName() + " was set to be a rebel");

		instance.save();
	}
		
	static public void setNoble(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		setRank(player, "noble");		

		Date thisDate = new Date();
		
		config.set("Imperials." + player.getUniqueId().toString() + ".Noble.JoinDate", formatter.format(thisDate));

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember mansion " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember imperials " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		
		DramaCraft.log(player.getName() + " was set to be a noble");

		instance.save();
	}

	static public void setInnerCircle(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		setRank(player, "innercircle");		
	
		Date thisDate = new Date();

		config.set("Rebels." + playerId.toString() + ".InnerCircle.JoinDate", formatter.format(thisDate));
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember rebels " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region addmember mansion " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		
		DramaCraft.log(player.getName() + " was set to be in innercircle");

		instance.save();
	}
	
	static public void setNeutral(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		Date thisDate = new Date();

		PermissionsManager.setDramaCraftGroup(player, "default");		

		config.set("Players." + player.getUniqueId().toString(), null);
		//config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", permissionsManager.getGroup(player.getName()));
		//config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", "default");
		//config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", formatter.format(thisDate));
		
		config.set("Rebels." + playerId.toString(), null);
		config.set("Imperials." + playerId.toString(), null);

		updatePrefix(player.getUniqueId());
						
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember imperials " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember rebels " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember mansion " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());
		
		DramaCraft.log(player.getName() + " was set to be neutral");

		instance.save();
	}
	
	static public void clearKing()
	{				
		String kingId = config.getString("Queen.Id");

		if(kingId == null)
		{
			return;
		}
		
		UUID playerId = UUID.fromString(kingId);
		
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());

		config.set("King", null);
		
		instance.save();
	}
	
	static public void clearQueen()
	{
		String queenId = config.getString("Queen.Id");
		
		if(queenId == null)
		{
			return;
		}

		UUID playerId = UUID.fromString(queenId);

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "region removemember castle " + player.getName() + " -w " + Bukkit.getServer().getWorlds().get(0).getName());

		config.set("Queen", null);
						
		instance.save();
	}

	static public Date getJoinDate(UUID playerId)
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
	
	static public void setRingLeader1(UUID playerId)
	{
		
	}
	
	static public void setRingLeader2(UUID playerId)
	{
	}
	
	public void sendToRebels(String message)
	{
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(isRebel(player.getUniqueId()))
			{
				player.sendMessage(message);				
			}			
		}		
	}
	
	static public void downgradeRank(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);		
		String previousRank = config.getString("Players." + playerId.toString() + ".PreviousRank");

		PermissionsManager.setDramaCraftGroup(player, previousRank);
		
		updatePrefix(playerId);

		config.set("Players." + playerId.toString() + ".PreviousRank", null);				
	}
	
	static public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, int amount, int delay)
	{
		Player player = Bukkit.getServer().getPlayer(playerId);

		if (player == null)
		{
			DramaCraft.logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance(), new InfoTask(DramaCraft.instance(), color, playerId, message, amount), delay);
	}
}