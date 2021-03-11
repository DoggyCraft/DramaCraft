package com.dogonfire.dramacraft.treasurehunt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.dogonfire.dramacraft.DramaCraft;
import com.dogonfire.dramacraft.tasks.FindTreasureChestLocationTask;
import com.dogonfire.dramacraft.tasks.FireworkTask;
import com.dogonfire.dramacraft.tasks.TeleportTask;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class TreasureHunt
{
	private boolean			started			= false;
	private long			timestart;
	private int				value;
	private boolean			locked;
	private Random			random;

	public int				duration;
	public Inventory		contents;
	public Location			startLocation;
	public Location			location;
	//public List<Location>	fakeLocations	= new ArrayList();
	
	private Player			playerfound;
	private Player			closestplayer;
	private int				playerClosestDistance = 3000;
	
	private HashMap<Player, Integer> playerDistances = new HashMap<Player, Integer>(); 
	
	public TreasureHunt(long timestart, int maxvalue, int duration, World world)
	{
		this.locked = false;
		this.duration = duration;
		this.timestart = timestart;
		this.random = new Random();
		this.contents = null;
		this.playerfound = null;
		this.closestplayer = null;
		this.value = maxvalue;

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new FindTreasureChestLocationTask(this, world), 10);
		
		/*
		int numberOfFakeChests = this.random.nextInt(11);
		int x = 0;
		int y = 0;
		int z = 0;
		int run = 0;
		int maxDist = plugin.getMaxDistance();
		int minDist = plugin.getMinDistance();
		int centerx = plugin.getCenterX();
		int centerz = plugin.getCenterZ();
		int minxpos = centerx + minDist;
		int minxneg = centerx - minDist;
		int minzpos = centerz + minDist;
		int minzneg = centerz - minDist;
		int minele = plugin.getMinElevation();
		int maxele = plugin.getMaxElevation();
		int maxelerare = plugin.getMaxElevationRare();
		int maxlight = plugin.getMaxLightLevel();
		int minlight = plugin.getMinLightLevel();
		boolean usemarkers = plugin.isUsingMarkers();

		List spawnables = plugin.getSpawnables();

		Block target = null;
		this.contents = null;

		List<Material> defaultspawnblocks = new ArrayList();
		defaultspawnblocks.add(Material.STONE);
		defaultspawnblocks.add(Material.SMOOTH_BRICK);
		defaultspawnblocks.add(Material.MOSSY_COBBLESTONE);
		defaultspawnblocks.add(Material.OBSIDIAN);
		
		boolean canBuild = true;
		
		do
		{
			run++;

			int minLevel = 4;
			int maxLevel = 50;
			int maxLight = 4;
			int minLight = 0;
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
			
			target = world.getBlockAt(x, y, z);
			if ((target.getType() == Material.AIR) && (world.getBlockAt(x, y + 1, z).getType() == Material.AIR) && (target.getLightLevel() <= maxLight) && (target.getLightLevel() >= minLight))
			{
				target = world.getBlockAt(x, y - 1, z);
				
				if (defaultspawnblocks.contains(target.getType()) && world.getHighestBlockAt(target.getLocation()).getRelative(BlockFace.UP).getType() != Material.WATER)
				{
					target.setType(Material.GLOWSTONE);

					target = world.getBlockAt(x, y, z);
					target.setType(Material.CHEST);

					Chest tb = (Chest) target.getState();
					this.contents = tb.getInventory();
				}
			}
		}
		while ((this.contents == null) && (run < 100));
		
		if ((run >= 100) || (this.contents == null))
		{
			plugin.log("Chest generation in " + world.getName() + " FAILED");
			this.duration = 0;
			return;
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
							this.fakeLocations.add(stoneTarget.getLocation());
							world.getBlockAt(ox, oy - 1, oz).setType(Material.GLOWSTONE);
							numberOfFakeChests--;
						}
					}
				}
			}
		}
		
		plugin.log("Chest generation with " + this.fakeLocations.size() + " fake cheststook " + run + " runs.");

		assignTreasureItems();

		this.location = new Location(world, x, y, z);
		*/
	}

	/*
	public void findLocation(World world, String messageText, Sound sound, long delay)
	{
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new FindLocationTask(this.plugin, this, world), delay);
	}
	*/
