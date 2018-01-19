package lord.ffa.main.stats;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileSystem {
	public static File path = new File("plugins/LordFFA");
	public static File file = new File("plugins/LordFFA", "Stats.yml");
	public static FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

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

	public static void set(String o, Object value) {
		configuration.set(o, value);
		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}