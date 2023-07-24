package com.dogonfire.dramacraft;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


public class BodyguardManager implements Listener
{
	private DramaCraft					plugin;
	private Random						random	= new Random();
	private HashMap<UUID, IronGolem>	guards	= new HashMap<UUID, IronGolem>();

	public BodyguardManager(DramaCraft plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getEntity() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getEntity();

		if (!hasGuard(player))
		{
			return;
		}

		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

		if (damageEvent.getDamager() instanceof LivingEntity)
		{
			LivingEntity target = (LivingEntity) damageEvent.getDamager();
			guards.get(player.getUniqueId()).setTarget((LivingEntity) target);
		}

	}

	private boolean hasGuard(Player player)
	{
		return guards.containsKey(player.getUniqueId());
	}

	static public void spawnGuard(Player guarded)
	{
		Location loc = guarded.getLocation();
		IronGolem guard = guarded.getWorld().spawn(loc, IronGolem.class);
		guard.isLeashed();
		guard.setLeashHolder(guarded);
		guard.isPlayerCreated();
		EntityDamageEvent target = guarded.getLastDamageCause();

		if (target instanceof LivingEntity)
		{
			guard.setTarget((LivingEntity) target);
		}

		if (guard.getLastDamageCause() == guarded)
		{
			guard.setLastDamage(00.00);
			guard.setLastDamageCause(target);
		}
	}

