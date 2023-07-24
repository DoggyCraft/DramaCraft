package com.dogonfire.dramacraft.votes.general;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.PhantomPreventer;
import com.dogonfire.dramacraft.RankManager;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DisablePhantomsVote extends Vote
{
	public DisablePhantomsVote(World world, Player voter, String voteText, boolean vote) {
		super("PHANTOMS", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void broadcastFinishVote(boolean success) {
		LanguageManager.setAmount1(PhantomPreventer.getDisabledTimeMinutes());
		if (success) {
			DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType+"_SUCCESS"), ChatColor.GREEN));
		}
		else {
			DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType+"_FAILED"), ChatColor.RED));
		}
	}

	@Override
	public void broadcastVote() {
		LanguageManager.setAmount1(PhantomPreventer.getDisabledTimeMinutes());
		DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType), ChatColor.AQUA));
	}

	@Override
	public void successAction() {
		DramaCraft.disablePhantoms();
	}

	@Override
	public void failedAction() {

	}
}