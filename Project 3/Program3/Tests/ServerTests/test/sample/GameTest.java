package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void restartWithPlayerOne() {
        Game newgame = new Game(5);
        assertEquals(newgame.getScore(newgame.PLAYER_ONE), 5);
        newgame.restart();
        assertEquals(newgame.getScore(newgame.PLAYER_TWO), 0);
    }

    @Test
    void restartWithPlayerTwo() {
        Game newgame = new Game(5);
        assertEquals(newgame.getScore(newgame.PLAYER_TWO), 3);
        newgame.restart();
        assertEquals(newgame.getScore(newgame.PLAYER_TWO), 0);
    }

    @Test
    void getScoreFromNonexistant() {
        Game newgame = new Game();
        assertEquals(newgame.getScore(1000), -999);
    }
}