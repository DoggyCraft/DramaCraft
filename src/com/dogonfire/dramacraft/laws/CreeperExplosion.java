package com.dogonfire.dramacraft.laws;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;


public class CreeperExplosion implements Listener, ILaw
{
	static private CreeperExplosion instance;
	
	CreeperExplosion()
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
		
	
	@EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) 
	{
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN)
        {
            event.setCancelled(true);
        }
    }
}