package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ringleader1Vote extends RebelRankVote
{
	public Ringleader1Vote(World world, Player voter, String voteText, boolean vote) {
		super("RINGLEADER1", DramaCraft.instance().requiredYesPercentage, 7, 1000, 10, world, voter, voteText, vote);
	}

	public Ringleader1Vote(String voteType, World world, Player voter, String voteText, boolean vote) {
		super(voteType, DramaCraft.instance().requiredYesPercentage, 7, 1000, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.setRingLeader1(targetPlayerId);
	}

	@Override
	public void failedAction() {

	}

	@Override
	public boolean checkSpecialConditions() {
		if(!RankManager.isRebel(voter.getUniqueId()))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANVOTEFORRINGLEADER, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote ringleader but player was not a rebel");
			return false;
		}

		if(!RankManager.isInnerCircle(targetPlayerId))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYINNERCIRCLECANBERINGLEADER, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote ringleader but target player was not a Inner Circles");
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(!RankManager.isRebel(votingPlayer.getUniqueId()))
		{
			votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANVOTEFORRINGLEADER, ChatColor.RED));
			return false;
		}
		return true;
	}
}