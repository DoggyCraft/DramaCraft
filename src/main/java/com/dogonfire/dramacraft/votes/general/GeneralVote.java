package com.dogonfire.dramacraft.votes.general;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.LanguageManager;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GeneralVote extends Vote
{
	public GeneralVote(World world, Player voter, String voteText, boolean vote) {
		super("GENERAL", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void broadcastVote() {
		LanguageManager.setPlayerName(super.voteText);
		DramaCraft.broadcastMessage(LanguageManager.getLanguageString(LanguageManager.LANGUAGESTRING.valueOf("VOTE_BROADCAST_"+voteType), ChatColor.AQUA));
	}

	@Override
	public void successAction() {

	}

	@Override
	public void failedAction() {

	}
}