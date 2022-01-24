package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class QueenVote extends RankVote
{
	public QueenVote(World world, Player voter, String voteText, boolean vote) {
		super("QUEEN", DramaCraft.instance().requiredYesPercentage, 7, 1000, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.setQueen(targetPlayerId);
	}

	@Override
	public void failedAction() {

	}

	@Override
	public boolean enoughOnlinePlayers() {
		if(RankManager.getOnlineImperials() < reqVotes)
		{
			LanguageManager.setAmount1(reqVotes);
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWIMPERIALS_ONLINE, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to vote queen, but there are too few players online");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkSpecialConditions() {
		if(!RankManager.isImperial(voter.getUniqueId()))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANVOTEFORQUEEN, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote queen but player was not an imperial");
			return false;
		}

		if(!RankManager.isNoble(targetPlayerId))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYNOBLESCANBEQUEEN, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote queen but target player was not a noble");
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(!RankManager.isImperial(votingPlayer.getUniqueId()))
		{
			votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANVOTEFORQUEEN, ChatColor.RED));
			return false;
		}
		return true;
	}
}