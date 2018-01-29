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
				MessageUtils.msg(p, "&8&m-------------- &r&aFFA Help &8&m--------------");
				MessageUtils.msg(p, "");
				MessageUtils.msg(p, "                 &ePlugin Coded by                   ");
				MessageUtils.msg(p, "                   &aLordOfSupeRz                    ");
				MessageUtils.msg(p, "");
				MessageUtils.msg(p, "%prefix% &3/ffa setspawn");
				MessageUtils.msg(p, "%prefix% &3/ffa setregion <1 - 2>");
				MessageUtils.msg(p, "%prefix% &3/ffa resetregion");
				MessageUtils.msg(p, "%prefix% &3/ffa reset <player>");
				MessageUtils.msg(p, "%prefix% &3/ffa addkit <name> <permission>");
				MessageUtils.msg(p, "%prefix% &3/ffa build");
				MessageUtils.msg(p, "%prefix% &3/top");
				MessageUtils.msg(p, "%prefix% &3/fix");
				MessageUtils.msg(p, "%prefix% &3/save");
				MessageUtils.msg(p, "%prefix% &3/unsave");
				MessageUtils.msg(p, "&8&m-----------------------------------------");
				return true;
			}

			if (args[0].equalsIgnoreCase("setspawn")) {
				if (args.length != 1) {
					MessageUtils.msg(p, "%prefix% &3/ffa setspawn");
					return true;
				}
				plugin.getConfig().set("Spawn.world", p.getWorld().getName());
				plugin.getConfig().set("Spawn.x", p.getLocation().getX());
				plugin.getConfig().set("Spawn.y", p.getLocation().getY());
				plugin.getConfig().set("Spawn.z", p.getLocation().getZ());
				plugin.getConfig().set("Spawn.yaw", p.getLocation().getYaw());
				plugin.getConfig().set("Spawn.pitch", p.getLocation().getPitch());
				plugin.saveConfig();
				MessageUtils.msg(p, "%prefix% &aYou have set the spawn location successfully.");
			}
			else if (args[0].equalsIgnoreCase("build")) {
				if (args.length != 1) {
					MessageUtils.msg(p, "%prefix% &3/ffa build");
					return true;
				}
				if (plugin.build.contains(p)) {
					MessageUtils.msg(p, "%prefix% &cYou are now unable to build.");
					plugin.build.remove(p);
				} else {
					MessageUtils.msg(p, "%prefix% &aYou are now able to build.");
					plugin.build.add(p);
				}
			}
			else if (args[0].equalsIgnoreCase("addkit")) {
				if (args.length != 3) {
					MessageUtils.msg(p, "%prefix% &3/ffa addkit <name> <permission>");
					return true;
				}
				KitManager.addKit(args[1], p.getInventory(), args[2]);
				MessageUtils.msg(p, "%prefix% &aYou have added the '" + args[1] + "' kit.");
				MessageUtils.msg(p, "%prefix% &aPlease setup kit settings from Kits.yml.");
			}
			else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length != 2) {
					MessageUtils.msg(p, "%prefix% &3/ffa reset <player>");

					return true;
				}
				Player target = p.getServer().getPlayer(args[1]);
				if (target != null) {
					Stats.setKills(target.getUniqueId().toString(), 0);
					Stats.setDeaths(target.getUniqueId().toString(), 0);
					Stats.setPoints(target.getUniqueId().toString(), 0);
					MessageUtils.msg(p, "%prefix% &aYou have reset &e" + target.getName() + "'s &astats.");

					MessageUtils.msg(target, "%prefix% &aYour stats have been reset by &e" + p.getName() + ".");
				} else {
					@SuppressWarnings("deprecation")
					OfflinePlayer target2 = plugin.getServer().getOfflinePlayer(args[1]);
					if (!Stats.playerExists(target2.getUniqueId().toString())) {
						MessageUtils.msg(p, "%prefix% &cThere is not a player with that name in the database.");
						return true;
					}
					Stats.setKills(target2.getUniqueId().toString(), 0);
					Stats.setDeaths(target2.getUniqueId().toString(), 0);
					Stats.setPoints(target2.getUniqueId().toString(), 0);
					MessageUtils.msg(p, "%prefix% &aYou have reset &e" + target2.getName() + "'s &astats.");
				}
			}
			else if (args[0].equalsIgnoreCase("setregion")) {
				if (args.length != 2) {
					MessageUtils.msg(p, "%prefix% &3/ffa setregion <1 - 2>");

					return true;
				}
				int i = 0;
				try {
					i = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					MessageUtils.msg(p, "%prefix% &cYou must provide an integer value (1 or 2).");
					return true;
				}
				if (i != 1 && i != 2) {
					MessageUtils.msg(p, "%prefix% &cYou must provide 1 or 2 as the value.");
					return true;
				}
				plugin.getConfig().set("Region." + i + ".world", p.getWorld().getName());
				plugin.getConfig().set("Region." + i + ".x", p.getLocation().getX());
				plugin.getConfig().set("Region." + i + ".y", p.getLocation().getY());
				plugin.getConfig().set("Region." + i + ".z", p.getLocation().getZ());
				plugin.saveConfig();
				MessageUtils.msg(p, "%prefix% &aYou have set region point #" + i + " successfully.");
			}
			else if (args[0].equalsIgnoreCase("regionclear") || args[0].equalsIgnoreCase("clearregion") || args[0].equalsIgnoreCase("resetregion")) {
				plugin.getConfig().set("Region", null);
				plugin.saveConfig();
				MessageUtils.msg(p, "%prefix% &aYou've cleared the regions! Spawn protection and other functions no longer apply.");
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Fix")) {
			if (plugin.fixSpam.contains(p)) {
				MessageUtils.sendMessage(p, "fix.spam");
				return true;
			}
			plugin.fixSpam.add(p);
			for (Player s : p.getServer().getOnlinePlayers()) {
				s.hidePlayer(p);
				s.showPlayer(p);
			}
			p.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.fixSpam.remove(p);
				}
			}, plugin.getConfig().getInt("Fix-Cooldown") * 20);
			MessageUtils.sendMessage(p, "fix-successfully");
		}
		else if (cmd.getName().equalsIgnoreCase("Top")) {
			String uuid;
			List<String> messages = plugin.getConfig().getStringList("Messages.top");
			MessageUtils.sendMessage(p, messages.get(0));
			int i = 0;
			Iterator<String> it = Stats.getTopPlayers().iterator();
			for (; it.hasNext();) {
				uuid = (String) it.next();
				i++;
				MessageUtils.msg(p, messages.get(1).replace("%name%", Stats.getName(uuid))
						.replace("%kills%", Stats.getKills(uuid) + "").replace("%ranking%", i + ""));
			}

			MessageUtils.sendMessage(p, messages.get(2));
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
					OfflinePlayer target2 = plugin.getServer().getOfflinePlayer(args[0]);
					if (!Stats.playerExists(target2.getUniqueId().toString())) {
						MessageUtils.msg(p, "%prefix% &cThere is not a player with that name in the database.");
						return true;
					}
					int kills = Stats.getKills(target2.getUniqueId().toString());
					int deaths = Stats.getDeaths(target2.getUniqueId().toString());
					int points = Stats.getPoints(target2.getUniqueId().toString());
					int ranking = Stats.getDeaths(target2.getUniqueId().toString());
					String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
					for (String str : plugin.getConfig().getStringList("Messages.stats")) {
						MessageUtils.msg(p, str.replace("%name%", target2.getName()).replace("%kills%", kills + "")
										.replace("%deaths%", deaths + "").replace("%points%", points + "")
										.replace("%ranking%", ranking + "").replace("%kd%", kd));
					}
				}
			}
		}
		return true;
	}
}