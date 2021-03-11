package com.dogonfire.dramacraft;

import java.util.Comparator;
import java.util.UUID;

class Bounty
{
	public UUID PlayerId;
	public int Bounty;	
	
	public Bounty(UUID playerId, int bounty)
	{
		this.PlayerId = playerId;
		this.Bounty = bounty;
	}
}

public class BountyComparator implements Comparator<Bounty>
{
	public BountyComparator()
	{
	}

	public int compare(Bounty bounty1, Bounty bounty2)
	{
		return (int) (bounty2.Bounty - bounty1.Bounty);
	}
}
