package com.dogonfire.dramacraft;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;



public class BrewingManager implements Listener
{
	public BrewingManager()
	{
	}
	
	@EventHandler
	// Called when the brewing of the contents inside the Brewing Stand is complete.
	public void BrewEvent(Block brewer, BrewerInventory contents, int fuelLevel)
	{	
		//TODO: Only proceed when the contents include ingredients for Drugs & Alcohol
		if(!contents.contains(Material.TALL_GRASS))
		{
			return;
		}
		
		//TODO: Spawn Drugs & Alcohol
	}

	@EventHandler
	// Called when an ItemStack is about to increase the fuel level of a brewing stand.
	public void BrewingStandFuelEvent(Block brewingStand, ItemStack fuel, int fuelPower)
	{	
		//TODO: Only proceed when the contents include ingredients for Drugs & Alcohol
		///if(!brewingStand.contents.contains(Material.TALL_GRASS))
		///{
		//	return;
		//}
		
		//TODO: Slight chance of explode. Making Drugs & Alcohol is risky business
	}
}