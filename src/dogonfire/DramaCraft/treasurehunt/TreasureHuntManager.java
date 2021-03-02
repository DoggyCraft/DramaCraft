package dogonfire.DramaCraft.treasurehunt;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import dogonfire.DramaCraft.DramaCraft;
import dogonfire.DramaCraft.tasks.FireworkTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;


public class TreasureHuntManager implements Listener
{
	static private TreasureHuntManager	instance;
	
	private Random						random				= new Random();
	private File						configFile			= null;
	private FileConfiguration			config				= null;
	private final TreasureHuntTimer				timer				= new TreasureHuntTimer();
	private int							timerid				= 0;
	private boolean						useperms			= false;
	private Economy						economy				= null;
	private Permission					permission			= null;
	private HashMap<Material, Integer>	multiples			= new HashMap<Material, Integer>();
	private HashMap<Material, Integer>	singles				= new HashMap<Material, Integer>();
	private List<Material>				spawnableblocks		= new ArrayList<Material>();
	private HashMap<Player, Long>		lastcheck			= new HashMap<Player, Long>();
	private int							duration			= 60;
	private int							interval			= 60;
	private int							maxdistance			= 1000;
	private int							mindistance			= 500;
	private int							minonlineplayers	= 1;
	private int							chance				= 100;
	private int							maxvalue			= 7500;
	private int							minlight			= 0;
	private int							maxlight			= 4;
	private int							maxelevation		= 50;
	private int							maxelevationrare	= 25;
	private int							minelevation		= 4;
	private int							centerx				= 0;
	private int							centerz				= 0;
	private int							drawweight			= 2;
	private int							checksec			= 5;
	private int							consumechance		= 50;
	private int							minmoney			= 100;
	private double						moneymultiplier		= 1.0D;
	private boolean						usemarker			= true;
	private List<String>				worlds				= new ArrayList<String>();
	private Material					huntTool			= Material.ROTTEN_FLESH;
	private HashMap<String, Integer>	newfilemultiples	= new HashMap<String, Integer>();
	private HashMap<String, Integer>	newfilesingles		= new HashMap<String, Integer>();
	private int							maxConcurrentHunts	= 1;
	private long						timeLastBroadcast	= 0L;
	private int							broadcastInterval	= 120000;	
	private TreasureHunt				currentHunt;
	
	public TreasureHuntManager()
	{
		instance = this;
		this.random = new Random();
	}
	
	static public TreasureHunt getCurrentHunt()
	{		
		return null;
	}
	
	static public int getChestChance()
	{
		return instance.chance;
	}

	static public int getChestInterval()
	{
		return instance.interval;
	}
	
	static public void clearCurrentHunt()
	{
		instance.currentHunt = null;
	}
	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onPlayerDeathEvent(EntityDamageByEntityEvent event)
//	{					
//		if (event.getEntity() instanceof Player)
//		{
//			Player player = (Player) event.getEntity();
//
//			if(!plugin.isImperial(player.getName()) && !plugin.isRebel(player.getName()))
//			{
//				return;
//			}			
//			
//			// only active in survival mode
//			if (!player.getGameMode().equals(GameMode.SURVIVAL))
//			{
//				return;
//			}			
//		}
//	}
	
	
	public static void startRandomHunt()
	{
		int value = instance.maxvalue + 1;
		int current = value;
		
		for (int i = 1; i <= instance.drawweight; i++)
		{
			value = instance.random.nextInt(instance.maxvalue);
			current = value < current ? value : current;
		}
		
		if (instance.worlds.size() == 0)
		{
			DramaCraft.log("Unable to start hunt! No worlds set!");
			return;
		}
		
		if ((instance.minonlineplayers > 0) && (Bukkit.getServer().getOnlinePlayers().size() < instance.minonlineplayers))
		{
			DramaCraft.log("Unable to start hunt! Too few players online.");
			return;
		}
		
		World worldtouse = Bukkit.getServer().getWorld((String) instance.worlds.get(instance.random.nextInt(instance.worlds.size())));

		long startTime = System.currentTimeMillis() + 600000L;

		TreasureHunt hunt = new TreasureHunt(startTime, current, instance.duration, worldtouse);
		/*
		if ((hunt == null) || (hunt.isExpired()))
		{
			return;
		}
		
		this.huntList.add(hunt);

		value = hunt.getValue();
		Location where = hunt.getLocation();

		log("TreasureHunt setup in " + worldtouse + ": " + where.getBlockX() + "," + where.getBlockY() + "," + where.getBlockZ() + " - Value: " + hunt.getValue());
		*/
	}

	public void startRandomHunt(int value, World world)
	{
		if (this.worlds.size() == 0)
		{
			DramaCraft.log("Unable to start hunt!  No worlds set!");
			return;
		}
		
		World worldtouse = Bukkit.getServer().getWorld((String) this.worlds.get(this.random.nextInt(this.worlds.size())));

		long startTime = System.currentTimeMillis() + 600000L;
		TreasureHunt hunt = new TreasureHunt(startTime, value, this.duration, worldtouse);

		/*
		if (hunt.isExpired())
		{
			return;
		}
		
		this.huntList.add(hunt);
		value = hunt.getValue();
		Location where = hunt.getLocation();

		log("Hunt setup: " + where.getBlockX() + "," + where.getBlockY() + "," + where.getBlockZ() + " - Value: " + hunt.getValue());*/
	}
	
	static public void registerActiveHunt(TreasureHunt hunt)
	{		
		instance.currentHunt = hunt;

		DramaCraft.log("TreasureHunt started with chest at: " + hunt.getLocation().getBlockX() + "," + hunt.getLocation().getBlockY() + "," + hunt.getLocation().getBlockZ() + " - Value: " + hunt.getValue());
	}

