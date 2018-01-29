package lord.ffa.stats;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lord.ffa.plugin.FFA;

public class FileSystem {
	private static File path = FFA.getInstance().getDataFolder();
	private static File file = new File(FFA.getInstance().getDataFolder(), "Stats.yml");
	private static FileConfiguration configuration = YamlConfiguration.loadConfiguration(file); // TODO possible exception
	
	public static void setupStatsFile() {
		path.mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void set(String path, Object value) {
		configuration.set(path, value);
	}

	public static void setAndSave(String path, Object value) {
		set(path, value);
		save();
	}

	public static Object get(String path) {
		return configuration.get(path);
	}

	public static String getString(String path) {
		return configuration.getString(path);
	}

	public static int getInt(String path) {
		return configuration.getInt(path);
	}

	public static ConfigurationSection getSection(String path) {
		return configuration.getConfigurationSection(path);
	}

	public static void save() {
		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}