package com.dogonfire.dramacraft.votes.disabled;

import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FameVote extends Vote
{
	public FameVote(World world, Player voter, String voteText, boolean vote) {
		super("FAME", world, voter, voteText, vote);
	}

	@Override
	public void successAction() {

	}

	@Override
	public void failedAction() {

	}
}