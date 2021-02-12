package bowling.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Player {
	
	@Id
	@GeneratedValue
	private int playerID;
	private String playerName;
	private boolean isWinner;
	
	@OneToOne(mappedBy = "player", cascade = {CascadeType.ALL})
	private ScoreBoard scoreboard;
	
	protected Player() {
		
	}
	
	public Player(int playerID, String playerName, boolean isWinner) {
		super();
		this.playerID = playerID;
		this.playerName = playerName;
		this.isWinner = isWinner;
	}

	public int getPlayerID() {
		return playerID;
	}
	
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public ScoreBoard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(ScoreBoard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}
	
}
