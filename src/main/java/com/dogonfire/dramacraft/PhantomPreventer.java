package com.dogonfire.dramacraft;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class PhantomPreventer implements Listener
{
	public int disableTimeMinutes = 60;
	static private PhantomPreventer instance;
	private boolean preventPhantoms = false;
	private long timeStarted;
	
	PhantomPreventer()
	{
		instance = this;
	}
	
	static public int getDisabledTimeMinutes()
	{
		return instance.disableTimeMinutes;
	}
	
	static public void disablePhantoms()
	{
		instance.preventPhantoms = true;
		instance.timeStarted = System.currentTimeMillis();
	}
	
	static public void evaluate()
	{
		if(!instance.preventPhantoms)
		{
			return;
		}

		if(System.currentTimeMillis() - instance.timeStarted > instance.disableTimeMinutes * 60 * 1000)
		{
			instance.preventPhantoms = false;
			DramaCraft.broadcastMessage("Phantoms are now enabled");
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if(!preventPhantoms)
		{
			return;
		}
		
		Entity entity = event.getEntity();
		
		if (entity.getType() != EntityType.PHANTOM)
		{
			return;
		}
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(!preventPhantoms)
		{
			return;
		}

		Entity damager = event.getDamager();
		Entity victim = event.getEntity();

		if (victim.getType() != EntityType.PLAYER || damager.getType() != EntityType.PHANTOM)
		{
			return;
		}
		
		Phantom phantom = (Phantom)damager;
		phantom.damage(10000);

		event.setDamage(0);
	}
}