package dogonfire.DramaCraft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class LanguageManager
{
	static public enum LANGUAGESTRING
	{
		ERROR_NOTHING_TO_VOTE,
		ERROR_INVALID_VOTE,
		ERROR_PLAYER_NOT_ONLINE,
		ERROR_NOTAGAIN,
		ERROR_TOOFEWPLAYERS,
		ERROR_TOOFEWNOBLES_ONLINE,
		ERROR_TOOFEWINNERCIRCLE_ONLINE,
		ERROR_TOOFEWIMPERIALS_ONLINE,
		ERROR_TOOFEWREBELS_ONLINE,
		ERROR_TOOSOON,
		ERROR_ONLYNOBLESCANBEKING,
		ERROR_ONLYNOBLESCANBEQUEEN,
		ERROR_ONLYREBELSCANREVOLUTION,
		ERROR_ONLYIMPERIALSCANVOTEFORNOBLE,
		ERROR_ONLYNOBLESCANVOTEFORNOBLE,
		ERROR_ONLYREBELSCANVOTEFORINNERCIRCLE,
		ERROR_ONLYINNERCIRCLECANVOTEFORINNERCIRCLE,
		VOTE_BROADCAST_FINISHED,
		VOTE_BROADCAST_NOT_ENOUGH_VOTES,
		VOTE_BROADCAST_KING,
		VOTE_BROADCAST_KING_SUCCESS,
		VOTE_BROADCAST_KING_FAILED,
		VOTE_BROADCAST_KING_OVERTURNED,
		VOTE_BROADCAST_NOBLE,
		VOTE_BROADCAST_NOBLE_SUCCESS,
		VOTE_BROADCAST_NOBLE_FAILED,
		VOTE_BROADCAST_NOBLE_KICK,
		VOTE_BROADCAST_NOBLE_KICK_SUCCESS,
		VOTE_BROADCAST_NOBLE_KICK_FAILED,
		VOTE_BROADCAST_QUEEN,
		VOTE_BROADCAST_QUEEN_SUCCESS,
		VOTE_BROADCAST_QUEEN_FAILED,
		VOTE_BROADCAST_QUEEN_OVERTURNED,
		VOTE_BROADCAST_INNERCIRCLE,
		VOTE_BROADCAST_BOSS1_SUCCESS,
		VOTE_BROADCAST_BOSS1_FAILED,
		VOTE_BROADCAST_BOSS1_OVERTURNED,
		VOTE_BROADCAST_BOSS2_SUCCESS,
		VOTE_BROADCAST_BOSS2_FAILED,
		VOTE_BROADCAST_BOSS2_OVERTURNED,
		VOTE_BROADCAST_PROGRESS,
		VOTE_BROADCAST_HELP,
		VOTE_BROADCAST_DAY,
		VOTE_BROADCAST_DAY_SUCCESS,
		VOTE_BROADCAST_DAY_FAILED,
		VOTE_BROADCAST_NIGHT,
		VOTE_BROADCAST_NIGHT_SUCCESS,
		VOTE_BROADCAST_NIGHT_FAILED,
		VOTE_BROADCAST_SUN,
		VOTE_BROADCAST_SUN_SUCCESS,
		VOTE_BROADCAST_SUN_FAILED,
		VOTE_BROADCAST_RAIN,
		VOTE_BROADCAST_RAIN_SUCCESS,
		VOTE_BROADCAST_RAIN_FAILED,
		VOTE_BROADCAST_GENERAL,
		VOTE_BROADCAST_GENERAL_SUCCESS,
		VOTE_BROADCAST_GENERAL_FAILED,
		VOTE_BROADCAST_REVOLUTION,
		VOTE_BROADCAST_REVOLUTION_SUCCESS,
		VOTE_BROADCAST_REVOLUTION_FAILED,
		VOTE_BROADCAST_KING_KILLED,
		VOTE_BROADCAST_QUEEN_KILLED,
		VOTING_COMMANDS_HEAD,
		VOTING_COMMANDS_VOTE_DESC_IMPERIALS,
		VOTING_COMMANDS_VOTE_DESC_REBELS,
		VOTING_COMMANDS_VOTE_DESC_REVOLUTION,
		VOTING_COMMANDS_VOTE_DESC_QUESTION,
		VOTING_COMMANDS_VOTE_DESC_DAY,
		VOTING_COMMANDS_VOTE_DESC_SUN,
		VOTING_COMMANDS_VOTE_DESC_NIGHT,
		VOTING_COMMANDS_VOTE_DESC_RAIN,
		VOTING_COMMANDS_VOTE_DESC_KING,
		VOTING_COMMANDS_VOTE_DESC_QUEEN,
		VOTING_COMMANDS_VOTE_DESC_INFO,
		VOTING_COMMANDS_VOTE_DESC_NOOB,
		VOTING_COMMANDS_VOTE_DESC_NOBLE,
		VOTING_COMMANDS_VOTE_DESC_NOBLE_KICK,
		VOTE_KING_CHANGE,
		ERROR_NOTENOUGHMONEY,
		VOTE_COST,
		VOTE_PAYMENT,
		VOTE_ALREADY_KING,
		VOTE_ALREADY_QUEEN,
		VOTE_ALREADY_DAY,
		VOTE_ALREADY_NIGHT,
		VOTE_ALREADY_SUN,
		VOTE_ALREADY_RAIN,
		VOTE_ALREADY_GENERAL,
		VOTE_DAY_ALREADY,
		VOTE_NIGHT,
		VOTE_NIGHT_ALREADY,
		VOTE_SUN,
		VOTE_SUN_ALREADY,
		VOTE_RAIN,
		VOTE_RAIN_ALREADY,
		VOTE_TIME_CHANGE,
		VOTE_WEATHER_CHANGE,
		TRANSLATION,
		VOTE_NO_PERMISSION,
		VOTE_BROADCAST,
		INFO_IMPERIAL_ACTIVE_TRANSMITTERS,
		INFO_REBEL_BUILD_TRANSMITTERS,
		INFO_REBEL_VOTE_REVOLUTION,
		INFO_IMPERIAL_VOTE_NOBLES,
		INFO_IMPERIAL_VOTE_KING,
		INFO_IMPERIAL_VOTE_QUEEN,
		INFO_IMPERIAL_VOTE_KICK_NOBLE,
		INFO_IMPERIAL_ADDBOUNTY,
		INFO_IMPERIAL_CLAIMBOUNTY,
		INFO_REBEL_REVOLUTION
	}
	
	private DramaCraft plugin;
	private String generalLanguageFileName = null;
	private FileConfiguration languageConfig;
	
	private int amount;
	private int amount2;
	private int amount3;
	
	private String playerName;
	private String type;
	private Random random;

	public LanguageManager(DramaCraft plugin)
	{
		this.plugin = plugin;
		this.random = new Random();
	}
	
	private void downloadLanguageFile(String fileName) throws IOException
	{
		
		BufferedInputStream in = null;
		
		BufferedOutputStream bout = null;
		
		try {
			
	
		in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/DoggyCraftDK/DramaCraft/master/lang/" + fileName).openStream());

		FileOutputStream fos = new FileOutputStream(this.plugin.getDataFolder() + "/lang/" + fileName);

		bout = new BufferedOutputStream(fos, 1024);

		byte[] data = new byte[1024];

		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0)
		{
			bout.write(data, 0, x);
		}
		
		} catch (Exception ex) {

			this.plugin.logDebug(Arrays.toString(ex.getStackTrace()));
			
		}
		finally {
		bout.close();

		in.close();
		}
	}

	private boolean loadLanguageFile(String fileName)
	{
		File languageConfigFile = new File(this.plugin.getDataFolder() + "/lang/" + fileName);
		
		if (!languageConfigFile.exists())
		{
			return false;
		}
		
		languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

		this.plugin.logDebug("Loaded " + languageConfig.getString("Version.Name") + " by " + languageConfig.getString("Version.Author") + " version " + languageConfig.getString("Version.Version"));

		return true;
	}

	public void load()
	{
		this.generalLanguageFileName = (this.plugin.languageIdentifier + ".yml");

		this.plugin.logDebug("generalFileName is " + this.generalLanguageFileName);
		this.plugin.logDebug("plugin.language is " + this.plugin.languageIdentifier);

		File directory = new File(this.plugin.getDataFolder() + "/lang");
		
		if (!directory.exists())
		{
			System.out.println("Creating language file directory '/lang'...");

			boolean result = directory.mkdir();
			if (result)
			{
				this.plugin.logDebug("Directory created");
			}
			else
			{
				this.plugin.logDebug(ChatColor.DARK_RED + "Directory creation FAILED!");
				return;
			}
		}
		
		if (!loadLanguageFile(this.generalLanguageFileName))
		{
			this.plugin.log(ChatColor.DARK_RED + "Could not load " + this.generalLanguageFileName + " from the /lang folder!");
			
			if (this.plugin.downloadLanguageFile)
			{
				this.plugin.log("Downloading " + this.generalLanguageFileName + " from DogOnFire...");
				try
				{
					downloadLanguageFile(this.generalLanguageFileName);
					this.plugin.log(this.generalLanguageFileName + " downloaded.");
				}
				catch (Exception ex)
				{
					this.plugin.log(ChatColor.DARK_RED + "Could not download " + this.generalLanguageFileName + " language file from DogOnFire: " + ex.getMessage());
					return;
				}
				
				if (loadLanguageFile(this.generalLanguageFileName))
				{
					this.plugin.log(this.generalLanguageFileName + " loaded.");
				}
			}
			else
			{
				this.plugin.log(ChatColor.DARK_RED + "Will NOT download from DogOnFire. Please place a valid language file in your /lang folder!");
			}
		}
	}

	public boolean setDefault()
	{
		return true;
	}

	public String getPriestAssignCommand(String playerName)
	{
		return "";
	}

	public String getPriestRemoveCommand(String playerName)
	{
		return "";
	}

	public String parseString(String id)
	{
		String string = id;
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + ChatColor.WHITE);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + ChatColor.WHITE);
		}
		
		if (string.contains("$Amount1"))
		{
			string = string.replace("$Amount1", ChatColor.GOLD + String.valueOf(this.amount) + ChatColor.WHITE);
		}
		if (string.contains("$Amount2"))
		{
			string = string.replace("$Amount2", ChatColor.GOLD + String.valueOf(this.amount2) + ChatColor.WHITE);
		}
		if (string.contains("$Amount3"))
		{
			string = string.replace("$Amount3", ChatColor.GOLD + String.valueOf(this.amount3) + ChatColor.WHITE);
		}
		
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", ChatColor.GOLD + this.type + ChatColor.WHITE);
		}
		return string;
	}

	public String parseString(String id, ChatColor defaultColor)
	{
		String string = defaultColor + id;
		
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + defaultColor);
		}
		
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + defaultColor);
		}
		
		if (string.contains("$Amount1"))
		{
			string = string.replace("$Amount1", ChatColor.GOLD + String.valueOf(this.amount) + defaultColor);
		}
		
		if (string.contains("$Amount2"))
		{
			string = string.replace("$Amount2", ChatColor.GOLD + String.valueOf(this.amount2) + defaultColor);
		}

		if (string.contains("$Amount3"))
		{
			string = string.replace("$Amount3", ChatColor.GOLD + String.valueOf(this.amount3) + defaultColor);
		}

		if (string.contains("$Type"))
		{
			string = string.replace("$Type", ChatColor.GOLD + this.type + defaultColor);
		}
		
		return string;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public void setPlayerName(String name)
	{
		if (name == null)
		{
			this.plugin.logDebug("WARNING: Setting null playername");
		}
		this.playerName = name;
	}
	
	public void setAmount1(int a)
	{
		this.amount = a;
	}

	public int getAmount2()
	{
		return this.amount2;
	}

	public void setAmount2(int a)
	{
		this.amount2 = a;
	}

	public void setAmount3(int a)
	{
		this.amount3 = a;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(String t) throws Exception
	{
		if (t == null)
		{
			this.plugin.logDebug("WARNING: Setting null string");
			//throw new Exception("WARNING: Setting null type");
		}
		this.type = t;
	}

	public String getLanguageString(LANGUAGESTRING languageString, ChatColor defaultColor)
	{	
		List<String> strings = languageConfig.getStringList("Info." + languageString.name());

		if (strings == null || strings.size() == 0)
		{
			this.plugin.logDebug("WARNING: No language string in " + this.generalLanguageFileName + " for the info type '" + languageString.name() + "'");
			return languageString.name() + " MISSING in " + this.generalLanguageFileName;
		}

		String text = (String) strings.toArray()[this.random.nextInt(strings.size())];
			
		return parseString(text, defaultColor);	
	}
}