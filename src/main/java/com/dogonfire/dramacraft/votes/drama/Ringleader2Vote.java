package com.dogonfire.dramacraft.votes.drama;

import com.dogonfire.dramacraft.RankManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ringleader2Vote extends Ringleader1Vote
{
	public Ringleader2Vote(World world, Player voter, String voteText, boolean vote) {
		super("RINGLEADER2", world, voter, voteText, vote);
	}

	@Override
	public void successAction() {
		RankManager.setRingLeader2(targetPlayerId);
	}

	@Override
	public void failedAction() {

	}
}