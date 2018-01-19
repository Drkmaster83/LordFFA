package lord.ffa.main.stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lord.ffa.main.FFA;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Stats {
	public static boolean playerExists(String uuid) {
		if (FFA.getMySQL().isOpened()) {
			try {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA WHERE UUID= '" + uuid + "'");
				if (rs.next()) {
					return rs.getString("UUID") != null;
				}
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return FileSystem.get("Users." + uuid) != null;

		return Boolean.valueOf(false).booleanValue();
	}

	public static void createPlayer(String uuid) {
		if (!playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("INSERT INTO LORDFFA(UUID, KILLS, DEATHS, POINTS, NAME) VALUES ('" + uuid
						+ "', '0', '0', '100', '" + Bukkit.getPlayer(UUID.fromString(uuid)).getName() + "');");
			} else {
				FileSystem.set("Users." + uuid + ".Name", Bukkit.getPlayer(UUID.fromString(uuid)).getName());
				FileSystem.set("Users." + uuid + ".Kills", Integer.valueOf(0));
				FileSystem.set("Users." + uuid + ".Deaths", Integer.valueOf(0));
				FileSystem.set("Users." + uuid + ".Points", Integer.valueOf(100));
			}
		} else {
			setName(uuid, Bukkit.getPlayer(UUID.fromString(uuid)).getName());
		}
	}

	public static String getName(String uuid) {
		String i = "";
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				try {
					ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA WHERE UUID= '" + uuid + "'");
					if ((rs.next()) && (rs.getString("NAME") == null)) {
					}
					i = rs.getString("NAME");

				} catch (SQLException localSQLException) {
				}
			} else {
				return FileSystem.getString("Users." + uuid + ".Name");
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
				FFA.mysql.update("UPDATE LORDFFA SET NAME= '" + name + "' WHERE UUID= '" + uuid + "';");
			} else {
				FileSystem.set("Users." + uuid + ".Name", name);
			}
		} else {
			createPlayer(uuid);
			setName(uuid, name);
		}
	}

	public static ArrayList<String> getTopPlayers() {
		ArrayList<String> top = new ArrayList();
		if (FFA.getMySQL().isOpened()) {
			ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA ORDER BY KILLS desc LIMIT 10");
			try {
				while (rs.next()) {
					top.add(rs.getString("UUID"));
				}
			} catch (SQLException localSQLException) {
			}
		} else {
			HashMap<String, Integer> tops = new HashMap();

			for (String str : FileSystem.getSection("Users").getKeys(false)) {
				tops.put(str, getKills(str));
			}

			String nextTop = "";
			Integer nextTopKills = Integer.valueOf(-1);
			for (int i = 1; i < 11; i++) {
				for (String str : tops.keySet()) {
					if (((Integer) tops.get(str)).intValue() > nextTopKills.intValue()) {
						nextTop = str;
						nextTopKills = (Integer) tops.get(str);
					}
				}
				if (!nextTop.equals("")) {
					top.add(nextTop);
				}
				tops.remove(nextTop);
				nextTop = "";
				nextTopKills = Integer.valueOf(-1);
			}
		}
		return top;
	}

	public static int getRanking(String uuid) {
		int Ranking = 1;

		if (!playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA ORDER BY Wins desc");

				try {
					while (rs.next()) {
						if (rs.getString("UUID").equalsIgnoreCase(uuid)) {
							return Ranking;
						}
						Ranking++;
					}
				} catch (SQLException localSQLException) {
				}
			}

			HashMap<String, Integer> top = new HashMap();

			for (String str : FileSystem.getSection("Users").getKeys(false)) {
				top.put(str, getKills(str));
			}

			int playerRank = 0;
			if (top.containsKey(uuid)) {
				playerRank++;
				int playerKills = ((Integer) top.get(uuid)).intValue();
				for (String playerName : top.keySet()) {
					if ((!playerName.equalsIgnoreCase(uuid))
							&& (((Integer) top.get(playerName)).intValue() > playerKills)) {
						playerRank++;
					}
				}
			}
			return playerRank++;
		}

		createPlayer(uuid);
		return getRanking(uuid);

		return Ranking;
	}

	public static Integer getKills(String uuid) {
		Integer i = Integer.valueOf(0);
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				try {
					ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA WHERE UUID= '" + uuid + "'");
					if ((rs.next()) && (Integer.valueOf(rs.getInt("KILLS")) == null)) {
					}
					i = Integer.valueOf(rs.getInt("KILLS"));

				} catch (SQLException localSQLException) {
				}
			} else {
				return Integer.valueOf(FileSystem.getInt("Users." + uuid + ".Kills"));
			}

		} else {
			createPlayer(uuid);
			return getKills(uuid);
		}
		return i;
	}

	public static Integer getDeaths(String uuid) {
		Integer i = Integer.valueOf(0);
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				try {
					ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA WHERE UUID= '" + uuid + "'");
					if ((rs.next()) && (Integer.valueOf(rs.getInt("DEATHS")) == null)) {
					}
					i = Integer.valueOf(rs.getInt("DEATHS"));

				} catch (SQLException localSQLException) {
				}
			} else {
				return Integer.valueOf(FileSystem.getInt("Users." + uuid + ".Deaths"));
			}

		} else {
			createPlayer(uuid);
			return getDeaths(uuid);
		}
		return i;
	}

	public static Integer getPoints(String uuid) {
		Integer i = Integer.valueOf(0);
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				try {
					ResultSet rs = FFA.getMySQL().query("SELECT * FROM LORDFFA WHERE UUID= '" + uuid + "'");
					if ((rs.next()) && (Integer.valueOf(rs.getInt("POINTS")) == null)) {
					}
					i = Integer.valueOf(rs.getInt("POINTS"));

				} catch (SQLException localSQLException) {
				}
			} else {
				return Integer.valueOf(FileSystem.getInt("Users." + uuid + ".Points"));
			}

		} else {
			createPlayer(uuid);
			return getPoints(uuid);
		}
		return i;
	}

	public static void setKills(String uuid, Integer kills) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE LORDFFA SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
			} else {
				FileSystem.set("Users." + uuid + ".Kills", kills);
			}
		} else {
			createPlayer(uuid);
			setKills(uuid, kills);
		}
	}

	public static void setDeaths(String uuid, Integer deaths) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE LORDFFA SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
			} else {
				FileSystem.set("Users." + uuid + ".Deaths", deaths);
			}

		} else {
			createPlayer(uuid);
			setDeaths(uuid, deaths);
		}
	}

	public static void setPoints(String uuid, Integer points) {
		if (playerExists(uuid)) {
			if (FFA.getMySQL().isOpened()) {
				FFA.getMySQL().update("UPDATE LORDFFA SET POINTS= '" + points + "' WHERE UUID= '" + uuid + "';");
			} else {
				FileSystem.set("Users." + uuid + ".Points", points);
			}

		} else {
			createPlayer(uuid);
			setPoints(uuid, points);
		}
	}

	public static void addKills(String uuid, Integer kills) {
		if (playerExists(uuid)) {
			setKills(uuid, Integer.valueOf(getKills(uuid).intValue() + kills.intValue()));
		} else {
			createPlayer(uuid);
			addKills(uuid, kills);
		}
	}

	public static void addDeaths(String uuid, Integer deaths) {
		if (playerExists(uuid)) {
			setDeaths(uuid, Integer.valueOf(getDeaths(uuid).intValue() + deaths.intValue()));
		} else {
			createPlayer(uuid);
			addDeaths(uuid, deaths);
		}
	}

	public static void addPoints(String uuid, Integer points) {
		if (playerExists(uuid)) {
			setPoints(uuid, Integer.valueOf(getPoints(uuid).intValue() + points.intValue()));
		} else {
			createPlayer(uuid);
			addPoints(uuid, points);
		}
	}
}