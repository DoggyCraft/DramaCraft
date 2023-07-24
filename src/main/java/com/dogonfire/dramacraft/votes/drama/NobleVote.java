package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NobleVote extends RankVote
{
	public NobleVote(World world, Player voter, String voteText, boolean vote) {
		super("NOBLE", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.setNoble(targetPlayerId);
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
		// if not enough nobles online to vote a new noble in by themselves
		if(RankManager.getActiveNobles() < reqVotes)
		{
			if(!RankManager.isImperial(voter.getUniqueId()))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANVOTEFORNOBLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but player was not an imperial");
				return false;
			}

			if(!RankManager.isImperial(targetPlayerId))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANBENOBLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but target player was not an imperial");
				return false;
			}

			if(RankManager.getOnlineImperials() < reqVotes)
			{
				LanguageManager.setAmount1(reqVotes);
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWIMPERIALS_ONLINE, ChatColor.RED));
				DramaCraft.logDebug(voter.getName() + " tried to start a vote again, but there are too few imperials online");
				return false;
			}
		}
		else
		{
			if(!RankManager.isImperial(voter.getUniqueId()))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALCANVOTEFORNOBLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but player was not a imperial");
				return false;
			}

			if(!RankManager.isImperial(targetPlayerId))
			{
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANBENOBLE, ChatColor.RED));
				DramaCraft.log(voter.getName() + " tried to vote but target player was not an imperial");
				return false;
			}

			if(RankManager.getOnlineNobles() < reqVotes)
			{
				LanguageManager.setAmount1(reqVotes);
				voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWNOBLES_ONLINE, ChatColor.RED));
				DramaCraft.logDebug(voter.getName() + " tried to start a vote again, but there are too few imperial noble online");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(RankManager.getActiveNobles() < 3)
		{
			if(!RankManager.isImperial(votingPlayer.getUniqueId()))
			{
				votingPlayer.sendMessage(ChatColor.RED + "You are not an imperial! Only imperials can vote for nobles when there are less than 3 active nobles!");
				return false;
			}
		}
		else
		{
			if(!RankManager.isNoble(votingPlayer.getUniqueId()))
			{
				votingPlayer.sendMessage(ChatColor.RED + "You are not an imperial noble!");
				return false;
			}
		}
		return true;
	}
}