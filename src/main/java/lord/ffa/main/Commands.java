package lord.ffa.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import lord.ffa.main.additions.InventoryUtils;
import lord.ffa.main.stats.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class Commands implements org.bukkit.command.CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("ffa")) {
			if (p.isOp()) {
				if (args.length == 0) {
					p.sendMessage(FFA.getString("&8&m-------------- &r&aLordFFA Help &8&m--------------"));
					p.sendMessage(FFA.getString(""));
					p.sendMessage(FFA.getString("                 &ePlugin Coded by                   "));
					p.sendMessage(FFA.getString("                   &aLordOfSupeRz                    "));
					p.sendMessage(FFA.getString(""));
					p.sendMessage(FFA.getString("%prefix% &3/ffa setspawn"));
					p.sendMessage(FFA.getString("%prefix% &3/ffa setregion <1 - 2>"));
					p.sendMessage(FFA.getString("%prefix% &3/ffa reset <player>"));
					p.sendMessage(FFA.getString("%prefix% &3/ffa addkit <name> <permission>"));
					p.sendMessage(FFA.getString("%prefix% &3/ffa build"));
					p.sendMessage(FFA.getString("%prefix% &3/top"));
					p.sendMessage(FFA.getString("%prefix% &3/fix"));
					p.sendMessage(FFA.getString("%prefix% &3/save"));
					p.sendMessage(FFA.getString("%prefix% &3/unsave"));
					p.sendMessage(FFA.getString("&8&m-----------------------------------------"));

					return false;
				}

				if (args[0].equalsIgnoreCase("setspawn")) {
					if (args.length != 1) {
						p.sendMessage(FFA.getString("%prefix% &3/ffa setspawn"));
						return false;
					}
					FFA.getInstance().getConfig().set("Spawn.world", p.getWorld().getName());
					FFA.getInstance().getConfig().set("Spawn.x", Double.valueOf(p.getLocation().getX()));
					FFA.getInstance().getConfig().set("Spawn.y", Double.valueOf(p.getLocation().getY()));
					FFA.getInstance().getConfig().set("Spawn.z", Double.valueOf(p.getLocation().getZ()));
					FFA.getInstance().getConfig().set("Spawn.yaw", Float.valueOf(p.getLocation().getYaw()));
					FFA.getInstance().getConfig().set("Spawn.pitch", Float.valueOf(p.getLocation().getPitch()));
					FFA.getInstance().saveConfig();
					p.sendMessage(FFA.getString("%prefix% &aYou have set spawn location sucessfully."));
				}
				if (args[0].equalsIgnoreCase("build")) {
					if (args.length != 1) {
						p.sendMessage(FFA.getString("%prefix% &3/ffa build"));
						return false;
					}
					if (FFA.build.contains(p)) {
						p.sendMessage(FFA.getString("%prefix% &cYou can't build now."));
						FFA.build.remove(p);
					} else {
						p.sendMessage(FFA.getString("%prefix% &aYou can build now."));
						FFA.build.add(p);
					}
				}

				if (args[0].equalsIgnoreCase("addkit")) {
					if (args.length != 3) {
						p.sendMessage(FFA.getString("%prefix% &3/ffa addkit <name> <permission>"));

						return false;
					}
					lord.ffa.main.additions.KitManager.addKit(args[1], p.getInventory(), args[2]);
					p.sendMessage(FFA.getString("%prefix% &aYou have added " + args[1] + " kit."));
					p.sendMessage(FFA.getString("%prefix% &aPlease setup kit settings from Kits.yml."));
				}

				if (args[0].equalsIgnoreCase("reset")) {
					if (args.length != 2) {
						p.sendMessage(FFA.getString("%prefix% &3/ffa reset <player>"));

						return false;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null) {
						Stats.setKills(target.getUniqueId().toString(), Integer.valueOf(0));
						Stats.setDeaths(target.getUniqueId().toString(), Integer.valueOf(0));
						Stats.setPoints(target.getUniqueId().toString(), Integer.valueOf(0));
						p.sendMessage(
								FFA.getString("%prefix% &aYour have reseted &e" + target.getName() + " &astats."));

						target.sendMessage(
								FFA.getString("%prefix% &aYour stats has been reseted by &e" + p.getName() + "."));
					} else {
						OfflinePlayer target2 = Bukkit.getOfflinePlayer(args[1]);
						if (Stats.playerExists(target2.getUniqueId().toString())) {
							Stats.setKills(target2.getUniqueId().toString(), Integer.valueOf(0));
							Stats.setDeaths(target2.getUniqueId().toString(), Integer.valueOf(0));
							Stats.setPoints(target2.getUniqueId().toString(), Integer.valueOf(0));
							p.sendMessage(
									FFA.getString("%prefix% &aYour have reseted &e" + target2.getName() + "&astats."));
						} else {
							p.sendMessage(FFA.getString("%prefix% &cThere isnot player with this name in database."));
						}
					}
				}

				if (args[0].equalsIgnoreCase("setregion")) {
					if (args.length != 2) {
						p.sendMessage(FFA.getString("%prefix% &3/ffa setregion <1 - 2>"));

						return false;
					}
					int i = 0;
					try {
						i = Integer.valueOf(args[1]).intValue();
					} catch (NumberFormatException e) {
						p.sendMessage(FFA.getString("%prefix% &cYou have to put integer value 1 or 2."));
						return true;
					}
					if ((i == 1) || (i == 2)) {
						FFA.getInstance().getConfig().set("Region." + i + ".world", p.getWorld().getName());
						FFA.getInstance().getConfig().set("Region." + i + ".x", Double.valueOf(p.getLocation().getX()));
						FFA.getInstance().getConfig().set("Region." + i + ".y", Double.valueOf(p.getLocation().getY()));
						FFA.getInstance().getConfig().set("Region." + i + ".z", Double.valueOf(p.getLocation().getZ()));
						FFA.getInstance().getConfig().set("Region." + i + ".yaw",
								Float.valueOf(p.getLocation().getYaw()));
						FFA.getInstance().getConfig().set("Region." + i + ".pitch",
								Float.valueOf(p.getLocation().getPitch()));
						FFA.getInstance().saveConfig();
						p.sendMessage(FFA.getString("%prefix% &aYou have set region point " + i + " sucessfully."));
					} else {
						p.sendMessage(FFA.getString("%prefix% &cYou have to put value 1 or 2."));

					}

				}

			} else {

				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.no-command-permission")));
			}
		}
		Object localObject;
		if (cmd.getName().equalsIgnoreCase("fix")) {
			if (FFA.fixspam.contains(p)) {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.fix.spam")));

				return false;
			}
			FFA.fixspam.add(p);
			Player[] arrayOfPlayer;
			localObject = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
			for (e = 0; e < localObject; e++) {
				Player s = arrayOfPlayer[e];
				s.hidePlayer(p);
				s.showPlayer(p);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getInstance(), new Runnable() {
				public void run() {
					FFA.fixspam.remove(p);
				}
			}, FFA.getInstance().getConfig().getInt("Fix-Cooldown") * 20);
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.fix.successfully")));
		}
		String uuid;
		if (cmd.getName().equalsIgnoreCase("top")) {
			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.header")));
			int i = 0;
			for (localObject = Stats.getTopPlayers().iterator(); ((Iterator) localObject).hasNext();) {
				uuid = (String) ((Iterator) localObject).next();
				i++;
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.format")
						.replace("%name%", Stats.getName(uuid)).replace("%kills%", Stats.getKills(uuid))
						.replace("%ranking%", i)));
			}

			p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.top.footer")));
		}

		if (cmd.getName().equalsIgnoreCase("save")) {
			if (FFA.inSideSpawn(p.getLocation())) {
				FFA.Save.put(p, InventoryUtils.ItemstoString(p.getInventory().getContents()) + "!"
						+ InventoryUtils.ItemstoString(p.getInventory().getArmorContents()));
				for (String str : FFA.getInstance().getConfig().getStringList("Messages.save-successfully")) {
					p.sendMessage(FFA.getString(str));
				}
			} else {
				p.sendMessage(FFA.getString(FFA.getInstance().getConfig().getString("Messages.save-outspawn")));
			}
		}

		if (cmd.getName().equalsIgnoreCase("unsave")) {
			if (FFA.inSideSpawn(p.getLocation())) {
				if (FFA.Save.containsKey(p)) {
					FFA.Save.remove(p);
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

		if ((cmd.getName().equalsIgnoreCase("stats")) || (cmd.getName().equalsIgnoreCase("records"))) {
			DecimalFormat df = new DecimalFormat("####.##");

			if (args.length == 0) {
				int kills = Stats.getKills(p.getUniqueId().toString()).intValue();
				int deaths = Stats.getDeaths(p.getUniqueId().toString()).intValue();
				int points = Stats.getPoints(p.getUniqueId().toString()).intValue();
				int ranking = Stats.getDeaths(p.getUniqueId().toString()).intValue();
				String kd = df.format(kills / deaths);
				if ((kills == 0) && (deaths == 0)) {
					kd = "0.0";
				}
				for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
					p.sendMessage(FFA.getString(
							str.replace("%name%", p.getName()).replace("%kills%", kills).replace("%deaths%", deaths)
									.replace("%points%", points).replace("%ranking%", ranking).replace("%kd%", kd)));
				}
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					int kills = Stats.getKills(target.getUniqueId().toString()).intValue();
					int deaths = Stats.getDeaths(target.getUniqueId().toString()).intValue();
					int points = Stats.getPoints(target.getUniqueId().toString()).intValue();
					int ranking = Stats.getDeaths(target.getUniqueId().toString()).intValue();
					String kd = df.format(kills / deaths);
					if ((kills == 0) && (deaths == 0)) {
						kd = "0.0";
					}
					for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
						p.sendMessage(FFA.getString(str.replace("%name%", target.getName()).replace("%kills%", kills)
								.replace("%deaths%", deaths).replace("%points%", points).replace("%ranking%", ranking)
								.replace("%kd%", kd)));
					}
				} else {
					OfflinePlayer target2 = Bukkit.getOfflinePlayer(args[0]);
					if (Stats.playerExists(target2.getUniqueId().toString())) {
						int kills = Stats.getKills(target2.getUniqueId().toString()).intValue();
						int deaths = Stats.getDeaths(target2.getUniqueId().toString()).intValue();
						int points = Stats.getPoints(target2.getUniqueId().toString()).intValue();
						int ranking = Stats.getDeaths(target2.getUniqueId().toString()).intValue();
						String kd = df.format(kills / deaths);
						if ((kills == 0) && (deaths == 0)) {
							kd = "0.0";
						}
						for (String str : FFA.getInstance().getConfig().getStringList("Messages.stats")) {
							p.sendMessage(FFA.getString(str.replace("%name%", target2.getName())
									.replace("%kills%", kills).replace("%deaths%", deaths).replace("%points%", points)
									.replace("%ranking%", ranking).replace("%kd%", kd)));
						}
					} else {
						p.sendMessage(FFA.getString("%prefix% &cThere isnot player with this name in database."));
					}
				}
			}
		}

		return true;
	}
}