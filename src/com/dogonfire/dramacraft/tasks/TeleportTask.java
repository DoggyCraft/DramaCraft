package com.dogonfire.dramacraft.tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class TeleportTask implements Runnable
{
	private Player player;
	private Player targetPlayer;
	private Random random = new Random();

	public TeleportTask(Player player, Player targetPlayer)
	{
		this.player = player;
		this.targetPlayer = targetPlayer;
	}

	public void run()
	{
		this.player.teleport(targetPlayer);
	}
}
