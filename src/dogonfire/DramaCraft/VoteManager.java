package dogonfire.DramaCraft;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;

public class VoteManager
{
	public String	name;
	public String	voteString;
	public UUID		lastVoterId = UUID.randomUUID();

	public static enum VOTE_TYPE
	{
		VOTE_NONE,
		VOTE_HOF,
		VOTE_GENERAL,
		VOTE_INFO,
		VOTE_HELP,
		VOTE_YES,
		VOTE_NO,
		VOTE_DAY,
		VOTE_NIGHT,
		VOTE_RAIN,
		VOTE_SUN,
		VOTE_REVOLUTION,
		VOTE_NOBLE,
		VOTE_NOBLE_KICK,
		VOTE_INNERCIRCLE,
		MASTER_COIN,
		MASTER_LAW,
		MASTER_WAR,
		VOTE_KING,
		VOTE_QUEEN,
		VOTE_BOSS1,
		VOTE_BOSS2,
		VOTE_FAME,
		VOTE_SHAME,
	}
	
	/*
	most
	least
	
	singer
	builder
	funny
	badass
	prettiest
	
	
	boy
	girl
	
	public void setPlayerSign()
	{
		HOF_QUEEN
		HOF_KING
		HOF_CUTEST_GIRL
		HOF_BEST_BUILDER
		HOF_
	}
	*/	

	public List<String>	all				= new ArrayList();
	public List<String>	yes				= new ArrayList();
	public List<String>	no				= new ArrayList();
	private long		startVoteTime	= 0;
	private long		lastVoteTime	= 0;
	private VOTE_TYPE	currentVoteType;
	private Random		random			= new Random();
	private DramaCraft	plugin;
	
	private void resetVotes()
	{
		this.currentVoteType = VOTE_TYPE.VOTE_NONE;
		this.yes.clear();
		this.no.clear();
	}

	public VoteManager(DramaCraft plugin)
	{
		this.currentVoteType = VOTE_TYPE.VOTE_NONE;
		this.startVoteTime = System.nanoTime();
		this.name = "No Vote";
		this.plugin = plugin;
	}
	

	
	public void checkVote(int timeFactor)
	{
		String broadcast = "";
		boolean success = false;
		
		if(plugin.isRevolution())
		{
			DramaCraft.broadcastMessage(ChatColor.AQUA + "Revolution!! Will the King and Queen survive the attack by the rebels?");
			DramaCraft.broadcastMessage(ChatColor.AQUA + "The Revolution will end in " + ChatColor.GOLD + plugin.getRevolutionManager().getMinutesUntilRevolutionEnd() + " minutes.");
			return;
		}
		
		if (this.currentVoteType == VOTE_TYPE.VOTE_NONE)
		{
			switch (this.random.nextInt(20))
			{
				case 0:
					if(plugin.getKingName()!=null)
					{
						broadcast = "Hil vores konge, hans majestæt " + ChatColor.GOLD + plugin.getKingName() + " kongen af DoggyCraft!";
					}
					break;
				case 1:
					if(plugin.getQueenName()!=null)
					{
						broadcast = "Hil vores dronning, hendes majestæt " + ChatColor.GOLD + plugin.getQueenName() + " dronningen af DoggyCraft!";
					}
					break;
			}

			if(broadcast.length() > 0)
			{
				DramaCraft.broadcastMessage(broadcast);
			}

			return;
		}

		long checkVotePeriod = this.plugin.voteTimeLength / timeFactor;

		if (System.nanoTime() < this.lastVoteTime + checkVotePeriod)
		{
			return;
		}

		double reqYesPercentage = plugin.requiredYesPercentage / 100.0D;
		int reqVotes = plugin.requiredVotes;

		if ((this.currentVoteType == VOTE_TYPE.VOTE_DAY) || this.currentVoteType == VOTE_TYPE.VOTE_NIGHT || this.currentVoteType == VOTE_TYPE.VOTE_RAIN || this.currentVoteType == VOTE_TYPE.VOTE_SUN)
		{
			reqVotes = 5;
		}

		if (this.currentVoteType == VOTE_TYPE.VOTE_REVOLUTION)
		{
			reqVotes = 5; // 7
		}
		
		if (this.currentVoteType == VOTE_TYPE.VOTE_KING || this.currentVoteType == VOTE_TYPE.VOTE_QUEEN)
		{
			reqVotes = 7;//7
		}

		if (this.currentVoteType == VOTE_TYPE.VOTE_NOBLE || this.currentVoteType == VOTE_TYPE.VOTE_INNERCIRCLE)
		{
			reqVotes = 5;
		}

		if (this.currentVoteType == VOTE_TYPE.VOTE_BOSS1 || this.currentVoteType == VOTE_TYPE.VOTE_BOSS2)
		{
			reqVotes = 7;//7
		}

		if ((this.yes.size() + this.no.size() >= reqVotes) || (System.nanoTime() - this.startVoteTime > plugin.voteTimeLength))
		{
			broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_FINISHED, ChatColor.AQUA);

			DramaCraft.broadcastMessage(broadcast);

			if (this.yes.size() + this.no.size() < reqVotes)
			{
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOT_ENOUGH_VOTES, ChatColor.RED);
				DramaCraft.broadcastMessage(broadcast);
				resetVotes();
				return;
			}

