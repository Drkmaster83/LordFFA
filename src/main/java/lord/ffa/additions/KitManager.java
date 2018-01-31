package lord.ffa.additions;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import lord.ffa.plugin.FFA;

public class KitManager {
	public static void addKit(String name, PlayerInventory inv, String permission) {
		FFA.getKits().set("Kits." + name + ".item.type", "STONE:0");
		FFA.getKits().set("Kits." + name + ".item.slot", FFA.getKits().getConfigurationSection("Kits").getKeys(false).size());
		FFA.getKits().set("Kits." + name + ".item.displayname", "&9"+name);
		FFA.getKits().set("Kits." + name + ".Message", "%prefix% &aYou have recevied &9"+name+" &akit.");
		FFA.getKits().set("Kits." + name + ".Permission", permission);
		FFA.getKits().set("Kits." + name + ".Armor", InventoryUtils.itemsToString(inv.getArmorContents()));
		FFA.getKits().setAndSave("Kits." + name + ".Inventory", InventoryUtils.itemsToString(inv.getContents()));
	}

	public static String getKitMessage(String kit) {
		if (!kitExists(kit)) {
			return null;
		}
		return MessageUtils.formatString(FFA.getKits().getString("Kits." + kit + ".Message"));
	}

	public static ItemStack getKitInvItem(String kit) {
		if (!kitExists(kit)) {
			return null;
		}
		String[] str = FFA.getKits().getString("Kits." + kit + ".item.type").split(":");
		ItemStack g = new ItemStack(Material.getMaterial(str[0]), 1,
				Byte.valueOf(str[1]));
		ItemMeta gMeta = g.getItemMeta();
		gMeta.setDisplayName(MessageUtils.cc(FFA.getKits().getString("Kits." + kit + ".item.displayname")));
		g.setItemMeta(gMeta);
		return g;
	}

	public static int getKitSlot(String kit) {
		if (!kitExists(kit)) {
			return -1;
		}

		return FFA.getKits().getInt("Kits." + kit + ".item.slot") - 1;
	}

	public static String getKitPermission(String kit) {
		if (!kitExists(kit)) {
			return null;
		}
		return FFA.getKits().getString("Kits." + kit + ".Permission");
	}

	public static HashMap<Integer, ItemStack> getKitContents(String kit, String type) {
		if (!kitExists(kit)) {
			return null;
		}
		return InventoryUtils.itemsFromString(FFA.getKits().getString("Kits." + kit + "." + type));
	}

	public static boolean kitExists(String kit) {
		return FFA.getKits().get("Kits." + kit) != null;
	}
}