package dogonfire.DramaCraft.tasks;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
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

import dogonfire.DramaCraft.DramaCraft;

public class FireworkTask implements Runnable
{
	private Player player;
	private Random random = new Random();
	private int numberOfRockets = 1;

	public FireworkTask(Player player, int numberOfRockets)
	{
		this.player = player;
		this.numberOfRockets = numberOfRockets;
	}

	public void run()
	{
		int a = (int) this.player.getLocation().getX();
		int y = (int) this.player.getLocation().getY();
		int z = (int) this.player.getLocation().getZ();

		int power = (int) (Math.random() * 2.0D) + 1;
		List<Color> c = new ArrayList<Color>();

		for(int n=0; n<2; n++)
		{
			switch(random.nextInt(19))
			{
				case 0 : c.add(Color.PURPLE); break;
				case 1 : c.add(Color.AQUA);  break;
				case 2 : c.add(Color.BLACK);  break;
				case 3 : c.add(Color.BLUE);  break;
				case 4 : c.add(Color.FUCHSIA);  break;
				case 5 : c.add(Color.GRAY);  break;
				case 6 : c.add(Color.GREEN);  break; 
				case 7 : c.add(Color.LIME);  break; 
				case 8 : c.add(Color.MAROON); break;
				case 9 : c.add(Color.NAVY);  break;
				case 10 : c.add(Color.OLIVE);  break;
				case 11 : c.add(Color.ORANGE);  break;
				case 12 : c.add(Color.PURPLE);  break;
				case 13 : c.add(Color.RED);  break;
				case 14 : c.add(Color.SILVER); break;
				case 15 : c.add(Color.TEAL); break;
				case 16 : c.add(Color.WHITE);  break;
				case 17 : c.add(Color.YELLOW); break;
				default: break;
			}
		}
		
		int type = (int) (5.0D * Math.random()) + 1;

		FireworkEffect.Type effect = FireworkEffect.Type.BALL;
		
		switch (type)
		{
			case 1:	effect = FireworkEffect.Type.BALL; break;
			case 2: effect = FireworkEffect.Type.BALL_LARGE; break;
			case 3: effect = FireworkEffect.Type.BURST; break;
			case 4:	effect = FireworkEffect.Type.CREEPER; break;
			case 5:	effect = FireworkEffect.Type.STAR; break;
			default: break;
		}
		
		for (int x = 0; x <= this.numberOfRockets; x++)
		{
			Firework fireworks = (Firework) this.player.getWorld().spawnEntity(new Location(this.player.getWorld(), a + x, y, z), EntityType.FIREWORK);
			FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
			FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(effect).trail(true).build();

			fireworkmeta.addEffect(e);
			fireworkmeta.setPower(power);
			fireworks.setFireworkMeta(fireworkmeta);
		}
		
		int n = (int) (((float)this.numberOfRockets) / 1.50f);
		
		if (n > 0)
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DramaCraft.instance(), new FireworkTask(this.player, n), 20 + this.random.nextInt(80));
		}
	}
}
