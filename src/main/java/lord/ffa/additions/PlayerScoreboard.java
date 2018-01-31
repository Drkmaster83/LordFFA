package lord.ffa.additions;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerScoreboard {
	private String title = "";
	private Scoreboard scoreboard;
	private HashMap<String, Integer> scores;
	private Player p;

	public PlayerScoreboard(Player p) {
		this.scoreboard = p.getServer().getScoreboardManager().getNewScoreboard();
		this.scores = new HashMap<>();
		this.p = p;
		this.title = "";
	}

	public void setTitle(String title) {
		this.title = MessageUtils.formatString(title);
	}

	public void addScore(String score, int i) {
		this.scores.put(MessageUtils.formatString(fixScore(score)), i);
	}

	private String fixScore(String text) {
		if (this.scores.containsKey(text)) {
			text += "&r";
		}
		if (text.length() > 48) {
			text.substring(0, 47);
		}
		return text;
	}

	public void build() {
		Objective o = this.scoreboard.registerNewObjective("ffaObj", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(this.title);

		for (String score : this.scores.keySet()) {
			/*Team team = this.scoreboard.registerNewTeam("Scores" + this.scores.get(score));
			OfflinePlayer player = Bukkit.getOfflinePlayer(score);

			if (score.length() > 16) {
				Iterator<String> iterator = Splitter.fixedLength(16).split(score).iterator();
				team.setPrefix(iterator.next());
				player = Bukkit.getOfflinePlayer(iterator.next());

				team.addPlayer(player);

				if (score.length() > 32) {
					team.setSuffix(iterator.next());
				}
			}*/
			o.getScore(score).setScore(this.scores.get(score));
		}
	}

	public void sendScoreboard() {
		this.p.setScoreboard(this.scoreboard);
	}

	public void clearScoreboard() {
		this.p.setScoreboard(null);
	}
}