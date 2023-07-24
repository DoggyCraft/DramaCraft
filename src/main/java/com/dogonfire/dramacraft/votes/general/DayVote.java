package com.dogonfire.dramacraft.votes.general;

import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dogonfire.dramacraft.DramaCraft;

public class DayVote extends Vote
{
	public DayVote(World world, Player voter, String voteText, boolean vote) {
		super("DAY", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		DramaCraft.setDay(super.voteText);
	}

	@Override
	public void failedAction() {

	}
}