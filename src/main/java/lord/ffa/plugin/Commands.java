package lord.ffa.plugin;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lord.ffa.additions.InventoryUtils;
import lord.ffa.additions.KitManager;
import lord.ffa.stats.Stats;

public class Commands implements CommandExecutor {
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;

		final Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("FreeForAll")) {
			if (!p.isOp()) {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.no-command-permission")));
				return true;
			}
			if (args.length == 0) {
				p.sendMessage(FFA.getString("&8&m-------------- &r&aLordFFA Help &8&m--------------"));
				p.sendMessage(FFA.getString(""));
				p.sendMessage(FFA.getString("                 &ePlugin Coded by                   "));
				p.sendMessage(FFA.getString("                   &aLordOfSupeRz                    "));
				p.sendMessage(FFA.getString(""));
				p.sendMessage(FFA.getString("%prefix% &3/ffa setspawn"));
				p.sendMessage(FFA.getString("%prefix% &3/ffa setregion <1 - 2>"));
				p.sendMessage(FFA.getString("%prefix% &3/ffa resetregion"));
				p.sendMessage(FFA.getString("%prefix% &3/ffa reset <player>"));
				p.sendMessage(FFA.getString("%prefix% &3/ffa addkit <name> <permission>"));
				p.sendMessage(FFA.getString("%prefix% &3/ffa build"));
				p.sendMessage(FFA.getString("%prefix% &3/top"));
				p.sendMessage(FFA.getString("%prefix% &3/fix"));
				p.sendMessage(FFA.getString("%prefix% &3/save"));
				p.sendMessage(FFA.getString("%prefix% &3/unsave"));
				p.sendMessage(FFA.getString("&8&m-----------------------------------------"));
				return true;
			}

			if (args[0].equalsIgnoreCase("setspawn")) {
				if (args.length != 1) {
					p.sendMessage(FFA.getString("%prefix% &3/ffa setspawn"));
					return true;
				}
				FFA.getInstance().getConfig().set("Spawn.world", p.getWorld().getName());
				FFA.getInstance().getConfig().set("Spawn.x", p.getLocation().getX());
				FFA.getInstance().getConfig().set("Spawn.y", p.getLocation().getY());
				FFA.getInstance().getConfig().set("Spawn.z", p.getLocation().getZ());
				FFA.getInstance().getConfig().set("Spawn.yaw", p.getLocation().getYaw());
				FFA.getInstance().getConfig().set("Spawn.pitch", p.getLocation().getPitch());
				FFA.getInstance().saveConfig();
				p.sendMessage(FFA.getString("%prefix% &aYou have set the spawn location successfully."));
			}
			else if (args[0].equalsIgnoreCase("build")) {
				if (args.length != 1) {
					p.sendMessage(FFA.getString("%prefix% &3/ffa build"));
					return true;
				}
				if (FFA.build.contains(p)) {
					p.sendMessage(FFA.getString("%prefix% &cYou are now unable to build."));
					FFA.build.remove(p);
				} else {
					p.sendMessage(FFA.getString("%prefix% &aYou are now able to build."));
					FFA.build.add(p);
				}
			}
			else if (args[0].equalsIgnoreCase("addkit")) {
				if (args.length != 3) {
					p.sendMessage(FFA.getString("%prefix% &3/ffa addkit <name> <permission>"));
					return true;
				}
				KitManager.addKit(args[1], p.getInventory(), args[2]);
				p.sendMessage(FFA.getString("%prefix% &aYou have added the '" + args[1] + "' kit."));
				p.sendMessage(FFA.getString("%prefix% &aPlease setup kit settings from Kits.yml."));
			}
			else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length != 2) {
					p.sendMessage(FFA.getString("%prefix% &3/ffa reset <player>"));

					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					Stats.setKills(target.getUniqueId().toString(), 0);
					Stats.setDeaths(target.getUniqueId().toString(), 0);
					Stats.setPoints(target.getUniqueId().toString(), 0);
					p.sendMessage(
							FFA.getString("%prefix% &aYou have reset &e" + target.getName() + "'s &astats."));

					target.sendMessage(
							FFA.getString("%prefix% &aYour stats have been reset by &e" + p.getName() + "."));
				} else {
					OfflinePlayer target2 = Bukkit.getOfflinePlayer(args[1]);
					if (!Stats.playerExists(target2.getUniqueId().toString())) {
						p.sendMessage(FFA.getString("%prefix% &cThere is not a player with that name in the database."));
						return true;
					}
					Stats.setKills(target2.getUniqueId().toString(), 0);
					Stats.setDeaths(target2.getUniqueId().toString(), 0);
					Stats.setPoints(target2.getUniqueId().toString(), 0);
					p.sendMessage(FFA.getString("%prefix% &aYou have reset &e" + target2.getName() + "'s &astats."));
				}
			}
			else if (args[0].equalsIgnoreCase("setregion")) {
				if (args.length != 2) {
					p.sendMessage(FFA.getString("%prefix% &3/ffa setregion <1 - 2>"));

					return true;
				}
				int i = 0;
				try {
					i = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					p.sendMessage(FFA.getString("%prefix% &cYou must provide an integer value (1 or 2)."));
					return true;
				}
				if (i != 1 && i != 2) {
					p.sendMessage(FFA.getString("%prefix% &cYou must provide 1 or 2 as the value."));
					return true;
				}
				FFA.getInstance().getConfig().set("Region." + i + ".world", p.getWorld().getName());
				FFA.getInstance().getConfig().set("Region." + i + ".x", p.getLocation().getX());
				FFA.getInstance().getConfig().set("Region." + i + ".y", p.getLocation().getY());
				FFA.getInstance().getConfig().set("Region." + i + ".z", p.getLocation().getZ());
				FFA.getInstance().saveConfig();
				p.sendMessage(FFA.getString("%prefix% &aYou have set region point #" + i + " successfully."));
			}
			else if (args[0].equalsIgnoreCase("regionclear") || args[0].equalsIgnoreCase("clearregion") || args[0].equalsIgnoreCase("resetregion")) {
				FFA.getInstance().getConfig().set("Region", null);
				FFA.getInstance().saveConfig();
				p.sendMessage(FFA.getString("%prefix% &aYou've cleared the regions! Spawn protection and other functions no longer apply."));
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Fix")) {
			if (FFA.fixSpam.contains(p)) {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.fix.spam")));

				return true;
			}
			FFA.fixSpam.add(p);
			Player[] arrayOfPlayer = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
			for (int e = 0; e < arrayOfPlayer.length; e++) {
				Player s = arrayOfPlayer[e];
				s.hidePlayer(p);
				s.showPlayer(p);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getInstance(), new Runnable() {
				public void run() {
					FFA.fixSpam.remove(p);
				}
			}, FFA.getInstance().getConfig().getInt("Fix-Cooldown") * 20);
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.fix.successfully")));
		}
		else if (cmd.getName().equalsIgnoreCase("Top")) {
			String uuid;
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.header")));
			int i = 0;
			Iterator<String> it = Stats.getTopPlayers().iterator();
			for (; it.hasNext();) {
				uuid = (String) it.next();
				i++;
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.format")
						.replace("%name%", Stats.getName(uuid)).replace("%kills%", Stats.getKills(uuid) + "")
						.replace("%ranking%", i + "")));
			}

			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.footer")));
		}

		else if (cmd.getName().equalsIgnoreCase("Save")) {
			if (FFA.insideSpawn(p.getLocation())) {
				FFA.save.put(p, InventoryUtils.itemsToString(p.getInventory().getContents()) + "!"
						+ InventoryUtils.itemsToString(p.getInventory().getArmorContents()));
				for (String str : FFA.getInstance().getConfig().getStringList("Messages.save-successfully")) {
					p.sendMessage(FFA.getString(str));
				}
			} else {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.save-outspawn")));
			}
		}
		else if (cmd.getName().equalsIgnoreCase("Unsave")) {
			if (FFA.insideSpawn(p.getLocation())) {
				if (FFA.save.containsKey(p)) {
					FFA.save.remove(p);
					p.getInventory().clear();
					p.getInventory().setHelmet(null);
					p.getInventory().setChestplate(null);
					p.getInventory().setLeggings(null);
					p.getInventory().setBoots(null);
					p.getInventory().setItem(FFA.getInstance().getConfig().getInt("Kit.slot"), FFA.getKitItem());
					p.updateInventory();
					p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.save-unsaved")));
				} else {
					p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.save-notsaved")));
				}
			} else {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.save-outspawn")));
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
				for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
					p.sendMessage(FFA.getString(str.replace("%name%", p.getName()).replace("%kills%", kills + "")
							.replace("%deaths%", deaths + "").replace("%points%", points + "")
							.replace("%ranking%", ranking + "").replace("%kd%", kd)));
				}
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					int kills = Stats.getKills(target.getUniqueId().toString());
					int deaths = Stats.getDeaths(target.getUniqueId().toString());
					int points = Stats.getPoints(target.getUniqueId().toString());
					int ranking = Stats.getDeaths(target.getUniqueId().toString());
					String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
					for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
						p.sendMessage(
								FFA.getString(str.replace("%name%", target.getName()).replace("%kills%", kills + "")
										.replace("%deaths%", deaths + "").replace("%points%", points + "")
										.replace("%ranking%", ranking + "").replace("%kd%", kd)));
					}
				} else {
					OfflinePlayer target2 = Bukkit.getOfflinePlayer(args[0]);
					if (!Stats.playerExists(target2.getUniqueId().toString())) {
						p.sendMessage(FFA.getString("%prefix% &cThere is not a player with that name in the database."));
						return true;
					}
					int kills = Stats.getKills(target2.getUniqueId().toString());
					int deaths = Stats.getDeaths(target2.getUniqueId().toString());
					int points = Stats.getPoints(target2.getUniqueId().toString());
					int ranking = Stats.getDeaths(target2.getUniqueId().toString());
					String kd = deaths == 0 ? (kills != 0 ? "Inf" : "0.0") : df.format(kills / deaths);
					for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
						p.sendMessage(FFA
								.getString(str.replace("%name%", target2.getName()).replace("%kills%", kills + "")
										.replace("%deaths%", deaths + "").replace("%points%", points + "")
										.replace("%ranking%", ranking + "").replace("%kd%", kd)));
					}
				}
			}
		}
		return true;
	}
}