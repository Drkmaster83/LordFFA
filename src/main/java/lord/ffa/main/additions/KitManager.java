package lord.ffa.main.additions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import lord.ffa.main.FFA;

public class KitManager {
	private static File path = new File("plugins/LordFFA");
	private static File file = new File("plugins/LordFFA", "Kits.yml");
	private static FileConfiguration configuration = YamlConfiguration.loadConfiguration(file); //TODO possible NPE

	public static void setupKitFile() {
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

	public static String getString(String path) {
		return configuration.getString(path);
	}

	public static int getInt(String path) {
		return configuration.getInt(path);
	}

	public static Object get(String path) {
		return configuration.get(path);
	}

	public static void addKit(String name, PlayerInventory inv, String permission) {
		set("Kits." + name + ".item.type", "1:0");
		set("Kits." + name + ".item.slot", getSection("Kits").getKeys(false).size());
		set("Kits." + name + ".item.displayname", "&9Test");
		set("Kits." + name + ".Message", "%prefix% &aYou have recevied &9Test &akit.");
		set("Kits." + name + ".Permission", permission);
		set("Kits." + name + ".Armor", InventoryUtils.itemsToString(inv.getArmorContents()));
		set("Kits." + name + ".Inventory", InventoryUtils.itemsToString(inv.getContents()));
	}

	public static String getKitMessage(String kit) {
		if (!KitExists(kit)) {
			return null;
		}
		return FFA.getString(getString("Kits." + kit + ".Message"));
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getKitItem(String kit) {
		if (!KitExists(kit)) {
			return null;
		}
		String[] str = getString("Kits." + kit + ".item.type").split(":");
		ItemStack g = new ItemStack(Material.getMaterial(Integer.valueOf(str[0]).intValue()), 1,
				Byte.valueOf(str[1]).byteValue());
		ItemMeta gMeta = g.getItemMeta();
		gMeta.setDisplayName(FFA.getString(getString("Kits." + kit + ".item.displayname")));
		g.setItemMeta(gMeta);
		return g;
	}

	public static int getKitSlot(String kit) {
		if (!KitExists(kit)) {
			return -1;
		}

		return getInt("Kits." + kit + ".item.slot") - 1;
	}

	public static String getKitPermission(String kit) {
		if (!KitExists(kit)) {
			return null;
		}
		return getString("Kits." + kit + ".Permission");
	}

	public static HashMap<Integer, ItemStack> getKitContents(String kit, String type) {
		if (!KitExists(kit)) {
			return null;
		}
		return InventoryUtils.itemsFromString(getString("Kits." + kit + "." + type));
	}

	public static boolean KitExists(String kit) {
		return get("Kits." + kit) != null;
	}

	public static ConfigurationSection getSection(String path) {
		return configuration.getConfigurationSection(path);
	}
}