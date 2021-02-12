# BowlingGame

Simple Spring Boot BowlingGame game
====================================
REST API for playing the bowling game.
It involves only backend , Java with Spring framework is used.

Methods
=======

### POST base_url/startGame

Starts a new game and generates a unique game ID.

### GET base_url/players

This method will get the list of the players in the current game with their scoreboard. It displays if we have a winner of the game or not.

### POST base_url/bowling/{gameID}/play/{playerID}

This method will roll the bowl in the game represented with gameID for the given playerID, the falling of pins is random.

Build and run
================

1. Maven is used as build tool. Use below command to generate jar:

                           mvn clean install
2. Import the project in your STS workspace and run the application.
3. Use PostMan to fire the POST/GET requests for the game.
