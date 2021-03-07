package dogonfire.DramaCraft;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;
import dogonfire.DramaCraft.tasks.InfoTask;



public class RankManager implements Listener
{		
	private Random 					random 		= new Random();
	private FileConfiguration		config		= null;
	private File					configFile	= null;
	static  private RankManager		instance	= null;
		
	private String 					pattern 	= "HH:mm:ss dd-MM-yyyy";
	private DateFormat 				formatter 	= new SimpleDateFormat(pattern);

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
	
	public static String convertToRomanNumeral(int input) 
	{
	    if (input < 1 || input > 3999)
	        return "INVALID";
	    
		String s = "";
		while (input >= 1000)
		{
			s += "M";
			input -= 1000;
		}
		while (input >= 900)
		{
			s += "CM";
			input -= 900;
		}
		while (input >= 500)
		{
			s += "D";
			input -= 500;
		}
		while (input >= 400)
		{
			s += "CD";
			input -= 400;
		}
		while (input >= 100)
		{
			s += "C";
			input -= 100;
		}
		while (input >= 90)
		{
			s += "XC";
			input -= 90;
		}
		while (input >= 50)
		{
			s += "L";
			input -= 50;
		}
		while (input >= 40)
		{
			s += "XL";
			input -= 40;
		}
		while (input >= 10)
		{
			s += "X";
			input -= 10;
		}
		while (input >= 9)
		{
			s += "IX";
			input -= 9;
		}
		while (input >= 5)
		{
			s += "V";
			input -= 5;
		}
		while (input >= 4)
		{
			s += "IV";
			input -= 4;
		}
		while (input >= 1)
		{
			s += "I";
			input -= 1;
		}
		return s;
	}
	
	static public int getNumberOfImperials()
	{	
		ConfigurationSection section = instance.config.getConfigurationSection("Imperials");	
		
		if(section==null)
		{
			return 0;
		}

		return section.getKeys(false).size();
	}

	static public int getNumberOfRebels()
	{	
		ConfigurationSection section = instance.config.getConfigurationSection("Rebels");	
		
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
		return instance.config.getString("Rebels." + playerId.toString()) != null;
	}

	static public boolean isImperial(UUID playerId)
	{	
		return instance.config.getString("Imperials." + playerId.toString()) != null;
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
		
		ConfigurationSection section = instance.config.getConfigurationSection("Rebels");	
		
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
		String king = instance.config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		return UUID.fromString(king);
	}

	static public String getKingName()
	{
		String king = instance.config.getString("King.Id");

		if (king == null)
		{
			return null;
		}

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(king));

