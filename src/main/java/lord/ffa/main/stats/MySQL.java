package lord.ffa.main.stats;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lord.ffa.main.FFA;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {
	public Connection conn = null;

	public void connect() {
		try {
			this.conn = DriverManager.getConnection(
					"jdbc:mysql://" + FFA.getInstance().getConfig().getString("MySQL.Host") + ":"
							+ FFA.getInstance().getConfig().getInt("MySQL.Port") + "/"
							+ FFA.getInstance().getConfig().getString("MySQL.Database") + "?autoReconnect=true",
					FFA.getInstance().getConfig().getString("MySQL.Username"),
					FFA.getInstance().getConfig().getString("MySQL.Password"));

			System.out.println("[LordFFA] MySQL Connection Connected to Driver Successfully");
		} catch (SQLException e) {
			this.conn = null;
			System.out.println("[LordFFA] Disconnected from Driver reason :" + e.getMessage());
		}
	}

	public boolean isOpened() {
		return this.conn != null;
	}

	public void close() {
		try {
			if (this.conn != null) {
				this.conn.close();
				System.out.println("[LordFFA] MySQL Connection Closed from Driver Successfully.");
			}
		} catch (SQLException e) {
			System.out.println("[LordFFA] Erorr while we disconnect to MySQL " + e.getMessage());
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