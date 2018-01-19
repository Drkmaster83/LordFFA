package lord.ffa.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lord.ffa.main.additions.KitManager;
import lord.ffa.main.stats.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.ItemMeta.Spigot;
import org.bukkit.util.Vector;

public class Events implements org.bukkit.event.Listener {
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (FFA.getInstance().getConfig().getBoolean("Messages.JoinMessage.Enable")) {
			e.setJoinMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.JoinMessage.Message")
					.replace("%player%", e.getPlayer().getName())));
		} else {
			e.setJoinMessage(null);
		}
		Stats.createPlayer(p.getUniqueId().toString());
		if (FFA.getSpawnLocation() != null) {
			p.teleport(FFA.getSpawnLocation());
		}
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setHealth(20.0D);
		p.setGameMode(org.bukkit.GameMode.ADVENTURE);
		p.setLevel(0);
		p.getInventory().setItem(FFA.getInstance().getConfig().getInt("Kit.slot"), FFA.getKitItem());
		p.updateInventory();

		FFA.ScoreboardCreate(p);
	}

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		final Block b = e.getBlock();
		if (b.getType() == Material.FIRE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getInstance(), new Runnable() {
				public void run() {
					b.setType(Material.AIR);
				}
			}, 60L);
			return;
		}

		if (!FFA.build.contains(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPlace(BlockBreakEvent e) {
		Player p = e.getPlayer();

		if (!FFA.build.contains(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (FFA.getInstance().getConfig().getBoolean("Messages.QuitMessage.Enable")) {
			e.setQuitMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.QuitMessage.Message")
					.replace("%player%", e.getPlayer().getName())));
		} else {
			e.setQuitMessage(null);
		}
		FFA.Save.remove(e.getPlayer());
		FFA.fixspam.remove(e.getPlayer());
		FFA.build.remove(e.getPlayer());
		FFA.target.remove(e.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		p.setPlayerTime(1L, false);
		p.setPlayerWeather(org.bukkit.WeatherType.CLEAR);
		if ((p.getInventory().contains(FFA.getKitItem())) && (!FFA.inSideSpawn(p.getLocation()))) {
			if (FFA.getSpawnLocation() != null) {
				p.teleport(FFA.getSpawnLocation());
			}
			return;
		}

		Location PlayerLocation = p.getLocation();
		Material Block = PlayerLocation.getWorld().getBlockAt(PlayerLocation).getRelative(0, -1, 0).getType();
		Material Plate = PlayerLocation.getWorld().getBlockAt(PlayerLocation).getType();
		if ((Block == Material.REDSTONE_BLOCK) && ((Plate == Material.STONE_PLATE) || (Plate == Material.IRON_PLATE)
				|| (Plate == Material.GOLD_PLATE))) {
			p.setVelocity(PlayerLocation.getDirection().multiply(2));
			p.setVelocity(new Vector(p.getVelocity().getX(), 1.0D, p.getVelocity().getZ()));
			p.playSound(PlayerLocation, org.bukkit.Sound.BAT_TAKEOFF, 1.0F, 1.0F);
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		FFA.fixspam.remove(e.getPlayer());
		FFA.target.remove(e.getPlayer());
		Player p = e.getPlayer();
		if (FFA.getSpawnLocation() != null) {
			e.setRespawnLocation(FFA.getSpawnLocation());
		}

		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setHealth(20.0D);
		p.setGameMode(org.bukkit.GameMode.ADVENTURE);
		p.setLevel(0);
		if (FFA.Save.containsKey(p)) {
			HashMap<Integer, ItemStack> armor = lord.ffa.main.additions.InventoryUtils
					.ItemsFromString(((String) FFA.Save.get(p)).split("!")[1]);
			HashMap<Integer, ItemStack> inv = lord.ffa.main.additions.InventoryUtils
					.ItemsFromString(((String) FFA.Save.get(p)).split("!")[0]);
			int is = 0;
			for (Integer i : armor.keySet()) {
				if (is == 0)
					p.getInventory().setBoots((ItemStack) armor.get(i));
				if (is == 1)
					p.getInventory().setLeggings((ItemStack) armor.get(i));
				if (is == 2)
					p.getInventory().setChestplate((ItemStack) armor.get(i));
				if (is == 3) {
					p.getInventory().setHelmet((ItemStack) armor.get(i));
				}

				is++;
			}

			for (Integer i : inv.keySet()) {
				if (((ItemStack) inv.get(i)).getType() == Material.FISHING_ROD) {
					ItemMeta item = ((ItemStack) inv.get(i)).getItemMeta();
					item.spigot().setUnbreakable(true);
					((ItemStack) inv.get(i)).setItemMeta(item);
					p.getInventory().setItem(i.intValue(), (ItemStack) inv.get(i));
				} else {
					p.getInventory().setItem(i.intValue(), (ItemStack) inv.get(i));
				}
			}
		} else {
			p.getInventory().setItem(FFA.getInstance().getConfig().getInt("Kit.slot"), FFA.getKitItem());
		}

		p.updateInventory();

		FFA.ScoreboardCreate(p);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL) {
			e.setCancelled(true);
			return;
		}
		if (FFA.inSideSpawn(e.getEntity().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void on(PlayerPickupItemEvent e) {
		if (!FFA.build.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void on(PlayerDropItemEvent e) {
		if (!FFA.build.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (FFA.inSideSpawn(e.getEntity().getLocation())) {
			e.setCancelled(true);
			return;
		}

		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
			Player p = (Player) e.getEntity();
			Player k = (Player) e.getDamager();
			FFA.target.put(p, k);
		} else if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Projectile))) {
			Player p = (Player) e.getEntity();
			Projectile pro = (Projectile) e.getDamager();
			Player k = (Player) pro.getShooter();
			FFA.target.put(p, k);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		Action action = e.getAction();
		if (((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.RIGHT_CLICK_AIR))
				&& ((item != null) || (item.getType() != Material.AIR))
				&& (item.getItemMeta().getDisplayName().equals(FFA.getKitItem().getItemMeta().getDisplayName()))
				&& (FFA.inSideSpawn(e.getPlayer().getLocation()))) {
			Inventory inv = Bukkit.createInventory(null, 9, FFA.getString("&eFFA Kits"));
			for (String kit : KitManager.getSection("Kits").getKeys(false)) {
				inv.setItem(KitManager.getKitSlot(kit).intValue(), KitManager.getKitItem(kit));
			}
			e.getPlayer().openInventory(inv);
		}
	}

	@EventHandler
	public void onPlayerInteract(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getClickedInventory();
		if (inv.getName().equals(FFA.getString("&eFFA Kits"))) {
			e.setCancelled(true);

			if (item.getType() != Material.AIR) {
				for (String kit : KitManager.getSection("Kits").getKeys(false)) {
					if (KitManager.getKitItem(kit).getItemMeta().getDisplayName()
							.equals(item.getItemMeta().getDisplayName())) {
						if (((Player) e.getWhoClicked()).hasPermission(KitManager.getKitPermission(kit))) {
							((Player) e.getWhoClicked()).closeInventory();
							HashMap<Integer, ItemStack> armor = KitManager.getKitContents(kit, "Armor");
							HashMap<Integer, ItemStack> invs = KitManager.getKitContents(kit, "Inventory");
							int is = 0;
							for (Integer i : armor.keySet()) {
								if (is == 0)
									((Player) e.getWhoClicked()).getInventory().setBoots((ItemStack) armor.get(i));
								if (is == 1)
									((Player) e.getWhoClicked()).getInventory().setLeggings((ItemStack) armor.get(i));
								if (is == 2)
									((Player) e.getWhoClicked()).getInventory().setChestplate((ItemStack) armor.get(i));
								if (is == 3) {
									((Player) e.getWhoClicked()).getInventory().setHelmet((ItemStack) armor.get(i));
								}

								is++;
							}
							for (Integer i : invs.keySet()) {
								if (((ItemStack) invs.get(i)).getType() == Material.FISHING_ROD) {
									ItemMeta items = ((ItemStack) invs.get(i)).getItemMeta();
									items.spigot().setUnbreakable(true);
									((ItemStack) invs.get(i)).setItemMeta(items);
									((Player) e.getWhoClicked()).getInventory().setItem(i.intValue(),
											(ItemStack) invs.get(i));
								} else {
									((Player) e.getWhoClicked()).getInventory().setItem(i.intValue(),
											(ItemStack) invs.get(i));
								}
							}

							((Player) e.getWhoClicked()).getInventory().remove(FFA.getKitItem());
							((Player) e.getWhoClicked()).sendMessage(FFA.getString(KitManager.getKitMessage(kit)));

							((Player) e.getWhoClicked()).updateInventory();
						} else {
							((Player) e.getWhoClicked()).sendMessage(FFA
									.getString(FFA.getInstance().getConfig().getString("Messages.no-kitpermission")));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		e.setDeathMessage(null);
		Player k = null;
		if ((p.getKiller() instanceof Player)) {
			k = p.getKiller();
		} else if (FFA.target.containsKey(p)) {
			k = (Player) FFA.target.get(p);
		}

		if (k != null) {
			DecimalFormat df = new DecimalFormat("####.##");

			k.setLevel(k.getLevel() + 1);
			k.setHealth(20.0D);
			Damageable d = k;
			String kd = df.format(d.getHealth() / 2.0D);
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.death")
					.replace("%hearts%", kd).replace("%killer%", k.getName())));
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.points-death")
					.replace("%points%", FFA.getInstance().getConfig().getInt("Kill-Points"))));
			k.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.killer")
					.replace("%hearts%", kd).replace("%death%", p.getName())));
			k.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.points-killer")
					.replace("%points%", FFA.getInstance().getConfig().getInt("Kill-Points"))));
			Stats.addKills(k.getUniqueId().toString(), Integer.valueOf(1));
			Stats.addDeaths(p.getUniqueId().toString(), Integer.valueOf(1));
			Stats.addPoints(k.getUniqueId().toString(),
					Integer.valueOf(FFA.getInstance().getConfig().getInt("Kill-Points")));
			if ((Stats.getPoints(p.getUniqueId().toString()).intValue() != 0)
					&& (Stats.getPoints(p.getUniqueId().toString()).intValue() - 5 <= 0)) {
				Stats.addPoints(p.getUniqueId().toString(),
						Integer.valueOf(FFA.getInstance().getConfig().getInt("Kill-Points")));
			}

			if (k.getLevel() == 5) {
				k.setLevel(0);
				Bukkit.broadcastMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.killstreak")
						.replace("%player%", k.getName())));
				k.playSound(k.getLocation(), org.bukkit.Sound.LEVEL_UP, 10.0F, 20.0F);
			}
			for (ItemStack item : FFA.DeathItems()) {
				k.getInventory().addItem(new ItemStack[] { item });
			}
			k.updateInventory();
			FFA.ScoreboardCreate(k);
		}
	}
}