		return player.getName() + " den " + convertToRomanNumeral(getKingCardinality(player.getUniqueId()) + 1) + ".";
	}

	static public UUID getQueen()
	{
		String queen = instance.config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		return UUID.fromString(queen);
	}

	static public String getQueenName()
	{
		String queen = instance.config.getString("Queen.Id");

		if (queen == null)
		{
			return null;
		}

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(queen));

		return player.getName() + " den " + convertToRomanNumeral(getKingCardinality(player.getUniqueId()) + 1) + ".";
	}

	static public boolean setKingHead(Location location)
	{
		UUID ownerId = UUID.fromString(instance.config.getString("King.Id"));

		setKingHead(ownerId, location);
		
		return true;
	}

	static public void setKingHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		instance.config.set("King.Head.World", location.getWorld().getName());
		instance.config.set("King.Head.X", location.getBlockX());
		instance.config.set("King.Head.Y", location.getBlockY());
		instance.config.set("King.Head.Z", location.getBlockZ());
		
		instance.save();				
	}

	static public boolean setQueenHead(Location location)
	{
		UUID ownerId = UUID.fromString(instance.config.getString("Queen.Id"));
		
		setQueenHead(ownerId, location);
		
		return true;
	}

	static public void setQueenHead(UUID ownerId, Location location)
	{
		setHead(ownerId, location);
		
		instance.config.set("Queen.Head.World", location.getWorld().getName());
		instance.config.set("Queen.Head.X", location.getBlockX());
		instance.config.set("Queen.Head.Y", location.getBlockY());
		instance.config.set("Queen.Head.Z", location.getBlockZ());
		
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
		String oldClientId = instance.config.getString("Nobles." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = instance.config.getString("Nobles." + player.getUniqueId().toString() + ".Client.OldRank");
			
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
		String oldClientId = instance.config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.Id");
		if(oldClientId!=null)
		{
			String oldClientName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(oldClientId)).getName();
			String oldClientRank = instance.config.getString("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank");
			
			//permissionsManager.setRankGroup(oldClientName, oldClientRank);		
		}

		String playerName = Bukkit.getServer().getOfflinePlayer(clientId).getName();

		instance.config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Id", clientId.toString());
		instance.config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.OldRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("InnerCircle." + player.getUniqueId().toString() + ".Client.Rank", rankname);
		
		//permissionsManager.setDramaCraftGroup(playerName, "DoggyCraft");
		
		instance.save();								
	}

	static public void clearRingLeader1()
	{
		instance.config.set("RingLeader1", null);
						
		instance.save();
	}
	
	public void clearRingLeader2()
	{
		instance.config.set("RingLeader2", null);
						
		instance.save();
	}
	
	static public void setImperialLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		instance.config.set("Imperials." + playerId.toString() + ".LastLoginTime", instance.formatter.format(thisDate));
		
		instance.save();				
	}

	static public void clearImperialLastLogin(UUID playerId)
	{
		instance.config.set("Imperials." + playerId.toString(), null);
		
		instance.save();				
	}

	static public void setRebelLastLogin(UUID playerId)
	{
		Date thisDate = new Date();
		instance.config.set("Rebels." + playerId.toString() + ".LastLoginTime", instance.formatter.format(thisDate));
		
		instance.save();				
	}

	static public void clearRebelLastLogin(UUID playerId)
	{
		instance.config.set("Rebels." + playerId.toString(), null);
		
		instance.save();				
	}

	static public long getRebelLastOnlineDays(UUID playerId)
	{
		String electionTime = instance.config.getString("Rebels." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(instance.pattern);
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
		String electionTime = instance.config.getString("Imperials." + playerId.toString() + ".LastLoginTime");

		DateFormat formatter = new SimpleDateFormat(instance.pattern);
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
		String electionTime = instance.config.getString("Imperials." + playerId.toString() + ".Noble.JoinDate");

		DateFormat formatter = new SimpleDateFormat(instance.pattern);
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
		String electionTime = instance.config.getString("King.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(instance.pattern);
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
		String electionTime = instance.config.getString("Queen.ElectionTime");

		DateFormat formatter = new SimpleDateFormat(instance.pattern);
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
		ConfigurationSection section = instance.config.getConfigurationSection("Imperials");
		
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
		ConfigurationSection section = instance.config.getConfigurationSection("Imperials");
		
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
		ConfigurationSection section = instance.config.getConfigurationSection("Rebels");

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

	static public String getLastKing()
	{
		ConfigurationSection section = instance.config.getConfigurationSection("Kings");
		
		if(section==null)
		{
			return null;			
		}
		
		return (String) section.getKeys(false).toArray()[section.getKeys(false).size() - 1];
	}
	
	static public int getKingCardinality(UUID playerId)
	{
		ConfigurationSection section = instance.config.getConfigurationSection("Kings");
		
		if(section==null)
		{
			return 0;			
		}
		
		int cardinality = 0;
		
		for(String key : section.getKeys(false))
		{
			if(instance.config.getString("Kings." + key + ".EndDate") != null)
			{
				String kingPlayerId = instance.config.getString("Kings." + key + ".PlayerId");
				if(kingPlayerId.equals(playerId.toString()))
				{
					cardinality++;
				}			
			}
		}
		
		return cardinality;
	}	

	static public String getLastQueen()
	{
		ConfigurationSection section = instance.config.getConfigurationSection("Queens");
		
		if(section==null)
		{
			return null;			
		}
				
		return (String) section.getKeys(false).toArray()[section.getKeys(false).size() - 1];
	}

	static public int getQueenCardinality(UUID playerId)
	{
		ConfigurationSection section = instance.config.getConfigurationSection("Queens");
		
		if(section==null)
		{
			return 0;			
		}
		
		int cardinality = 0;
		
		for(String key : section.getKeys(false))
		{
			if(instance.config.getString("Queens." + key + ".EndDate") != null)
			{
				String queenPlayerId = instance.config.getString("Queens." + key + ".PlayerId");
				if(queenPlayerId.equals(playerId.toString()))
				{
					cardinality++;
				}
			}
		}
		
		return cardinality;
	}	

	static public void setKing(UUID playerId)
	{
		String currentKingId = instance.config.getString("King.Id");
		String currentKingPreviousRank = instance.config.getString("Players." + currentKingId + ".PreviousRank");
		String currentKingName = null;
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String playerName = player.getName();
		Date thisDate = new Date();

		if(currentKingId!=null)
		{
			currentKingName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentKingId)).getName();
		}

		String kingHeadWorld = instance.config.getString("King.Head.World");
		if(kingHeadWorld!=null)
		{
			try
			{
				String kingHeadX = instance.config.getString("King.Head.X");
				String kingHeadY = instance.config.getString("King.Head.Y");
				String kingHeadZ = instance.config.getString("King.Head.Z");
				
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
			
			String lastKingId = getLastKing();
			if(lastKingId != null)
			{
				instance.config.set("Kings." + lastKingId + ".EndDate", instance.formatter.format(thisDate));				
			}
		}
		
		DramaCraft.log("Setting new king '" + playerName + "' previous rank to '" + PermissionsManager.getDramaCraftGroup(player) + "'");
		instance.config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("Players." + playerId + ".CurrentRank", "king");

		DramaCraft.log("Setting new king '" + playerName + "' rank to 'king'");
		PermissionsManager.setDramaCraftGroup(player, "king");
		
		clearRebelLastLogin(playerId);
		setImperialLastLogin(playerId);
		
		//updatePrefix(playerId);
			
		instance.config.set("King.Id", playerId.toString());

		long id = System.currentTimeMillis();
		instance.config.set("Kings." + id + ".PlayerId", playerId.toString());
		instance.config.set("Kings." + id + ".StartDate", instance.formatter.format(thisDate));
				
		instance.save();
	}
	
	static public void setQueen(UUID playerId)
	{
		String currentQueenId = instance.config.getString("Queen.Id");
		String currentQueenPreviousRank = instance.config.getString("Players." + currentQueenId + ".PreviousRank");
		String currentQueenName = null;
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String playerName = player.getName();
		Date thisDate = new Date();

		if(currentQueenId!=null)
		{
			currentQueenName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentQueenId)).getName();
		}

		String queenHeadWorld = instance.config.getString("Queen.Head.World");
		if(queenHeadWorld!=null)
		{
			try
			{
				String headX = instance.config.getString("Queen.Head.X");
				String headY = instance.config.getString("Queen.Head.Y");
				String headZ = instance.config.getString("Queen.Head.Z");
				
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

			String lastQueenId = getLastQueen();
			if(lastQueenId != null)
			{
				instance.config.set("Queens." + lastQueenId + ".EndDate", instance.formatter.format(thisDate));				
			}
		}
		
		DramaCraft.log("Setting new queen '" + playerName + "' previous rank to '" + PermissionsManager.getDramaCraftGroup(player) + "'");
		instance.config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("Players." + playerId + ".CurrentRank", "queen");

		DramaCraft.log("Setting new queen '" + playerName + "' rank to 'queen'");
		PermissionsManager.setDramaCraftGroup(player, "queen");
		
		clearRebelLastLogin(playerId);
		setImperialLastLogin(playerId);

		//updatePrefix(playerId);
			
		instance.config.set("Queen.Id", playerId.toString());
		//config.set("King.JoinDate", formatter.format(thisDate));
		instance.config.set("Queen.ElectionTime", instance.formatter.format(thisDate));
				
		instance.save();
	}

	static public void setRank(OfflinePlayer player, String groupname)
	{
		Date thisDate = new Date();

		instance.config.set("Players." + player.getUniqueId().toString() + ".PreviousRank", PermissionsManager.getDramaCraftGroup(player));

		PermissionsManager.setDramaCraftGroup(player, groupname);		

		instance.config.set("Players." + player.getUniqueId().toString() + ".CurrentRank", groupname);
		instance.config.set("Players." + player.getUniqueId().toString() + ".ChangeDate", instance.formatter.format(thisDate));

		String joinDate = instance.config.getString("Players." + player.getUniqueId().toString() + ".JoinDate");
		if(joinDate == null)
		{
			instance.config.set("Players." + player.getUniqueId().toString() + ".JoinDate", instance.formatter.format(thisDate));			
		}
		
		//updatePrefix(player.getUniqueId());
	}
	
	static public void setImperial(UUID playerId)
	{							
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);

		clearRank(player.getUniqueId());
		setRank(player, "imperial");		
		setImperialLastLogin(playerId);
		
		DramaCraft.log(player.getName() + " was set to be an imperial");

		instance.save();
	}

	static public void setRebel(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
		
		clearRank(player.getUniqueId());
		setRank(player, "rebel");
		setRebelLastLogin(playerId);
		
		DramaCraft.log(player.getName() + " was set to be a rebel");

		instance.save();
	}
		
	static public void setNoble(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		clearRank(player.getUniqueId());
		setRank(player, "noble");		

		Date thisDate = new Date();
		
		instance.config.set("Imperials." + player.getUniqueId().toString() + ".Noble.JoinDate", instance.formatter.format(thisDate));
		
		DramaCraft.log(player.getName() + " was set to be a noble");

		instance.save();
	}

	static public void setInnerCircle(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		clearRank(player.getUniqueId());
		setRank(player, "innercircle");		
	
		Date thisDate = new Date();

		instance.config.set("Rebels." + playerId.toString() + ".InnerCircle.JoinDate", instance.formatter.format(thisDate));
				
		DramaCraft.log(player.getName() + " was set to be in innercircle");

		instance.save();
	}
	
	static public void setNeutral(UUID playerId)
	{
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		clearRank(player.getUniqueId());

		PermissionsManager.setDramaCraftGroup(player, "default");		
		
		instance.config.set("Players." + player.getUniqueId().toString(), null);
		
		instance.config.set("Rebels." + playerId.toString(), null);
		instance.config.set("Imperials." + playerId.toString(), null);

		updatePrefix(player.getUniqueId());
								
		DramaCraft.log(player.getName() + " was set to be neutral");

		instance.save();
	}
	
	static public void clearRank(UUID playerId)
	{				
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		Date thisDate = new Date();

		if(instance.config.getString("King.Id") != null)
		{				
			if(UUID.fromString(instance.config.getString("King.Id")).equals(playerId))
			{
				String lastKingId = getLastKing();
				if(lastKingId != null)
				{					
					if(instance.config.getString("Kings." + lastKingId + ".PlayerId").equals(playerId.toString()))
					{
						instance.config.set("Kings." + lastKingId + ".EndDate", instance.formatter.format(thisDate));			
					}
				}
				
				instance.config.set("King", null);
			}			
		}
		
		if(instance.config.getString("Queen.Id") != null)
		{				
			if(UUID.fromString(instance.config.getString("Queen.Id")).equals(playerId))
			{
				String lastQueenId = getLastQueen();
				if(lastQueenId != null)
				{
					if(instance.config.getString("Queens." + lastQueenId + ".PlayerId").equals(playerId.toString()))
					{
						instance.config.set("Queens." + lastQueenId + ".EndDate", instance.formatter.format(thisDate));			
					}
				}

				instance.config.set("Queen", null);
			}
		}

		if(instance.config.getString("Imperials." + playerId.toString()) != null)
		{				
			instance.config.set("Imperials." + playerId.toString(), null);
		}
		
		if(instance.config.getString("Rebels." + playerId.toString()) != null)
		{				
			instance.config.set("Rebels." + playerId.toString(), null);
		}

		
		
		instance.save();
	}

	static public void clearKing()
	{				
		String kingId = instance.config.getString("King.Id");

		if(kingId == null)
		{
			return;
		}
		
		UUID playerId = UUID.fromString(kingId);
		
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		instance.config.set("King", null);
		
		instance.save();
	}
	
	static public void clearQueen()
	{
		String queenId = instance.config.getString("Queen.Id");
		
		if(queenId == null)
		{
			return;
		}

		UUID playerId = UUID.fromString(queenId);

		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);

		instance.config.set("Queen", null);
						
		instance.save();
	}

	static public Date getJoinDate(UUID playerId)
	{		
		String joinDate = instance.config.getString("Players." + playerId.toString() + ".JoinDate");

		if(joinDate == null)
		{
			return null;
		}
		
		try
		{
			return instance.formatter.parse(joinDate);
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
		String currentRingleaderId = instance.config.getString("Ringleader1.Id");
		String currentRingleaderPreviousRank = instance.config.getString("Players." + currentRingleaderId + ".PreviousRank");
		String currentKingName = null;
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String playerName = player.getName();
		Date thisDate = new Date();

		if(currentRingleaderId!=null)
		{
			currentKingName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentRingleaderId)).getName();
		}

		String headWorld = instance.config.getString("Ringleader1.Head.World");
		if(headWorld!=null)
		{
			try
			{
				String headX = instance.config.getString("Ringleader1.Head.X");
				String headY = instance.config.getString("Ringleader1.Head.Y");
				String headZ = instance.config.getString("Ringleader1.Head.Z");
				
				Location location = new Location(Bukkit.getServer().getWorld(headWorld), Integer.parseInt(headX), Integer.parseInt(headY), Integer.parseInt(headZ));
				
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
			//DramaCraft.broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentRingleaderId));
			
			try
			{
				DramaCraft.log("Setting current ringleader1 '" + currentKingPlayer.getName() + "' to his previous rank '" + currentRingleaderPreviousRank + "'");
				PermissionsManager.setDramaCraftGroup(currentKingPlayer, currentRingleaderPreviousRank);
			}
			catch (Exception ex)
			{
				DramaCraft.log("Error while setting current ringleader1 to his previous rank '" + currentRingleaderPreviousRank + "'");
			}
			
			String lastKingId = getLastKing();
			if(lastKingId != null)
			{
				instance.config.set("Ringleader1." + lastKingId + ".EndDate", instance.formatter.format(thisDate));				
			}
		}
		
		DramaCraft.log("Setting new ringleader1 '" + playerName + "' previous rank to '" + PermissionsManager.getDramaCraftGroup(player) + "'");
		instance.config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("Players." + playerId + ".CurrentRank", "ringleader");

		DramaCraft.log("Setting new ringleader1 '" + playerName + "' rank to 'ringleader'");
		PermissionsManager.setDramaCraftGroup(player, "ringleader");
		
		clearRebelLastLogin(playerId);
		setImperialLastLogin(playerId);
		
		//updatePrefix(playerId);
			
		instance.config.set("Ringleader1.Id", playerId.toString());

		long id = System.currentTimeMillis();
		instance.config.set("Ringleaders." + id + ".PlayerId", playerId.toString());
		instance.config.set("Ringleaders." + id + ".StartDate", instance.formatter.format(thisDate));
				
		instance.save();
	}
	
	static public void setRingLeader2(UUID playerId)
	{
		String currentRingleaderId = instance.config.getString("Ringleader2.Id");
		String currentRingleaderPreviousRank = instance.config.getString("Players." + currentRingleaderId + ".PreviousRank");
		String currentKingName = null;
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerId);
		String playerName = player.getName();
		Date thisDate = new Date();

		if(currentRingleaderId!=null)
		{
			currentKingName = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentRingleaderId)).getName();
		}

		String headWorld = instance.config.getString("Ringleader2.Head.World");
		if(headWorld!=null)
		{
			try
			{
				String headX = instance.config.getString("Ringleader2.Head.X");
				String headY = instance.config.getString("Ringleader2.Head.Y");
				String headZ = instance.config.getString("Ringleader2.Head.Z");
				
				Location location = new Location(Bukkit.getServer().getWorld(headWorld), Integer.parseInt(headX), Integer.parseInt(headY), Integer.parseInt(headZ));
				
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
			//DramaCraft.broadcastMessage(broadcast);

			OfflinePlayer currentKingPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentRingleaderId));
			
			try
			{
				DramaCraft.log("Setting current ringleader2 '" + currentKingPlayer.getName() + "' to his previous rank '" + currentRingleaderPreviousRank + "'");
				PermissionsManager.setDramaCraftGroup(currentKingPlayer, currentRingleaderPreviousRank);
			}
			catch (Exception ex)
			{
				DramaCraft.log("Error while setting current ringleader2 to his previous rank '" + currentRingleaderPreviousRank + "'");
			}
			
			String lastKingId = getLastKing();
			if(lastKingId != null)
			{
				instance.config.set("Ringleaders." + lastKingId + ".EndDate", instance.formatter.format(thisDate));				
			}
		}
		
		DramaCraft.log("Setting new ringleader2 '" + playerName + "' previous rank to '" + PermissionsManager.getDramaCraftGroup(player) + "'");
		instance.config.set("Players." + playerId + ".PreviousRank", PermissionsManager.getDramaCraftGroup(player));
		instance.config.set("Players." + playerId + ".CurrentRank", "ringleader");

		DramaCraft.log("Setting new ringleader '" + playerName + "' rank to 'ringleader'");
		PermissionsManager.setDramaCraftGroup(player, "ringleader");
		
		clearRebelLastLogin(playerId);
		setImperialLastLogin(playerId);
		
		//updatePrefix(playerId);
			
		instance.config.set("Ringleader2.Id", playerId.toString());

		long id = System.currentTimeMillis();
		instance.config.set("Ringleaders." + id + ".PlayerId", playerId.toString());
		instance.config.set("Ringleaders." + id + ".StartDate", instance.formatter.format(thisDate));
				
		instance.save();
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
		String previousRank = instance.config.getString("Players." + playerId.toString() + ".PreviousRank");

		PermissionsManager.setDramaCraftGroup(player, previousRank);
		
		updatePrefix(playerId);

		instance.config.set("Players." + playerId.toString() + ".PreviousRank", null);				
	}
	
	static public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, int amount, int delay)
	{
		Player player = Bukkit.getServer().getPlayer(playerId);

		if (player == null)
		{
			DramaCraft.logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance(), new InfoTask(color, playerId, message, amount), delay);
	}
}