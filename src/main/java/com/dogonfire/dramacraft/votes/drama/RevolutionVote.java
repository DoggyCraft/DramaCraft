package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import com.dogonfire.dramacraft.RevolutionManager;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RevolutionVote extends Vote
{
	public RevolutionVote(World world, Player voter, String voteText, boolean vote) {
		super("REVOLUTION", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RevolutionManager.startRevolution();
	}

	@Override
	public void failedAction() {

	}

	@Override
	public boolean enoughOnlinePlayers() {
		if(RankManager.getOnlineRebels() < reqVotes)
		{
			LanguageManager.setAmount1(reqVotes);
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWREBELS_ONLINE, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to vote revolution, but there are too few rebels online");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkSpecialConditions() {
		if(!RankManager.isRebel(voter.getUniqueId()))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANREVOLUTION, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote but player was not rebel");
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(!RankManager.isRebel(votingPlayer.getUniqueId()))
		{
			votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANREVOLUTION, ChatColor.RED));
			return false;
		}
		return true;
	}
}