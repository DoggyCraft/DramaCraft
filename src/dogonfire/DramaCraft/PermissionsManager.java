package dogonfire.DramaCraft;

import net.milkbowl.vault.permission.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;


public class PermissionsManager
{
	private static PermissionsManager instance;
	private List<String> dramaCraftGroups = new ArrayList<String>();

	public static PermissionsManager instance()
	{
		if (instance == null)
			instance = new PermissionsManager();
		return instance;
	}

	private String				pluginName			= "null";
	private Permission 			vaultPermission;
	
	public PermissionsManager()
	{
		RegisteredServiceProvider<Permission> permissionProvider = DramaCraft.instance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		
		if(permissionProvider==null)
		{
			//Gods.instance().log(ChatColor.RED + "Could not detect Vault plugin.");
			return;
		}
		
		vaultPermission = permissionProvider.getProvider();
		
		dramaCraftGroups.add("Rebel");
		dramaCraftGroups.add("Imperial");
		dramaCraftGroups.add("King");
		dramaCraftGroups.add("Queen");
		dramaCraftGroups.add("Noble");
		dramaCraftGroups.add("InnerCircle");
		dramaCraftGroups.add("Citizen");
	}

	public void load()
	{
		// Nothing to see here
	}

	public String getPermissionPluginName()
	{
		return pluginName;
	}

	public boolean hasPermission(Player player, String node)
	{
		return vaultPermission.has(player, node);
	}

	public String getGroup(String playerName)
	{
		return vaultPermission.getPrimaryGroup(DramaCraft.instance().getServer().getPlayer(playerName));
	}
	
	public String getDramaCraftGroup(OfflinePlayer player)
	{
		for(String groupName : vaultPermission.getPlayerGroups("DoggyCraft", player))
		{
			if(dramaCraftGroups.contains(groupName))
			{
				return groupName;
			}			
		}
		
		return null;	
	}

	public void setDramaCraftGroup(OfflinePlayer player, String newGroupName)
	{
		for(String groupName : vaultPermission.getPlayerGroups("DoggyCraft", player))
		{
			if(dramaCraftGroups.contains(groupName))
			{
				vaultPermission.playerRemoveGroup("DoggyCraft", player, groupName);
			}			
		}

		vaultPermission.playerAddGroup("DoggyCraft", player, newGroupName);
	}

	public boolean isInGroup(OfflinePlayer player, String worldName, String groupName)
	{
		return vaultPermission.playerInGroup("DoggyCraft", player, groupName);//playerInGroup(worldName, playerName, groupName);		
	}

	public void setRankGroup(OfflinePlayer player, String groupName)
	{
		//Player player = DramaCraft.instance().getServer().getPlayer(playerName);
		vaultPermission.playerAddGroup("DoggyCraft", player, groupName);
	}

	public void setPrefix(OfflinePlayer player, String prefix)
	{
		//Player player = DramaCraft.instance().getServer().getPlayer(playerName);
		//vaultPermission.set(player, prefix);
	}

}