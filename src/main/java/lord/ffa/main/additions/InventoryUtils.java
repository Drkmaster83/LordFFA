package lord.ffa.main.additions;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtils {
	@SuppressWarnings("deprecation")
	public static String itemsToString(ItemStack[] invContents) {
		String invToString = "";
		ItemStack[] items = invContents.clone();
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if(item == null || item.getType() == Material.AIR) {
				if (invToString.equals("")) { //First item is null
					invToString = "null";
				} else {
					invToString += "/" + "null";
				}
				continue;
			}
			String data = item.getTypeId() + ":" + item.getData().getData() + ":" + item.getAmount() + ":" + item.getDurability() + ":" + i;
			String displayName = !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()
					? "" : item.getItemMeta().getDisplayName().replace("§", "&");

			String enchantment = "";
			for (Enchantment enchant : item.getEnchantments().keySet()) {
				if (enchantment.equals("")) {
					enchantment = enchant.getId() + "," + item.getEnchantmentLevel(enchant);
				} else {
					enchantment = enchantment + "@" + enchant.getId() + "," + item.getEnchantmentLevel(enchant);
				}
			}
			if (invToString.equals("")) {
				invToString = data + ";" + displayName + ";" + enchantment;
			} else {
				invToString += "/" + data + ";" + displayName + ";" + enchantment;
			}
		}

		return invToString;
	}

	@SuppressWarnings("deprecation")
	public static HashMap<Integer, ItemStack> itemsFromString(String str) { //<InvSlot, ItemStack>
		String[] items = str.split("/");

		HashMap<Integer, ItemStack> invMap = new HashMap<>();
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			if (item.equalsIgnoreCase("null")) continue;
			String[] string = item.split(";");

			String[] data = string[0].split(":");
			int id = Integer.valueOf(data[0]).intValue();
			byte itemData = Byte.valueOf(data[1]).byteValue();
			int amount = Integer.valueOf(data[2]).intValue();
			short durability = Short.valueOf(data[3]).shortValue();
			int slot = Integer.valueOf(data[4]).intValue();
			String displayName = "";
			if (string.length > 1) {
				displayName = string[1].replace("&", "§");
			}

			ItemStack toItem = new ItemStack(Material.getMaterial(id), amount, durability, Byte.valueOf(itemData));
			ItemMeta tometa = toItem.getItemMeta();
			if (string.length > 1) {
				tometa.setDisplayName(displayName);
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
			invMap.put(Integer.valueOf(slot), toItem);
		}
		return invMap;
	}
}