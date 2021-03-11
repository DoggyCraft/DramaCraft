package com.dogonfire.dramacraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.md_5.bungee.api.ChatColor;


public class TeleportPreventer implements Listener
{
	public boolean isWithinRegion(Player player, String regionName)
	{
		// Check for worldguard region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
											
		for(ProtectedRegion region : set)
		{
			if(region.getId().equals(regionName))
			{
				return true;
			}						
		}
	    
	    return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event)
	{
		if(RankManager.isImperial(event.getPlayer().getUniqueId()))
		{
			if(isWithinRegion(event.getPlayer(), "rebels"))
			{
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot do that from within the Rebel area.");
				event.setCancelled(true);
				return;
			}
		}
						
		if(RankManager.isRebel(event.getPlayer().getUniqueId()))
		{
			if(isWithinRegion(event.getPlayer(), "imperials"))
			{
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot do that from within the Imperial area.");
				event.setCancelled(true);
				return;
			}
		}
	}
}