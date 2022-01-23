package com.dogonfire.dramacraft.treasurehunt;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TreasureHuntBlockListener implements Listener
{
	public TreasureHuntBlockListener()
	{
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		TreasureHunt h = TreasureHuntManager.getCurrentHunt();
		
		if (h != null)
		{
			event.getPlayer().sendMessage(ChatColor.GRAY + "Du kan ikke ødelægge en skattekiste eller blokken under den!");
			event.setCancelled(true);
			return;
		}
	}
}