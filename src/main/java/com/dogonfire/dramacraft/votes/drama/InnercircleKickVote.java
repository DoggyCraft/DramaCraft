package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class InnercircleKickVote extends RebelRankVote
{
	public InnercircleKickVote(World world, Player voter, String voteText, boolean vote) {
		super("INNERCIRCLE_KICK", DramaCraft.instance().requiredYesPercentage, 3, 100, 10, world, voter, voteText, vote);
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
		if(RankManager.getOnlineInnerCircle() < reqVotes)
		{
			LanguageManager.setAmount1(reqVotes);
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_TOOFEWINNERCIRCLE_ONLINE, ChatColor.RED));
			DramaCraft.logDebug(voter.getName() + " tried to vote innercircle kick, but there are too few innercircle players online");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkSpecialConditions() {
		if(!RankManager.isRebel(voter.getUniqueId()))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYREBELSCANVOTEFORINNERCIRCLEKICK, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote kick innercircle but player was not a rebel");
			return false;
		}

		if(!RankManager.isInnerCircle(targetPlayerId))
		{
			voter.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYINNERCIRCLECANBEKICKEDFROMINNERCIRCLE, ChatColor.RED));
			DramaCraft.log(voter.getName() + " tried to vote kick innercircle but target player was not a Inner Circles");
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsVoteRequirement(Player votingPlayer) {
		if(!RankManager.isInnerCircle(votingPlayer.getUniqueId()))
		{
			votingPlayer.sendMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.ERROR_ONLYINNERCIRCLECANKICKINNERCIRCLE, ChatColor.RED));
			return false;
		}
		return true;
	}
}