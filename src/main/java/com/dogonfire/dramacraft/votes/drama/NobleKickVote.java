package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NobleKickVote extends RankVote
{
	public NobleKickVote(World world, Player voter, String voteText, boolean vote) {
		super("NOBLE_KICK", DramaCraft.instance().requiredYesPercentage, 3, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.downgradeRank(targetPlayerId);
	}

	@Override
	public void failedAction() {

	}

	@Override
	public boolean enoughOnlinePlayers() {
		if(RankManager.getOnlineNobles() < reqVotes)
		{
			LanguageManager.setAmount1(reqVotes);
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWNOBLES_ONLINE, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to vote noble kick, but there are too few nobles online");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkSpecialConditions() {
		if(!RankManager.isRebel(voter.getUniqueId()))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYIMPERIALSCANVOTEFORNOBLEKICK, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote kick noble but player was not an imperial");
			return false;
		}

		if(!RankManager.isInnerCircle(targetPlayerId))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYNOBLESCANBEKICKEDFROMNOBLES, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote kick noble but target player was not a noble");
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(!RankManager.isNoble(votingPlayer.getUniqueId()))
		{
			votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYNOBLESCANVOTEFORNOBLEKICK, ChatColor.RED));
			return false;
		}
		return true;
	}
}