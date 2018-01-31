package lord.ffa.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;

import lord.ffa.plugin.FFA;

public class MySQL {
	private Connection conn = null;

	public void connect() {
		try {
			FFA inst = FFA.getInstance();
			FileConfiguration conf = inst.getConfig();
			this.conn = DriverManager.getConnection(
					"jdbc:mysql://" + conf.getString("MySQL.Host") + ":"
							+ conf.getInt("MySQL.Port") + "/"
							+ conf.getString("MySQL.Database") + "?autoReconnect=true",
					conf.getString("MySQL.Username"),
					conf.getString("MySQL.Password"));

			inst.getLogger().info("Connection to MySQL was successful!");
		} catch (SQLException e) {
			this.conn = null;
			FFA.getInstance().getLogger().warning("(Unable to connect MySQL) Disconnected from driver, reason:" + e.getMessage());
		}
	}

	public boolean isOpened() {
		return this.conn != null;
	}

	public void close() {
		try {
			if (this.conn != null) {
				this.conn.close();
				FFA.getInstance().getLogger().info("MySQL connection closed successfully!");
			}
		} catch (SQLException e) {
			FFA.getInstance().getLogger().severe("Error while closing MySQL connection, reason:" + e.getMessage());
		}
	}

	public void update(String qry) {
		try {
			Statement st = this.conn.createStatement();
			st.executeUpdate(qry);
			st.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement st = this.conn.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			System.err.println(e);
		}
		return rs;
	}
}