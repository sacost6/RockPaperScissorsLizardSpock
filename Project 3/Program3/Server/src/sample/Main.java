package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.scene.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application {
    // JavaFX Stuff
    private Label portLabel;
    private TextField portField;
    private Button portButton, playAgain;
    private BorderPane border;
    private ToggleButton on, off;
    private ToggleGroup serverControl;
    private Text numberPlayers1, playerOneScore, playerTwoScore, playerOneMove, playerTwoMove;


    // Server Stuff
    private boolean server;
    private int portNumber;
    private ServerSocket gameSocket;
    private Socket connection;
    private NetworkConnection server1;

    // Game stuff
    private int restart, movesPlayed;
    private GameThread gameThread = new GameThread();
    private Game game;
    private boolean gameStarted;


    // GUI Server


    @Override
    public void start(Stage primaryStage) throws Exception{

        border = new BorderPane();

        /* this portion of the code lets the user
         * choose the port and enter it for the game
         */
        portLabel = new Label("Enter a port number");
        portField = new TextField();
        portButton = new Button("Enter");
        playAgain = new Button("Play again?");
        playerTwoScore = new Text();
        playerOneScore = new Text();
        numberPlayers1 = new Text();
        playerOneMove = new Text();
        playerTwoMove = new Text();
        playerOneScore.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        playerTwoScore.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        numberPlayers1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        portButton.setOnAction(e -> {
           setPortNumber();
        });

        HBox bottomServer = new HBox();
        bottomServer.setPadding(new Insets(15, 12, 15, 12));
        bottomServer.setSpacing(10);
        bottomServer.setStyle("-fx-background-color: #E0E0E0;");
        bottomServer.getChildren().addAll(portLabel, portField, portButton);
        border.setBottom(bottomServer);

        HBox playerInformation = new HBox();
        playerInformation.setPadding(new Insets(0, 12, 15, 12));
        playerInformation.setSpacing(10);
        playerInformation.getChildren().addAll(playerOneScore, playerTwoScore);

        HBox numberPlayers = new HBox();
        numberPlayers.setPadding(new Insets(15, 12, 15, 12));
        numberPlayers.setSpacing(10);
        numberPlayers.getChildren().add(numberPlayers1);

        VBox gameInfo = new VBox();
        gameInfo.getChildren().addAll(numberPlayers, playerInformation);
        border.setTop(gameInfo);

        /*
         * This portion deals with the moves and whether the player wants to play again
         */

        VBox stateGame = new VBox();
        stateGame.getChildren().addAll(playerOneMove, playerTwoMove);
        border.setCenter(stateGame);
        stateGame.setAlignment(Pos.CENTER);
        playerOneMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        playerTwoMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        /* this portion of the code allows the user
         * to turn the server on or off
         */

        ToggleGroup serverControl = new ToggleGroup();
        off = new ToggleButton("Off");
        on = new ToggleButton("On");
        off.setToggleGroup(serverControl);
        on.setToggleGroup(serverControl);
        off.setSelected(true);
        on.setDisable(true);
        bottomServer.getChildren().addAll(off, on);

        // Creating listener to toggle on and off
        serverControl.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle toggle, Toggle new_toggle) {
                if (new_toggle == off) {
                    on.setDisable(true);
                    if(server) {
                        server1.closeConnection();
                    }
                    server = false;
                }

                else if(new_toggle == on){
                    server = true;
                    try {
                        initializeServer();

                    }
                    catch (Exception u ) { }
                }

            }
        });

        /* This portion of the code allows the server user
         * to watch the status of the game being played
         */

        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(border, 500, 475));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    // This function changes the port number for the server
    public void setPortNumber() {
        try {
            portNumber = Integer.parseInt(portField.getText());
            on.setDisable(false);
        }
        catch(Exception e) {
            playerOneMove.setText("Retry entering a port number");
            on.setDisable(true);
        }

    }

    //This function initializes the server thread, the game thread, and the scoring thread
    public void initializeServer() throws Exception {
        server1 = createServer();
        server1.startConn();
        Thread.sleep(50);
        gameThread.start();
        playerOneMove.setText("Server connected!");
    }

    // This function initializes the server and network connection instance
    private Server createServer() {
        return new Server(portNumber, data-> {
            Platform.runLater(()->{
                movesPlayed++;
                if(movesPlayed == 2) {
                    movesPlayed = 0;
                    compareMoves();
                }
                if(data.toString().equals("Restart")) {
                    restart++;
                }
                if(data.toString().equals("Quit")) {
                    try {
                        if(server1.players.get(game.PLAYER_ONE)!=null) {
                            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Server closing");
                        }
                        if(server1.players.get(game.PLAYER_TWO)==null){
                            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Server closing");
                        }
                    }
                    catch(Exception e) {
                        System.exit(0);
                    }
                    server1.closeConnection();
                    System.exit(0);
                }
                if(data.toString().equals("Closed")) {
                    try {
                        if(server1.players.get(0)!=null) {
                            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Server closing");
                        }
                        if(server1.players.get(1)==null){
                            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Server closing");
                        }

                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                if(restart == 2) {
                    restart = 0;
                    restartGame();
                }
            });
        });
    }


    // This function reads the moves from the players' socket and compares them
    public void compareMoves() {
        if(!gameStarted){
            return;
        }
        String player1 = server1.players.get(game.PLAYER_ONE).currentMove;
        String player2 = server1.players.get(game.PLAYER_TWO).currentMove;

        playerOneMove.setText("Player one: " + player1);
        playerTwoMove.setText("Player two: " + player2);

        /* This function takes in the first player's choice
         * and compares it to the second player's choice
         */
        switch(player1) {
            case "Spock":
                switch(player2) {
                    case "Spock":
                        break;
                    case "Lizard":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Scissors":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Paper":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Rock":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    default:

                        break;
                }
                break;
            case "Lizard":
                switch(player2) {
                    case "Spock":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Lizard":
                        break;
                    case "Scissors":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Paper":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Rock":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    default:
                        break;
                }
                break;
            case "Scissors":
                switch(player2) {
                    case "Spock":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Lizard":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Scissors":
                        break;
                    case "Paper":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Rock":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    default:
                        break;
                }
                break;
            case "Rock":
                switch(player2) {
                    case "Spock":

                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Lizard":

                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Scissors":

                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Paper":

                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Rock":
                        break;
                    default:
                        break;
                }
                break;
            case "Paper":
                switch(player2) {
                    case "Spock":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    case "Lizard":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Scissors":
                        game.score[game.PLAYER_TWO]++;
                        break;
                    case "Paper":
                        break;
                    case "Rock":
                        game.score[game.PLAYER_ONE]++;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        try {
            server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Moves compared");
            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Moves compared");

            // Send the moves to the clients
            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player 1: " + player1);
            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player 2: " + player2);
            server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player 1: " + player1);
            server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player 2: " + player2);

            // Send the points to the clients
            if(game.score[game.PLAYER_ONE] == 0) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player one has zero points");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player one has zero points");
            }
            if(game.score[game.PLAYER_TWO] == 0) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player two has zero points");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player two has zero points");
            }
            if(game.score[game.PLAYER_ONE] == 1) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player one has one point");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player one has one point");
            }
            if(game.score[game.PLAYER_TWO] == 1) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player two has one point");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player two has one point");
            }
            if(game.score[game.PLAYER_ONE] == 2) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player one has two points");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player one has two points");
            }
            if(game.score[game.PLAYER_TWO] == 2 ) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player two has two points");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player two has two points");
            }
            if(game.score[0] == 3) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player one has won the game!");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player one has won the game!");
                playerOneMove.setText("Player one is the winner!");
                playerTwoMove.setText("Sorry, player two.");
            }
            if(game.score[1] == 3) {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Player two has won the game!");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Player two has won the game!");
                playerOneScore.setText("Player two is the winner!");
                playerTwoScore.setText("Sorry, player one.");
            }

        }
        catch(Exception e) {
            System.out.println(e + "in Compare Moves");
        }

        playerOneScore.setText("Player one: " + game.score[game.PLAYER_ONE]);
        playerTwoScore.setText("Player two: " + game.score[game.PLAYER_TWO]);
    }



    // This thread makes sure there are two players before a game is initialized and keeps track of scoring
    class GameThread extends Thread {
        /*
         * Checks the number of players until there are 2
         * and then establish a new game.
         */
        public void run() {
            int numPlayers = server1.players.size();
            while (numPlayers != 2) {
                try {
                    /*
                     * If there are 0 players, connected then have the
                     * server state it's waiting for two people. If there is
                     * only one player connected, tell the player that and wait for
                     * another player to connect before the game is started.
                     */
                    switch(numPlayers) {
                        case 0:
                            Thread.sleep(500);
                            numberPlayers1.setText("Players connected: 0");
                            break;
                        case 1:
                            Thread.sleep(500);
                            server1.sendMessage(server1.players.get(0), "There is only one player " +
                                    "connected at the moment..");
                            numberPlayers1.setText("Players connected: 1");
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e + "in GameThread");
                }
                numPlayers = server1.players.size();
            }
            numberPlayers1.setText("Players connected: 2");
            game = new Game();
            gameStarted = true;
            try {
                server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Start");
                server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Start");
            }
            catch(Exception e) {
                System.out.println(e + " in Start message sent");
            }
        }
    }

    private void restartGame() {
        game.restart();
        try {
            server1.sendMessage(server1.players.get(game.PLAYER_ONE), "Start");
            server1.sendMessage(server1.players.get(game.PLAYER_TWO), "Start");
        }
        catch (Exception e) {
            System.out.println(e + " in restartGame");
        }
        playerOneScore.setText("Player one: 0");
        playerTwoScore.setText("Player two: 0");
    }

}