	public void spawnTerminator(Player player, Player target)
	{
		int radius = 20 + this.random.nextInt(25);
		double r = 2 * Math.PI * this.random.nextInt(101) / 100.0D;

		Location location = new Location(player.getWorld(), player.getLocation().getX() + radius * Math.cos(r), player.getLocation().getY() + 0.0D, player.getLocation().getZ() + radius * Math.sin(r));

		Block block = location.getWorld().getHighestBlockAt(location);
		location = block.getLocation();

		IronGolem guard = player.getWorld().spawn(location, IronGolem.class);
		guard.isPlayerCreated();

		guard.setTarget(target);
		
		guard.setCustomName(player.getName() + "'s terminator");
		guard.setCustomNameVisible(true);

		//if (guard.getLastDamageCause() == spawner)
		//{
		//	guard.setLastDamage(00.00);
		//	guard.setLastDamageCause(target);
		//}
	}
/*
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			return true;
		}
		
		if (!sender.hasPermission("guard.spawn"))
		{
			sender.sendMessage(ChatColor.RED + "You " + ChatColor.RED + "do not have permission to spawn a guard.");
			return true;
		}
		
		p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("guard"))
		{
			if (args.length > 2)
			{
				p.sendMessage(ChatColor.RED + "The Guard's name must be one word.");
				return true;
			}
			if ((args.length == 1) && (!args[0].equalsIgnoreCase("remove")))
			{
				p.sendMessage(ChatColor.RED + "Usage: /Guard Spawn <Name>");
				return true;
			}
			if (args.length == 0)
			{
				p.sendMessage(ChatColor.RED + "Usage: /Guard Spawn <Name>");
				return true;
			}
			String leggings1;
			if ((args.length == 2) && (args[0].equalsIgnoreCase("spawn")))
			{
				String helmet1 = plugin.getConfig().getString("helmet");

				String chestplate1 = plugin.getConfig().getString("chestplate");

				leggings1 = plugin.getConfig().getString("leggings");

				String sword1 = plugin.getConfig().getString("sword");
				String boots1 = plugin.getConfig().getString("boots");
				guard = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
				guardName = args[1];
				String color = guardName.substring(0, 1);
				if (args[1].length() > 1)
				{
					int i = guardName.charAt(1);
				}
				if (color.equalsIgnoreCase("&"))
				{
					char colorType = guardName.charAt(1);
					guard.setCustomName(ChatColor.getByChar(colorType) + guardName.substring(2));
				}
				else
				{
					guard.setCustomName(guardName);
				}
				
				guard.setCustomNameVisible(true);
				sword = new ItemStack(Material.getMaterial(sword1.toUpperCase()));
				sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
				helmet = new ItemStack(Material.getMaterial(helmet1.toUpperCase()));
				helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 50);
				chest = new ItemStack(Material.getMaterial(chestplate1.toUpperCase()));
				chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				chest.addUnsafeEnchantment(Enchantment.DURABILITY, 50);
				legs = new ItemStack(Material.getMaterial(leggings1.toUpperCase()));
				legs.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				legs.addUnsafeEnchantment(Enchantment.DURABILITY, 50);
				boots = new ItemStack(Material.getMaterial(boots1.toUpperCase()));
				boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				boots.addUnsafeEnchantment(Enchantment.DURABILITY, 50);
				guard.getEquipment().setItemInMainHand(sword);
				guard.getEquipment().setHelmet(helmet);
				guard.getEquipment().setChestplate(chest);
				guard.getEquipment().setLeggings(legs);
				guard.getEquipment().setBoots(boots);
				guard.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255));
				guard.setBaby(false);
				guard.setRemoveWhenFarAway(false);
				guard.getEquipment().setItemInMainHandDropChance(0.0F);
				guard.getEquipment().setBootsDropChance(0.0F);
				guard.getEquipment().setChestplateDropChance(0.0F);
				guard.getEquipment().setHelmetDropChance(0.0F);
				guard.getEquipment().setLeggingsDropChance(0.0F);
				guard.setInvulnerable(true);
				map.put(guard.getUniqueId(), guard.getLocation());
				return true;
			}
			if (args[0].equalsIgnoreCase("remove"))
			{
				List<Entity> nearby = p.getNearbyEntities(2.0D, 2.0D, 2.0D);
				for (Entity tmp : nearby)
				{
					if (((tmp instanceof Zombie)) && (tmp.isCustomNameVisible()))
					{
						tmp.remove();
						p.sendMessage(ChatColor.RED + "You removed a guard.");
					}
				}
				a = false;
				return true;
			}
			return true;
		}
		return true;
	}
/*
	@EventHandler
	public static void onHit(EntityDamageByEntityEvent e)
	{
		List<Entity> nearby = p.getNearbyEntities(50.0D, 7.0D, 50.0D);
		for (Entity o : nearby)
		{
			if ((o.isCustomNameVisible()) && ((o instanceof Zombie)))
			{
				z = (Zombie) o;
				if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player)))
				{
					target = true;
					t = (Player) e.getDamager();
					if (t.getLocation().distance((Location) map.get(z.getUniqueId())) > 50.0D)
					{
						return;
					}
					t.sendMessage(ChatColor.DARK_RED + "A Guard Is Coming To Kill You!");
					z.setTarget(t);
					z.removePotionEffect(PotionEffectType.SLOW);
					z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 4));
					t.playSound(t.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
					Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
					{
						public void run()
						{
							if (GuardCommand.t.isDead())
							{
								GuardCommand.z.removePotionEffect(PotionEffectType.SPEED);
								GuardCommand.z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255));
								GuardCommand.z.teleport((Location) GuardCommand.map.get(GuardCommand.z.getUniqueId()));
								GuardCommand.z.setTarget(GuardCommand.z);
								GuardCommand.target = false;
							}
							else
							{
							}
						}
					}, 5L, 5L);
				}
			}
		}
	}
/*
	@EventHandler
	public static void onTarget(EntityTargetEvent e)
	{
		List<Entity> nearby = p.getNearbyEntities(50.0D, 5.0D, 50.0D);
		
		for (Entity o : nearby)
		{
			if ((o.isCustomNameVisible()) && ((o instanceof Zombie)))
			{
				z = (Zombie) o;
				if (target)
				{
					Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
					{
						public void run()
						{
							if (GuardCommand.t.isDead())
							{
								GuardCommand.z.removePotionEffect(PotionEffectType.SPEED);
								GuardCommand.z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255));
								GuardCommand.z.teleport((Location) GuardCommand.map.get(GuardCommand.z.getUniqueId()));
								GuardCommand.z.setTarget(GuardCommand.z);
								GuardCommand.target = false;
							}
						}
					}, 5L, 5L);
				}
				else if (!target)
				{
					e.setCancelled(true);
				}
			}
		}
	}
/*
	@EventHandler
	public static void notMoving(PlayerMoveEvent e)
	{
		if ((target) && (!e.getPlayer().isSprinting()) && (e.getPlayer().equals(t)))
		{
			t.damage(2.0D);
		}
	}
*/
	
/*	
	@EventHandler
	public static void pDamage(PlayerDeathEvent e)
	{
		if (e.equals(t))
		{
			z.removePotionEffect(PotionEffectType.SPEED);
			z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255));
			z.teleport((Location) map.get(z.getUniqueId()));
			z.setHealth(20.0D);
			target = false;
		}
	}
*/
	
/*	
	@EventHandler
	public static void combust(EntityCombustEvent e)
	{
		if (((e.getEntity() instanceof Zombie)) && (e.getEntity().isCustomNameVisible()))
		{
			e.setCancelled(true);
		}
		else
		{
		}
	}
*/
}