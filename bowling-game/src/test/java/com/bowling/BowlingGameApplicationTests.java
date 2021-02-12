package com.bowling;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bowling.BowlingGameApplication;
import bowling.controller.GameController;

@SpringBootTest(classes = BowlingGameApplication.class)
class BowlingGameApplicationTests {
	@Autowired
	private GameController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
