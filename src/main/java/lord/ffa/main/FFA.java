package lord.ffa.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lord.ffa.main.additions.Scoreboard;
import lord.ffa.main.stats.FileSystem;
import lord.ffa.main.stats.MySQL;
import lord.ffa.main.stats.Stats;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

public class FFA extends org.bukkit.plugin.java.JavaPlugin {
	public static FFA instance;
	public static String Prefix;
	public static MySQL mysql;
	public static HashMap<Player, String> Save = new HashMap();
	public static ArrayList<Player> fixspam = new ArrayList();
	public static ArrayList<Player> build = new ArrayList();
	public static HashMap<Player, Player> target = new HashMap();

	public void onEnable() {
		instance = this;
		getConfig().options().copyHeader(true);
		getConfig().options().header(
				"Welcome to LordFFA plugin. Plugin was made by LordOfSupeRz\nThis plugin is very simple and easy to use.\nEnjoy with customizable config. You can edit messages and plugin settings");

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		saveConfig();
		Prefix = getInstance().getConfig().getString("Prefix");
		FileSystem.setupStatsFile();
		lord.ffa.main.additions.KitManager.setupKitFile();
		getCommand("ffa").setExecutor(new Commands());
		getCommand("top").setExecutor(new Commands());
		getCommand("stats").setExecutor(new Commands());
		getCommand("records").setExecutor(new Commands());
		getCommand("save").setExecutor(new Commands());
		getCommand("fix").setExecutor(new Commands());
		getCommand("unsave").setExecutor(new Commands());

		getServer().getPluginManager().registerEvents(new Events(), this);
		mysql = new MySQL();
		mysql.connect();
		if (mysql.isOpened()) {
			mysql.update(
					"CREATE TABLE IF NOT EXISTS LORDFFA(UUID varchar(64), KILLS int, DEATHS int, POINTS int, NAME varchar(64));");
		}
	}

	public static FFA getInstance() {
		return instance;
	}

	public static String getString(String str) {
		return org.bukkit.ChatColor.translateAlternateColorCodes('&', str.replace("%prefix%", Prefix));
	}

	public static MySQL getMySQL() {
		return mysql;
	}

	public static Location getSpawnLocation() {
		if (getInstance().getConfig().get("Spawn") != null) {
			World w = org.bukkit.Bukkit.getWorld(getInstance().getConfig().getString("Spawn.world"));
			double x = getInstance().getConfig().getDouble("Spawn.x");
			double y = getInstance().getConfig().getDouble("Spawn.y");
			double z = getInstance().getConfig().getDouble("Spawn.z");
			float yaw = (float) getInstance().getConfig().getDouble("Spawn.yaw");
			float pitch = (float) getInstance().getConfig().getDouble("Spawn.pitch");
			return new Location(w, x, y, z, yaw, pitch);
		}

		return null;
	}

	public static boolean inSideSpawn(Location loc) {
		if ((getInstance().getConfig().get("Region.1") != null)
				&& (getInstance().getConfig().get("Region.2") != null)) {
			if ((loc.getWorld().getName().equals(getInstance().getConfig().getString("Region.1.world")))
					&& (loc.getWorld().getName().equals(getInstance().getConfig().getString("Region.2.world")))) {
				Double minx;
				Double minx;
				Double highx;
				if (getInstance().getConfig().getDouble("Region.1.x") > getInstance().getConfig()
						.getDouble("Region.2.x")) {
					Double highx = Double.valueOf(getInstance().getConfig().getDouble("Region.1.x"));
					minx = Double.valueOf(getInstance().getConfig().getDouble("Region.2.x"));
				} else {
					minx = Double.valueOf(getInstance().getConfig().getDouble("Region.1.x"));
					highx = Double.valueOf(getInstance().getConfig().getDouble("Region.2.x"));
				}
				Double miny;
				Double miny;
				Double highy;
				if (getInstance().getConfig().getDouble("Region.1.y") > getInstance().getConfig()
						.getDouble("Region.2.y")) {
					Double highy = Double.valueOf(getInstance().getConfig().getDouble("Region.1.y"));
					miny = Double.valueOf(getInstance().getConfig().getDouble("Region.2.y"));
				} else {
					miny = Double.valueOf(getInstance().getConfig().getDouble("Region.1.y"));
					highy = Double.valueOf(getInstance().getConfig().getDouble("Region.2.y"));
				}
				Double minz;
				Double minz;
				Double highz;
				if (getInstance().getConfig().getDouble("Region.1.z") > getInstance().getConfig()
						.getDouble("Region.2.z")) {
					Double highz = Double.valueOf(getInstance().getConfig().getDouble("Region.1.z"));
					minz = Double.valueOf(getInstance().getConfig().getDouble("Region.2.z"));
				} else {
					minz = Double.valueOf(getInstance().getConfig().getDouble("Region.1.z"));
					highz = Double.valueOf(getInstance().getConfig().getDouble("Region.2.z"));
				}

				if ((loc.getX() <= highx.doubleValue()) && (loc.getY() <= highy.doubleValue())
						&& (loc.getBlockZ() <= highz.doubleValue()) && (loc.getX() >= minx.doubleValue())
						&& (loc.getBlockY() >= miny.doubleValue()) && (loc.getZ() >= minz.doubleValue())) {
					return true;
				}
			}
		}

		return false;
	}

	public static ItemStack getKitItem() {
		int i = Integer.valueOf(getInstance().getConfig().getString("Kit.item").split(":")[1]).intValue();
		ItemStack item = new ItemStack(
				Material.getMaterial(
						Integer.valueOf(getInstance().getConfig().getString("Kit.item").split(":")[0]).intValue()),
				1, (byte) i);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(getString(getInstance().getConfig().getString("Kit.name")));
		item.setItemMeta(itemmeta);
		return item;
	}

	public static void ScoreboardCreate(Player p) {
		Scoreboard board = new Scoreboard();
		board.setPlayer(p);
		boolean title = true;
		int i = getInstance().getConfig().getStringList("Scoreboard").size() - 1;
		for (String str : getInstance().getConfig().getStringList("Scoreboard")) {
			if (title) {
				board.setTitle(getString(str));
				title = false;
			} else {
				str = str.replace("%kills%", Stats.getKills(p.getUniqueId().toString()));
				str = str.replace("%deaths%", Stats.getDeaths(p.getUniqueId().toString()));
				str = str.replace("%points%", Stats.getPoints(p.getUniqueId().toString()));
				str = str.replace("%name%", p.getName());
				board.addScore(getString(str), Integer.valueOf(i));
				i--;
			}
		}

		board.ScoreBuilder();
		board.sendScoreboad();
	}

	public static ArrayList<ItemStack> DeathItems() {
		ArrayList<ItemStack> items = new ArrayList();
		for (String str : getInstance().getConfig().getStringList("Death.items")) {
			String[] string = str.split(",");
			ItemStack item = new ItemStack(Material.getMaterial(Integer.valueOf(string[0]).intValue()),
					Integer.valueOf(string[1]).intValue(), Byte.valueOf(string[2]).byteValue());
			items.add(item);
		}
		return items;
	}
}