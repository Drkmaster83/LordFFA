package lord.ffa.main.additions;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class InventoryUtils {
	public static String ItemstoString(ItemStack[] inv) {
		String inventory = "";
		int i = 0;
		ItemStack[] arrayOfItemStack = inv;
		int j = inv.length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			String data = "";
			try {
				data = item.getTypeId() + ":" + item.getData().getData() + ":" + item.getAmount() + ":"
						+ item.getDurability() + ":" + i;
			} catch (NullPointerException e) {
				if (inventory.equals("")) {
					inventory = "0";
				} else {
					inventory = inventory + "/" + "0";
				}

				i++;

				continue;
			}
			String displayname = "";
			try {
				item.getItemMeta().getDisplayName();
				item.getItemMeta().hasDisplayName();
				displayname = item.getItemMeta().getDisplayName().toString().replace("§", "&");
			} catch (NullPointerException localNullPointerException1) {
			}

			String enchantment = "";
			try {
				item.getItemMeta().getEnchants();
				item.getItemMeta().hasEnchants();
				for (Enchantment enchant : item.getEnchantments().keySet()) {
					if (enchantment.equals("")) {
						enchantment = enchant.getId() + "," + item.getEnchantmentLevel(enchant);
					} else {
						enchantment = enchantment + "@" + enchant.getId() + "," + item.getEnchantmentLevel(enchant);
					}
				}
				if (inventory.equals("")) {
					inventory = data + ";" + displayname + ";" + enchantment;
				} else {
					inventory = inventory + "/" + data + ";" + displayname + ";" + enchantment;
				}
			} catch (NullPointerException e) {
				if (inventory.equals("")) {
					inventory = data + ";" + displayname;
				} else {
					inventory = inventory + "/" + data + ";" + displayname;
				}
			}

			i++;
		}

		return inventory;
	}

	public static HashMap<Integer, ItemStack> ItemsFromString(String str) {
		String[] items = str.split("/");

		HashMap<Integer, ItemStack> ite = new HashMap();
		String[] arrayOfString1;
		int j = (arrayOfString1 = items).length;
		for (int i = 0; i < j; i++) {
			String item = arrayOfString1[i];
			if (!item.equalsIgnoreCase("0")) {
				String[] string = item.split(";");

				String[] data = string[0].split(":");
				int id = Integer.valueOf(data[0]).intValue();
				byte itemData = Byte.valueOf(data[1]).byteValue();
				int amount = Integer.valueOf(data[2]).intValue();
				short durability = Short.valueOf(data[3]).shortValue();
				int slot = Integer.valueOf(data[4]).intValue();
				String displayname = "";
				if (string.length > 1) {
					displayname = string[1].replace("&", "§");
				}

				ItemStack toItem = new ItemStack(Material.getMaterial(id), amount, durability, Byte.valueOf(itemData));
				ItemMeta tometa = toItem.getItemMeta();
				if (string.length > 1) {
					tometa.setDisplayName(displayname);
					toItem.setItemMeta(tometa);
				}
				if (string.length > 2) {
					String[] enchantments = string[2].split("@");
					String[] arrayOfString2;
					int m = (arrayOfString2 = enchantments).length;
					for (int k = 0; k < m; k++) {
						String enchantment = arrayOfString2[k];
						String[] enchant = enchantment.split(",");

						Enchantment ench = Enchantment.getById(Integer.valueOf(enchant[0]).intValue());
						int level = Integer.valueOf(enchant[1]).intValue();

						toItem.addUnsafeEnchantment(ench, level);
					}
				}
				ite.put(Integer.valueOf(slot), toItem);
			}
		}

		return ite;
	}
}