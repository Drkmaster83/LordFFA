package lord.ffa.stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import lord.ffa.plugin.FFA;

public class Stats {
	public static boolean playerExists(String uuid) {
		if (!FFA.getMySQL().isOpened()) return FFA.getStats().get("Users." + uuid) != null;
		try {
			ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA WHERE UUID= '" + uuid + "'");
			if (rs.next()) {
				return rs.getString("UUID") != null;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void createPlayer(String uuid) {
		if (!playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("INSERT INTO FFA(UUID, KILLS, DEATHS, POINTS, NAME) VALUES ('" + uuid
						+ "', '0', '0', '100', '" + Bukkit.getPlayer(UUID.fromString(uuid)).getName() + "');");
			} else {
				FFA.getStats().set("Users." + uuid + ".Name", Bukkit.getPlayer(UUID.fromString(uuid)).getName());
				FFA.getStats().set("Users." + uuid + ".Kills", 0);
				FFA.getStats().set("Users." + uuid + ".Deaths", 0);
				FFA.getStats().setAndSave("Users." + uuid + ".Points", 100);
			}
		} else {
			setName(uuid, Bukkit.getPlayer(UUID.fromString(uuid)).getName());
		}
	}

	public static String getName(String uuid) {
		String i = "";
		if (playerExists(uuid)) {
			if (!FFA.getMySQL().isOpened())	return FFA.getStats().getString("Users." + uuid + ".Name");
			try {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA WHERE UUID= '" + uuid + "'");
				if ((rs.next()) && (rs.getString("NAME") == null)) {
				}
				i = rs.getString("NAME");

			} catch (SQLException e) {
			}

		} else {
			createPlayer(uuid);
			return getName(uuid);
		}
		return i;
	}

	public static void setName(String uuid, String name) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.mysql.update("UPDATE FFA SET NAME= '" + name + "' WHERE UUID= '" + uuid + "';");
			} else {
				FFA.getStats().setAndSave("Users." + uuid + ".Name", name);
			}
		} else {
			createPlayer(uuid);
			setName(uuid, name);
		}
	}

	public static List<String> getTopPlayers() {
		List<String> top = new ArrayList<>();
		if (FFA.getMySQL().isOpened()) {
			ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA ORDER BY KILLS desc LIMIT 10");
			try {
				while (rs.next()) {
					top.add(rs.getString("UUID"));
				}
			} catch (SQLException e) {
			}
		} else {
			HashMap<String, Integer> tops = new HashMap<>();

			for (String str : FFA.getStats().getConfigurationSection("Users").getKeys(false)) {
				tops.put(str, getKills(str));
			}

			String nextTop = "";
			int nextTopKills = -1;
			for (int i = 1; i < 11; i++) {
				for (String str : tops.keySet()) {
					if (tops.get(str) > nextTopKills) {
						nextTop = str;
						nextTopKills = tops.get(str);
					}
				}
				if (!nextTop.equals("")) {
					top.add(nextTop);
				}
				tops.remove(nextTop);
				nextTop = "";
				nextTopKills = -1;
			}
		}
		return top;
	}

	public static int getRanking(String uuid) {
		int Ranking = 1;

		if (!playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA ORDER BY Wins desc");

				try {
					while (rs.next()) {
						if (rs.getString("UUID").equalsIgnoreCase(uuid)) {
							return Ranking;
						}
						Ranking++;
					}
				} catch (SQLException e) {
				}
			}

			HashMap<String, Integer> top = new HashMap<>();

			for (String str : FFA.getStats().getConfigurationSection("Users").getKeys(false)) {
				top.put(str, getKills(str));
			}

			int playerRank = 0;
			if (top.containsKey(uuid)) {
				playerRank++;
				int playerKills = top.get(uuid);
				for (String playerName : top.keySet()) {
					if ((!playerName.equalsIgnoreCase(uuid))
							&& (top.get(playerName) > playerKills)) {
						playerRank++;
					}
				}
			}
			return playerRank++;
		}

		createPlayer(uuid);
		return Ranking;
	}

	public static int getKills(String uuid) {
		int i = 0;
		if (playerExists(uuid)) {
			if (!FFA.getMySQL().isOpened()) return FFA.getStats().getInt("Users." + uuid + ".Kills");
			try {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA WHERE UUID= '" + uuid + "'");
				rs.next(); //TODO Possible exception?
				i = rs.getInt("KILLS");

			} catch (SQLException e) {
			}

		} else {
			createPlayer(uuid);
			return getKills(uuid);
		}
		return i;
	}

	public static int getDeaths(String uuid) {
		int i = 0;
		if (playerExists(uuid)) {
			if (!FFA.getMySQL().isOpened()) return FFA.getStats().getInt("Users." + uuid + ".Deaths");
			try {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA WHERE UUID= '" + uuid + "'");
				rs.next();
				rs.getInt("DEATHS");

			} catch (SQLException e) {
			}

		} else {
			createPlayer(uuid);
			return getDeaths(uuid);
		}
		return i;
	}

	public static int getPoints(String uuid) {
		int i = 0;
		if (playerExists(uuid)) {
			if (!FFA.getMySQL().isOpened()) return FFA.getStats().getInt("Users." + uuid + ".Points");
			try {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM FFA WHERE UUID= '" + uuid + "'");
				rs.next();
				i = rs.getInt("POINTS");

			} catch (SQLException e) {
			}
		} else {
			createPlayer(uuid);
			return getPoints(uuid);
		}
		return i;
	}

	public static void setKills(String uuid, int kills) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE FFA SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
			} else {
				FFA.getStats().setAndSave("Users." + uuid + ".Kills", kills);
			}
		} else {
			createPlayer(uuid);
			setKills(uuid, kills);
		}
	}

	public static void setDeaths(String uuid, int deaths) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE FFA SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
			} else {
				FFA.getStats().setAndSave("Users." + uuid + ".Deaths", deaths);
			}

		} else {
			createPlayer(uuid);
			setDeaths(uuid, deaths);
		}
	}

	public static void setPoints(String uuid, int points) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE FFA SET POINTS= '" + points + "' WHERE UUID= '" + uuid + "';");
			} else {
				FFA.getStats().setAndSave("Users." + uuid + ".Points", points);
			}

		} else {
			createPlayer(uuid);
			setPoints(uuid, points);
		}
	}

	public static void addKills(String uuid, int kills) {
		if (playerExists(uuid)) {
			setKills(uuid, getKills(uuid) + kills);
		} else {
			createPlayer(uuid);
			addKills(uuid, kills);
		}
	}

	public static void addDeaths(String uuid, int deaths) {
		if (playerExists(uuid)) {
			setDeaths(uuid, getDeaths(uuid) + deaths);
		} else {
			createPlayer(uuid);
			addDeaths(uuid, deaths);
		}
	}

	public static void addPoints(String uuid, int points) {
		if (playerExists(uuid)) {
			setPoints(uuid, getPoints(uuid) + points);
		} else {
			createPlayer(uuid);
			addPoints(uuid, points);
		}
	}
}