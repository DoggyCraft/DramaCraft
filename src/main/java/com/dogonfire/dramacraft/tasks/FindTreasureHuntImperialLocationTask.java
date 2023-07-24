package com.dogonfire.dramacraft.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.plugin.Plugin;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.treasurehunt.TreasureHunt;
import com.dogonfire.dramacraft.treasurehunt.TreasureHuntManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;


public class FindTreasureHuntImperialLocationTask implements Runnable
{
	private TreasureHunt hunt;
	private Random random;
	private World world;
	private int run;

	public FindTreasureHuntImperialLocationTask(TreasureHunt hunt, World world)
	{
		this.hunt = hunt;
		this.random = new Random();
		this.world = world;
		this.run = 0;
	}
	
	public FindTreasureHuntImperialLocationTask(TreasureHunt hunt, World world, int run)
	{
		this.hunt = hunt;
		this.random = new Random();
		this.world = world;
		this.run = run;
	}
	
	public WorldGuardPlugin getWorldGuard()
	{
		Plugin worldGuardPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
	
		if (!(worldGuardPlugin instanceof WorldGuardPlugin))
		{
			return null;
		}
		
		return (WorldGuardPlugin) worldGuardPlugin;
	}

	private boolean hasNoWaterAbove(Location location)
	{
		//world.getHighestBlockAt(target.getLocation()).getRelative(BlockFace.UP).getType() != Material.WATER		
		
		for(int y=location.getBlockY(); y<200; y++)
		{
			if(world.getBlockAt(location.getBlockX(), y, location.getBlockZ()).getType() == Material.WATER)
			{
				return false;
			}			
		}
		
		return true;
	}
	
	public void run()
	{
		boolean canBuild = true;
		

		// Make sure we start in a nice biome, flat area and nice surface area
		List<Material> defaultspawnblocks = new ArrayList<Material>();
		defaultspawnblocks.add(Material.STONE);
		//defaultspawnblocks.add(Material.SMOOTH_BRICK);
		defaultspawnblocks.add(Material.MOSSY_COBBLESTONE);
		defaultspawnblocks.add(Material.OBSIDIAN);
		
		int numberOfFakeChests = this.random.nextInt(11);
		int x = 0;
		int y = 0;
		int z = 0;
		
		boolean usemarkers = TreasureHuntManager.isUsingMarkers();
		
		int minLevel = 4;
		int maxLevel = 50;
		int maxLight = 4;
		int minLight = 0;

		int t = 0;

		// Check for regions & claims
		
		if (hunt.contents == null)
		{
			if (run >= 100)
			{
				DramaCraft.log("Chest generation in " + world.getName() + " FAILED after " + run + " attempts");
				hunt.duration = 0;
				return;
			}
			else
			{
				DramaCraft.log("Did not find a location for treasure chest after " + run + " attempts. Retrying...");
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance(), new FindTreasureHuntImperialLocationTask(this.hunt, world, ++run), 2*20);				
				return;
			}
		}

		hunt.assignTreasureItems();

		hunt.location = new Location(world, x, y, z);
		//hunt.startLocation = new Location(world, TreasureHuntManager.getCenterX(), world.getHighestBlockYAt(TreasureHuntManager.getCenterX(), TreasureHuntManager.getCenterZ()), TreasureHuntManager.getCenterZ());
		
		TreasureHuntManager.registerActiveHunt(hunt);
	}	
}