package com.dogonfire.dramacraft.laws;


import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class HardDifficulty implements Listener, ILaw
{
	static private HardDifficulty instance;
	
	HardDifficulty()
	{
		instance = this;
	}

	@Override
	public String title()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description()
	{
		// TODO Auto-generated method stub
		return null;
	}
		
	
	
	

}