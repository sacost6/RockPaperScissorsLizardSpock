package sample;


public class Game extends Main {
    // Setting final player variables
    final protected int PLAYER_ONE = 0;
    final protected int PLAYER_TWO = 1;

    // Variables to keep track of the game's state
    protected int[] score;

    public Game() {
        score = new int[2];
        score[PLAYER_ONE] = 0;
        score[PLAYER_TWO] = 0;
    }

    public Game(int test){
        score = new int[2];
        increaseScorefortest();
    }

    public void restart(){
        score[PLAYER_ONE] = score[PLAYER_TWO] = 0;
    }

    private void increaseScorefortest() {
        score[PLAYER_TWO] = 3;
        score[PLAYER_ONE] = 5;
    }

    public int getScore(int playerNumber) {
        if(playerNumber != 0 && playerNumber != 1) {
            return -999;
        }
        return score[playerNumber];
    }
}


