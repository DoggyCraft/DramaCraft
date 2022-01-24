package com.dogonfire.dramacraft.votes.general;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SunVote extends Vote
{
	public SunVote(World world, Player voter, String voteText, boolean vote) {
		super("SUN", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		DramaCraft.setSun(super.voteText);
	}

	@Override
	public void failedAction() {

	}
}