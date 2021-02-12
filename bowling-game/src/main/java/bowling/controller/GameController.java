package bowling.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import bowling.entity.Player;
import bowling.entity.PlayersRepository;
import bowling.entity.ScoreBoard;
import bowling.entity.ScoreBoardRepository;
import bowling.exception.InvalidPlayException;
import bowling.exception.PlayerNotFound;

@RestController
public class GameController {
	
	@Autowired
	public PlayersRepository playersRepo;
	
	@Autowired
	public ScoreBoardRepository scoreBoardRepo;
	
	private static final Integer noOfLanes = 4;
	private static final Integer noOfBowlingPins = 10;
	private static final Integer maxNoOfPlayers = 12;
	private Integer maxTotalScore =0;
	private ScoreBoard winnerScoreBoard = null; 
	private TreeMap<Integer, Boolean> turn;
	
	@PostMapping(path="/startGame")
	public ResponseEntity<String> startGame(@RequestBody List<Player> players) {
		Random rand = new Random(); 
		Integer gameID = rand.nextInt(100);
		createPlayers(players, gameID);
		return new ResponseEntity<>(
			      "Game is started....Game ID is " + gameID, 
			      HttpStatus.CREATED);
	}
	
	@GetMapping(path="/players")
	public List<Player> getPlayers(){
		return playersRepo.findAll();
	}
	
	@GetMapping(path="/bowling/{gameID}/play/{playerID}")
	public ResponseEntity<String> playTheBowl(@PathVariable Integer playerID, @PathVariable Integer gameID) {
		int score = 0;
		ScoreBoard scoreBoard ;
		Optional<Player> optionalPlayer = playersRepo.findById(playerID);
		if(!optionalPlayer.isEmpty()) {
			Player player = optionalPlayer.get();
			scoreBoard = player.getScoreboard(); 

			if(scoreBoard.getGameID()!=gameID) {
				throw new InvalidPlayException(player.getPlayerName() + " doesn't belong to this game or the gameID is invalid!!!" );
			}
			
			if(scoreBoard.getSetsCompleted()==10) {
				throw new InvalidPlayException("Game is already over!!!!" );
			}
			
			for(Map.Entry<Integer, Boolean> entry : turn.entrySet()) {
				if(entry.getValue()==true && entry.getKey()!=player.getPlayerID()) {
					throw new InvalidPlayException(player.getPlayerName() + "'s chance for this set is yet to come!!!" ); 
				}
			}
			
			// play the bowl
			score = rollTheBowl(score, scoreBoard, player); 
		
			// calculate score
			if(isAStrike(score, scoreBoard)) {
				updateScoreIfItsAStrike(score, scoreBoard);
			} else if(isASpare(score, scoreBoard)) {
				updateScoreIfItsASpare(score, scoreBoard);
			} else {
				scoreBoard.setTotalScore(scoreBoard.getTotalScore()+score);
				scoreBoard.setCurrentScore(score);
			}
			
			saveScoresInMemory(scoreBoard, player);
			scoreBoard = findWinnerOfGame(scoreBoard, player);
			return new ResponseEntity<>("Player "+  player.getPlayerName() + " rolled the bowl and scored " +
			scoreBoard.getCurrentScore() + " scoreboard now is " + scoreBoard.toString(), HttpStatus.OK);
		} else {
			throw new PlayerNotFound("Player with id " + playerID + " not found!!!");
		}
		
	}

	private void updateScoreIfItsASpare(int score, ScoreBoard scoreBoard) {
		scoreBoard.setCurrentScore(score+5);
		scoreBoard.setTotalScore(scoreBoard.getTotalScore()+score+5);
		if(isItSecondChanceOfLastSet(scoreBoard)) {
			scoreBoard.setChancesInSet(0);
		}
	}

	private void updateScoreIfItsAStrike(int score, ScoreBoard scoreBoard) {
		scoreBoard.setCurrentScore(score+10);
		scoreBoard.setCurrentStrikes(scoreBoard.getCurrentStrikes()+1);
		scoreBoard.setTotalStrikes(scoreBoard.getTotalStrikes()+1);
		scoreBoard.setTotalScore(scoreBoard.getTotalScore()+score+10);
		if(isItTheFirstChanceOfLastSet(scoreBoard)) {
			scoreBoard.setChancesInSet(0);
		}
	}

