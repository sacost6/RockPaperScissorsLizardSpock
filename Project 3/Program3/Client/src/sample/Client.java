package sample;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends NetworkConnection {
    private int port;
    private String IP;

    public Client(int port, String IP, Consumer<Serializable> callback) {
        super(callback);
        if(port < 1000) {
            this.port = 5555;
            this.IP = IP;
            return;
        }
        this.port = port;
        this.IP = IP;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getIP() {
        return IP;
    }
}
