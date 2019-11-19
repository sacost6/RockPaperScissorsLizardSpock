package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;


public class Main extends Application {

    private TextField userPort ,userIP;
    private Button[] moves;
    private Button enterPort, enterIP, connect, playAgain, quit;
    private BorderPane border;
    private boolean IP, Port;
    private NetworkConnection conn;
    private int portNumber;
    private String IPHost;
    private Text connectedUser;
    private VBox gameStatus, playerOne, playerTwo;
    private Text playerOnePoints, playerTwoPoints, playerNumber;
    private int playerID, movesMade;

    @Override
    public void start(Stage primaryStage) throws Exception {
        border = new BorderPane();

        /* this portion of the code lets the user
         * choose the port and enter it for the game
         */

        userIP = new TextField();
        userPort = new TextField();
        enterIP = new Button("Enter IP Addresses");
        enterPort = new Button("Enter the port");
        connect = new Button("Connect to Server!");
        connectedUser = new Text();
        connect.setDisable(true);
        moves = new Button[5];
        playerOnePoints = new Text();
        playerTwoPoints = new Text();
        playerTwo = new VBox();
        playerOne = new VBox();
        playerNumber = new Text();
        playAgain = new Button("Play again!");
        quit = new Button("Quit game");

        playAgain.setOnAction(e -> {
            try {
                conn.sendData("Restart");
            }
            catch(Exception u) {
                System.out.println(u + " in button Restart");
            }
        });

        quit.setOnAction(e -> {
            try {
                conn.sendData("Quit");
            }
            catch(Exception u) {
                System.out.println(u + " in button Quit");
            }
        });


        playAgain.setDisable(true);

        enterIP.setOnAction( e -> {
            IP = true;
            setIP();
        });

        enterPort.setOnAction(e -> {
            Port = true;
            setPort();
        });

        connect.setOnAction( e -> {
            initializeConn();
            gameStatus.getChildren().removeAll(gameStatus.getChildren());
        });

        HBox bottomClient1 = new HBox();
        bottomClient1.setPadding(new Insets(15, 12, 5, 12));
        bottomClient1.setSpacing(10);
        bottomClient1.setStyle("-fx-background-color: #E0E0E0;");
        bottomClient1.getChildren().addAll(userIP, enterIP, connect);

        HBox bottomClient2 = new HBox();
        bottomClient2.setPadding(new Insets(5, 12, 5, 12));
        bottomClient2.setSpacing(10);
        bottomClient2.setStyle("-fx-background-color: #E0E0E0;");
        bottomClient2.getChildren().addAll(userPort, enterPort, playAgain, quit);

        HBox playerIdentification = new HBox();
        playerIdentification.getChildren().add(playerNumber);


        // GUI portion of displaying game states
        BorderPane states = new BorderPane();

        connectedUser.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        VBox clientOptions = new VBox();
        clientOptions.getChildren().addAll(bottomClient1, bottomClient2, playerIdentification);

        gameStatus = new VBox();
        Text player = new Text();
        gameStatus.getChildren().addAll(playerNumber, connectedUser);
        states.setCenter(gameStatus);
        gameStatus.setSpacing(20);
        gameStatus.setAlignment(Pos.CENTER);

        playerOne.getChildren().add(playerOnePoints);
        border.setLeft(playerOne);
        playerOne.setAlignment(Pos.CENTER_LEFT);
        playerOne.setPadding(new Insets(5, 10, 5, 5));
        playerOnePoints.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        playerTwo.getChildren().add(playerTwoPoints);
        border.setRight(playerTwo);
        playerTwo.setAlignment(Pos.CENTER_RIGHT);
        playerTwo.setPadding(new Insets(5, 5, 5, 10));
        playerTwoPoints.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));


        border.setAlignment(states, Pos.CENTER);
        border.setCenter(gameStatus);
        border.setTop(clientOptions);
        border.setStyle("-fx-background-color: #E0E0E0;");

        /* This part of the code sets the buttons
         * with images associated with the move
         */


        Image spock = new Image("spock.png");
        Image rock = new Image("rock.jpg");
        Image scissor = new Image("scissor.jpg");
        Image paper = new Image("paper.jpg");
        Image lizard = new Image("lizard.png");

        ImageView spockView = new ImageView(spock);
        ImageView rockView = new ImageView(rock);
        ImageView scissorView = new ImageView(scissor);
        ImageView paperView = new ImageView(paper);
        ImageView lizardView = new ImageView(lizard);

        for(int i = 0; i < 5; i++) {
            moves[i] = new Button();
            moves[i].setDisable(true);
        }

        spockView.setFitHeight(75);
        spockView.setFitWidth(75);

        rockView.setFitHeight(75);
        rockView.setFitWidth(75);

        scissorView.setFitHeight(75);
        scissorView.setFitWidth(75);

        paperView.setFitHeight(75);
        paperView.setFitWidth(75);

        lizardView.setFitHeight(75);
        lizardView.setFitWidth(75);

        ImageView[] images = {spockView, rockView, scissorView, paperView, lizardView};

        for(int i = 0; i < 5; i++) {
            moves[i].setGraphic(images[i]);
            moves[i].setDisable(true);
        }

        moves[0].setOnAction(e -> {
            try {
                conn.sendData("Spock");
                disableChoices();
            }
            catch(Exception u ) {
                System.out.println(u);
            }
        });

        moves[1].setOnAction(e -> {
            try {
                conn.sendData("Rock");
                disableChoices();
            }
            catch(Exception u ) {
                System.out.println(u);
            }
        });

        moves[2].setOnAction(e -> {
            try {
                conn.sendData("Scissors");
                disableChoices();
            }
            catch(Exception u ) {
                System.out.println(u);
            }
        });

        moves[3].setOnAction(e -> {
            try {
                conn.sendData("Paper");
                disableChoices();
            }
            catch(Exception u ) {
                System.out.println(u);
            }
        });

        moves[4].setOnAction(e -> {
            try {
                conn.sendData("Lizard");
                disableChoices();
            }
            catch(Exception u ) {
                System.out.println(u);
            }
        });


        HBox imageBox = new HBox();
        imageBox.getChildren().addAll(moves);
        imageBox.setPadding(new Insets(10,10, 10,10 ));
        imageBox.setSpacing(5);

        border.setBottom(imageBox);

        primaryStage.setTitle("Rock, Paper, Scissor, Lizard, Spock");
        primaryStage.setScene(new Scene(border, 525, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void initializeConn() {
        conn = createClient();
        conn.connectToServer();
        connect.setDisable(true);
    }

    public Client createClient() {
        String string;
        return new Client(portNumber, IPHost, data -> {
            Platform.runLater(() -> {

                if(data.toString().equals("Start")) {
                    enableChoices();
                    connectedUser.setText("Begin!");
                    playerOnePoints.setText("Player one: 0");
                    playerTwoPoints.setText("Player two: 0");
                    if(playerID != 1) {
                        playerID = 2;
                        playerNumber.setText("You are player " + playerID );
                    }

                }

                else if(data.toString().equals("Moves compared")) {
                    connectedUser.setText("");
                    enableChoices();
                    if(movesMade == 2) {
                        movesMade = 0;
                        gameStatus.getChildren().removeAll(gameStatus.getChildren());
                    }
                }

                else if(data.toString().equals("There is only one player " +
                        "connected at the moment..")) {
                    connectedUser.setText("There is only one user connected at the moment.");
                    playerID = 1;
                    playerNumber.setText("You are player " + playerID);
                }

                else if(data.toString().equals("Player one has zero points")) {
                    playerOnePoints.setText("Player one: 0");
                }

                else if(data.toString().equals("Player two has zero points")) {
                    playerTwoPoints.setText("Player two: 0");
                }

                else if(data.toString().equals("Player one has one point")) {
                    playerOnePoints.setText("Player one: 1");
                }

                else if(data.toString().equals("Player two has one point")) {
                    playerTwoPoints.setText("Player two: 1");
                }

                else if(data.toString().equals("Player one has two points")) {
                    playerOnePoints.setText("Player one: 2");
                }

                else if(data.toString().equals("Player two has two points")) {
                    playerTwoPoints.setText("Player two: 2");
                }

                else if(data.toString().equals("Player one has won the game!")) {
                    gameStatus.getChildren().removeAll(gameStatus.getChildren());
                    Text userMove = new Text("Winner: Player one");
                    userMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    gameStatus.getChildren().addAll(userMove);
                    playerOnePoints.setText("Player one: 3");
                    playAgain.setDisable(false);
                    quit.setDisable(false);
                    disableChoices();
                }

                else if(data.toString().equals("Player two has won the game!")) {
                    gameStatus.getChildren().removeAll(gameStatus.getChildren());
                    Text userMove = new Text("Winner: Player two");
                    userMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    playerTwoPoints.setText("Player two: 3");
                    gameStatus.getChildren().addAll(userMove);


                    playAgain.setDisable(false);
                    quit.setDisable(false);
                    disableChoices();

                }
                else if(data.toString().equals("Player 1: Restart")) {
                    Text done = new Text("New game!");
                    done.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    gameStatus.getChildren().addAll(done);
                }

                else if(data.toString().equals("Player 2: Restart")) {
                    Text done = new Text("New game!");
                    done.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    gameStatus.getChildren().addAll(done);
                }
                else if(data.toString().equals("Server not working")){
                    gameStatus.getChildren().removeAll(gameStatus.getChildren());
                    Text userMove = new Text("Retry connecting to server");
                    userMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    gameStatus.getChildren().addAll(userMove);
                    enterIP.setDisable(false);
                    enterPort.setDisable(false);
                    connect.setDisable(false);
                }
                else if(data.toString().equals("Server closing")){
                    System.exit(0);
                }
                else {
                    movesMade++;
                    Text userMove = new Text(data.toString());
                    userMove.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    gameStatus.getChildren().addAll(userMove);
                }

            });
        });
    }

    private void setPort() {
        try {
            String s = userPort.getText();
            portNumber = Integer.parseInt(s);
            if(IP && Port) {
                connect.setDisable(false);
            }
            enterPort.setDisable(true);
        }
        catch(Exception e) {
            connectedUser.setText("Retry entering a port number");
            enterPort.setDisable(false);
        }


    }

    private void setIP() {
        IPHost = userIP.getText();
        if(IP && Port) {
            connect.setDisable(false);
        }
        enterIP.setDisable(true);
    }

    private void disableChoices() {
        for(int i = 0; i < 5; i++) {
            moves[i].setDisable(true);
        }
    }

    private void enableChoices(){
        for(int i = 0; i < 5; i++) {
            moves[i].setDisable(false);
        }
    }

}

