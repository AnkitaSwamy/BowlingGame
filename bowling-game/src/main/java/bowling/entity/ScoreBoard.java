package bowling.entity;

import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ScoreBoard {
	
	@Id
	@GeneratedValue
	private int id;
	private Integer currentScore;
	private Integer lane;
	private Integer totalScore;
	private Integer totalStrikes;
	private Integer currentStrikes;
	private Integer setsCompleted;
	private Integer chancesInSet;
	private Integer gameID;
	
	private int[] bowlingPins ;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Player player; 
	
	public ScoreBoard() {
		
	}
	
	public ScoreBoard(int id, Integer currentScore, Integer lane, Integer totalScore, Integer totalStrikes,
			Integer currentStrikes, Integer setsCompleted, Integer chancesInSet, Integer gameID, int[] bowlingPins,
			Player player) {
		this.id = id;
		this.currentScore = currentScore;
		this.lane = lane;
		this.totalScore = totalScore;
		this.totalStrikes = totalStrikes;
		this.currentStrikes = currentStrikes;
		this.setsCompleted = setsCompleted;
		this.chancesInSet = chancesInSet;
		this.gameID = gameID;
		this.bowlingPins = bowlingPins;
		this.player = player;
	}

	public Integer getCurrentScore() {
		return currentScore;
	}
	
	public void setCurrentScore(Integer currentScore) {
		this.currentScore = currentScore;
	}
	
	public Integer getLane() {
		return lane;
	}
	
	public void setLane(Integer lane) {
		this.lane = lane;
	}
	
	public Integer getTotalScore() {
		return totalScore;
	}
	
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}
	
	public Integer getTotalStrikes() {
		return totalStrikes;
	}
	
	public void setTotalStrikes(Integer totalStrikes) {
		this.totalStrikes = totalStrikes;
	}
	
	public Integer getCurrentStrikes() {
		return currentStrikes;
	}
	
	public void setCurrentStrikes(Integer currentStrikes) {
		this.currentStrikes = currentStrikes;
	}
	
	public Integer getSetsCompleted() {
		return setsCompleted;
	}

	public void setSetsCompleted(Integer setsCompleted) {
		this.setsCompleted = setsCompleted;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Integer getChancesInSet() {
		return chancesInSet;
	}

	public void setChancesInSet(Integer chancesInSet) {
		this.chancesInSet = chancesInSet;
	}

	public int[] getBowlingPins() {
		return bowlingPins;
	}

	public void setBowlingPins(int[] bowlingPins) {
		this.bowlingPins = bowlingPins;
	}

	public Integer getGameID() {
		return gameID;
	}

	public void setGameID(Integer gameID) {
		this.gameID = gameID;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", currentScore=" + currentScore + ", lane=" + lane + ", totalScore="
				+ totalScore + ", totalStrikes=" + totalStrikes + ", currentStrikes=" + currentStrikes
				+ ", setsCompleted=" + setsCompleted + ", chancesInSet=" + chancesInSet + ", gameID=" + gameID
				+ ", bowlingPins=" + Arrays.toString(bowlingPins) + "]";
	}
	
	
	
}
