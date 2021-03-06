package dogonfire.DramaCraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class TitleManager implements Listener
{
	public static void sendPacket(Player player, Object packet)
	{
		try
		{
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object getFieldInDeclaredNMSClass(String name, String fieldName)
	{
		Class<?> e = getNMSClass(name);
		
		if(e == null)
		{
			return null;
		}

		if(e.getDeclaredClasses().length == 0)
		{
			return null;
		}			
		
		try
		{
			Field field = e.getDeclaredClasses()[0].getField(fieldName);
			try
			{
				return field.get((Object) null);
			}
			catch (IllegalArgumentException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (NoSuchFieldException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (SecurityException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		TitleSendEvent titleSendEvent = new TitleSendEvent(player, title, subtitle);
		Bukkit.getPluginManager().callEvent(titleSendEvent);
		
		if (titleSendEvent.isCancelled())
		{
			return;
		}
		
		try
		{
			Object e;
			Object chatTitle;
			Object chatSubtitle;
			Constructor subtitleConstructor;
			Object titlePacket;
			Object subtitlePacket;

			if (title != null)
			{
				title = ChatColor.translateAlternateColorCodes('&', title);
				title = title.replaceAll("%player%", player.getDisplayName());

				e = getFieldInDeclaredNMSClass("PacketPlayOutTitle", "TIMES");//getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle, fadeIn, stay, fadeOut });
				sendPacket(player, titlePacket);

				e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object) null);
				chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent") });
				titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle });
				sendPacket(player, titlePacket);
			}

			if (subtitle != null)
			{
				subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
				subtitle = subtitle.replaceAll("%player%", player.getDisplayName());

				e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
				chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + title + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);

				e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
				chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object) null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
				subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);
			}
		}
		catch (Exception ex)
		{
			DramaCraft.log("sendTitle:" + ex.getMessage());
		}
	}

	public static void clearTitle(Player player)
	{
		sendTitle(player, 0, 0, 0, "", "");
	}
}
