package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class InnercircleVote extends RebelRankVote
{
	public InnercircleVote(World world, Player voter, String voteText, boolean vote) {
		super("INNERCIRCLE", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.setInnerCircle(targetPlayerId);
	}

	@Override
	public void failedAction() {

	}

	// we check in special conditions
	@Override
	public boolean enoughOnlinePlayers() {
		return true;
	}

	@Override
	public boolean checkSpecialConditions() {
		// if not enough inner circle players online to vote a new inner circle member in by themselves
		if(RankManager.getActiveInnerCircle() < reqVotes)
		{
			if(!RankManager.isRebel(voter.getUniqueId()))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANVOTEFORINNERCIRCLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but player was not a rebel");
				return false;
			}

			if(!RankManager.isRebel(targetPlayerId))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANBEINNERCIRCLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but target player was not a rebel");
				return false;
			}

			if(RankManager.getOnlineRebels() < reqVotes)
			{
				LanguageManager.setAmount1(reqVotes);
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWREBELS_ONLINE, ChatColor.RED));
				DramaCraft.logDebug(voter.getName() + " tried to start a vote again, but there are too few rebels online");
				return false;
			}
		}
		else
		{
			if(!RankManager.isInnerCircle(voter.getUniqueId()))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELCANVOTEFORINNERCIRCLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but player was not inner circle");
				return false;
			}

			if(!RankManager.isRebel(targetPlayerId))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANBEINNERCIRCLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but target player was not a rebel");
				return false;
			}

			if(RankManager.getOnlineInnerCircle() < reqVotes)
			{
				LanguageManager.setAmount1(reqVotes);
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWINNERCIRCLE_ONLINE, ChatColor.RED));
				DramaCraft.logDebug(voter.getName() + " tried to start a vote again, but there are too few inner circle online");
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(RankManager.getNumberOfInnerCircle() < 3)
		{
			if(!RankManager.isRebel(votingPlayer.getUniqueId()))
			{
				votingPlayer.sendMessage(ChatColor.RED + "You are not a rebel! Only rebels can vote for inner circle when there are less than 3 in the inner circle!");
				return false;
			}
		}
		else
		{
			if(!RankManager.isInnerCircle(votingPlayer.getUniqueId()))
			{
				votingPlayer.sendMessage(ChatColor.RED + "You are not in the rebel inner circle!");
				return false;
			}
		}
		return true;
	}
}