/*
	public THHunt(long timestart, int maxvalue, int duration, Block loc, TreasureHunt plugin)
	{
		this.plugin = plugin;
		this.locked = false;
		this.duration = duration;
		this.timestart = timestart;
		this.random = new Random();
		this.contents = null;
		this.playerfound = null;
		this.closestplayer = null;
		this.value = maxvalue;

		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		boolean usemarkers = plugin.isUsingMarkers();
		if (usemarkers)
		{
			loc.setType(Material.GLOWSTONE);
		}
		Block target = loc.getWorld().getBlockAt(x, y + 1, z);
		target.setType(Material.CHEST);
		ContainerBlock tb = (ContainerBlock) target.getState();
		this.contents = tb.getInventory();

		assignTreasureItems();

		this.location = new Location(loc.getWorld(), x, y + 1, z);
	}
	*/

	public void assignTreasureItems()
	{
		int generatedvalue = 0;
		
		/*
		HashMap<Material, Integer> singles = this.plugin.getSingles();
		HashMap<Material, Integer> multiples = this.plugin.getMultiples();

		HashMap<Integer, Material> all = new HashMap<Integer, Material>();
		int i = 0;
		
		for (Material material : this.plugin.getSingles().keySet())
		{
			all.put(i, material);
			i++;
		}

		for (Material material : this.plugin.getMultiples().keySet())
		{
			all.put(i, material);
			i++;
		}
		do
		{
			Material item = (Material) all.get(Integer.valueOf(this.random.nextInt(all.size())));
			if (singles.containsKey(item))
			{
				this.contents.addItem(new ItemStack[] { new ItemStack(item, 1) });
				generatedvalue += ((Integer) singles.get(item)).intValue();
			}
			else
			{
				int maxamt = (this.value - generatedvalue) / ((Integer) multiples.get(item)).intValue();
				if (maxamt == 0)
				{
					this.contents.addItem(new ItemStack[] { new ItemStack(item, 1) });
					generatedvalue += ((Integer) multiples.get(item)).intValue();
				}
				else
				{
					int amt = this.random.nextInt(maxamt) + 1;
					int amt2 = this.random.nextInt(maxamt) + 1;
					if (amt > amt2)
					{
						amt = amt2;
					}
					this.contents.addItem(new ItemStack[] { new ItemStack(item, amt) });
					generatedvalue += ((Integer) multiples.get(item)).intValue() * amt;
				}
			}
		}
		while ((generatedvalue < this.value) && (this.contents.firstEmpty() >= 0));
		
		this.value = generatedvalue;
		*/
	}

	public int getMinutesLeft()
	{
		return (int) ((this.timestart + this.duration * 60000 - System.currentTimeMillis()) / 60000L);
	}

	public Location getLocation()
	{
		return this.location;
	}

	public int getPlaneDistanceFrom(Location location)
	{
		int xdiff = Math.abs(this.location.getBlockX() - location.getBlockX());
		int zdiff = Math.abs(this.location.getBlockZ() - location.getBlockZ());
		return (int)(Math.sqrt(Math.pow(xdiff, 2.0D) + Math.pow(zdiff, 2.0D)));
	}

	public int getHeightDistanceFrom(Location location)
	{
		return this.location.getBlockY() - location.getBlockY();
	}

	public int getValue()
	{
		return this.value;
	}

	public Player getPlayerFound()
	{
		return this.playerfound;
	}

	public void showClosestPlayer()
	{
		if (this.locked)
		{
			return;
		}
		
		Player current = null;
		int currdist = 1000;
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			int xdiff = Math.abs(this.location.getBlockX() - p.getLocation().getBlockX());
			int zdiff = Math.abs(this.location.getBlockZ() - p.getLocation().getBlockZ());
			
			int i = (int) Math.sqrt(Math.pow(xdiff, 2.0D) + Math.pow(zdiff, 2.0D));
			
			if (i < currdist)
			{
				currdist = i;
				current = p;
			}
		}
		
		if (current == null)
		{
			return;
		}
		
		String typestring = "";
		if (this.value <= 1500)
		{
			typestring = ChatColor.WHITE + "Normale";
		}
		else if (this.value <= 2500)
		{
			typestring = ChatColor.YELLOW + "Sjældne";
		}
		else if (this.value <= 3500)
		{
			typestring = ChatColor.GREEN + "Vilde";
		}
		else if (this.value <= 4500)
		{
			typestring = ChatColor.BLUE + "Seje";
		}
		else
		{
			typestring = ChatColor.DARK_PURPLE + "EPIC";
		}

		if (currdist < playerClosestDistance)
		{
			if ((int) (currdist / 50) < (int) (playerClosestDistance / 50))
			{
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + current.getName() + ChatColor.AQUA + " er nu kun " + currdist + " blocks væk fra den " + typestring + ChatColor.AQUA + " skat!");
			}

			if (current != this.closestplayer)
			{
				if (closestplayer != null)
				{
					this.closestplayer.sendMessage(ChatColor.RED + "Du er ikke længere den nærmeste spiller i forhold til den " + typestring + ChatColor.RED + " skat!");
				}

				this.closestplayer = current;

				this.closestplayer.sendMessage(ChatColor.GREEN + "Du er nu den nærmeste spiller i forhold til den " + typestring + ChatColor.GREEN + " skat!");
			}

			playerClosestDistance = currdist;
		}
		
		if ((current != null) && (getMinutesLeft() < 10))
		{
			this.timestart = (System.currentTimeMillis() - (this.duration - 10) * 60000);
		}
	}

	public boolean isLocked()
	{
		return this.locked;
	}

	public void registerPlayer(Player p, int planeDistance)
	{
		playerDistances.put(p, planeDistance);				
	}
	
	public void teleportAllRegisteredPlayers()
	{
		int n = 1;
		
		/*
		for(Player player : playerDistances.keySet())
		{
			if(player==closestplayer)
			{
				continue;				
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new TeleportTask(DramaCraft.instance(), player, closestplayer), n * 40L);
			n++;
		}
		*/
	}
	
	public void shootFireworks()
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new FireworkTask(closestplayer, 10), 1L);
	}

	public void chestFoundBy(Player p)
	{
		Bukkit.broadcastMessage(ChatColor.AQUA + "Skatten ved værdien " + ChatColor.GREEN + this.value + ChatColor.AQUA + " blev fundet af " + ChatColor.GOLD + p.getName() + ChatColor.AQUA + "!");
		DramaCraft.log("Treasure of value " + this.value + " was found by " + p.getName());

		/*
		int moneyamount = 0;
		double mult = this.plugin.getMoneyMultiplier();
		if (mult != 0.0D)
		{
			if (TreasureHunt.economy != null)
			{
				int maxmoney = (int) (this.value * mult);
				moneyamount = this.random.nextInt(maxmoney + 1);
				if (moneyamount < this.plugin.getMinMoney())
				{
					moneyamount = this.plugin.getMinMoney();
				}
			}
		}
		
		if (moneyamount > 0)
		{
			p.sendMessage(ChatColor.AQUA + " Du fandt " + TreasureHunt.economy.format(moneyamount) + " i kisten!");
			TreasureHunt.economy.depositPlayer(p.getName(), moneyamount);
		}
		
		this.timestart = 0;
		this.locked = true;
		this.playerfound = p;
		this.closestplayer = null;

		TreasureHuntManager.addFoundTreasure(p.getName());
		
		teleportAllRegisteredPlayers();
		
		shootFireworks();
		*/
	}

	public void run()
	{
		if (System.currentTimeMillis() < this.timestart)
		{
			TreasureHuntManager.sendWarmupBroadcast(this.timestart - System.currentTimeMillis());
		}
		else if (!this.started)
		{
			if (isExpired())
			{
				return;
			}
			
			this.started = true;

			Location where = getLocation();
			
			TreasureHuntManager.sendStartBroadcast(this.value);
			DramaCraft.log("Hunt started: " + where.getBlockX() + "," + where.getBlockY() + "," + where.getBlockZ() + " - Value: " + getValue());
		}
		else
		{
			if (!isExpired() && !locked)
			{
				TreasureHuntManager.sendInprogressBroadcast(this.value);
				
				TreasureHuntManager.spawnAttackersAgainst(this.closestplayer);
			}
		}
	}

	public boolean isExpired()
	{
		return (this.duration == 0) || (System.currentTimeMillis() >= this.timestart + this.duration * 60000);
	}

	public boolean isStarted()
	{
		return this.started;
	}

	public void removeChests()
	{
		this.contents.clear();

		this.location.getBlock().setType(Material.AIR);
		
		//for (Location fakeLocation : this.fakeLocations)
		//{
		//	fakeLocation.getBlock().setType(Material.AIR);
		//}
		
		if (TreasureHuntManager.isUsingMarkers())
		{
			this.location.getWorld().getBlockAt(this.location.getBlockX(), this.location.getBlockY() - 1, this.location.getBlockZ()).setType(Material.SOUL_SAND);
		}
		
		this.timestart = 0L;
		
		if (!this.locked)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + " Skatten med værdien " + ChatColor.GREEN + this.value + ChatColor.AQUA + " " + ChatColor.RED + "forsvandt" + ChatColor.AQUA + " uden at blive fundet...");
		}
	}
	
	public double getChestDistance()
	{
		return startLocation.distance(location);		
	}
	

}