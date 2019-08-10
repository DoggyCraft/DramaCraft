package dogonfire.DramaCraft;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;


public class VotePlayerListener implements Listener
{
	private DramaCraft	plugin;
	private World	currentWorld;

	public VotePlayerListener(DramaCraft plugin)
	{
		this.plugin = plugin;
	}
	
	private void doVote(CommandSender sender, Player player, VoteManager.VOTE_TYPE voteType)
	{
		if (DramaCraft.getVoteManager().getCurrentVoteType() == VoteManager.VOTE_TYPE.VOTE_NONE)
		{
			sender.sendMessage(ChatColor.RED + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_NOTHING_TO_VOTE, ChatColor.RED));
			return;
		}

		/*
		if (KingVote.getVoteManager().getCurrentVoteType() == VoteManager.VOTE_TYPE.VOTE_REVOLUTION)
		{
			if(plugin.getRevolutionManager().isRebel(player))
			{
				sender.sendMessage(ChatColor.RED + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_ONLYREBELSCANREVOLUTION, ChatColor.RED));
				return;
			}
		}*/

		DramaCraft.getVoteManager().doVote(this.currentWorld, player, true, voteType);
		DramaCraft.getVoteManager().checkVote(40);
	}

	private boolean newVote(CommandSender sender, Player player, VoteManager.VOTE_TYPE voteType, String text)
	{
		if (DramaCraft.getVoteManager().getCurrentVoteType() != VoteManager.VOTE_TYPE.VOTE_NONE)
		{
			String message = "NO_ALREADY";

			switch (DramaCraft.getVoteManager().getCurrentVoteType())
			{
				case VOTE_INNERCIRCLE: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_KING, ChatColor.RED); break;
				case VOTE_NOBLE: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_KING, ChatColor.RED); break;
				case VOTE_KING: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_KING, ChatColor.RED); break;
				case VOTE_QUEEN: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_QUEEN, ChatColor.RED); break;
				case VOTE_NIGHT: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_NIGHT, ChatColor.RED); break;
				case VOTE_DAY: 		message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_DAY, ChatColor.RED); break;
				case VOTE_SUN: 		message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_SUN, ChatColor.RED); break;
				case VOTE_RAIN:		message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_RAIN, ChatColor.RED); break;
				case VOTE_GENERAL: 	message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_ALREADY_GENERAL, ChatColor.RED); break;
				case VOTE_HELP: 	sendHelp(sender); break;
			}
			
			sender.sendMessage(ChatColor.RED + message);

			return false;
		}

		return DramaCraft.getVoteManager().newVote(this.currentWorld, player, text, true, voteType);
	}

	private void sendHelp(CommandSender sender)
	{
		//sender.sendMessage("" + ChatColor.WHITE + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_HEAD, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_INFO, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote day " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_DAY, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote night " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_NIGHT, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote sun " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_SUN, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote rain " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_RAIN, ChatColor.AQUA));

		sender.sendMessage("" + ChatColor.WHITE + "/vote imperials " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_IMPERIALS, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote rebels " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_REBELS, ChatColor.AQUA));

		Player player = (Player)sender;
		
		if(plugin.isNoble(player.getUniqueId()))
		{	
			sender.sendMessage("" + ChatColor.WHITE + "/vote king <playername> " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_KING, ChatColor.AQUA));
			sender.sendMessage("" + ChatColor.WHITE + "/vote queen <playername> " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_QUEEN, ChatColor.AQUA));
		}

		if(plugin.isNoble(player.getUniqueId()) || plugin.getActiveNobles() < 3)
		{	
			sender.sendMessage("" + ChatColor.WHITE + "/vote noble <playername> " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_NOBLE, ChatColor.AQUA));
		}

		if(plugin.isNoble(player.getUniqueId()))
		{	
			sender.sendMessage("" + ChatColor.WHITE + "/vote kicknoble <playername> " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_NOBLE_KICK, ChatColor.AQUA));
		}

		sender.sendMessage("" + ChatColor.WHITE + "/vote question " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_QUESTION, ChatColor.AQUA));
		sender.sendMessage("" + ChatColor.WHITE + "/vote revolution " + plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTING_COMMANDS_VOTE_DESC_REVOLUTION, ChatColor.AQUA));
	}

	private void sendInfo(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "By DogOnFire");
		sender.sendMessage("" + ChatColor.AQUA);
		
		sender.sendMessage("" + ChatColor.GOLD + plugin.getNumberOfRebels() + ChatColor.AQUA + " Rebels vs " + ChatColor.GOLD + plugin.getNumberOfImperials() + ChatColor.AQUA + " Imperials");
		sender.sendMessage("" + ChatColor.AQUA);

		sendKingQueenWho(sender);

		sender.sendMessage("" + ChatColor.AQUA);

		Player player = (Player)sender;

		if(plugin.isNoble(player.getUniqueId()))
		{	
			sender.sendMessage(ChatColor.WHITE + "You are an Imperial Noble");
		}

		if(plugin.isInnerCircle(player.getUniqueId()))
		{	
			sender.sendMessage(ChatColor.WHITE + "You are part of the Rebel Inner Circle");
		}

		if(plugin.isCitizen(player.getUniqueId()))
		{	
			sender.sendMessage(ChatColor.WHITE + "You are a Citizen");
		}
		
		if(plugin.isImperial(player.getUniqueId()))
		{	
			sender.sendMessage(ChatColor.WHITE + "You are an Imperial");
		}

		if(plugin.isRebel(player.getUniqueId()))
		{	
			sender.sendMessage(ChatColor.WHITE + "You are a Rebel");
		}

		sender.sendMessage("" + ChatColor.AQUA);
		sender.sendMessage(ChatColor.AQUA + "See " + ChatColor.WHITE + "/vote help" + ChatColor.AQUA + " for how to use it");

	}

	private void sendKingQueenWho(CommandSender sender)
	{
		String kingName = plugin.getKingName();
		String queenName = plugin.getQueenName();
		
		if (kingName == null)
		{
			sender.sendMessage(ChatColor.AQUA + "DoggyCraft has no King!");
		}
		else
		{
			long kingDays = plugin.getKingElectionDays();
			
			if(kingDays == 0)
			{
				sender.sendMessage(ChatColor.AQUA + "The king of DoggyCraft is " + ChatColor.GOLD + kingName);
			}
			else
			{
				sender.sendMessage(ChatColor.AQUA + "The king of DoggyCraft is " + ChatColor.GOLD + kingName + ChatColor.AQUA + " for " + ChatColor.GOLD + kingDays + " days");				
			}
		}
		
		if (queenName == null)
		{
			sender.sendMessage(ChatColor.AQUA + "DoggyCraft has no Queen!");
		}
		else
		{
			long queenDays = plugin.getQueenElectionDays();

			if(queenDays == 0)
			{
				sender.sendMessage(ChatColor.AQUA + "The queen of DoggyCraft is " + ChatColor.GOLD + queenName);
			}
			else
			{
				sender.sendMessage(ChatColor.AQUA + "The queen of DoggyCraft is " + ChatColor.GOLD + queenName + ChatColor.AQUA + " for " + ChatColor.GOLD + queenDays + " days");				
			}
		}
	}

	public void addBounty(Player player, Player targetPlayer, int bounty)
	{
		if(!plugin.isImperial(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Only an imperial can set a bounty a rebel");
			return;			
		}

		if(!plugin.isRebel(targetPlayer.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "You can only set a bounty on a rebel");
			return;			
		}

		if(!plugin.getEconomyManager().has(player.getName(), bounty))
		{
			player.sendMessage(ChatColor.RED + "You do not have " + bounty + " wanks");
			return;
		}
		
		plugin.getEconomyManager().withdrawPlayer(player.getName(), bounty);
		plugin.getBountyManager().addBounty(targetPlayer, bounty);
		
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "A bounty of " + ChatColor.GOLD + bounty + " wanks " + ChatColor.AQUA + " was put on " + ChatColor.GOLD + targetPlayer.getName());
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "The total bounty on " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.AQUA + " is now " + ChatColor.GOLD + plugin.getBountyManager().getBounty(targetPlayer.getUniqueId()) + " wanks");
	}

	public void listBounties(Player player)
	{		
		List<Bounty> bounties = plugin.getBountyManager().getBounties();
		
		if(bounties==null)
		{
			player.sendMessage(ChatColor.RED + "There are no bounties on rebels");
			return;
		}
		
		int n = 1;
		
		player.sendMessage("");
		player.sendMessage(ChatColor.YELLOW + " --------- The Most Wanted Rebels --------- ");
		player.sendMessage("");
		
		for(Bounty bounty : bounties)
		{
			if(n<10)
			{
				player.sendMessage(ChatColor.WHITE + "  " + n + ". " + plugin.getServer().getOfflinePlayer(bounty.PlayerId).getName() + " - " + bounty.Bounty + " wanks");
			}
			
			n++;
		}
		
		//player.sendMessage("");
		//player.sendMessage("The Empire wants these players dead!");
	}

	public void updatePrefix(CommandSender sender, String[] args)
	{		
		Player player = plugin.getServer().getPlayer(args[0]);
		
		if(plugin.isRebel(player.getUniqueId()))
		{
			plugin.setRebelPrefix(player);
		}
		
		if(plugin.isImperial(player.getUniqueId()))
		{
			plugin.setImperialPrefix(player);
		}
		
		if(plugin.isCitizen(player.getUniqueId()))
		{
			plugin.setNeutralPrefix(player);
		}

		sender.sendMessage("Prefix updated.");
	}
	
	@EventHandler
	public boolean onPlayerCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player;

		if (sender instanceof Player)
		{
			player = (Player) sender;

			this.currentWorld = player.getWorld();
		}
		else
		{
			this.plugin.log(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
			this.plugin.log("" + ChatColor.GOLD + plugin.getNumberOfRebels() + ChatColor.AQUA + " Rebels vs " + ChatColor.GOLD + plugin.getNumberOfImperials() + ChatColor.AQUA + " Imperials");
			this.plugin.log("");

			sendKingQueenWho(sender);
			this.plugin.log("");
		
			if(command.getName().equalsIgnoreCase("setimperial"))
			{
				if(args[1].equalsIgnoreCase("13370x"))
				{
					Player targetPlayer = plugin.getServer().getPlayer(args[0]);
					
					if(targetPlayer!=null)
					{
						plugin.setImperial(targetPlayer);
					}
					else
					{
						plugin.log("No such player " + args[0]);
					}
				}
				
				return true;
			}			
			
			if(command.getName().equalsIgnoreCase("setrebel"))
			{
				if(args[1].equalsIgnoreCase("13370x"))
				{
					Player targetPlayer = plugin.getServer().getPlayer(args[0]);

					if(targetPlayer!=null)
					{
						plugin.setRebel(targetPlayer);
					}
					else
					{
						plugin.log("No such player " + args[0]);
					}
				}								

				return true;
			}		
			
			if(command.getName().equalsIgnoreCase("clearimperialrebel"))
			{
				if(args[1].equalsIgnoreCase("13370x"))
				{
					Player targetPlayer = plugin.getServer().getPlayer(args[0]);

					if(targetPlayer!=null)
					{
						plugin.clearImperial(targetPlayer);
						plugin.clearRebel(targetPlayer);
						plugin.setPrefix(targetPlayer.getUniqueId());
					}
					else
					{
						plugin.log("No such player " + args[0]);
					}
				}								

				return true;
			}		

			if(command.getName().equalsIgnoreCase("updateprefix"))
			{
				if(args[1].equalsIgnoreCase("13370x"))
				{
					this.updatePrefix(sender, args);
				}								

				return true;
			}		

			return false;
		}
	
		if(command.getName().equalsIgnoreCase("imperials"))
		{
			imperials(sender);
			return true;
		}			
		
		if(command.getName().equalsIgnoreCase("rebels"))
		{
			if(args.length==1)
			{
				if(args[0].equals("help"))
				{
					if(plugin.isRebel(player.getUniqueId()))
					{			
						rebelsHelp(sender);
						return true;
					}
				}
			}

			rebels(sender);			

			return true;
		}							

		if(command.getName().equalsIgnoreCase("addbounty"))
		{
			if(args.length == 2)
			{
				Player targetPlayer = plugin.getServer().getPlayer(args[0]);
				int bounty = Integer.parseInt(args[1]);

				if(targetPlayer!=null)
				{
					addBounty((Player)sender, targetPlayer, bounty);
				}
				else
				{
					plugin.log("No such online player " + args[0]);
				}
			}								
			else
			{
				sender.sendMessage("Usage: /addbounty <playername> <bounty>");
			}

			return true;
		}		
			
		if(command.getName().equalsIgnoreCase("bounty"))
		{
			if(args.length == 0)
			{
				listBounties(player);
			}								

			return true;
		}		

		if(command.getName().equalsIgnoreCase("guard"))
		{
			if(plugin.isNoble(player.getUniqueId()))
			{			
				if(args.length == 0)
				{
					plugin.getBodyguardManager().spawnGuard(player);
				}	
			}

			return true;
		}		

		if(command.getName().equalsIgnoreCase("terminator"))
		{
			//if(plugin.isNoble(player.getUniqueId()))
			{			
				if(args.length == 1)
				{
					Player target = plugin.getServer().getPlayer(args[0]);
					plugin.getBodyguardManager().spawnTerminator(player, target);
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "A terminator has been sent towards " + ChatColor.GOLD + target.getName() + ChatColor.AQUA + "...");
				}	
				else
				{
					player.sendMessage("Usage: /terminator <playername>");
				}
			}

			return true;
		}		

		if(command.getName().equalsIgnoreCase("appoint"))
		{
			if(args.length!=2)
			{
				player.sendMessage(ChatColor.RED + "Usage: /appoint <playername> <rankname>");													
				return false;
			}
		
			String rankname = args[1]; 
			
			if(plugin.isNoble(player.getUniqueId()))
			{			
				if(rankname.equals("wizard") || rankname.equals("knight") || rankname.equals("farmer") || rankname.equals("shopkeeper"))
				{
					Player targetPlayer = plugin.getServer().getPlayer(args[0]);

					if(targetPlayer!=null)
					{					
						if(!plugin.isImperial(player.getUniqueId()))
						{
							player.sendMessage(ChatColor.RED + "Target player must be an Imperial");
							return true;							
						}
						
						if(plugin.isRoyal(player))
						{
							player.sendMessage(ChatColor.RED + "Target player cannot be an Imperial Noble or Royal");
							return true;							
						}

						if(rankname.equals("knight"))
						{
							rankname = "police";
						}
						
						plugin.getPermissionsManager().setRankGroup(targetPlayer, rankname);
						plugin.setNobleClientRank(player, targetPlayer.getUniqueId(), rankname);
						plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " appointed " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.AQUA + " to imperial " + rankname);
						targetPlayer.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " appointed you to " + rankname);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Target player must be online to be appointed");													
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Valid ranks are: wizard, knight, farmer or shopkeeper");																		
				}
			}
			else
			if(plugin.isInnerCircle(player.getUniqueId()))
			{			
				if(rankname.equals("wizard") || rankname.equals("rogue") || rankname.equals("farmer") || rankname.equals("shopkeeper"))
				{
					Player targetPlayer = plugin.getServer().getPlayer(args[0]);

					if(targetPlayer!=null)
					{					
						if(!plugin.isRebel(player.getUniqueId()))
						{			
							player.sendMessage(ChatColor.RED + "Target player must be a Rebel");																											
							return true;
						}
						
						if(plugin.isBoss(targetPlayer))
						{			
							player.sendMessage(ChatColor.RED + "Target player cannot be in the Rebel inner circle or boss");																											
							return true;
						}

						if(rankname.equals("rogue"))
						{
							rankname = "police";
						}

						plugin.getPermissionsManager().setRankGroup(targetPlayer, rankname);
						plugin.setInnerCircleClientRank(player, targetPlayer.getUniqueId(), rankname);
						plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " appointed " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.AQUA + " to rebel " + rankname);
						targetPlayer.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " appointed you to " + rankname);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Target player must be online to be appointed");													
					}
				}								
				else
				{
					player.sendMessage(ChatColor.RED + "Valid ranks are: wizard, rogue, farmer or shopkeeper");																		
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You do not have the rights to use this command");								
			}

			return true;
		}			
	

		if(command.getName().equalsIgnoreCase("track"))
		{
			if(plugin.isImperial(player.getUniqueId()))
			{
				double distance = plugin.getTransmitterManager().getClosestDistanceToTransmitter(player.getLocation());
				
				if(distance < 999999)
				{
					player.sendMessage(ChatColor.GRAY + "Distance to nearest Rebel Transmitter: " + distance + " blocks");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "There are no Rebel Transmitters in this world");
				}
			}								
