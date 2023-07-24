package com.dogonfire.dramacraft.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.plugin.Plugin;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.treasurehunt.TreasureHunt;
import com.dogonfire.dramacraft.treasurehunt.TreasureHuntManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;


public class FindTreasureChestLocationTask implements Runnable
{
	private TreasureHunt hunt;
	private Random random;
	private World world;
	private int run;

	public FindTreasureChestLocationTask(TreasureHunt hunt, World world)
	{
		this.hunt = hunt;
		this.random = new Random();
		this.world = world;
		this.run = 0;
	}
	
	public FindTreasureChestLocationTask(TreasureHunt hunt, World world, int run)
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
				
		List<Material> defaultspawnblocks = new ArrayList<Material>();
		defaultspawnblocks.add(Material.STONE);
		//defaultspawnblocks.add(Material.SMOOTH_BRICK);
		defaultspawnblocks.add(Material.MOSSY_COBBLESTONE);
		defaultspawnblocks.add(Material.OBSIDIAN);
		
		int numberOfFakeChests = this.random.nextInt(11);
		int y = 0;
		
		int maxDist = 1000;
		int minDist = 500;
		int centerx = 0;
		int centerz = 0;
		int minxpos = centerx + minDist;
		int minxneg = centerx - minDist;
		int minzpos = centerz + minDist;
		int minzneg = centerz - minDist;
		boolean usemarkers = TreasureHuntManager.isUsingMarkers();
		
		int minLevel = 4;
		int maxLevel = 50;
		int maxLight = 4;
		int minLight = 0;

		int t = 0;

		int maxRadius = 10000;
		
		String worldName = Bukkit.getWorlds().get(0).getName();
		BorderData border = Config.Border(worldName);
		// if border isn't set for world, it returns null; need to check for that
		if (border != null)
		{
			maxRadius = border.getRadiusX() - 100;
			DramaCraft.log("Border radius for " + worldName + " is " + maxRadius);
		    // can use border.getX() and border.getZ() to get the center position,
		    // border.getRadiusX() and border.getRadiusZ() to get the radius values,
		    // border.insideBorder(...) for checking if a location is inside the border,
		    // along with everything else that's available through BorderData
		}
		
		double radius = random.nextFloat() * maxRadius;		
		
		double a = random.nextFloat() * 2.0 * Math.PI;
		int x = (int)(radius * Math.cos(a));
		int z = (int)(radius * Math.sin(a));
		
		Location location = new Location(Bukkit.getWorlds().get(0), x, 0, z);

		location = location.getWorld().getHighestBlockAt(location).getLocation().add(0, 1.0, 0);
	
		if(DramaCraft.isWorldGuardLocation(location))
		{
			return;
		}

		if(DramaCraft.isGriefPreventionLocation(location))
		{
			return;
		}
		
		// Check for worldborder region
		if (border != null && !border.insideBorder(location))
		{
			return;
		}

		do
		{
			y = this.random.nextInt(maxLevel);
		}
		while (y < minLevel);

		Block target = world.getBlockAt(x, y, z);
		if ((target.getType() == Material.AIR) && (world.getBlockAt(x, y + 1, z).getType() == Material.AIR) && (target.getLightLevel() <= maxLight) && (target.getLightLevel() >= minLight))
		{
			target = world.getBlockAt(x, y - 1, z);

			if (defaultspawnblocks.contains(target.getType()) && hasNoWaterAbove(target.getLocation()))
			{
				target.setType(Material.GLOWSTONE);

				target = world.getBlockAt(x, y, z);
				target.setType(Material.CHEST);

				Chest tb = (Chest) target.getState();
				hunt.contents = tb.getInventory();
			}
		}

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
				Bukkit.getServer().getScheduler().runTaskLater(DramaCraft.instance(), new FindTreasureHuntImperialLocationTask(this.hunt, world, ++run), 2 * 20);
				return;
			}
		}

		
		DramaCraft.log("Chest generation after " + run + " runs.");

		//hunt.assignTreasureItems();

		//hunt.location = new Location(world, x, y, z);
		//hunt.startLocation = new Location(world, TreasureHuntManager.getCenterX(), world.getHighestBlockYAt(TreasureHuntManager.getCenterX(), TreasureHuntManager.getCenterZ()), TreasureHuntManager.getCenterZ());
		
		TreasureHuntManager.registerActiveHunt(hunt);		
	}	
}