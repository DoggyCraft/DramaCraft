package com.dogonfire.dramacraft.treasurehunt;

import java.util.Random;



public class TreasureHuntTimer implements Runnable
{
	private long			lastrun;
	private Random			random;

	public TreasureHuntTimer()
	{
		this.lastrun = System.currentTimeMillis();
		this.random = new Random();
	}

	//
	// Run every 10 seconds
	//
	public void run()
	{
		if (TreasureHuntManager.getCurrentHunt()==null)
		{
			if (System.currentTimeMillis() >= this.lastrun + TreasureHuntManager.getChestInterval() * 1000)
			{
				if (this.random.nextInt(100) < TreasureHuntManager.getChestChance())
				{
					TreasureHuntManager.startRandomHunt();
				}
				this.lastrun = System.currentTimeMillis();
			}
		}
				
	    TreasureHunt hunt = TreasureHuntManager.getCurrentHunt();
		
		if (hunt != null)
		{
			hunt.run();

			if (hunt.isExpired())
			{
				hunt.removeChests();

				TreasureHuntManager.clearCurrentHunt();
			}
		}
	}
}