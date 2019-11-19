package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void getPort() {
        Server newServer = new Server(5555, data -> {
            //Do nothing
        });

        assertEquals(newServer.getPort(), 5555);
    }
}