package com.dogonfire.dramacraft.laws;


import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;


public class RegainHealth implements Listener, ILaw
{
	static private RegainHealth instance;
	
	RegainHealth()
	{
		instance = this;
	}
		
	
	@EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) 
	{
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN)
        {
            event.setCancelled(true);
        }
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