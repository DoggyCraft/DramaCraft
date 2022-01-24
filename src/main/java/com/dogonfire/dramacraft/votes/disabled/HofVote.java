package com.dogonfire.dramacraft.votes.disabled;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.RevolutionManager;
import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HofVote extends Vote
{
	public HofVote(World world, Player voter, String voteText, boolean vote) {
		super("HOF", DramaCraft.instance().requiredYesPercentage, 5, 100, 10, world, voter, voteText, vote);
	}

	@Override
	public void successAction() {

	}

	@Override
	public void failedAction() {

	}
}