	private int rollTheBowl(int score, ScoreBoard scoreBoard, Player player) {
		if(scoreBoard.getChancesInSet()==0) {
			score += playBowlForFirstChanceOfSet(scoreBoard);
			scoreBoard.setChancesInSet(1);
		} else if(scoreBoard.getChancesInSet()==1) {
			score += playBowlForSecondChanceOfSet(scoreBoard);
			scoreBoard.setSetsCompleted(scoreBoard.getSetsCompleted()+1);
			scoreBoard.setChancesInSet(0);
			updateTheTurnForNextPlayer(player);
		}
		return score;
	}

	private ScoreBoard findWinnerOfGame(ScoreBoard scoreBoard, Player player) {
		System.out.println(player.getPlayerID()==turn.lastEntry().getKey());
		if(player.getPlayerID()==turn.lastEntry().getKey() && scoreBoard.getSetsCompleted()==10) {
			for(Player p : playersRepo.findAll()) {
				if(p.getScoreboard().getTotalScore()>maxTotalScore) {
					winnerScoreBoard = p.getScoreboard();
					maxTotalScore = p.getScoreboard().getTotalScore();
				}
			}
			scoreBoard = winnerScoreBoard;
			Player winner = scoreBoard.getPlayer();
			winner.setWinner(true);
			playersRepo.save(winner);
		}
		return scoreBoard;
	}

	private void saveScoresInMemory(ScoreBoard scoreBoard, Player player) {
		player.setScoreboard(scoreBoard);
		playersRepo.save(player);
		scoreBoardRepo.save(scoreBoard);
	}

	private void updateTheTurnForNextPlayer(Player player) {
		turn.put(player.getPlayerID(), false);
		Map.Entry<Integer, Boolean> nextPlayer = null;
		for (Map.Entry<Integer, Boolean> e : turn.entrySet()) {
			if(e.getKey()==player.getPlayerID()) {
				nextPlayer = turn.higherEntry(e.getKey());
				if(nextPlayer==null) {
					nextPlayer = turn.firstEntry();
				}
				break;
			}
		}
		turn.put(nextPlayer.getKey(), true);
	}

	private void createPlayers(List<Player> players, int gameID) {
		int i=0;
		if(players.size()>maxNoOfPlayers) {
			throw new InvalidPlayException("Players more than 12 not allowed in the game");
		}
		turn = new TreeMap<>();
		for(Player player : players) {
			ScoreBoard scoreBoard = new ScoreBoard();
			scoreBoard.setCurrentScore(0);
			scoreBoard.setCurrentStrikes(0);
			scoreBoard.setLane(i++/noOfLanes);
			scoreBoard.setTotalScore(0);
			scoreBoard.setTotalStrikes(0);
			scoreBoard.setPlayer(player);
			scoreBoard.setChancesInSet(0);
			scoreBoard.setSetsCompleted(0);
			scoreBoard.setBowlingPins(new int[noOfBowlingPins]);
			scoreBoard.setGameID(gameID);
			saveScoresInMemory(scoreBoard, player);
			turn.put(player.getPlayerID(), false);
		}
	}
	
	private int playBowlForSecondChanceOfSet(ScoreBoard scoreBoard) {
		int score = 0;
		int[] bowlingPins = scoreBoard.getBowlingPins();
		Random rand = new Random();
		for(int i=0;i<noOfBowlingPins;i++) {
			if(bowlingPins[i]==1) {
				bowlingPins[i]= rand.nextInt(2);
				if(bowlingPins[i]==0) {
					score++;
				}
			}
		}
		scoreBoard.setBowlingPins(bowlingPins);
		return score;
	}

	private int playBowlForFirstChanceOfSet(ScoreBoard scoreBoard) {
		int score =0;
		int[] bowlingPins = scoreBoard.getBowlingPins();
		Random rand = new Random();
		for(int i=0;i<noOfBowlingPins;i++) {
			bowlingPins[i]= rand.nextInt(2);
			if(bowlingPins[i]==0) {
				score++;
			}
		}
		scoreBoard.setBowlingPins(bowlingPins);
		return score;
	}
	
	private boolean isASpare(int score, ScoreBoard scoreBoard) {
		return score==noOfBowlingPins/2 && scoreBoard.getChancesInSet()==2 && scoreBoard.getCurrentScore()+score==10;
	}

	private boolean isAStrike(int score, ScoreBoard scoreBoard) {
		return score==noOfBowlingPins && scoreBoard.getChancesInSet()==1;
	}
	
	private boolean isItTheFirstChanceOfLastSet(ScoreBoard scoreBoard) {
		return scoreBoard.getSetsCompleted()==9 &&scoreBoard.getChancesInSet()==1;
	}
	
	private boolean isItSecondChanceOfLastSet(ScoreBoard scoreBoard) {
		return scoreBoard.getSetsCompleted()==9 && scoreBoard.getChancesInSet()==0;
	}

}
