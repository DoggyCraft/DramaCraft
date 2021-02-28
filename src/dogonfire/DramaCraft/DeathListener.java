package dogonfire.DramaCraft;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class DeathListener implements Listener
{
	private DramaCraft								plugin;
	private HashMap<String, ProtectedItemsSnapshot>	playerItemSnapshots;

	public DeathListener(DramaCraft plugin)
	{
		this.plugin = plugin;
		this.playerItemSnapshots = new HashMap<String, ProtectedItemsSnapshot>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!plugin.getRevolutionManager().isRevolution)
		{
			return;
		}
						
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();

			if(!plugin.isImperial(player.getUniqueId()) && !plugin.isRebel(player.getUniqueId()))
			{
				return;
			}			
			
			// only active in survival mode
			if (!player.getGameMode().equals(GameMode.SURVIVAL))
			{
				return;
			}

			int numProtectedSlots = 36;//this.plugin.getNumProtectedSlotsForPlayer(player);

			ProtectedItemsSnapshot protectedItemsSnapshot = new ProtectedItemsSnapshot(player, event.getDrops(), numProtectedSlots);

			// add the snapshot to load after the player respawns
			playerItemSnapshots.put(player.getName(), protectedItemsSnapshot);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();

		if(!plugin.getRevolutionManager().isRevolution)
		{
			return;
		}
				
		if(!plugin.isImperial(player.getUniqueId()) && !plugin.isRebel(player.getUniqueId()))
		{
			return;
		}
		
		ProtectedItemsSnapshot protectedItemsSnapshot = playerItemSnapshots.get(playerName);

		// if we have an inventory snapshot for this player
		if (protectedItemsSnapshot != null)
		{
			// merge the snapshot into the player's inventory, dropping overflow
			// items at the respawn location
			protectedItemsSnapshot.mergeIntoPlayerInventory(player, event.getRespawnLocation());

			//if (protectedItemsSnapshot.hasNonEmptyItems())
			//{
			//	player.sendMessage("[" + ChatColor.GREEN + "DropProtect" + ChatColor.RESET + "] Inventory restored. Type /dropprotect for details.");
			//}

			// remove the snapshot since it has now been applied
			playerItemSnapshots.remove(playerName);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(plugin.isImperial(event.getPlayer().getUniqueId()))
		{	
			plugin.setImperialLastLogin(event.getPlayer().getUniqueId());
		}

		if(plugin.isRebel(event.getPlayer().getUniqueId()))
		{	
			plugin.setRebelLastLogin(event.getPlayer().getUniqueId());
		}
	}
}