package lord.ffa.additions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YMLConfig extends YamlConfiguration
{
	private JavaPlugin pl;
	private File file;
	
	public YMLConfig(JavaPlugin pl, String name) {
		this.pl = pl;
		if(name == null || name.isEmpty()) throw new IllegalArgumentException("YAML file must have a name!");
		if(!name.endsWith(".yml")) name += ".yml";
		file = new File(pl.getDataFolder(), name);
		reload();
	}
	
	/** Reload config object in RAM to that of the file - useful if developer wants changes made to config yml to be brought in */
	public boolean reload() {
		boolean existed = file.exists();
		if(!file.exists()) {
			if(!file.getParentFile().exists()) { // Create parent folders if they don't exist
				file.getParentFile().mkdirs();
			}
			//TODO pl.getLogger().info(file.getName() + " not found, creating one for you...");
            if(pl.getResource(file.getName()) != null) {
				pl.saveResource(file.getName(), true); // Save the one from the JAR if possible
			}
			else {
				try { file.createNewFile(); } // Create a blank file if there's not one to copy from the JAR
				catch (IOException e) { e.printStackTrace(); }
			}
		}
		try { this.load(new InputStreamReader(new FileInputStream(file), "UTF-8")); }
		catch (Exception e) {
        	pl.getLogger().severe("An error occurred while loading " + file.getName() + " on disk into RAM. This could be due to improper YAML syntax. Copy-paste the file into http://yaml-online-parser.appspot.com/ to check the syntax.");
			e.printStackTrace();
		}
		if(pl.getResource(file.getName()) != null) { // Set up defaults in case their config is borked.
			InputStreamReader defConfigStream = null;
	        try {
	            defConfigStream = new InputStreamReader(pl.getResource(file.getName()), "UTF-8");
	        } catch (UnsupportedEncodingException e) {
	        	pl.getLogger().severe("Unable to add default configuration options to " + file.getName());
	        	e.printStackTrace();
	        }
	        this.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
		}
		return existed;
	}
	
	/** Save the config object in RAM to the file - overwrites any changes that the configurator has made to the file unless reload() has been called since */
	public void save() {
		try	{ this.save(file); }
		catch (Exception e)	{
			pl.getLogger().severe("An error occurred while saving " + file.getName() + " from RAM to disk.");
			e.printStackTrace();
		}
	}
	
	public void setAndSave(String path, Object o) {
		this.set(path, o);
		this.save();
	}
	
	public File getFile() {
		return file;
	}
}