package dogonfire.DramaCraft;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class PhantomPreventer implements Listener
{
	static private PhantomPreventer instance;
	private boolean preventPhantoms = false;
	private long timeStarted;
	
	PhantomPreventer()
	{
		instance = this;
	}
	
	static public void disablePhantoms()
	{
		
	}
	
	static public void evaluate()
	{
		if(!instance.preventPhantoms)
		{
			return;
		}

		if(System.currentTimeMillis() - instance.timeStarted > 60 * 60 * 1000)
		{
			instance.preventPhantoms = false;
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