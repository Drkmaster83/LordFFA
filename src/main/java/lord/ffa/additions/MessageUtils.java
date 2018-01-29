package lord.ffa.additions;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lord.ffa.plugin.FFA;

public class MessageUtils {
	private static String prefix = null;

	static {
		prefix = cc(FFA.getInstance().getConfig().getString("Prefix"));
	}

	public static String cc(String str)
	{
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String getString(String str)
	{
		if(prefix == null) prefix = cc(FFA.getInstance().getConfig().getString("Prefix", ""));
		return cc(str.replace("%prefix%", prefix));
	}

	public static void sendMessage(Player p, String path) {
		Object o = FFA.getInstance().getConfig().get("Messages."+path);
		if(o == null) {
			FFA.getInstance().getLogger().warning("Message \"" + path + "\" is not defined in config - contact plugin developer if this is unexpected!");
			return;
		}
		if(o instanceof String) {
			String s = (String) o;
			if(s.isEmpty()) return; //Simple; don't send a blank message!
			p.sendMessage(getString(s));
		}
		else if(o instanceof List) {
			List<String> l = (List<String>) o;
			for(int i = 0; i < l.size(); i++) {
				p.sendMessage(getString(l.get(i)));
			}
		}
	}
	
	public static void msg(Player p, String text) {
		p.sendMessage(getString(text));
	}
}