	public void startPlacedHunt(int value, World world)
	{
		long startTime = System.currentTimeMillis() + 90*20L;
		TreasureHunt hunt = new TreasureHunt(startTime, value, this.duration, world);
		
		/*
		if (!hunt.isExpired())
		{
			this.huntList.add(hunt);
			value = hunt.getValue();
			Location where = hunt.getLocation();
			sendStartBroadcast(value);

			log("Hunt setup: " + where.getBlockX() + "," + where.getBlockY() + "," + where.getBlockZ() + " - Value: " + hunt.getValue());
		}
		else
		{
			log("Could not place a treasure chest. Please try again!");
		}
		*/
	}

	public void broadcast(String s)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if ((!useperms) || ((useperms) && (permission.has(p, "treasurehunt.notify"))))
			{
				p.sendMessage(s);
			}
		}
	}

	public static void sendStartBroadcast(int value)
	{
		Bukkit.broadcastMessage(ChatColor.AQUA + "***** Så er der skattejagt!! *****");
		if (value <= 1500)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + " En normal skattekiste er dukket op i " + ChatColor.GOLD + "DoggyCraft" + ChatColor.AQUA + "!");
		}
		else if (value <= 2500)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + " En " + ChatColor.YELLOW + "Sjælden" + ChatColor.AQUA + " skattekiste er dukket op i " + ChatColor.GOLD + "DoggyCraft" + ChatColor.AQUA + "!");
		}
		else if (value <= 3500)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + " En " + ChatColor.GREEN + "Vild" + ChatColor.AQUA + " skattekiste er dukket op i " + ChatColor.GOLD + "DoggyCraft" + ChatColor.AQUA + "!");
		}
		else if (value <= 4500)
		{
			Bukkit.broadcastMessage(ChatColor.WHITE + " En " + ChatColor.BLUE + "Sej" + ChatColor.WHITE + " skattekiste er dukket op i DoggyCraft!");
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.WHITE + " En " + ChatColor.DARK_PURPLE + "EPIC" + ChatColor.WHITE + " skattekiste er dukket op i DoggyCraft!");
		}
	}
	
	public static void spawnAttackersAgainst(Player player)
	{
		if(instance.random.nextInt(20) > 0)
		{
			return;
		}
		
		if(player==null)
		{
			return;
		}
		
		int numberOfMobs = instance.random.nextInt(2) + 3;
	
		EntityType mobType = EntityType.CAVE_SPIDER;
		
		switch(instance.random.nextInt(5))
		{
		 	case 0 : mobType = EntityType.CAVE_SPIDER; break;
		 	case 1 : mobType = EntityType.SKELETON; break;
		 	case 2 : mobType = EntityType.WITCH; break;
		 	case 3 : mobType = EntityType.ZOMBIE; break;
		 	case 4 : mobType = EntityType.BLAZE; break;
		}		
		
		for (int i = 0; i < numberOfMobs; i++)
		{
			Location spawnLocation = player.getLocation().add(instance.random.nextInt(20) - 10, player.getLocation().getY(), instance.random.nextInt(20));
			Creature spawnedMob = (Creature) player.getWorld().spawnEntity(spawnLocation, mobType);
			spawnedMob.setTarget(player);
		}
	}
	
	public static void sendInprogressBroadcast(int value)
	{
		if (value == 0)
		{
			return;
		}
		if (System.currentTimeMillis() > instance.timeLastBroadcast + instance.broadcastInterval)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + "Der er en " + ChatColor.GOLD + "skattejagt" + ChatColor.AQUA + " igang lige nu!!");
			Bukkit.broadcastMessage(ChatColor.AQUA + "Tag til " + ChatColor.GOLD + "/warp skattejagt" + ChatColor.AQUA + " og skriv " + ChatColor.GOLD + "/hunt" + ChatColor.AQUA + " for a finde skatten!");
			instance.timeLastBroadcast = System.currentTimeMillis();
		}
	}

	public static void sendWarmupBroadcast(long milliseconds)
	{
		long minutes = milliseconds / 60000L;
		if (minutes == 0L)
		{
			return;
		}
		if (System.currentTimeMillis() > instance.timeLastBroadcast + instance.broadcastInterval)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + "Der er " + ChatColor.GOLD + "skattejagt" + ChatColor.AQUA + " om " + ChatColor.GOLD + minutes + ChatColor.AQUA + " minutter!!");
			Bukkit.broadcastMessage(ChatColor.AQUA + "Tag til Skattejagt Centret ved at skrive " + ChatColor.GOLD + "/warp skattejagt" + ChatColor.AQUA + ". Det bliver VILDT!");
			instance.timeLastBroadcast = System.currentTimeMillis();
		}
	}

	public void shootFirework(Player player, int powerValue)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new FireworkTask(player, powerValue), 1L);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getClickedBlock() == null)
		{
			return;
		}

		TreasureHunt hunt = getCurrentHunt();

		if (hunt == null)
		{
			return;
		}
				
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST)
		{
			Player p = event.getPlayer();

			if (event.getClickedBlock().getLocation().equals(hunt.location))
			{
				if (hunt.isLocked())
				{
					if (p == hunt.getPlayerFound())
					{
						return;
					}

					p.sendMessage(ChatColor.GRAY + "Denne kiste er allerede claimet af " + ChatColor.GREEN + hunt.getPlayerFound().getName() + ChatColor.GRAY + "!");
					event.setCancelled(true);
				}
				else
				{
					hunt.chestFoundBy(p);
				}
			}
		}				
	}

	public static boolean isUsingMarkers()
	{
		// TODO Auto-generated method stub
		return false;
	}
}