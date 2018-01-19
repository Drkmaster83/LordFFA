package lord.ffa.main.additions;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import lord.ffa.main.FFA;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Scoreboard {
	public String title = "";
	public org.bukkit.scoreboard.Scoreboard scoreboard;
	public HashMap<String, Integer> Scores;
	public ArrayList<BukkitRunnable> refreshes = new ArrayList();
	public Player p;

	public Scoreboard() {
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.Scores = new HashMap();
	}

	public void setPlayer(Player p) {
		this.p = p;
	}

	public void setTitle(String title) {
		this.title = FFA.getString(title);
	}

	public void addScore(String score, Integer i) {
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

	public void ScoreBuilder() {
		Objective o = this.scoreboard.registerNewObjective("test", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(this.title);

		for (String score : this.Scores.keySet()) {
			Team team = this.scoreboard.registerNewTeam("Scores" + this.Scores.get(score));
			OfflinePlayer player = Bukkit.getOfflinePlayer(score);

			if (score.length() > 16) {
				Iterator<String> iterator = Splitter.fixedLength(16).split(score).iterator();
				team.setPrefix((String) iterator.next());
				player = Bukkit.getOfflinePlayer((String) iterator.next());

				team.addPlayer(player);

				if (score.length() > 32) {
					team.setSuffix((String) iterator.next());
				}
			}

			o.getScore(player).setScore(((Integer) this.Scores.get(score)).intValue());
		}
	}

	public void sendScoreboad() {
		this.p.setScoreboard(this.scoreboard);
	}

	public void clearScoreboard() {
		for (BukkitRunnable run : this.refreshes) {
			run.cancel();
		}
		this.p.setScoreboard(null);
	}
}