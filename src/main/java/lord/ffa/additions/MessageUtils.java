package lord.ffa.additions;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lord.ffa.plugin.FFA;

public class MessageUtils {
	private static String prefix = null;

	static {
		refresh();
	}

	public static String cc(String str) {
		if(str == null || str.isEmpty()) return "";
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String formatString(String str) {
		if(str == null || str.isEmpty()) return "";
		if(prefix == null) refresh();
		return cc(str.replace("%prefix%", prefix));
	}

	public static void sendMessage(Player p, String path) {
		Object o = FFA.getDefConfig().get("Messages."+path);
		if(o == null) {
			FFA.getInstance().getLogger().warning("(sendMessage method) Message \"" + path + "\" is not defined in config - contact plugin developer if this is unexpected!");
			return;
		}
		if(o instanceof String) {
			String s = (String) o;
			if(s.isEmpty()) return; //Simple; don't send a blank message!
			p.sendMessage(formatString(s));
		}
		else if(o instanceof List) {
			List<String> l = (List<String>) o;
			for(int i = 0; i < l.size(); i++) {
				p.sendMessage(formatString(l.get(i)));
			}
		}
	}
	
	public static String getMessage(String path) {
		String s = FFA.getDefConfig().getString("Messages."+path);
		if(s == null) {
			FFA.getInstance().getLogger().warning("(getMessage method) Message \"" + path + "\" is not defined in config - contact plugin developer if this is unexpected!");
		}
		return formatString(FFA.getDefConfig().getString("Messages."+path));
	}
	
	public static void msg(Player p, String text) {
		if(text == null || text.isEmpty()) return;
		p.sendMessage(formatString(text));
	}
	
	public static void refresh() {
		prefix = cc(FFA.getDefConfig().getString("Prefix", ""));
	}
}