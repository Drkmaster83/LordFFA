package lord.ffa.plugin;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import lord.ffa.additions.InventoryUtils;
import lord.ffa.additions.KitManager;
import lord.ffa.stats.Stats;

public class Events implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (FFA.getInstance().getConfig().getBoolean("Messages.JoinMessage.Enable")) {
			e.setJoinMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.JoinMessage.Message").replace("%player%", e.getPlayer().getName())));
		} else {
			e.setJoinMessage(null);
		}
		Stats.createPlayer(p.getUniqueId().toString()); //MAJOR TODO: Cache system, MySQL is mean.
		resetPlayer(p, true);
	}
	
	public void resetPlayer(Player p, boolean justJoined) {
		if (justJoined && FFA.getSpawnLocation() != null) {
			p.teleport(FFA.getSpawnLocation());
		}
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setHealth(p.getMaxHealth());
		p.setGameMode(GameMode.ADVENTURE);
		p.setLevel(0);
		if(justJoined || !FFA.save.containsKey(p)) {
			p.getInventory().setItem(FFA.getInstance().getConfig().getInt("Kit.slot"), FFA.getKitItem());
			p.updateInventory();
			FFA.scoreboardCreate(p);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (FFA.getInstance().getConfig().getBoolean("Messages.QuitMessage.Enable")) {
			e.setQuitMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.QuitMessage.Message").replace("%player%", e.getPlayer().getName())));
		} else {
			e.setQuitMessage(null);
		}
		FFA.save.remove(e.getPlayer());
		FFA.fixSpam.remove(e.getPlayer());
		FFA.build.remove(e.getPlayer());
		FFA.target.remove(e.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)	{
		Player p = e.getPlayer();
		p.setPlayerTime(1L, false); //what.
		p.setPlayerWeather(WeatherType.CLEAR);
		if (p.getInventory().contains(FFA.getKitItem()) && !FFA.insideSpawn(p.getLocation(), true)) {
			if (FFA.getSpawnLocation() != null) {
				p.teleport(FFA.getSpawnLocation());
			}
			return;
		}

		//Launchpads of some sort
		Location pLoc = p.getLocation();
		Material blockType = pLoc.getWorld().getBlockAt(pLoc).getRelative(0, -1, 0).getType();
		Material footBlockType = pLoc.getWorld().getBlockAt(pLoc).getType();
		if (blockType == Material.REDSTONE_BLOCK && 
				(footBlockType == Material.STONE_PLATE || footBlockType == Material.IRON_PLATE || footBlockType == Material.GOLD_PLATE)) //No wood plate?
		{
			p.setVelocity(pLoc.getDirection().multiply(2));
			p.setVelocity(new Vector(p.getVelocity().getX(), 1.0D, p.getVelocity().getZ()));
			p.playSound(pLoc, Sound.BAT_TAKEOFF, 1.0F, 1.0F);
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		FFA.fixSpam.remove(p);
		FFA.target.remove(p);
		if (FFA.getSpawnLocation() != null) {
			e.setRespawnLocation(FFA.getSpawnLocation());
		}

		resetPlayer(p, false);

		if(FFA.save.containsKey(p)) {
			HashMap<Integer, ItemStack> armor = InventoryUtils.itemsFromString(FFA.save.get(p).split("!")[1]);
			HashMap<Integer, ItemStack> inv = InventoryUtils.itemsFromString(FFA.save.get(p).split("!")[0]);
			int is = 0;
			for (Integer i : armor.keySet()) {
				if (is == 0)
					p.getInventory().setBoots(armor.get(i));
				if (is == 1)
					p.getInventory().setLeggings(armor.get(i));
				if (is == 2)
					p.getInventory().setChestplate(armor.get(i));
				if (is == 3) {
					p.getInventory().setHelmet(armor.get(i));
				}

				is++;
			}

			for (Integer i : inv.keySet()) {
				if (inv.get(i).getType() == Material.FISHING_ROD) {
					ItemMeta item = inv.get(i).getItemMeta();
					item.spigot().setUnbreakable(true);
					inv.get(i).setItemMeta(item);
					p.getInventory().setItem(i.intValue(), inv.get(i));
				} else {
					p.getInventory().setItem(i.intValue(), inv.get(i));
				}
			}
			p.updateInventory();
			FFA.scoreboardCreate(p);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e)	{
		if (!(e.getEntity() instanceof Player)) return;
		if (FFA.insideSpawn(e.getEntity().getLocation())) {
			e.setCancelled(true);
			return;
		}
		Player d = (Player) e.getEntity();
		if (e.getDamager() instanceof Player) {
			Player k = (Player)e.getDamager();
			FFA.target.put(d, k);
		}
		else if (e.getDamager() instanceof Projectile) {
			Projectile pro = (Projectile)e.getDamager();
			Player k = (Player)pro.getShooter();
			FFA.target.put(d, k);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)	{
		ItemStack item = e.getItem();
		Action action = e.getAction();
		if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;
		if(item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return; //Fix NPE here
		if(item.getItemMeta().getDisplayName().equals(FFA.getKitItem().getItemMeta().getDisplayName()) && FFA.insideSpawn(e.getPlayer().getLocation())) {
			Inventory inv = Bukkit.createInventory(null, 9, FFA.getString("&eFFA Kits"));
			for (String kit : KitManager.getSection("Kits").getKeys(false)) {
				inv.setItem(KitManager.getKitSlot(kit), KitManager.getKitInvItem(kit));
			}
			e.getPlayer().openInventory(inv);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)	{
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		Player p = (Player) e.getWhoClicked();
		if (!inv.getName().equals(FFA.getString("&eFFA Kits"))) return;

		e.setCancelled(true);
		if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		for (String kit : KitManager.getSection("Kits").getKeys(false)) {
			if (KitManager.getKitInvItem(kit).getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
				if (p.hasPermission(KitManager.getKitPermission(kit))) {
					p.closeInventory();
					HashMap<Integer, ItemStack> armor = KitManager.getKitContents(kit, "Armor");
					HashMap<Integer, ItemStack> invs = KitManager.getKitContents(kit, "Inventory");
					int is = 0;
					for (Integer i : armor.keySet()) {
						if (is == 0)
							p.getInventory().setBoots(armor.get(i));
						if (is == 1)
							p.getInventory().setLeggings(armor.get(i));
						if (is == 2)
							p.getInventory().setChestplate(armor.get(i));
						if (is == 3) {
							p.getInventory().setHelmet(armor.get(i));
						}

						is++;
					}
					for (Integer i : invs.keySet()) {
						if (invs.get(i).getType() == Material.FISHING_ROD) {
							ItemMeta items = invs.get(i).getItemMeta();
							items.spigot().setUnbreakable(true);
							invs.get(i).setItemMeta(items);
							p.getInventory().setItem(i.intValue(), invs.get(i));
						} else {
							p.getInventory().setItem(i.intValue(), invs.get(i));
						}
					}

					p.getInventory().remove(FFA.getKitItem());
					p.sendMessage(FFA.getString(KitManager.getKitMessage(kit)));

					p.updateInventory();
				} else {
					p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.no-kitpermission")));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player died = e.getEntity();
		e.setDeathMessage(null);
		Player k = null;
		if (died.getKiller() instanceof Player) {
			k = died.getKiller();
		} else if (FFA.target.containsKey(died)) {
			k = (Player)FFA.target.get(died);
		}

		if (k == null) return;
		final Player kill = k;
		DecimalFormat df = new DecimalFormat("####.##");

		k.setLevel(k.getLevel() + 1);
		k.setHealth(k.getMaxHealth());
		String hearts = df.format(k.getHealth() / 2.0D);
		FileConfiguration conf = FFA.getInstance().getConfig();
		died.sendMessage(FFA.getString(conf.getString("Messages.death").replace("%hearts%", hearts).replace("%killer%", k.getName())));
		died.sendMessage(FFA.getString(conf.getString("Messages.points-death").replace("%points%", conf.getInt("Kill-Points")+"")));
		k.sendMessage(FFA.getString(conf.getString("Messages.killer").replace("%hearts%", hearts).replace("%death%", died.getName())));
		k.sendMessage(FFA.getString(conf.getString("Messages.points-killer").replace("%points%", conf.getInt("Kill-Points")+"")));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				Stats.addKills(kill.getUniqueId().toString(), 1);
				Stats.addDeaths(died.getUniqueId().toString(), 1);
				Stats.addPoints(kill.getUniqueId().toString(), conf.getInt("Kill-Points"));
				if (Stats.getPoints(died.getUniqueId().toString()) != 0 && Stats.getPoints(died.getUniqueId().toString()) - 5 <= 0) {
					Stats.addPoints(died.getUniqueId().toString(), conf.getInt("Kill-Points"));
				}
			}
		}.runTaskAsynchronously(FFA.getInstance());

		if (k.getLevel() == 5) {
			k.setLevel(0);
			Bukkit.broadcastMessage(FFA.getString(conf.getString("Messages.killstreak").replace("%player%", k.getName())));
			k.playSound(k.getLocation(), Sound.LEVEL_UP, 10.0F, 20.0F);
		}
		for (ItemStack item : FFA.deathItems()) {
			k.getInventory().addItem(new ItemStack[] { item });
		}
		k.updateInventory();
		FFA.scoreboardCreate(k);
	}

	//Boring/easy logic events

	@EventHandler
	public void onPlayerPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		final Block b = e.getBlock();
		if (b.getType() == Material.FIRE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getInstance(), new Runnable() {
				public void run() {
					if(b.getType() == Material.FIRE) b.setType(Material.AIR);
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
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
			return;
		}
		if (FFA.insideSpawn(e.getEntity().getLocation())) {
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
}