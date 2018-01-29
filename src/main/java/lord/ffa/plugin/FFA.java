package lord.ffa.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import lord.ffa.additions.KitManager;
import lord.ffa.additions.MessageUtils;
import lord.ffa.additions.PlayerScoreboard;
import lord.ffa.stats.FileSystem;
import lord.ffa.stats.MySQL;
import lord.ffa.stats.Stats;

public class FFA extends JavaPlugin
{
	public static FFA instance;
	public static MySQL mysql;
	public HashMap<Player, String> save = new HashMap<>();
	public ArrayList<Player> fixSpam = new ArrayList<>();
	public ArrayList<Player> build = new ArrayList<>();
	public HashMap<Player, Player> target = new HashMap<>();

	@Override
	public void onEnable()
	{
		instance = this;
		getConfig().options().copyHeader(true);
		getConfig().options().header("Welcome to FFA plugin. Plugin was made by LordOfSupeRz\n"
				                   + "This plugin is very simple and easy to use.\n"
				                   + "Enjoy with customizable config. You can edit messages and plugin settings");

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		saveConfig();
		FileSystem.setupStatsFile();
		KitManager.setupKitFile();
		Commands cmdExec = new Commands(this);
		getCommand("FreeForAll").setExecutor(cmdExec);
		getCommand("Fix").setExecutor(cmdExec);
		getCommand("Top").setExecutor(cmdExec);
		getCommand("Save").setExecutor(cmdExec);
		getCommand("Unsave").setExecutor(cmdExec);
		getCommand("Stats").setExecutor(cmdExec);

		getServer().getPluginManager().registerEvents(new Events(this), this);
		mysql = new MySQL();
		mysql.connect();
		if (mysql.isOpened()) {
			mysql.update("CREATE TABLE IF NOT EXISTS FFA(UUID varchar(64), KILLS int, DEATHS int, POINTS int, NAME varchar(64));");
		}
	}

	public static MySQL getMySQL() {
		return mysql;
	}

	public Location getSpawnLocation() {
		if (getInstance().getConfig().get("Spawn") != null) {
			World w = org.bukkit.Bukkit.getWorld(getInstance().getConfig().getString("Spawn.world"));
			double x = getInstance().getConfig().getDouble("Spawn.x");
			double y = getInstance().getConfig().getDouble("Spawn.y");
			double z = getInstance().getConfig().getDouble("Spawn.z");
			float yaw = (float)getInstance().getConfig().getDouble("Spawn.yaw");
			float pitch = (float)getInstance().getConfig().getDouble("Spawn.pitch");
			return new Location(w, x, y, z, yaw, pitch);
		}

		return null;
	}

	public boolean insideSpawn(Location loc) {
		return insideSpawn(loc, false);
	}
	
	public boolean insideSpawn(Location loc, boolean moveEvent) {
		FileConfiguration config = getInstance().getConfig();
		if(config.get("Region.1") == null || config.get("Region.2") == null) return moveEvent;
		if (loc.getWorld().getName().equals(config.getString("Region.1.world"))	&& loc.getWorld().getName().equals(config.getString("Region.2.world"))) {
			double x1 = config.getDouble("Region.1.x"),
			x2 = config.getDouble("Region.2.x");
			
			double y1 = config.getDouble("Region.1.y"),
			y2 = config.getDouble("Region.2.y");
			
			double z1 = config.getDouble("Region.1.z"),
			z2 = config.getDouble("Region.2.z");

			if (loc.getX() < Math.max(x1, x2) && loc.getX() > Math.min(x1, x2) &&
					loc.getY() < Math.max(y1, y2) && loc.getBlockY() > Math.min(y1, y2) &&
					loc.getBlockZ() < Math.max(z1, z2) && loc.getZ() > Math.min(z1, z2)) {
				return true;
			}
		}

		return false;
	}

	public ItemStack getKitItem() {
		int i = Integer.valueOf(getInstance().getConfig().getString("Kit.item").split(":")[1]);
		ItemStack item = new ItemStack(Material.getMaterial(getInstance().getConfig().getString("Kit.item").split(":")[0]), 1, (short) i);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(MessageUtils.cc(getInstance().getConfig().getString("Kit.name")));
		item.setItemMeta(itemmeta);
		return item;
	}

	public void scoreboardCreate(Player p)
	{
		PlayerScoreboard board = new PlayerScoreboard(p);
		boolean title = true;
		int i = getInstance().getConfig().getStringList("Scoreboard").size() - 1;
		for (String str : getInstance().getConfig().getStringList("Scoreboard")) {
			if (title) {
				board.setTitle(MessageUtils.getString(str));
				title = false;
			} else {
				str = str.replace("%kills%", Stats.getKills(p.getUniqueId().toString())+"");
				str = str.replace("%deaths%", Stats.getDeaths(p.getUniqueId().toString())+"");
				str = str.replace("%points%", Stats.getPoints(p.getUniqueId().toString())+"");
				str = str.replace("%name%", p.getName());
				board.addScore(MessageUtils.getString(str), i);
				i--;
			}
		}

		board.build();
		board.sendScoreboard();
	}

	public ArrayList<ItemStack> deathItems()
	{
		ArrayList<ItemStack> items = new ArrayList<>();
		for (String str : getInstance().getConfig().getStringList("Death.items")) {
			String[] string = str.split(",");
			ItemStack item = new ItemStack(Material.getMaterial(string[0]), Integer.valueOf(string[1]), Short.valueOf(string[2]));
			items.add(item);
		}
		return items;
	}
	
	public static FFA getInstance()
	{
		return instance;
	}
}