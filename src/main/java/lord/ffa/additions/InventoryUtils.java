package lord.ffa.additions;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtils {
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
			//IRON_SWORD:5:1:16:SLOT
			@SuppressWarnings("deprecation")
			String data = item.getType().name() + ":" + item.getData().getData() + ":" + item.getAmount() + ":" + item.getDurability() + ":" + i;
			String displayName = !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()
					? "" : ChatColor.stripColor(item.getItemMeta().getDisplayName());

			String enchantment = "";
			for (Enchantment enchant : item.getEnchantments().keySet()) {
				if (enchantment.equals("")) {
					enchantment = enchant.getName() + "," + item.getEnchantmentLevel(enchant);
				} else {
					enchantment += "@" + enchant.getName() + "," + item.getEnchantmentLevel(enchant);
				} //Sharpness,5@Arrow_Infinite,1
			}
			if (invToString.equals("")) {
				invToString = data + ";" + displayName + ";" + enchantment;
			} else {
				invToString += "/" + data + ";" + displayName + ";" + enchantment;
			}
			//IRON_SWORD:5:1:16:SLOT;RIGHTEOUS_HAMMER;SHARPNESS,5@ARROW_INFINITE,1
			// /DIAMOND_ORE:5:1:6:SLOT;POTATO-EYITEM;
		}

		return invToString;
	}

	public static HashMap<Integer, ItemStack> itemsFromString(String str) { //<InvSlot, ItemStack>
		String[] items = str.split("/");

		HashMap<Integer, ItemStack> invMap = new HashMap<>();
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			if (item.equalsIgnoreCase("null")) continue;
			String[] string = item.split(";");

			String[] data = string[0].split(":");
			String name = data[0];
			byte itemData = Byte.valueOf(data[1]).byteValue();
			int amount = Integer.valueOf(data[2]).intValue();
			short durability = Short.valueOf(data[3]).shortValue();
			int slot = Integer.valueOf(data[4]).intValue();
			String displayName = "";
			if (string.length > 1) {
				displayName = ChatColor.translateAlternateColorCodes('&', string[1]);
			}

			@SuppressWarnings("deprecation")
			ItemStack toItem = new ItemStack(Material.getMaterial(name), amount, durability, itemData);
			ItemMeta tometa = toItem.getItemMeta();
			if (string.length > 1 && !displayName.isEmpty()) {
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

					Enchantment ench = Enchantment.getByName(enchant[0]);
					int level = Integer.valueOf(enchant[1]).intValue();

					toItem.addUnsafeEnchantment(ench, level);
				}
			}
			invMap.put(slot, toItem);
		}
		return invMap;
	}
}