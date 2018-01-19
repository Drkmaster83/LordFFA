package lord.ffa.main.additions;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Splitter;

import lord.ffa.main.FFA;

public class Scoreboard {
	private String title = "";
	private org.bukkit.scoreboard.Scoreboard scoreboard;
	private HashMap<String, Integer> Scores;
	private Player p;

	public Scoreboard(Player p) {
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.Scores = new HashMap<>();
		this.p = p;
	}

	public void setTitle(String title) {
		this.title = FFA.getString(title);
	}

	public void addScore(String score, int i) {
		this.Scores.put(FFA.getString(fixScore(score)), i);
	}

	private String fixScore(String text) {
		if (this.Scores.containsKey(text)) {
			text = text + FFA.getString("&r");
		}
		if (text.length() > 48) {
			text.substring(0, 47);
		}
		return text;
	}

	@SuppressWarnings("deprecation")
	public void build() {
		Objective o = this.scoreboard.registerNewObjective("test", "dummy"); //what
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(this.title);

		for (String score : this.Scores.keySet()) {
			Team team = this.scoreboard.registerNewTeam("Scores" + this.Scores.get(score));
			OfflinePlayer player = Bukkit.getOfflinePlayer(score);

			if (score.length() > 16) {
				Iterator<String> iterator = Splitter.fixedLength(16).split(score).iterator();
				team.setPrefix(iterator.next());
				player = Bukkit.getOfflinePlayer(iterator.next());

				team.addPlayer(player);

				if (score.length() > 32) {
					team.setSuffix(iterator.next());
				}
			}
			o.getScore(player).setScore(this.Scores.get(score).intValue());
		}
	}

	public void sendScoreboard() {
		this.p.setScoreboard(this.scoreboard);
	}

	public void clearScoreboard() {
		this.p.setScoreboard(null);
	}
}