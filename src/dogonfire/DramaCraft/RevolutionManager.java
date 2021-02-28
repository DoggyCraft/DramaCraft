package dogonfire.DramaCraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import dogonfire.DramaCraft.LanguageManager.LANGUAGESTRING;


public class RevolutionManager implements Listener
{
	public boolean isRevolution;
	
	private String battleRegionName = "castle";
	private String rebelRegionName = "dannevirke4";
	private Location imperialRevolutionSpawn;
	private Location rebelRevolutionSpawn;
	private DramaCraft plugin;
	private long timeRevolutionStarted;
	private long timeRevolutionVoteInfo;
	private Random random = new Random();
	private int rebelPoints;
	private int imperialPoints;
	
	private HashMap<UUID, ItemStack[]> savedInventories;

	public RevolutionManager(DramaCraft plugin)
	{
		this.plugin = plugin;
		
		imperialRevolutionSpawn = new Location(Bukkit.getServer().getWorlds().get(0), 1042501, 78, 21925);
		rebelRevolutionSpawn = new Location(Bukkit.getServer().getWorlds().get(0), 1042500, 68, 21861);
		
		motd.add(ChatColor.GREEN + "Ren hygge!");
		motd.add(ChatColor.GREEN + "Kufor de mest Hardcore typer!");
		motd.add(ChatColor.GREEN + "Der kommer flere updates inden jul!");
		motd.add(ChatColor.GREEN + "Mere awesome end din mor!");
		motd.add(ChatColor.GREEN + "Kom ind og hyg!");
		motd.add(ChatColor.GREEN + "Danmarks ældste server!");
		motd.add(ChatColor.GREEN + "Vi er fucking mærkelige!");
		motd.add(ChatColor.GREEN + "A wretched hive of scum and nerdery!");
		motd.add(ChatColor.GREEN + "Vi laver vores egne plugins!");
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{	
		if(!isRevolution)
		{
			return;
		}
		
		enforceRevolution(event.getPlayer());		
	}


	@EventHandler(ignoreCancelled = true)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		if(!isRevolution)
		{
			return;
		}
		
		final Player player = event.getPlayer();

		if(plugin.isImperial(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.AQUA + "You must kill the rebels to protect the king and queen during this revolution!");
			
			plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
			{
				public void run()
				{
					player.teleport(imperialRevolutionSpawn);
				}
			}, 40L);						
		}
		
