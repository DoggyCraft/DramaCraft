package dogonfire.DramaCraft;

import net.milkbowl.vault.chat.Chat;
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

	private String				pluginName			= "null";
	private Permission 			vaultPermission;
	private Chat 				vaultChat;
	
	public PermissionsManager()
	{
		instance = this;
		
		RegisteredServiceProvider<Permission> permissionProvider = DramaCraft.instance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		
		if(permissionProvider==null)
		{
			DramaCraft.log(ChatColor.RED + "Could not detect Vault permissions plugin.");
			return;
		}
		
		vaultPermission = permissionProvider.getProvider();
		
		RegisteredServiceProvider<Chat> chatProvider = DramaCraft.instance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		
		if(chatProvider==null)
		{
			DramaCraft.log(ChatColor.RED + "Could not detect Vault chat plugin.");
			return;
		}
		
		vaultChat = chatProvider.getProvider();

		dramaCraftGroups.add("rebel");
		dramaCraftGroups.add("imperial");
		dramaCraftGroups.add("king");
		dramaCraftGroups.add("queen");
		dramaCraftGroups.add("ringleader");
		dramaCraftGroups.add("noble");
		dramaCraftGroups.add("innerCircle");
		dramaCraftGroups.add("neutral");
	}

	public void load()
	{

	}

	public String getPermissionPluginName()
	{
		return pluginName;
	}

	static public boolean hasPermission(Player player, String node)
	{
		return instance.vaultPermission.has(player, node);
	}

	static public String getDramaCraftGroup(OfflinePlayer player)
	{
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();

		for(String groupName : instance.vaultPermission.getPlayerGroups(worldName, player))
		{
			if(instance.dramaCraftGroups.contains(groupName))
			{
				return groupName;
			}			
		}
		
		return null;	
	}

	static public void setDramaCraftGroup(OfflinePlayer player, String newGroupName)
	{
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		for(String groupName : instance.dramaCraftGroups)
		{
			instance.vaultPermission.playerRemoveGroup(worldName, player, groupName);
		}

		instance.vaultPermission.playerAddGroup(worldName, player, newGroupName);
	}

	static public boolean isInGroup(OfflinePlayer player, String worldName, String groupName)
	{
		return instance.vaultPermission.playerInGroup(worldName, player, groupName);		
	}

	static public void setRankGroup(OfflinePlayer player, String groupName)
	{
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		instance.vaultPermission.playerAddGroup(worldName, player, groupName);
	}

	public void setPrefix(OfflinePlayer player, String prefix)
	{
		String worldName = Bukkit.getServer().getWorlds().get(0).getName();
		vaultChat.setPlayerPrefix(worldName, player, prefix);
	}

}