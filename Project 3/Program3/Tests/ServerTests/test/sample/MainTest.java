package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void setPortNumber() {
        Main game = new Main();
        assertThrows(Exception.class , () -> {
            game.setPortNumber();
        });
    }



}