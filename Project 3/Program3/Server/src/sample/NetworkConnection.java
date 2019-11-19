package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public abstract class NetworkConnection extends Main {
    // Networking stuff
    private Consumer<Serializable> callback;
    private ServerSocket mainServer;
    private ConnectionThread server = new ConnectionThread();
    private int port;

    // Game stuff
    protected ArrayList<ServerThread> players;
    int numPlayers;

    public NetworkConnection(Consumer<Serializable> c, int port) {
        this.port = port;
        this.callback = c;
        server.setDaemon(true);
        // Establishing server with given port
        try {
            mainServer = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println(e + "in Network Connection");
        }
    }
    //initialize the thread running the server
    public void startConn()  {
        server.start();
    }
    // send a message to the server
    public void sendMessage(ServerThread client, String s) throws Exception {
        Serializable data = (Serializable) s;
        client.out.writeObject(s);
    }
    // close the connections
    public void closeConnection(){
        try {
            mainServer.close();
            for(int i = 0; i < players.size(); i++) {
                players.get(i).closeSocket();
                players.remove(i);
            }
        }
        catch(Exception e){
            System.exit(0);
        }
    }

    abstract protected int getPort();
    // this thread takes in data from the sockets that are connected to the server
    class ServerThread extends Thread {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        public String currentMove;
        public int playerNumber;

        ServerThread(Socket s) {
            this.socket = s;
        }

        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                this.out = new ObjectOutputStream(socket.getOutputStream());
                socket.setTcpNoDelay(true);

                playerNumber = numPlayers-1;
                while (true) {

                    Serializable data = (Serializable) in.readObject();
                    data.toString();

                    currentMove = data.toString();
                    callback.accept(data);
                }

            } catch (Exception e) {
                callback.accept("Closed");
            }
        }

        public void closeSocket(){
            try {
                socket.close();
            }
            catch(Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
    // this thread constantly connects new sockets that are attempting to connect
    class ConnectionThread extends Thread {

        public void run() {
            try {
                players = new ArrayList<>();
                while(true) {

                    Socket socket = mainServer.accept();
                    if(socket != null) {
                        numPlayers++;
                    }
                    ServerThread t1 = new ServerThread(socket);
                    t1.start();
                    players.add(t1);

                }
            } catch (Exception e) {
                for(int i = 0; i < players.size(); i++) {
                    players.get(i).closeSocket();
                }
            }

        }
    }


}
