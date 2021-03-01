package dogonfire.DramaCraft.tasks;

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

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.treasurehunt.TreasureHunt;
import dogonfire.DramaCraft.treasurehunt.TreasureHuntManager;


public class FindLocationTask implements Runnable
{
	private TreasureHunt hunt;
	private Random random;
	private World world;
	private int run;

	public FindLocationTask(TreasureHunt hunt, World world)
	{
		this.hunt = hunt;
		this.random = new Random();
		this.world = world;
		this.run = 0;
	}
	
	public FindLocationTask(TreasureHunt hunt, World world, int run)
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
		
		/*
		List<Material> defaultspawnblocks = new ArrayList<Material>();
		defaultspawnblocks.add(Material.STONE);
		//defaultspawnblocks.add(Material.SMOOTH_BRICK);
		defaultspawnblocks.add(Material.MOSSY_COBBLESTONE);
		defaultspawnblocks.add(Material.OBSIDIAN);
		
		int numberOfFakeChests = this.random.nextInt(11);
		int x = 0;
		int y = 0;
		int z = 0;
		
		int maxDist = TreasureHuntManager.getMaxDistance();
		int minDist = TreasureHuntManager.getMinDistance();
		int centerx = TreasureHuntManager.getCenterX();
		int centerz = TreasureHuntManager.getCenterZ();
		int minxpos = centerx + minDist;
		int minxneg = centerx - minDist;
		int minzpos = centerz + minDist;
		int minzneg = centerz - minDist;
		int minele = TreasureHuntManager.getMinElevation();
		int maxele = TreasureHuntManager.getMaxElevation();
		int maxelerare = TreasureHuntManager.getMaxElevationRare();
		int maxlight = TreasureHuntManager.getMaxLightLevel();
		int minlight = TreasureHuntManager.getMinLightLevel();
		boolean usemarkers = TreasureHuntManager.isUsingMarkers();
		
		int minLevel = 4;
		int maxLevel = 50;
		int maxLight = 4;
		int minLight = 0;

		int t = 0;

		do
		{
			do
			{
				x = this.random.nextInt(maxDist * 2) - maxDist + centerx;
				z = this.random.nextInt(maxDist * 2) - maxDist + centerz;

				if (getWorldGuard() != null)
				{
					Location location = new Location(world, x, y, z);

					RegionManager regionManager = WGBukkit.getRegionManager(location.getWorld());

					ApplicableRegionSet set = regionManager.getApplicableRegions(location);

					canBuild = (set.size() == 0);
				}
			}
			while ((Math.abs(x - centerx) < minDist) || (Math.abs(z - centerz) < minDist) || !canBuild);

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
		}
		while (t++ < 10 && hunt.contents == null);
		
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
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new FindLocationTask(this.hunt, world, ++run), 2*20);				
				return;
			}
		}
		
		for (int oy = y - 7; oy < y + 7; oy++)
		{
			for (int ox = x - 20; ox < x + 20; ox++)
			{
				for (int oz = z - 20; oz < z + 20; oz++)
				{
					if (numberOfFakeChests > 0)
					{
						Block stoneTarget = world.getBlockAt(ox, oy, oz);
						
						if ((this.random.nextInt(4) == 0) && (stoneTarget.getType() == Material.AIR) && (world.getBlockAt(ox, oy - 1, oz).getType() == Material.STONE))
						{
							stoneTarget.setType(Material.CHEST);
							hunt.fakeLocations.add(stoneTarget.getLocation());
							world.getBlockAt(ox, oy - 1, oz).setType(Material.GLOWSTONE);
							numberOfFakeChests--;
						}
					}
				}
			}
		}
		
		DramaCraft.log("Chest generation with " + hunt.fakeLocations.size() + " fake chests after " + run + " runs.");

		hunt.assignTreasureItems();

		hunt.location = new Location(world, x, y, z);
		hunt.startLocation = new Location(world, TreasureHuntManager.getCenterX(), world.getHighestBlockYAt(TreasureHuntManager.getCenterX(), TreasureHuntManager.getCenterZ()), TreasureHuntManager.getCenterZ());
		
		TreasureHuntManager.registerActiveHunt(hunt);
		*/
	}	
}