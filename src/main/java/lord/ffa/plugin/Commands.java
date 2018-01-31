package lord.ffa.plugin;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lord.ffa.additions.InventoryUtils;
import lord.ffa.additions.KitManager;
import lord.ffa.additions.MessageUtils;
import lord.ffa.stats.Stats;

public class Commands implements CommandExecutor {
	private FFA plugin;
	
	public Commands(FFA plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;

		final Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("FreeForAll")) {
			if (!p.isOp()) {
				MessageUtils.sendMessage(p, "no-command-permission");
				return true;
			}
			if (args.length == 0) {
				MessageUtils.sendMessage(p, "base-command-help");
				return true;
			}

			if (args[0].equalsIgnoreCase("setspawn")) {
				if (args.length != 1) {
					MessageUtils.sendMessage(p, "usage-setspawn");
					return true;
				}
				plugin.getConfig().set("Spawn.world", p.getWorld().getName());
				plugin.getConfig().set("Spawn.x", p.getLocation().getX());
				plugin.getConfig().set("Spawn.y", p.getLocation().getY());
				plugin.getConfig().set("Spawn.z", p.getLocation().getZ());
				plugin.getConfig().set("Spawn.yaw", p.getLocation().getYaw());
				plugin.getConfig().set("Spawn.pitch", p.getLocation().getPitch());
				plugin.saveConfig();
				MessageUtils.sendMessage(p, "spawn-set-successfully");
			}
			else if (args[0].equalsIgnoreCase("build")) {
				if (args.length != 1) {
					MessageUtils.sendMessage(p, "usage-build");
					return true;
				}
				if (plugin.build.contains(p)) {
					MessageUtils.sendMessage(p, "can-no-longer-build");
					plugin.build.remove(p);
				} else {
					MessageUtils.sendMessage(p, "can-now-build");
					plugin.build.add(p);
				}
			}
			else if (args[0].equalsIgnoreCase("addkit")) {
				if (args.length != 3) {
					MessageUtils.sendMessage(p, "usage-addkit");
					return true;
				}
				KitManager.addKit(args[1], p.getInventory(), args[2]);
				MessageUtils.msg(p, MessageUtils.getMessage("kit-added-name").replace("%kitname%", args[1]));
				MessageUtils.sendMessage(p, "setup-kit-yml");
			}
			else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length != 2) {
					MessageUtils.sendMessage(p, "usage-reset");

					return true;
				}
				Player target = p.getServer().getPlayer(args[1]);
				if (target != null) {
					Stats.setKills(target.getUniqueId().toString(), 0);
					Stats.setDeaths(target.getUniqueId().toString(), 0);
					Stats.setPoints(target.getUniqueId().toString(), 0);
					MessageUtils.msg(p, MessageUtils.getMessage("stats-reset-for").replace("%target%", target.getName()));

					MessageUtils.msg(target, MessageUtils.getMessage("stats-reset-by").replace("%resetter%", p.getName()));
				} else {
					@SuppressWarnings("deprecation")
					OfflinePlayer offTarget = plugin.getServer().getOfflinePlayer(args[1]);
					if (!Stats.playerExists(offTarget.getUniqueId().toString())) {
						MessageUtils.sendMessage(p, "database-nonexistent-player");
						return true;
					}
					Stats.setKills(offTarget.getUniqueId().toString(), 0);
					Stats.setDeaths(offTarget.getUniqueId().toString(), 0);
					Stats.setPoints(offTarget.getUniqueId().toString(), 0);
					MessageUtils.msg(p, MessageUtils.getMessage("stats-reset-for").replace("%target%", offTarget.getName()));
				}
			}
			else if (args[0].equalsIgnoreCase("setregion")) {
				if (args.length != 2) {
					MessageUtils.sendMessage(p, "usage-setregion");

					return true;
				}
				int i = 0;
				try {
					i = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					MessageUtils.sendMessage(p, "error-provide-num-1-or-2");
					return true;
				}
				if (i != 1 && i != 2) {
					MessageUtils.sendMessage(p, "error-provide-1-or-2");
					return true;
				}
				plugin.getConfig().set("Region." + i + ".world", p.getWorld().getName());
				plugin.getConfig().set("Region." + i + ".x", p.getLocation().getX());
				plugin.getConfig().set("Region." + i + ".y", p.getLocation().getY());
				plugin.getConfig().set("Region." + i + ".z", p.getLocation().getZ());
				plugin.saveConfig();
				MessageUtils.msg(p, MessageUtils.getMessage("region-point-set").replace("%point%", i+""));
			}
			else if (args[0].equalsIgnoreCase("regionclear") || args[0].equalsIgnoreCase("clearregion") || args[0].equalsIgnoreCase("resetregion")) {
				plugin.getConfig().set("Region", null);
				plugin.saveConfig();
				MessageUtils.sendMessage(p, "region-points-cleared");
			}
			else if(args[0].equalsIgnoreCase("reload")) {
				FFA.getDefConfig().reload();
				MessageUtils.refresh();
				Events.refresh();
				FFA.getKits().reload();
				FFA.getStats().reload();
				MessageUtils.sendMessage(p, "configs-reloaded");
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Fix")) {
			/*if (plugin.fixSpam.contains(p)) {
				MessageUtils.sendMessage(p, "fix.spam");
				return true;
			}
			plugin.fixSpam.add(p);*/
			for (Player s : p.getServer().getOnlinePlayers()) {
				s.hidePlayer(p);
				s.showPlayer(p);
			}
			/*p.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.fixSpam.remove(p);
				}
			}, plugin.getConfig().getInt("Fix-Cooldown") * 20);*/
			MessageUtils.sendMessage(p, "fix-successfully");
		}
		else if (cmd.getName().equalsIgnoreCase("Top")) {
			String uuid;
			List<String> messages = plugin.getConfig().getStringList("Messages.top");
			for(String s : messages) {
				if(!s.toLowerCase().startsWith("%playerformat%")) {
					MessageUtils.msg(p, s);
					continue;
				}
				int i = 0;
				String format = s.substring("%playerformat%".length());
				Iterator<String> it = Stats.getTopPlayers().iterator();
				for (; it.hasNext();) {
					uuid = (String) it.next();
					MessageUtils.msg(p, format.replace("%name%", Stats.getName(uuid))
							.replace("%kills%", Stats.getKills(uuid) + "").replace("%ranking%", ++i + ""));
				}
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Save")) {
			if (plugin.insideSpawn(p.getLocation())) {
				plugin.save.put(p, InventoryUtils.itemsToString(p.getInventory().getContents()) + "!"
						+ InventoryUtils.itemsToString(p.getInventory().getArmorContents()));
				MessageUtils.sendMessage(p, "save-successfully");
			} else {
				MessageUtils.sendMessage(p, "save-outspawn");
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Unsave")) {
			if (plugin.insideSpawn(p.getLocation())) {
				if (plugin.save.containsKey(p)) {
					plugin.save.remove(p);
					p.getInventory().clear();
					p.getInventory().setHelmet(null);
					p.getInventory().setChestplate(null);
					p.getInventory().setLeggings(null);
					p.getInventory().setBoots(null);
					p.getInventory().setItem(plugin.getConfig().getInt("Kit.slot"), plugin.getKitItem());
					p.updateInventory();
					MessageUtils.sendMessage(p, "save-unsaved");
				} else {
					MessageUtils.sendMessage(p, "save-notsaved");
				}
			} else {
				MessageUtils.sendMessage(p, "save-outspawn");
			}
		}
		else if (cmd.getName().equalsIgnoreCase("stats")) {
			DecimalFormat df = new DecimalFormat("####.##");

			if (args.length == 0) {
				int kills = Stats.getKills(p.getUniqueId().toString());
				int deaths = Stats.getDeaths(p.getUniqueId().toString());
				int points = Stats.getPoints(p.getUniqueId().toString());
				int ranking = Stats.getDeaths(p.getUniqueId().toString());
				String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
				for (String str : plugin.getConfig().getStringList("Messages.stats")) {
					MessageUtils.msg(p, str.replace("%name%", p.getName()).replace("%kills%", kills + "")
							.replace("%deaths%", deaths + "").replace("%points%", points + "")
							.replace("%ranking%", ranking + "").replace("%kd%", kd));
				}
			} else {
				Player target = p.getServer().getPlayer(args[0]);
				if (target != null) {
					int kills = Stats.getKills(target.getUniqueId().toString());
					int deaths = Stats.getDeaths(target.getUniqueId().toString());
					int points = Stats.getPoints(target.getUniqueId().toString());
					int ranking = Stats.getDeaths(target.getUniqueId().toString());
					String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
					for (String str : plugin.getConfig().getStringList("Messages.stats")) {
						MessageUtils.msg(p, str.replace("%name%", target.getName()).replace("%kills%", kills + "")
										.replace("%deaths%", deaths + "").replace("%points%", points + "")
										.replace("%ranking%", ranking + "").replace("%kd%", kd));
					}
				} else {
					@SuppressWarnings("deprecation")
					OfflinePlayer offTarget = plugin.getServer().getOfflinePlayer(args[0]);
					if (!Stats.playerExists(offTarget.getUniqueId().toString())) {
						MessageUtils.sendMessage(p, "database-nonexistent-player");
						return true;
					}
					int kills = Stats.getKills(offTarget.getUniqueId().toString());
					int deaths = Stats.getDeaths(offTarget.getUniqueId().toString());
					int points = Stats.getPoints(offTarget.getUniqueId().toString());
					int ranking = Stats.getDeaths(offTarget.getUniqueId().toString());
					String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
					for (String str : plugin.getConfig().getStringList("Messages.stats")) {
						MessageUtils.msg(p, str.replace("%name%", offTarget.getName()).replace("%kills%", kills + "")
										.replace("%deaths%", deaths + "").replace("%points%", points + "")
										.replace("%ranking%", ranking + "").replace("%kd%", kd));
					}
				}
			}
		}
		return true;
	}
}