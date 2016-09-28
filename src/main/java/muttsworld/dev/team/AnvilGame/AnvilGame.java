/*
 * AnvilGame is a CraftBukkit plugin created by Jasper Holton
 * Do not redistribute this plugin.
 * 
 */

package muttsworld.dev.team.AnvilGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class AnvilGame extends JavaPlugin implements Listener {

	boolean running = false;

	public void broadcastRadius(String str, Location loc, int radius) {
		String world = (String) list.get(13);
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			if (player.getWorld().getName().equals(world) && player.getLocation().distance(loc) < radius) {
				// Player is in radius, do something.
				player.sendMessage(str);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();

		// Bottom point
		int x1 = (Integer) list.get(0);
		int y1 = (Integer) list.get(1);
		int z1 = (Integer) list.get(2);

		// Top point
		int x2 = (Integer) list.get(3);
		int y2 = (Integer) list.get(4);
		int z2 = (Integer) list.get(5);

		if (!isInRect(e.getFrom(), new Location(Bukkit.getWorld((String) list.get(13)), x1, y1, z1),
				new Location(Bukkit.getWorld((String) list.get(13)), x2, y2, z2))) {
			// If there location is not in the area, do nothing.
			Location tploc = e.getTo();

			// Bottom point
			int lx1 = (Integer) list.get(0);
			int ly1 = (Integer) list.get(1);
			int lz1 = (Integer) list.get(2);

			// Top point
			int lx2 = (Integer) list.get(3);
			int ly2 = (Integer) list.get(4);
			int lz2 = (Integer) list.get(5);

			if (lx1 >= lx2) {
				lx1++;
				lx2--;
			} else {
				lx1--;
				lx2++;
			}

			if (lz1 >= lz2) {
				lz1++;
				lz2--;
			} else {
				lz1--;
				lz2++;
			}
			// Extended area
			Location loc1 = new Location(Bukkit.getWorld((String) list.get(13)), lx1, ly1, lz1);
			Location loc2 = new Location(Bukkit.getWorld((String) list.get(13)), lx2, ly2, lz2);

			// If the location is not in the AG area, return
			if (!isInRect(tploc, loc1, loc2)) {
				return;
			}

			/*
			 * double x = (Double) list.get(10); double y = (Double)
			 * list.get(11); double z = (Double) list.get(12);
			 * 
			 * String world = (String) list.get(13); Location l = new
			 * Location(Bukkit.getWorld(world), x, y, z); l.setYaw((float)
			 * ((Integer) list.get(22)));
			 * 
			 * p.teleport(l);
			 */
			if (running) {
				e.setCancelled(true);

				p.sendMessage(prefix + "Don't try to teleport in to the game!");
			}

		}

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		getLogger().info(p.getLocation().toString());

		// Bottom point
		int x1 = (Integer) list.get(0);
		int y1 = (Integer) list.get(1);
		int z1 = (Integer) list.get(2);

		// Top point
		int x2 = (Integer) list.get(3);
		int y2 = (Integer) list.get(4);
		int z2 = (Integer) list.get(5);

		if (isInRect(p, new Location(Bukkit.getWorld((String) list.get(13)), x1, y1, z1),
				new Location(Bukkit.getWorld((String) list.get(13)), x2, y2, z2))) {
			double x = (Double) list.get(10);
			double y = (Double) list.get(11);
			double z = (Double) list.get(12);

			String world = (String) list.get(13);
			Location l = new Location(Bukkit.getWorld(world), x, y, z);
			l.setYaw((float) ((Integer) list.get(22)));

			if (running) {
				p.teleport(l);
				p.sendMessage(prefix + "Don't try to teleport in to the game!");
			}

		}
	}

	// Prevent players from taking damage in the area while the game isnt
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			// Bottom point
			int x1 = (Integer) list.get(0);
			int y1 = (Integer) list.get(1);
			int z1 = (Integer) list.get(2);

			// Top point
			int x2 = (Integer) list.get(3);
			int y2 = (Integer) list.get(4);
			int z2 = (Integer) list.get(5);

			if (isInRect(player, new Location(Bukkit.getWorld((String) list.get(13)), x1, y1, z1),
					new Location(Bukkit.getWorld((String) list.get(13)), x2, y2, z2))) {

				if (event.getCause() == DamageCause.FALLING_BLOCK) {
					player.sendMessage(prefix + "Sorry, you have lost! Teleporting you back to AnvilGame.");

					double x = (Double) list.get(10);
					double y = (Double) list.get(11);
					double z = (Double) list.get(12);

					String world = (String) list.get(13);
					Location l = new Location(Bukkit.getWorld(world), x, y, z);
					l.setYaw((float) ((Integer) list.get(22)));

					player.teleport(l);

				}
				event.setCancelled(true);
			}
		}

	}

	public ArrayList<Player> playersInRect() {
		ArrayList<Player> players = new ArrayList<Player>();

		// Bottom point
		int x1 = (Integer) list.get(0);
		int y1 = (Integer) list.get(1);
		int z1 = (Integer) list.get(2);

		// Top point
		int x2 = (Integer) list.get(3);
		int y2 = (Integer) list.get(4);
		int z2 = (Integer) list.get(5);
		Location loc1 = new Location(Bukkit.getWorld((String) list.get(13)), x1, y1, z1);

		Location loc2 = new Location(Bukkit.getWorld((String) list.get(13)), x2, y2, z2);

		for (Player p : Bukkit.getOnlinePlayers()) {

			if (isInRect(p, loc1, loc2) && !p.isDead()) {
				players.add(p);
			}
		}
		return players;
	}

	public boolean isInRect(Location loc, Location loc1, Location loc2) {

		// if the player is in a different world...
		if (!loc.getWorld().getName().equals((String) list.get(13))) {
			// getLogger().info(prefix + "You are not in the right world!");
			return false;
		}

		double[] dim = new double[2];

		dim[0] = loc1.getBlockX();
		dim[1] = loc2.getBlockX();
		Arrays.sort(dim);
		if (loc.getBlockX() > dim[1] || loc.getBlockX() < dim[0])
			return false;

		dim[0] = loc1.getBlockY();
		dim[1] = loc2.getBlockY();
		Arrays.sort(dim);
		if (loc.getBlockY() > dim[1] || loc.getBlockY() < dim[0])
			return false;

		dim[0] = loc1.getBlockZ();
		dim[1] = loc2.getBlockZ();
		Arrays.sort(dim);
		if (loc.getBlockZ() > dim[1] || loc.getBlockZ() < dim[0])
			return false;

		return true;
	}

	public boolean isInRect(Player player, Location loc1, Location loc2) {

		// if the player is in a different world...
		if (!player.getWorld().getName().equals((String) list.get(13))) {
			// getLogger().info(prefix + "You are not in the right world!");
			return false;
		}

		double[] dim = new double[2];

		dim[0] = loc1.getBlockX();
		dim[1] = loc2.getBlockX();
		Arrays.sort(dim);
		if (player.getLocation().getBlockX() > dim[1] || player.getLocation().getBlockX() < dim[0])
			return false;

		dim[0] = loc1.getBlockY();
		dim[1] = loc2.getBlockY();
		Arrays.sort(dim);
		if (player.getLocation().getBlockY() > dim[1] || player.getLocation().getBlockY() < dim[0])
			return false;

		dim[0] = loc1.getBlockZ();
		dim[1] = loc2.getBlockZ();
		Arrays.sort(dim);
		if (player.getLocation().getBlockZ() > dim[1] || player.getLocation().getBlockZ() < dim[0])
			return false;

		return true;
	}

	public ArrayList<Object[]> sort(ArrayList<Object[]> data) {
		int lenD = data.size();
		int j = 0;
		Object[] tmp = new Object[2];
		for (int i = 0; i < lenD; i++) {
			j = i;
			for (int k = i; k < lenD; k++) {
				if ((Integer) data.get(j)[1] > (Integer) data.get(k)[1]) {
					j = k;
				}
			}
			tmp = data.get(i);
			data.set(i, data.get(j));
			data.set(j, tmp);
		}

		Collections.reverse(data);
		return data;
	}

	// run in async thread
	@SuppressWarnings("deprecation")
	public void startGame() {

		// Bottom point
		int x1 = (Integer) list.get(0);
		int y1 = (Integer) list.get(1);
		int z1 = (Integer) list.get(2);

		// Top point
		int x2 = (Integer) list.get(3);
		int y2 = (Integer) list.get(4);
		int z2 = (Integer) list.get(5);

		// Bottom point
		int lx1 = (Integer) list.get(0);
		int ly1 = (Integer) list.get(1);
		int lz1 = (Integer) list.get(2);

		// Top point
		int lx2 = (Integer) list.get(3);
		int ly2 = (Integer) list.get(4);
		int lz2 = (Integer) list.get(5);

		if (lx1 >= lx2) {
			lx1++;
			lx2--;
		} else {
			lx1--;
			lx2++;
		}

		if (lz1 >= lz2) {
			lz1++;
			lz2--;
		} else {
			lz1--;
			lz2++;
		}

		Location loc1 = new Location(Bukkit.getWorld((String) list.get(13)), lx1, ly1, lz1);
		Location loc2 = new Location(Bukkit.getWorld((String) list.get(13)), lx2, ly2, lz2);
		// getLogger().info(loc1.toString());
		// getLogger().info(loc2.toString());

		final ArrayList<Object[]> doors = (ArrayList<Object[]>) list.get(15);
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Object[] door : doors) {
				Location l = new Location(Bukkit.getWorld((String) list.get(13)), (Integer) door[0], (Integer) door[1],
						(Integer) door[2]);
				// if(l.getX() >= 0) l.setX(l.getX() + .5);
				// else l.setX(l.getX() - .5);

				// if(l.getZ() >= 0) l.setZ(l.getZ() + .5);
				// else l.setZ(l.getZ() - .5);

				// Same world
				if (p.getWorld().getName().equals((String) list.get(13))) {
					if (p.getLocation().distance(l) < 2 && isInRect(p, loc1, loc2)) {
						double x = (Double) list.get(10);
						double y = (Double) list.get(11);
						double z = (Double) list.get(12);

						String world = (String) list.get(13);
						Location loc = new Location(Bukkit.getWorld(world), x, y, z);
						loc.setYaw((float) ((Integer) list.get(22)));

						p.teleport(loc);
						p.sendMessage(
								prefix + "Do not stand in doors! You have been teleported back to the AnvilGame.");
					}
				}

			}

		}
		// Material.
		// Material.sku

		// Close the doors

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
			public void run() {
				for (Object[] door : doors) {
					Block b = new Location(Bukkit.getWorld((String) list.get(13)), (Integer) door[0], (Integer) door[1],
							(Integer) door[2]).getBlock();
					b.setType(Material.IRON_FENCE);
				}
			}
		});

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		running = true;

		// Say that we are starting
		broadcastRadius(prefix + "Starting AnvilGame! Good luck!", new Location(Bukkit.getWorld((String) list.get(13)),
				(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (x1 > x2) {
			// swap
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}

		if (y1 > y2) {
			// swap
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}

		if (z1 > z2) {
			// swap
			int temp = z1;
			z1 = z2;
			z2 = temp;
		}

		final int nx1 = x1;
		final int ny1 = y1;
		final int nz1 = z1;

		final int nx2 = x2;
		final int ny2 = y2;
		final int nz2 = z2;

		final double frequency = (Double) list.get(6);
		final double frequencymod = (Double) list.get(7);

		final int wait = (Integer) list.get(8);
		final int waitmod = (Integer) list.get(9);

		final String world = (String) list.get(13);

		double ifrequency = frequency;
		int iwait = wait;

		running = true;
		while (running) {

			final double ifrequency2 = ifrequency;
			// Place anvils
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
				public void run() {
					for (int ix = nx1; ix <= nx2; ix++) {
						for (int iz = nz1; iz <= nz2; iz++) {
							Location loc = new Location(Bukkit.getWorld(world), ix, ny2, iz);
							Block b = loc.getBlock();
							if (Math.random() < ifrequency2) {
								b.setType(Material.ANVIL);
							}
						}
					}
				}
			});

			if ((ifrequency + frequencymod > 0) && (ifrequency + frequencymod < 1)) {
				ifrequency += frequencymod;
			}
			if (iwait + waitmod > 300) {
				iwait += waitmod;
			}

			try {
				Thread.sleep(iwait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Check for winners
			ArrayList<Player> currentPlayers = playersInRect();

			// If no one is left...
			if (currentPlayers.size() == 1) {
				final Location spawnloc = new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
						(Double) list.get(11), (Double) list.get(12));
				broadcastRadius(prefix + ChatColor.GREEN + currentPlayers.get(0).getDisplayName() + ChatColor.WHITE + " has won AnvilGame!",
						spawnloc, (Integer) list.get(14));

				// Register win
				final Player p = currentPlayers.get(0);

				ArrayList<Object[]> top = (ArrayList<Object[]>) list.get(20);

				boolean found = false;
				for (int i = 0; i < top.size(); i++) {
					if (((String) top.get(i)[0]).equals(p.getName())) {
						top.get(i)[1] = ((Integer) top.get(i)[1]) + 1;
						found = true;
					}
				}

				if (!found) {
					top.add(new Object[] { p.getName(), 1 });
				}

				running = false;
				// Sort top
				top = sort(top);
				list.set(20, top);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				p.teleport(spawnloc);

				p.sendMessage(prefix + "Congratulations, " + ChatColor.GOLD + p.getDisplayName() + ChatColor.WHITE + "! Your prize is "
						+ rewardPlayer(p) + ".");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				broadcastRadius(
						prefix + "Resetting in:", new Location(Bukkit.getWorld((String) list.get(13)),
								(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)),
						(Integer) list.get(14));

				broadcastRadius(prefix + "3", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				broadcastRadius(prefix + "2", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				broadcastRadius(prefix + "1", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				broadcastRadius(
						prefix + "Area reset.", new Location(Bukkit.getWorld((String) list.get(13)),
								(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)),
						(Integer) list.get(14));

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
					public void run() {

						for (int iy = ny1; iy <= ny2; iy++) {
							for (int ix = nx1; ix <= nx2; ix++) {
								for (int iz = nz1; iz <= nz2; iz++) {
									Location loc = new Location(Bukkit.getWorld(world), ix, iy, iz);
									Block b = loc.getBlock();
									b.setType(Material.AIR);
								}
							}
						}
						for (Object[] door : doors) {
							Block b = new Location(Bukkit.getWorld(world), (Integer) door[0], (Integer) door[1],
									(Integer) door[2]).getBlock();
							b.setType(Material.AIR);
						}
					}
				});

				// Stop running
				running = false;

			}

			// If no one is left...
			if (currentPlayers.size() == 0) {
				broadcastRadius(
						prefix + "No one has won!", new Location(Bukkit.getWorld((String) list.get(13)),
								(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)),
						(Integer) list.get(14));

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				broadcastRadius(
						prefix + "Resetting in:", new Location(Bukkit.getWorld((String) list.get(13)),
								(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)),
						(Integer) list.get(14));

				broadcastRadius(prefix + "3", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				broadcastRadius(prefix + "2", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				broadcastRadius(prefix + "1", new Location(Bukkit.getWorld((String) list.get(13)),
						(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)), (Integer) list.get(14));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				broadcastRadius(
						prefix + "Area reset.", new Location(Bukkit.getWorld((String) list.get(13)),
								(Double) list.get(10), (Double) list.get(11), (Double) list.get(12)),
						(Integer) list.get(14));

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
					public void run() {
						for (int iy = ny1; iy <= ny2; iy++) {
							for (int ix = nx1; ix <= nx2; ix++) {
								for (int iz = nz1; iz <= nz2; iz++) {
									Location loc = new Location(Bukkit.getWorld(world), ix, iy, iz);
									Block b = loc.getBlock();
									b.setType(Material.AIR);
								}
							}
						}
						for (Object[] door : doors) {
							Block b = new Location(Bukkit.getWorld(world), (Integer) door[0], (Integer) door[1],
									(Integer) door[2]).getBlock();
							b.setType(Material.AIR);
						}
					}
				});

				// Stop running
				running = false;

			}

		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("anvilgame")) {

			if ((Boolean) list.get(18)) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					double x = (Double) list.get(10);
					double y = (Double) list.get(11);
					double z = (Double) list.get(12);

					String world = (String) list.get(13);
					Location l = new Location(Bukkit.getWorld(world), x, y, z);
					l.setYaw((float) ((Integer) list.get(22)));

					player.teleport(l);

					sender.sendMessage(prefix + "You have been teleported to AnvilGame!");

					return true;
				} else {
					sender.sendMessage(prefix + "You must be a player!");
					return true;
				}
			} else
				sender.sendMessage(prefix + "/anvilgame has not been set yet!");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("agleaderboard")) {
			ArrayList<Object[]> top = (ArrayList<Object[]>) list.get(20);

			sender.sendMessage(ChatColor.WHITE + "--------" + ChatColor.GOLD + "AnvilGame Leaderboard" + ChatColor.WHITE + "--------");
			for (int i = 0; i < top.size() && i < 10; i++) {
				sender.sendMessage((i + 1) + ". " + ChatColor.GOLD + top.get(i)[0] + ChatColor.WHITE + ": " + top.get(i)[1] + " wins.");
			}
			sender.sendMessage(ChatColor.WHITE + "--------" +  ChatColor.GOLD + "AnvilGame Leaderboard" + ChatColor.WHITE + "--------");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("agdev")) {

			sender.sendMessage(ChatColor.WHITE + "---------------------" +  ChatColor.GOLD + "Information" + ChatColor.WHITE + "---------------------");
			sender.sendMessage("AnvilGame was created by jcholton, AKA wenikalla");
			sender.sendMessage("The project was started on April 15, 2014.");
			sender.sendMessage("The current version is " + getDescription().getVersion());
			sender.sendMessage("For bukkit version " + getServer().getBukkitVersion());
			sender.sendMessage("If you have any questions, problems, or suggestions,");
			sender.sendMessage("Please email me at wenikalla@gmail.com");
			sender.sendMessage("Special thanks to wiwoh for the testing help.");
			sender.sendMessage(ChatColor.WHITE + "---------------------" + ChatColor.GOLD + "Information" + ChatColor.WHITE + "---------------------");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("aginfo")) {
			sender.sendMessage(ChatColor.WHITE + "--------" +  ChatColor.GOLD + "AnvilGame Info" + ChatColor.WHITE + "--------");
			// sender.sendMessage("");
			sender.sendMessage(
					"Bottom point is located at (" + list.get(0) + "," + list.get(1) + "," + list.get(2) + ")");
			sender.sendMessage("Top point is located at (" + list.get(3) + "," + list.get(4) + "," + list.get(5) + ")");
			sender.sendMessage("Frequency is " + list.get(6));
			sender.sendMessage("Frequency mod is " + list.get(7));
			sender.sendMessage("Delay is " + list.get(8));
			sender.sendMessage("Delay mod is " + list.get(9));
			sender.sendMessage(
					"/anvilgame is located at (" + list.get(10) + "," + list.get(11) + "," + list.get(12) + ")");
			sender.sendMessage("World is " + list.get(13));
			sender.sendMessage("Broadcast radius is " + list.get(14));
			sender.sendMessage("Minimum players is " + list.get(19));
			sender.sendMessage("Cycle time is " + timeString((Long) list.get(23)));
			sender.sendMessage("Games per cycle is " + list.get(24));

			sender.sendMessage("Warmup time is " + timeString((Long) list.get(25)));
			sender.sendMessage("Games in this cycle is " + currentGamesInCycle);
			ArrayList<ItemStack> rewards = (ArrayList<ItemStack>) list.get(26);
			String rewardsString = "";
			for (int i = 0; i < rewards.size(); i++) {
				ItemStack reward = rewards.get(i);
				if (i != rewards.size() - 1) {
					rewardsString += reward.getAmount() + " " + reward.getType().toString().toLowerCase() + ", ";

				} else {
					rewardsString += "and " + reward.getAmount() + " " + reward.getType().toString().toLowerCase();

				}
			}
			sender.sendMessage("Rewards are " + rewardsString);
			sender.sendMessage("You get " + list.get(27) + " random rewards per game.");

			ArrayList<String> doors = new ArrayList<String>();
			for (Object[] door : (ArrayList<Object[]>) list.get(15)) {
				doors.add("(" + door[0] + "," + door[1] + "," + door[2] + ")");
			}
			sender.sendMessage("Doors are " + doors.toString());

			sender.sendMessage(ChatColor.WHITE + "--------" +  ChatColor.GOLD + "AnvilGame Info" + ChatColor.WHITE + "--------");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("agset")) {
			// eg /agset reward remove index
			// eg /agset reward add <reward ID> <number>
			if (args.length == 0) {
				sender.sendMessage(prefix
						+ "Usage: /agset <delay, frequency, spawn, p1, p2, door, broadcastradius, frequencymod, delaymod, minplayers, leaderboardreset, cycle, gamespercycle, warmuptime, reward, numrewards>");
				return true;
			}
			if (args[0].equalsIgnoreCase("reward")) {

				if (args.length == 4) {
					if (args[1].equalsIgnoreCase("add")) {
						try {
							// Get the array of rewards
							ArrayList<ItemStack> rewards = (ArrayList<ItemStack>) list.get(26);
							// Material.

							Material m = Material.matchMaterial(args[2]);

							if (m == null) {
								sender.sendMessage(prefix + "Invalid material!");
								return true;
							}
							ItemStack reward = new ItemStack(m, Integer.parseInt(args[args.length - 1]));

							rewards.add(reward);

							list.set(26, rewards);

							sender.sendMessage(prefix + "Added " + reward.getAmount() + " "
									+ reward.getType().toString().toLowerCase() + " to rewards.");
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							sender.sendMessage(prefix + "Invalid arguemnts!");
							return true;
						}
					}
				} else if (args.length == 3) {

					if (args[1].equalsIgnoreCase("remove")) {
						try {
							// Get the array of rewards
							ArrayList<ItemStack> rewards = (ArrayList<ItemStack>) list.get(26);
							ItemStack reward = rewards.get(Integer.parseInt(args[2]) - 1);
							rewards.remove(Integer.parseInt(args[2]) - 1);
							list.set(26, rewards);
							sender.sendMessage(prefix + "Removed " + reward.getAmount() + " "
									+ reward.getType().toString().toLowerCase() + " from rewards.");
							return true;
						} catch (Exception e) {
							sender.sendMessage(prefix + "Invalid arguemnts!");
							return true;
						}
					}
				} else {
					sender.sendMessage(prefix + "Invalid arguemnts!");
					return true;
				}

			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("delay")) {
					try {
						if (Integer.parseInt(args[1]) >= 300) {
							list.set(8, Integer.parseInt(args[1]));
							sender.sendMessage(prefix + "Delay set to " + Integer.parseInt(args[1]));
						} else
							sender.sendMessage(prefix + "Delay must be at least 300!");
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("numrewards")) {
					try {
						list.set(27, Integer.parseInt(args[1]));
						sender.sendMessage(prefix + "Number of rewards set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("frequency")) {
					try {
						if (Double.parseDouble(args[1]) > 0 && Double.parseDouble(args[1]) < 1) {
							list.set(6, Double.parseDouble(args[1]));
							sender.sendMessage(prefix + "Frequency set to " + Double.parseDouble(args[1]));
						} else
							sender.sendMessage(prefix + "Frequency must be between 0 and 1");
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}

				}

				if (args[0].equalsIgnoreCase("broadcastradius")) {
					try {
						list.set(14, Integer.parseInt(args[1]));
						sender.sendMessage(prefix + "Broadcast radius set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}

				}

				if (args[0].equalsIgnoreCase("frequencymod")) {
					try {
						if (Double.parseDouble(args[1]) > -1 && Double.parseDouble(args[1]) < 1) {
							list.set(7, Double.parseDouble(args[1]));
							sender.sendMessage(prefix + "Frequency modifier set to " + Double.parseDouble(args[1]));
						} else
							sender.sendMessage(prefix + "Frequency modifier must be between -1 and 1");
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}

				}

				if (args[0].equalsIgnoreCase("delaymod")) {
					try {
						list.set(9, Integer.parseInt(args[1]));
						sender.sendMessage(prefix + "Delay modifier set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}

				}

				if (args[0].equalsIgnoreCase("minplayers")) {
					try {
						list.set(19, Integer.parseInt(args[1]));
						sender.sendMessage(prefix + "Minimum players set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("gamespercycle")) {
					try {
						list.set(24, Integer.parseInt(args[1]));
						sender.sendMessage(prefix + "Games per cycle set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("cycle")) {
					try {
						list.set(23, Long.parseLong(args[1]));
						sender.sendMessage(prefix + "Cycle time set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("warmuptime")) {
					try {
						list.set(25, Long.parseLong(args[1]));
						sender.sendMessage(prefix + "Warmup time set to " + Integer.parseInt(args[1]));
						return true;
					} catch (Exception e) {
						sender.sendMessage(prefix + "Invalid arguemnts!");
						return true;
					}
				}

				sender.sendMessage(prefix + "Invalid arguments!");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("leaderboardreset")) {
					sender.sendMessage(prefix + "Reset the leaderboard.");
					list.set(20, new ArrayList<Object[]>());
					return true;
				}

				if (sender instanceof Player) {
					Player player = (Player) sender;

					if (args[0].equals("p1")) {
						list.set(0, player.getLocation().getBlockX());
						list.set(1, player.getLocation().getBlockY());
						list.set(2, player.getLocation().getBlockZ());
						list.set(16, true);
						sender.sendMessage(prefix + "AnvilGame point 1 set to (" + list.get(0) + "," + list.get(1) + ","
								+ list.get(2) + ")");
						return true;
					}

					if (args[0].equals("p2")) {
						list.set(3, player.getLocation().getBlockX());
						list.set(4, player.getLocation().getBlockY());
						list.set(5, player.getLocation().getBlockZ());
						list.set(17, true);
						sender.sendMessage(prefix + "AnvilGame point 2 set to (" + list.get(3) + "," + list.get(4) + ","
								+ list.get(5) + ")");
						return true;
					}

					if (args[0].equals("spawn")) {
						list.set(10, player.getLocation().getX());
						list.set(11, player.getLocation().getY());
						list.set(12, player.getLocation().getZ());
						list.set(13, player.getWorld().getName());
						list.set(18, true);
						int dir = (int) ((player.getLocation().getYaw() - 90) % 360);
						if (dir < 0)
							dir += 360;

						dir += 90;
						if (dir > 360)
							dir -= 360;
						list.set(22, dir);
						sender.sendMessage(prefix + "AnvilGame spawn set to (" + list.get(10) + "," + list.get(11) + ","
								+ list.get(12) + ")");
						return true;
					}

					if (args[0].equals("door")) {
						// Get the array of doors
						ArrayList<Object[]> doors = (ArrayList<Object[]>) list.get(15);

						Location loc = player.getLocation();
						boolean removed = false;
						for (int i = 0; i < doors.size(); i++) {
							Object[] door = doors.get(i);

							// If this location is a door, make it not a door.
							if (loc.getBlockX() == (Integer) door[0] && loc.getBlockY() == (Integer) door[1]
									&& loc.getBlockZ() == (Integer) door[2]) {
								doors.remove(i);
								removed = true;
								break;
							}

						}
						// Otherwise, add a door here
						if (!removed) {
							doors.add(new Object[] { loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() });
						}

						sender.sendMessage(prefix + "Toggled door block at (" + player.getLocation().getBlockX() + ","
								+ player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ() + ")");
						return true;
					}

					sender.sendMessage(prefix + "Invalid arguments!");
					return true;

				} else {
					sender.sendMessage(prefix + "You must be a player!");
					return true;
				}

			} else {
				sender.sendMessage(prefix
						+ "Usage: /agset <delay, frequency, spawn, p1, p2, door, broadcastradius, frequencymod, delaymod, minplayers, leaderboardreset, cycle, gamespercycle, warmuptime, reward, numrewards>");
			}
			return true;
		}

		return false;
	}

	private ArrayList<Object> list = new ArrayList<Object>();

	boolean pluginRunning = false;

	int currentGamesInCycle = 0;

	@Override
	public void onEnable() {

		pluginRunning = true;

		getLogger().info(prefix + "is now enabled!");
		getServer().getPluginManager().registerEvents(this, this);

		// Register defaults
		ArrayList<Object> defaults = new ArrayList<Object>();
		// Defaults here
		// Bottom point
		int x1 = 0;
		int y1 = 0;
		int z1 = 0;

		// Top point
		int x2 = 0;
		int y2 = 0;
		int z2 = 0;

		double frequency = .5;
		double frequencymod = 0;

		int wait = 4000;
		int waitmod = 0;

		double xspawn = 0;
		double yspawn = 0;
		double zspawn = 0;

		String world = "world";

		ArrayList<Object[]> doors = new ArrayList<Object[]>();

		int broadcastRadius = 100;

		boolean p1set = false;
		boolean p2set = false;
		boolean spawnset = false;

		int minplayers = 2;

		ArrayList<Object[]> leaderboard = new ArrayList<Object[]>();

		String tellallmessage = "Depracated!";

		int direction = 0;

		long cycle = 0;

		int gamesPerCycle = 1;

		long warmupTime = 20000;

		ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();
		rewards.add(new ItemStack(Material.DIAMOND, 5));
		rewards.add(new ItemStack(Material.CAKE, 1));

		int numRewards = 2;

		defaults.add(x1);
		defaults.add(y1);
		defaults.add(z1);

		defaults.add(x2);
		defaults.add(y2);
		defaults.add(z2);

		defaults.add(frequency);
		defaults.add(frequencymod); // 7
		defaults.add(wait); // 8
		defaults.add(waitmod); // 9

		defaults.add(xspawn); // 10
		defaults.add(yspawn); // 11
		defaults.add(zspawn); // 12

		defaults.add(world); // 13

		defaults.add(broadcastRadius); // 14

		defaults.add(doors); // 15

		defaults.add(p1set); // 16
		defaults.add(p2set); // 17
		defaults.add(spawnset); // 18
		defaults.add(minplayers); // 19

		defaults.add(leaderboard); // 20

		defaults.add(tellallmessage); // 21

		defaults.add(direction); // 22

		defaults.add(cycle); // 23
		defaults.add(gamesPerCycle); // 24

		defaults.add(warmupTime); // 25

		ArrayList<Map<String, Object>> serializedRewards = new ArrayList<Map<String, Object>>();
		for (ItemStack is : rewards) {
			serializedRewards.add(is.serialize());
		}
		defaults.add(serializedRewards); // 26
		defaults.add(numRewards); // 27

		// Put defaults in a string
		String defaultsString = "";
		try {
			defaultsString = ObjectString.objectToString(defaults);
		} catch (Exception e) {
			getLogger().info(prefix + "CRITICAL ERROR: CORRUPTED DATA. PLEASE DELETE ANVILGAME DATA FILES.");
			e.printStackTrace();
		}

		// Add the defaults
		getConfig().addDefault("com.wenikalla.anvilgame", defaultsString);

		// Setup and save
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Load up list
		String loadedString = getConfig().getString("com.wenikalla.anvilgame");
		try {
			list = (ArrayList<Object>) ObjectString.objectFromString(loadedString);

			// Serialize and save rewards
			ArrayList<ItemStack> deserializedRewards = new ArrayList<ItemStack>();
			ArrayList<Map<String, Object>> iserializedRewards = (ArrayList<Map<String, Object>>) list.get(26);
			for (Map<String, Object> isr : iserializedRewards) {
				deserializedRewards.add(ItemStack.deserialize(isr));
			}
			list.set(26, deserializedRewards);
		} catch (Exception e) {
			getLogger().info(prefix + "CRITICAL ERROR: CORRUPTED DATA. PLEASE DELETE ANVILGAME DATA FILES.");
			e.printStackTrace();
		}
		if (list.size() < defaults.size()) {
			for (int i = list.size(); i < defaults.size(); i++) {
				list.add(defaults.get(i));
			}
		}

		// Manage cycles
		new Thread(new BukkitRunnable() {
			public void run() {
				while (pluginRunning) {

					// Reset the number of games in the cycle
					currentGamesInCycle = 0;
					try {
						long cycle = (Long) list.get(23);
						if (cycle == 0)
							cycle = 1000;
						// Wait 1 cycle
						int curr = 0;
						while (curr < cycle / 1000) {
							cycle = (Long) list.get(23);
							curr++;
							Thread.sleep(1000);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		// Start checking for players to auto-start games
		new Thread(new BukkitRunnable() {
			public void run() {
				try {
					int max = 30;
					int current = 0;
					while (pluginRunning) {
						// If we have enough players to start the game...
						if ((currentGamesInCycle < (Integer) list.get(24))) {

							//getLogger().info("Checking for players...");

							if ((playersInRect().size() >= (Integer) list.get(19))
									&& ((Boolean) list.get(18) && (Boolean) list.get(17) && (Boolean) list.get(16))) {

								long time = ((Long) list.get(25) / 4);
								Bukkit.getServer().broadcastMessage(
										prefix + "The AnvilGame warmup has started! " +  ChatColor.GOLD + "/AnvilGame" + ChatColor.WHITE + " to join!");
								broadcastRadius(prefix + "Starting in " + timeString(time * 4),
										new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
												(Double) list.get(11), (Double) list.get(12)),
										(Integer) list.get(14));
								try {
									Thread.sleep(time);
								} catch (Exception e) {
									e.printStackTrace();
								}
								broadcastRadius(prefix + "Starting in " + timeString(time * 3),
										new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
												(Double) list.get(11), (Double) list.get(12)),
										(Integer) list.get(14));

								try {
									Thread.sleep(time);
								} catch (Exception e) {
									e.printStackTrace();
								}
								broadcastRadius(prefix + "Starting in " + timeString(time * 2),
										new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
												(Double) list.get(11), (Double) list.get(12)),
										(Integer) list.get(14));

								try {
									Thread.sleep(time);
								} catch (Exception e) {
									e.printStackTrace();
								}
								broadcastRadius(prefix + "Starting in " + timeString(time),
										new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
												(Double) list.get(11), (Double) list.get(12)),
										(Integer) list.get(14));

								try {
									Thread.sleep(time);
								} catch (Exception e) {
									e.printStackTrace();
								}

								startGame();
								currentGamesInCycle++;

							}
						}

						if (current == max && ((currentGamesInCycle < (Integer) list.get(24)))) {

							if ((Boolean) list.get(18) && (Boolean) list.get(17) && (Boolean) list.get(16)) {
								broadcastRadius(
										prefix + "The game warmup will start as soon as " + list.get(19)
												+ " players join!",
										new Location(Bukkit.getWorld((String) list.get(13)), (Double) list.get(10),
												(Double) list.get(11), (Double) list.get(12)),
										(Integer) list.get(14));
							}

							current = 0;
						}

						current += 3;
						try {
							Thread.sleep(6000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					getLogger().info("An error has been encountered!");
					e.printStackTrace();
				}
			}
		}).start();
		if (!(Boolean) list.get(16) || !(Boolean) list.get(17) || !(Boolean) list.get(18)) {
			getLogger().info(prefix + "AnvilGame has not been set up yet! Please use /agset to set it up.");
		}

		// Color test

		/*
		 * new Thread(new BukkitRunnable() { public void run() { try {
		 * Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } getServer() .broadcastMessage( prefix +
		 * "The AnvilGame warmup has started! Come to &4&k/anvilgame&r to join!"
		 * ); /* getServer().broadcastMessage(prefix + "Color check:"); for (int
		 * i = 0; i < 20; i++) { getServer().broadcastMessage(i + ": &" + i +
		 * "color"); }
		 * 
		 * getServer().broadcastMessage(prefix + "Color check:"); for (int i =
		 * 0; i < 500; i++) { getServer().broadcastMessage((char)i + ": &" +
		 * (char)i + "color"); } } }).start();
		 */

	}

	private String prefix = ChatColor.RED + "[AnvilGame] " + ChatColor.WHITE;

	@Override
	public void onDisable() {
		pluginRunning = false;
		running = false;
		getLogger().info(prefix + "is now disabled.");

		// Save the list
		try {
			// Serialize and save rewards
			ArrayList<ItemStack> rewards = (ArrayList<ItemStack>) list.get(26);
			ArrayList<Map<String, Object>> serializedRewards = new ArrayList<Map<String, Object>>();
			for (ItemStack is : rewards) {
				serializedRewards.add(is.serialize());
			}
			list.set(26, serializedRewards);
			getConfig().set("com.wenikalla.anvilgame", ObjectString.objectToString(list));
		} catch (Exception e) {
			getLogger().info(prefix + "CRITICAL ERROR: CORRUPTED DATA. PLEASE DELETE ANVILGAME DATA FILES.");
			e.printStackTrace();
		}
		saveConfig();
	}

	// Returns a string containing info about the reward (eg. "1 diamond")
	public String rewardPlayer(Player p) {

		ArrayList<ItemStack> rewards = (ArrayList<ItemStack>) list.get(26);
		int numRewards = (Integer) list.get(27);

		String result = "";
		Random r = new Random();
		for (int i = 0; i < numRewards; i++) {
			ItemStack reward = rewards.get(r.nextInt(rewards.size()));
			p.getInventory().addItem(reward);
			if (i != numRewards - 1)
				result += reward.getAmount() + " " + reward.getType().toString().toLowerCase() + ", ";
			else
				result += "and " + reward.getAmount() + " " + reward.getType().toString().toLowerCase();
		}

		return result;
	}

	public String timeString(long millis) {
		return String.format("%d minutes, %d seconds", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}