		if(plugin.isRebel(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.AQUA + "You must kill imperials to overthrow the king and queen during this revolution!");			

			plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
			{
				public void run()
				{
					player.teleport(rebelRevolutionSpawn);
				}
			}, 40L);						
		}
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{		
		if(!isRevolution)
		{
			return;
		}
		
		Player player = event.getEntity();
		
		if(plugin.isImperial(player.getUniqueId()))
		{
			plugin.broadcastMessage("An imperial was killed!");			
			this.addPointToRebels();			
		}
		else if(plugin.isRebel(player.getUniqueId()))
		{			
			plugin.broadcastMessage("A rebel was killed!");
			this.addPointToImperials();
		}
		else
		{
			return;
		}
		
		// Prevent any items drops so that players can re-spawn and fight!
		//event.getDrops().clear(); 

		if(this.getImperialPoints() > this.getRebelPoints())
		{
			plugin.broadcastMessage("The Imperials are winning the revolution! ");
			plugin.broadcastMessage(ChatColor.GOLD + "  " + this.getImperialPoints() + ChatColor.AQUA + " vs " + ChatColor.GOLD + this.getRebelPoints());
		}
		else if(this.getImperialPoints() < this.getRebelPoints())
		{
			plugin.broadcastMessage("The Rebels are winning the revolution! ");
			plugin.broadcastMessage(ChatColor.GOLD + "  " + this.getRebelPoints() + ChatColor.AQUA + " vs " + ChatColor.GOLD + this.getImperialPoints());
		}		
	}

	private List<String> motd = new ArrayList<String>(); 
	
	@EventHandler
	public void countDown(final ServerListPingEvent event)
	{
		if(isRevolution)
		{
			event.setMotd(ChatColor.DARK_RED + "We have a REVOLUTION in progress!!");
			return;
		}
	
		event.setMotd(motd.get(random.nextInt(motd.size())));		
	}
	
	/*
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(!isRevolution)
		{
			return;
		}

		Player player = event.getPlayer();
		
		if(isImperial(player.getName()))
		{
			event.setCancelled(true);
		}
		else if(isRebel(player.getName()))
		{
			event.setCancelled(true);
		}
	}
	*/
	
	public void addPointToImperials()
	{
		imperialPoints++;		
	}
	
	public void addPointToRebels()
	{
		rebelPoints++;		
	}

	public int getImperialPoints()
	{
		return imperialPoints;
	}

	public int getRebelPoints()
	{
		return rebelPoints;		
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if(!isRevolution)
		{
			return;
		}

		Player player = event.getPlayer();

		if (plugin.isRoyal(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.DARK_RED + "You must stay in your castle and avoid getting killed!");
			event.setCancelled(true);
			return;
		}

		if (plugin.isImperial(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.DARK_RED + "You must stay and fight for your King & Queen!");
			event.setCancelled(true);
			return;
		}

		if (plugin.isRebel(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.DARK_RED + "You must stay and fight to overthrow the King & Queen!");
			event.setCancelled(true);
			return;
		}
	}
	
	public void startRevolution()
	{
		isRevolution = true;
		timeRevolutionStarted = System.currentTimeMillis();
		
		enforceRevolution();		
		
		World world = BukkitAdapter.adapt(plugin.getServer().getWorlds().get(0));	
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();	
		RegionManager regionManager = container.get(world);
		ProtectedRegion region = regionManager.getRegion(battleRegionName);
						
		region.setFlag(Flags.PVP, State.ALLOW);
		region.setFlag(Flags.ENTRY, State.ALLOW);
		region.setFlag(Flags.PASSTHROUGH, State.ALLOW);
		//String message = region.getFlag(Flags.GREET_MESSAGE);
		
		// Set PvP in castle
		// Set exit deny in castle
		// Remove tpa and home commands
		
		// Tp Queen into castle
		// Tp king into castle
		
		// Tp imperials to the internals
		// Tp rebels to the Gates
		
		// DoThis for OnJoin
		
		//List<String> imperialPlayers = this.permissionsManager.getOnlinePlayersInGroup("Imperial");
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(plugin.isImperial(player.getUniqueId()))
			{
				player.teleport(imperialRevolutionSpawn);
			}
			
			if(plugin.isRebel(player.getUniqueId()))
			{
				player.teleport(rebelRevolutionSpawn);
			}
		}
	}
	
	public void endRevolution()
	{
		isRevolution = false;

		World world = BukkitAdapter.adapt(plugin.getServer().getWorlds().get(0));	
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();	
		RegionManager regionManager = container.get(world);
		ProtectedRegion region = regionManager.getRegion(battleRegionName);
						
		region.setFlag(Flags.PVP, State.DENY);
		region.setFlag(Flags.ENTRY, State.DENY);
		region.setFlag(Flags.PASSTHROUGH, State.DENY);
		//String message = region.getFlag(Flags.GREET_MESSAGE);
				
		if(rebelPoints > imperialPoints)
		{
			Bukkit.broadcastMessage(ChatColor.GREEN + "The Revolution SUCEEDED!");
			Bukkit.broadcastMessage(ChatColor.GOLD + plugin.getKingName() + ChatColor.AQUA + " was removed from the throne!");
			Bukkit.broadcastMessage(ChatColor.GOLD + plugin.getQueenName() + ChatColor.AQUA + " was removed from the throne!");
			
			plugin.downgradeRank(plugin.getKing());
			plugin.downgradeRank(plugin.getQueen());
			
			plugin.clearKing();
			plugin.clearQueen();						
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.RED + "The Revolution FAILED!");			
		}

		imperialPoints = 0;
		rebelPoints = 0;
	}
	
	public boolean enforceRevolution()
	{
		if(!isRevolution)
		{		
			if(System.currentTimeMillis() - timeRevolutionVoteInfo > 15*60*1000)
			{
				timeRevolutionVoteInfo = System.currentTimeMillis();
				
				if(plugin.getOnlineRebels() >= 5)
				{
					String message = plugin.getLanguageManager().getLanguageString(LANGUAGESTRING.INFO_REBEL_VOTE_REVOLUTION, ChatColor.AQUA);

					for(Player player : plugin.getOnlineRebelPlayers())
					{
						player.sendMessage(message);
					}			
				}	
			}
			
			for(Player player : plugin.getServer().getOnlinePlayers())
			{
				enforceNotRevolution(player);
			}

			return false;			
		}
		
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			enforceRevolution(player);
		}
		
		if(System.currentTimeMillis() - timeRevolutionStarted > 10*60*1000)
		{
			plugin.getServer().broadcastMessage(ChatColor.AQUA + "The REVOLUTION is over!");
			
			endRevolution();
			
			return false;
		}
		
		return true;
	}
	
	public void enforceRevolution(Player player)
	{
		if(plugin.isRebel(player.getUniqueId()))
		{
			enforceRevolutionForRebel(player);
			return;
		}

		if(plugin.isRoyal(player.getUniqueId()))
		{
			enforceRevolutionForRoyal(player);			
			return;
		}

		if(plugin.isImperial(player.getUniqueId()))
		{
			enforceRevolutionForImperial(player);			
			return;
		}	
	}
	
	public void enforceNotRevolution(Player player)
	{
		if(plugin.isRebel(player.getUniqueId()))
		{
			enforceNotRevolutionForRebel(player);
			return;
		}

		//if(plugin.isImperial(player.getUniqueId()))
		//{
		//	enforceRevolutionForImperial(player);			
		//}
		
		//if(isRoyal(player))
		//{
		//	enforceRevolutionForRoyal(player);			
		//}
	}

	public void enforceRevolutionForImperial(Player player)
	{
		if(!isWithinRegion(player, battleRegionName))
		{
			/*
			if(!savedInventories.containsKey(player.getUniqueId()))
			{
				ItemStack[] playerInventory = player.getInventory().getContents();
				ItemStack[] inventory = new ItemStack[playerInventory.length];
				
				for(int i = 0; i < playerInventory.length; i++)
				{
				    if(playerInventory[i] != null)
				    {
				    	inventory[i] = playerInventory[i].clone();
				    }
				}
				
				savedInventories.put(player.getUniqueId(), inventory);
			}*/
			
			player.sendMessage(ChatColor.AQUA + "You must kill the rebels to protect the king and queen during this revolution!");
			player.teleport(imperialRevolutionSpawn);
		}
		else
		{
			if(random.nextInt(50)==0)
			{
				player.sendMessage(ChatColor.AQUA + "You must protect the king and queen against the rebels during this revolution!");			
			}
		}		
	}		

	public void enforceRevolutionForRebel(Player player)
	{
		if(!isWithinRegion(player, battleRegionName))
		{
			player.sendMessage(ChatColor.AQUA + "You must kill imperials to overthrow the king and queen during this revolution!");
			player.teleport(rebelRevolutionSpawn);
		}
		else
		{
			if(random.nextInt(50)==0)
			{
				player.sendMessage(ChatColor.AQUA + "You must kill imperials to overthrow the king and queen during this revolution!");			
			}
		}		
	}		

	public void enforceNotRevolutionForRebel(Player player)
	{
		if(isWithinRegion(player, battleRegionName))
		{
			player.sendMessage(ChatColor.DARK_RED + "Rebels are not allowed to enter the Imperial Royal Castle!");
			player.teleport(rebelRevolutionSpawn);
		}
	}		

	public void enforceRevolutionForRoyal(Player player)
	{
		if(!isWithinRegion(player, battleRegionName))
		{
			player.sendMessage(ChatColor.DARK_RED + "You must stand your ground in your castle!");
			player.teleport(imperialRevolutionSpawn);
		}
		else
		{
			if(random.nextInt(50)==0)
			{
				player.sendMessage(ChatColor.DARK_RED + "You must kill rebels keep your royal status during this revolution!");			
			}
		}		
		
		if(player.isFlying())
		{
			player.setFlying(false);
			player.sendMessage(ChatColor.DARK_RED + "Flying is not allowed during a revolution!");			
		}
	}		
	
	public long getMinutesUntilRevolutionEnd()
	{
		return (15 * 60 * 1000 + timeRevolutionStarted - System.currentTimeMillis()) / (60 * 1000);
	}

	public boolean isWithinRegion(Player player, String regionName)
	{
		// Check for worldguard region
		RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
											
		for(ProtectedRegion region : set)
		{
			if(region.getId().equals(regionName))
			{
				return true;
			}						
		}
	    
	    return false;
	}
}