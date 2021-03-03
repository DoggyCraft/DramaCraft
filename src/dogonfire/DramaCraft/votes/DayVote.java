package dogonfire.DramaCraft.votes;

import java.util.Arrays;
import java.util.UUID;


import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;
import dogonfire.DramaCraft.VoteManager.VOTE_TYPE;

public class DayVote extends Vote
{
	DayVote()
	{
		//reqVotes = DramaCraft.RequiredVotes();
		//voteCost 			= DramaCraft.instance().startVoteCost;
	}
	
	public void success()
	{
		
	}
	
	public void failed()
	{
		
	}
	
	public boolean checkVote(int timeFactor)
	{
		String broadcast = "";
		boolean success = false;

		return isCompleted();
		
	}
		
	public void progress()
	{
		/*
		broadcast = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_BROADCAST_DAY, ChatColor.AQUA);
				
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
		*/
	}

	public VOTE_TYPE getCurrentVoteType()
	{
		return this.voteType;
	}

	public boolean newVote(World world, Player voter, String voteText, boolean vote, VOTE_TYPE voteType)
	{
		/*
		String broadcast = "";

		broadcast = "A new vote for day was started by " + ChatColor.WHITE + voter.getDisplayName() + "!";
		this.voteString = voter.getWorld().getName();

		DramaCraft.broadcastMessage(broadcast);

		plugin.getLanguageManager().setAmount1(voteCost);
		String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.VOTE_COST, ChatColor.AQUA);
		voter.sendMessage(message);

		DramaCraft.economy.withdrawPlayer(voter, voteCost);
*/
		return true;
	}

	public boolean tryVote(World world, Player voter, boolean vote, VOTE_TYPE voteType)
	{	
		/*
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
			//return;
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

			DramaCraft.economy.depositPlayer(voter, plugin.votePayment);
		}
		*/
		
		return true;
	}

}