package sample;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

abstract class NetworkConnection {
    private Consumer<Serializable> callback;
    private Serializable fromClient;
    private Socket client;
    private ConnectionThread conn = new ConnectionThread();
    private boolean socket = false;

    public NetworkConnection(Consumer<Serializable> c) {
        this.callback = c;
        conn.setDaemon(true);
    }

    public void connectToServer() {
        conn.start();
    }

    public void sendData(String s) throws Exception {
        Serializable data = (Serializable) s;

        conn.out.writeObject(data);
    }
    public boolean getConn() {
        return socket;
    }
    abstract protected int getPort();
    abstract protected String getIP();

    class ConnectionThread extends Thread {
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public void run() {
            try {
                socket = true;
                client = new Socket(getIP(), getPort());

                this.out = new ObjectOutputStream(client.getOutputStream());
                this.in = new ObjectInputStream(client.getInputStream());

                while(true) {
                    Serializable data = (Serializable) in.readObject();
                    data.toString();
                    callback.accept(data);
                }
            }
            catch(Exception e ) {
                try {
                    if(socket || (client != null)) {
                        client.close();
                    }
                }
                catch(Exception u) {
                    callback.accept("Server not working");
                }
                callback.accept("Server not working");
            }
        }
    }
}
