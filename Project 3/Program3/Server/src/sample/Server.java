package sample;

import java.io.Serializable;
import java.util.function.Consumer;

public class Server extends NetworkConnection{
    private int port;

    public Server(int port, Consumer<Serializable> callback) {
        super(callback, port);
        this.port = port;
    }

    @Override
    protected int getPort() {
        return this.port;
    }

}
