package com.dogonfire.dramacraft.votes.disabled;

import com.dogonfire.dramacraft.votes.Vote;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ShameVote extends Vote
{
	public ShameVote(World world, Player voter, String voteText, boolean vote) {
		super("SHAME", world, voter, voteText, vote);
	}

	@Override
	public void successAction() {

	}

	@Override
	public void failedAction() {

	}
}