/*
			if(plugin.isRebel(player.getUniqueId()))
			{
				double distance = plugin.getStatueManager().getClosestDistanceToStatue(player.getLocation());
				
				if(distance < 999999)
				{
					player.sendMessage(ChatColor.GRAY + "Distance to nearest Imperial Statue: " + distance + " blocks");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "There are no Imperial Statues in this world");
				}
			}								
*/
			return true;
		}			

		if(args.length==0)
		{
			sendInfo(sender);
			
			return true;
		}
		else if(args.length==1)
		{
			if(args[0].equalsIgnoreCase("help"))
			{
				sendHelp(sender);
				return true;
			}			

			else if(args[0].equalsIgnoreCase("info"))
			{
				sendInfo(sender);
				return true;
			}
					
			else if(args[0].equalsIgnoreCase("setkinghead"))
			{
				setKingHead(sender, player);
				return true;
			}
			else if(args[0].equalsIgnoreCase("setqueenhead"))
			{
				setQueenHead(sender, player);
				return true;
			}
			else if(args[0].equalsIgnoreCase("day"))
			{
				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_DAY, ""))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}

				return true;
			}
			else if(args[0].equalsIgnoreCase("night"))
			{
				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_NIGHT, ""))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}
			
				return true;
			}
			else if(args[0].equalsIgnoreCase("sun"))
			{
				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_SUN, ""))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}
			
				return true;
			}
			else if(args[0].equalsIgnoreCase("rain"))
			{
				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_RAIN, ""))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}
			
				return true;
			}
			else if(args[0].equalsIgnoreCase("revolution"))
			{
				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_REVOLUTION, ""))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}
			
				return true;
			}
			else if(args[0].equalsIgnoreCase("yes"))
			{
				doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				this.plugin.printlog(player.getName() + " voted yes");

				return true;
			}
			else if(args[0].equalsIgnoreCase("no"))
			{
				doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_NO);
				this.plugin.printlog(player.getName() + " voted no");

				return true;
			}			
			
		}
		else if(args.length==2)
		{
			if(args[0].equalsIgnoreCase("king"))
			{
				Player targetPlayer = plugin.getServer().getPlayer(args[1]);
				
				if(targetPlayer==null)
				{
					sender.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_PLAYER_NOT_ONLINE, ChatColor.RED));
					return true;
				}				
				
				if(targetPlayer.isOp())
				{
					sender.sendMessage(ChatColor.RED + "No.");
					return true;
				}

				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_KING, targetPlayer.getUniqueId().toString()))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}

				return true;
			}
			else
			if(args[0].equalsIgnoreCase("queen"))
			{
				Player targetPlayer = plugin.getServer().getPlayer(args[1]);
				
				if(targetPlayer==null)
				{
					sender.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_PLAYER_NOT_ONLINE, ChatColor.RED));
					return true;
				}				
				
				if(targetPlayer.isOp())
				{
					sender.sendMessage(ChatColor.RED + "No.");
					return true;
				}

				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_QUEEN, targetPlayer.getUniqueId().toString()))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}

				return true;
			}
			if(args[0].equalsIgnoreCase("noble"))
			{
				Player targetPlayer = plugin.getServer().getPlayer(args[1]);
				
				if(targetPlayer==null)
				{
					sender.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_PLAYER_NOT_ONLINE, ChatColor.RED));
					return true;
				}				
				
				if(targetPlayer.isOp())
				{
					sender.sendMessage(ChatColor.RED + "Admins can not be an Imperial Noble.");
					return true;
				}

				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_NOBLE, targetPlayer.getUniqueId().toString()))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}

				return true;
			}
			if(args[0].equalsIgnoreCase("innercircle"))
			{
				Player targetPlayer = plugin.getServer().getPlayer(args[1]);
				
				if(targetPlayer==null)
				{
					sender.sendMessage(plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.ERROR_PLAYER_NOT_ONLINE, ChatColor.RED));
					return true;
				}				
				
				if(targetPlayer.isOp())
				{
					sender.sendMessage(ChatColor.RED + "Admins can not be in the Rebel Inner Circle.");
					return true;
				}

				if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_INNERCIRCLE, targetPlayer.getUniqueId().toString()))
				{
					doVote(sender, player, VoteManager.VOTE_TYPE.VOTE_YES);
				}

				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("question"))
		{
			String questionText = "";
			
			for(int i=1; i<args.length; i++)
			{
				questionText += args[i] + " ";
			}
			
			questionText = questionText.trim();
			
			if(newVote(sender, player, VoteManager.VOTE_TYPE.VOTE_GENERAL, questionText))
			{
			}
			
			return true;
		}

		sendHelp(sender);

		return true;
	}
	
	private void setKingHead(CommandSender sender, Player player)
	{
		if(player==null || !player.isOp())
		{
			return;
		}
		
		String kingName = plugin.getKingName();	
		
		if(kingName!=null)
		{
			plugin.setKingHead(player.getUniqueId(), player.getLocation());
		}
	}

	private void setQueenHead(CommandSender sender, Player player)
	{
		if(player==null || !player.isOp())
		{
			return;
		}
		
		String queenName = plugin.getQueenName();		
		
		if(queenName!=null)
		{
			plugin.setQueenHead(player.getUniqueId(), player.getLocation());
		}
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
	
	private void imperials(CommandSender sender)
	{
		//sender.sendMessage(ChatColor.WHITE + "The Empire has " + ChatColor.GOLD + plugin.getStatueManager().getStatues() + ChatColor.WHITE + " statues placed across these lands");
		
		Set<String> nobles = plugin.getNobles();
		List<Member> members = new ArrayList<Member>();
		
		for(String member : nobles)
		{
			UUID playerId = UUID.fromString(member);
			long days = plugin.getNobleElectionDays(playerId);
			
			members.add(new Member(playerId, days));
		}

		Collections.sort(members, new MemberComparator());
		
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + " --------- The Imperial Nobles --------- ");

		for(Member m : members)
		{
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(m.PlayerId);
			
			if(m.Days<=7)
			{
				sender.sendMessage(" " + player.getName() + "   Last login: " + ChatColor.GREEN + m.Days + ChatColor.WHITE + " days ago");
			}
			else
			{
				sender.sendMessage(" " + player.getName() + "   Last login: " + ChatColor.RED + m.Days + ChatColor.WHITE + " days ago");
			}
		}
	}

	private void rebels(CommandSender sender)
	{	
		sender.sendMessage(ChatColor.WHITE + "The Rebels has " + ChatColor.GOLD + plugin.getTransmitterManager().getTransmitters() + ChatColor.WHITE + " transmitters placed across these lands");

		if(!plugin.isRebel(((Player)sender).getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only rebels can view members of the Rebel Inner Circle");
			return;
		}

		Set<String> innerCircle = plugin.getInnerCircle();
		List<Member> members = new ArrayList<Member>();
		
		for(String member : innerCircle)
		{
			UUID playerId = UUID.fromString(member);
			long days = plugin.getNobleElectionDays(playerId);
			
			members.add(new Member(playerId, days));
		}

		Collections.sort(members, new MemberComparator());
		
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + " --------- The Rebel Inner Circle -------- ");

		for(Member m : members)
		{
			Player player = plugin.getServer().getPlayer(m.PlayerId);
			if(m.Days<=7)
			{
				sender.sendMessage(" " + player.getName() + "   Last login: " + ChatColor.GREEN + m.Days + ChatColor.WHITE + " days ago");
			}
			else
			{
				sender.sendMessage(" " + player.getName() + "   Last login: " + ChatColor.RED + m.Days + ChatColor.WHITE + " days ago");
			}
		}
	}

	private void rebelsHelp(CommandSender sender)
	{	
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "As a REBEL it is your duty to tell the truth about the King, Queen and the evil empire they rule!");
		sender.sendMessage(ChatColor.WHITE + "You can do this by building a REBEL TRANSMITTER");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + " --------- How to build a Rebel Transmitter -------- ");
		sender.sendMessage(ChatColor.WHITE + "  1) Place a STONE block");
		sender.sendMessage(ChatColor.WHITE + "  2) Place a TORCH on top of the STONE block");
		sender.sendMessage(ChatColor.WHITE + "  3) Place a sign on the STONE block");
		sender.sendMessage(ChatColor.WHITE + "  4) Write your TRUTH message on the SIGN");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "Try to be creative in your messages ;-)");			
	}

	double roundTwoDecimals(double d)
	{
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d)).doubleValue();
	}
}