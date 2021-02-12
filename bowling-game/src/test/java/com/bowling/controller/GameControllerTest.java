package com.bowling.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import bowling.controller.GameController;
import bowling.entity.Player;
import bowling.entity.PlayersRepository;
import bowling.entity.ScoreBoard;
import bowling.entity.ScoreBoardRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@EnableJpaRepositories (value = {"bowling.entity.PlayersRepository", "bowling.entity.ScoreBoardRepository"})
public class GameControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PlayersRepository playersRepo;
	
	@MockBean
	private ScoreBoardRepository scoreBoardRepo;
	
	@Test
	public void shouldCreateNewGame() throws Exception {
		
		List<Player> players = new ArrayList<>();
		players.add(new Player(1, "Ankita", false));
		players.add(new Player(2, "Sumit", false));
		
		mockMvc.perform(post("/startGame")
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(new ObjectMapper().writeValueAsString(players)) 
		        .accept(MediaType.APPLICATION_JSON)
	        ).andExpect(status().isCreated());
	}
	
	@Test
	public void shouldPlayTheChanceInGame() throws Exception {
		
		List<Player> players = new ArrayList<>();
		Player player1 = new Player(1, "Ankita", false);
		players.add(player1);
		
		int bowlingPin1[] = {1,0,1,0,1,1,1,0,0,0};
		ScoreBoard scoreBoard1 = new ScoreBoard(1, 5, 0, 10, 1, 0, 1, 1, 1, bowlingPin1, player1);
		player1.setScoreboard(scoreBoard1);
		
		when(playersRepo.save(player1)).thenReturn(player1);
		when(scoreBoardRepo.save(scoreBoard1)).thenReturn(scoreBoard1);
		when(playersRepo.findAll()).thenReturn(players);
		
		Optional<Player> optionalplayer1 = Optional.of(player1) ;
		when(playersRepo.findById(1)).thenReturn(optionalplayer1);
		
		mockMvc.perform(post("/startGame")
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(new ObjectMapper().writeValueAsString(players)) 
		        .accept(MediaType.APPLICATION_JSON)
	        ).andExpect(status().isCreated()).andReturn();
		
		MvcResult result = mockMvc.perform(get("/bowling/"+scoreBoard1.getGameID()+"/play/"+1)
	        ).andExpect(status().isOk()).andReturn();
		
		String responseScore = result.getResponse().getContentAsString();
		assertTrue(responseScore.contains("setsCompleted=2, chancesInSet=0, gameID=1"));
	}
	
	@Test
	public void shouldReturnListOfPlayers() throws Exception {
		
		List<Player> players = new ArrayList<>();
		Player player1 = new Player(1, "Ankita", false);
		players.add(player1);
		Player player2 = new Player(1, "Sumit", false);
		players.add(player2);
		
		int bowlingPin1[] = {1,0,1,0,1,1,1,0,0,0};
		ScoreBoard scoreBoard1 = new ScoreBoard(1, 5, 0, 10, 1, 0, 1, 1, 1, bowlingPin1, player1);
		player1.setScoreboard(scoreBoard1);
		int bowlingPin2[] = {1,1,1,0,1,1,1,0,1,0};
		ScoreBoard scoreBoard2 = new ScoreBoard(1, 5, 0, 10, 1, 0, 1, 1, 1, bowlingPin2, player2);
		player1.setScoreboard(scoreBoard2);
		
		when(playersRepo.save(player1)).thenReturn(player1);
		when(scoreBoardRepo.save(scoreBoard1)).thenReturn(scoreBoard1);
		when(playersRepo.save(player2)).thenReturn(player2);
		when(scoreBoardRepo.save(scoreBoard2)).thenReturn(scoreBoard2);
		when(playersRepo.findAll()).thenReturn(players);
		
		Optional<Player> optionalplayer1 = Optional.of(player1) ;
		when(playersRepo.findById(1)).thenReturn(optionalplayer1);
		Optional<Player> optionalplayer2 = Optional.of(player2) ;
		when(playersRepo.findById(2)).thenReturn(optionalplayer2);
		
		MvcResult result = mockMvc.perform(get("/players")
	        ).andExpect(status().isOk()).andReturn();
		assertEquals("[{\"playerID\":1,\"playerName\":\"Ankita\",\"scoreboard\":{\"id\":1,\"currentScore\":5,\"lane\":0,\"totalScore\":10,\"totalStrikes\":1,\"currentStrikes\":0,\"setsCompleted\":1,\"chancesInSet\":1,\"gameID\":1,\"bowlingPins\":[1,1,1,0,1,1,1,0,1,0]},\"winner\":false},{\"playerID\":1,\"playerName\":\"Sumit\",\"scoreboard\":null,\"winner\":false}]",result.getResponse().getContentAsString());
	
	}
}