			success = ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())) >= reqYesPercentage;
			
			plugin.logDebug("success " + ((float)this.yes.size()) / ((float)(this.no.size() + this.yes.size())));
			plugin.logDebug("reqYesPercentage/100 " + reqYesPercentage / 100.0);

			broadcast = "MISSING_SUCCESS";

			switch (this.currentVoteType)
			{
				case VOTE_RAIN:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_RAIN_SUCCESS, ChatColor.GREEN);
						plugin.setStorm(this.voteString);
						// plugin.setN00b(this.voteString);
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_RAIN_FAILED, ChatColor.RED);
					}
					break;
					
				case VOTE_SUN:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_SUN_SUCCESS, ChatColor.GREEN);
						plugin.setSun(this.voteString);
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_SUN_FAILED, ChatColor.RED);
					}
					break;
					
				case VOTE_KING:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_SUCCESS, ChatColor.GREEN);
						plugin.setKing(UUID.fromString(voteString));
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING_FAILED, ChatColor.RED);
					}
					break;
					
				case VOTE_NOBLE:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE_SUCCESS, ChatColor.GREEN);
						plugin.setNoble(UUID.fromString(voteString));
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE_FAILED, ChatColor.RED);
					}
					break;

				case VOTE_NOBLE_KICK:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE_KICK_SUCCESS, ChatColor.GREEN);
						plugin.downgradeRank(player.getUniqueId());
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE_KICK_FAILED, ChatColor.RED);
					}
					break;

				case VOTE_QUEEN:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_SUCCESS, ChatColor.GREEN);
						plugin.setQueen(UUID.fromString(voteString));
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN_FAILED, ChatColor.RED);
					}
					break;
					
				case VOTE_BOSS1:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_BOSS1_SUCCESS, ChatColor.GREEN);
						plugin.setBoss1(UUID.fromString(voteString));
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_BOSS1_FAILED, ChatColor.RED);
					}
					break;
					
				case VOTE_BOSS2:
					if (success)
					{
						OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(voteString));
						plugin.getLanguageManager().setPlayerName(player.getName());
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_BOSS2_SUCCESS, ChatColor.GREEN);
						plugin.setBoss2(UUID.fromString(voteString));
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_BOSS2_FAILED, ChatColor.RED);
					}
					break;

					// case VOTE_NOOB:
				// if (success)
				// {
				// broadcast =
				// plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_n00b_SUCCESS);
				// Vote.setStorm(this.voteString);
				// }
				// else
				// {
				// broadcast =
				// plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_STORM_FAILED);
				// }
				// break;
				case VOTE_DAY:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_DAY_SUCCESS, ChatColor.GREEN);
						plugin.setDay(this.voteString);
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_DAY_FAILED, ChatColor.RED);
					}
					break;
				case VOTE_NIGHT:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NIGHT_SUCCESS, ChatColor.GREEN);
						plugin.setNight(this.voteString);
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NIGHT_FAILED, ChatColor.RED);
					}
					break;
				case VOTE_GENERAL:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_GENERAL_SUCCESS, ChatColor.GREEN);
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_GENERAL_FAILED, ChatColor.RED);
					}

					// broadcast = broadcast.replaceAll("%voteString%",
					// ChatColor.WHITE + this.voteString + ChatColor.AQUA);
				case VOTE_REVOLUTION:
					if (success)
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_REVOLUTION_SUCCESS, ChatColor.GREEN);
						plugin.getRevolutionManager().startRevolution();
					}
					else
					{
						broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_REVOLUTION_FAILED, ChatColor.RED);
					}
			}

			DramaCraft.broadcastMessage(broadcast);

			resetVotes();

			return;
		}

		switch (this.currentVoteType)
		{
			case VOTE_RAIN:
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_RAIN, ChatColor.AQUA);
				break;
				
			case VOTE_SUN:
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_SUN, ChatColor.AQUA);
				break;
			
			case VOTE_NOBLE:
			{
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(this.voteString));
				plugin.getLanguageManager().setPlayerName(player.getName());
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE, ChatColor.AQUA);
			} break;

			case VOTE_NOBLE_KICK:
			{
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(this.voteString));
				plugin.getLanguageManager().setPlayerName(player.getName());
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NOBLE_KICK, ChatColor.AQUA);
			} break;

			case VOTE_INNERCIRCLE:
			{
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(this.voteString));
				plugin.getLanguageManager().setPlayerName(player.getName());
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_INNERCIRCLE, ChatColor.AQUA);
			} break;

			case VOTE_KING:
			{
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(this.voteString));
				plugin.getLanguageManager().setPlayerName(player.getName());
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_KING, ChatColor.AQUA);
			} break;

			case VOTE_QUEEN:
			{
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(this.voteString));
				plugin.getLanguageManager().setPlayerName(player.getName());
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_QUEEN, ChatColor.AQUA);
			} break;

			case VOTE_DAY:
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_DAY, ChatColor.AQUA);
				break;
				
			case VOTE_NIGHT:
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_NIGHT, ChatColor.AQUA);
				break;
				
			case VOTE_REVOLUTION:
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_REVOLUTION, ChatColor.AQUA);
				break;

			case VOTE_GENERAL:				
				plugin.getLanguageManager().setPlayerName(this.voteString);
				broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_GENERAL, ChatColor.AQUA);
				break;
		}

		DramaCraft.broadcastMessage(broadcast);

		int percent = 100;
		percent = (int) (100.0F * this.yes.size() / (this.yes.size() + this.no.size()));

		plugin.getLanguageManager().setAmount1(percent);
		plugin.getLanguageManager().setAmount2(yes.size() + no.size());
		plugin.getLanguageManager().setAmount3(reqVotes);

		broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_PROGRESS, ChatColor.AQUA);
		DramaCraft.broadcastMessage(broadcast);

		broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_HELP, ChatColor.AQUA);
		DramaCraft.broadcastMessage(broadcast);

		this.lastVoteTime = System.nanoTime();
	}

	public VOTE_TYPE getCurrentVoteType()
	{
		return this.currentVoteType;
	}

	public boolean newVote(World world, Player voter, String voteText, boolean vote, VOTE_TYPE voteType)
	{
		String broadcast = "";

		int reqVotes = plugin.requiredVotes;
		int voteCost = plugin.startVoteCost;
		long voteInterval = plugin.voteTimeLengthBetween;

		if (System.nanoTime() - this.startVoteTime < voteInterval)
		{
			int time = (int) ((this.startVoteTime + voteInterval - System.nanoTime()) / 60000000L);
			plugin.getLanguageManager().setAmount1(time);
			voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOSOON, ChatColor.RED));
			plugin.logDebug(voter.getName() + " tried to start a vote too soon");
			return false;
		}

		if (voter.getUniqueId() == lastVoterId)
		{
			voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_NOTAGAIN, ChatColor.RED));
			plugin.logDebug(voter.getName() + " tried to start a vote again, but now allowed to start another one");
			return false;
		}

		if (DramaCraft.economy.getBalance(voter.getName()) < voteCost)
		{
			plugin.getLanguageManager().setAmount1(plugin.startVoteCost);
			String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_NOTENOUGHMONEY, ChatColor.RED);
			voter.sendMessage(message);
			plugin.logDebug(voter.getName() + " tried to start a vote again, but did not have money for it");
			return false;
		}

		if (voteType == VOTE_TYPE.VOTE_REVOLUTION)
		{
			if(plugin.getOnlineRebels() < 5)
			{
				plugin.getLanguageManager().setAmount1(5);
				voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWREBELS_ONLINE, ChatColor.RED));
				plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few rebel players online");
				return false;
				//voter.sendMessage(ChatColor.RED + "Not yet ;-)");
				//return false;
			}
			
			if(!plugin.isRebel(voter.getUniqueId()))
			{
				voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYREBELSCANREVOLUTION, ChatColor.RED));
				plugin.log(voter.getName() + " tried to vote but player was not rebel");
				return false;
			}			
		}

		if (voteType == VOTE_TYPE.VOTE_KING || voteType == VOTE_TYPE.VOTE_QUEEN)
		{
			reqVotes = 5;
			
			if(DramaCraft.getOnlinePlayers() < reqVotes)
			{
				plugin.getLanguageManager().setAmount1(reqVotes);
				voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWPLAYERS, ChatColor.RED));
				plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few players online");
				return false;
			}

			if(!plugin.isNoble(voter.getUniqueId()))
			{
				voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYNOBLESCANBEKING, ChatColor.RED));
				plugin.log(voter.getName() + " tried to vote but player was not a noble");
				return false;
			}			
		}

		if (voteType == VOTE_TYPE.VOTE_NOBLE)
		{
			reqVotes = 5;
			
			if(plugin.getActiveNobles() < 3)
			{
				if(!plugin.isImperial(voter.getUniqueId()))
				{
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYIMPERIALSCANVOTEFORNOBLE, ChatColor.RED));
					plugin.log(voter.getName() + " tried to vote but player was not an imperial");
					return false;
				}

				if(plugin.getOnlineImperials() < reqVotes)
				{
					plugin.getLanguageManager().setAmount1(reqVotes);
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWIMPERIALS_ONLINE, ChatColor.RED));
					plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few imperials online");
					return false;
				}
			}		
			else
			{
				if(!plugin.isNoble(voter.getUniqueId()))
				{
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYNOBLESCANVOTEFORNOBLE, ChatColor.RED));
					plugin.log(voter.getName() + " tried to vote but player was not a noble");
					return false;
				}
				
				if(plugin.getOnlineNobles() < reqVotes)
				{
					plugin.getLanguageManager().setAmount1(reqVotes);
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWPLAYERS, ChatColor.RED));
					plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few imperial nobles online");
					return false;
				}

			}
		}
		
		if (voteType == VOTE_TYPE.VOTE_NOBLE_KICK)
		{
			reqVotes = 3;
			
			if(plugin.getOnlineNobles() < 3)
			{
				plugin.getLanguageManager().setAmount1(reqVotes);
				voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWPLAYERS, ChatColor.RED));
				plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few nobels online");
				return false;
			}		
		}

		if (voteType == VOTE_TYPE.VOTE_INNERCIRCLE)
		{
			reqVotes = 5;
			
			if(plugin.getActiveInnerCircle() < 3)
			{
				if(!plugin.isRebel(voter.getUniqueId()))
				{
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYREBELSCANVOTEFORINNERCIRCLE, ChatColor.RED));
					plugin.log(voter.getName() + " tried to vote but player was not a rebel");
					return false;
				}

				if(plugin.getOnlineRebels() < reqVotes)
				{
					plugin.getLanguageManager().setAmount1(reqVotes);
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWREBELS_ONLINE, ChatColor.RED));
					plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few rebels online");
					return false;
				}
			}		
			else
			{
				if(!plugin.isInnerCircle(voter.getUniqueId()))
				{
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYINNERCIRCLECANVOTEFORINNERCIRCLE, ChatColor.RED));
					plugin.log(voter.getName() + " tried to vote but player was not inner circle");
					return false;
				}
				
				if(plugin.getOnlineInnerCircle() < reqVotes)
				{
					plugin.getLanguageManager().setAmount1(reqVotes);
					voter.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_TOOFEWPLAYERS, ChatColor.RED));
					plugin.logDebug(voter.getName() + " tried to start a vote again, but there are too few inner circle online");
					return false;
				}
			}
		}

		this.currentVoteType = voteType;
		this.startVoteTime = System.nanoTime();
		this.lastVoterId = voter.getUniqueId();

		//TODO: Encapsulate votes logic into its own class and replace entire content with this:
		//if(currentVote.newVote(world, voter, voteText, vote, voteType))
		//{
		//this.broadcast = currentVote.getBroadcastText();
		//this.voteString =currentVote.getVoteText();
		//DramaCraft.broadcastMessage(broadcast);

		//plugin.getLanguageManager().setAmount1(voteCost);
		//String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_COST, ChatColor.AQUA);
		//voter.sendMessage(message);
		//DramaCraft.economy.withdrawPlayer(voter.getName(), voteCost);
		//}
		
		
		switch (voteType)
		{
			case VOTE_REVOLUTION:
				broadcast = "A new vote for REVOLUTION was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			case VOTE_KING:
				broadcast = "A new vote for king was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			case VOTE_QUEEN:
				broadcast = "A new vote for queen was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			case VOTE_DAY:
				broadcast = "A new vote for day was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voter.getWorld().getName();
				break;
			case VOTE_NIGHT:
				broadcast = "A new vote for night was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voter.getWorld().getName();
				break;
			case VOTE_SUN:
				broadcast = "A new vote for sun was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voter.getWorld().getName();
				break;
			case VOTE_RAIN:
				broadcast = "A new vote for rain was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voter.getWorld().getName();
				break;
			case VOTE_GENERAL:
				broadcast = "A new vote for a question was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			case VOTE_NOBLE:
				broadcast = "A new vote for a noble was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			case VOTE_NOBLE_KICK:
				broadcast = "A new vote for a removing a noble was started!";
				this.voteString = voteText;
				break;
			case VOTE_INNERCIRCLE:
				broadcast = "A new vote for the rebel inner circle was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
				this.voteString = voteText;
				break;
			default:
				broadcast = "Not a valid new Vote!";
				return false;
		}

		DramaCraft.broadcastMessage(broadcast);

		plugin.getLanguageManager().setAmount1(voteCost);
		String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_COST, ChatColor.AQUA);
		voter.sendMessage(message);

		DramaCraft.economy.withdrawPlayer(voter.getName(), voteCost);

		return true;
	}

	public void doVote(World world, Player voter, boolean vote, VOTE_TYPE voteType)
	{
		boolean firstVote = true;

		Server s = voter.getServer();

		if (voteType == VOTE_TYPE.VOTE_YES)
		{
			vote = true;
		}
		else if (voteType == VOTE_TYPE.VOTE_NO)
		{
			vote = false;
		}
		else
		{
			voter.sendMessage("INVALID Vote");
			return;
		}
		
		switch (voteType)
		{
			case VOTE_REVOLUTION:
				if(plugin.isImperial(voter.getUniqueId()))
				{
					voter.sendMessage(ChatColor.RED + "You are an imperial! You can't vote for a revolution!");
					return;
				} break;
				
			case VOTE_KING:
				if(!plugin.isNoble(voter.getUniqueId()))
				{
					voter.sendMessage(ChatColor.RED + "You are not a noble! Only imperial nobles can vote for the king!");
					return;
				} break;
				
			case VOTE_QUEEN:
				if(!plugin.isNoble(voter.getUniqueId()))
				{
					voter.sendMessage(ChatColor.RED + "You are not a noble! Only imperial nobles can vote for the queen!");
					return;
				} break;
				
			case VOTE_NOBLE:
				if(plugin.getActiveNobles() < 3)
				{
					if(!plugin.isImperial(voter.getUniqueId()))
					{				
						voter.sendMessage(ChatColor.RED + "You are not an imperial! Only imperials can vote for nobles when there are less than 3 active nobles!");
						return;
					}
				}
				else 
				{
					if(!plugin.isNoble(voter.getUniqueId()))
					{				
						voter.sendMessage(ChatColor.RED + "You are not an imperial noble!");
						return;
					}										
				} break;

			case VOTE_NOBLE_KICK:
				{
					if(!plugin.isNoble(voter.getUniqueId()))
					{				
						voter.sendMessage(ChatColor.RED + "You are not an imperial noble!");
						return;
					}										
				} break;

			case VOTE_INNERCIRCLE:
				if(plugin.getNumberOfInnerCircle() < 3)
				{
					if(!plugin.isRebel(voter.getUniqueId()))
					{				
						voter.sendMessage(ChatColor.RED + "You are not a citizen! Only citizens can vote for inner circle when there are less than 3 in the inner circle!");
						return;
					}
				}
				else 
				{
					if(!plugin.isInnerCircle(voter.getUniqueId()))
					{				
						voter.sendMessage(ChatColor.RED + "You are not in the rebel inner circle!");
						return;
					}										
				} break;

			default: break;
		}

		if (vote)
		{
			voter.sendMessage("You voted yes");
			if (!this.yes.contains(voter.getName()))
			{
				this.yes.add(voter.getName());
			}
			else
			{
				firstVote = false;
			}
			
			if (this.no.contains(voter.getName()))
			{
				this.no.remove(voter.getName());
				firstVote = false;
			}
		}
		else
		{
			voter.sendMessage("You voted no");
			
			if (this.yes.contains(voter.getName()))
			{
				this.yes.remove(voter.getName());
				firstVote = false;
			}
			
			if (!this.no.contains(voter.getName()))
			{
				this.no.add(voter.getName());
			}
			
			else
			{
				firstVote = false;
			}
		}

		if (firstVote)
		{
			plugin.getLanguageManager().setAmount1(plugin.votePayment);
			String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_PAYMENT, ChatColor.AQUA);

			voter.sendMessage(message);

			DramaCraft.economy.depositPlayer(voter.getName(), plugin.votePayment);
		}
	}
}