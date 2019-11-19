package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testPortNumber() {
        Client client = new Client(5678, "127.0.0.1", e -> {
            //do nothing
        });
        assertEquals(client.getPort(), 5678);
    }

    @Test
    void testIPAddress(){
        Client client = new Client(5555, "127.0.0.1", e -> {
            //do nothing
        });
        assertEquals(client.getIP(), "127.0.0.1");
    }

    @Test
    void testUnconnectedServer() {
        Client client = new Client(5555, "127.0.0.1", e -> {
            //do nothing
        });

        assertThrows(Exception.class, () -> {
            client.sendData("Hello");
        });
    }

    @Test
    void invalidPortNumber() {
        Client client = new Client(100, "127.0.0.1", e -> {
           //Do nothing
        });

        assertEquals(client.getPort(), 5555);
    }

    @Test
    void checkConnection(){
        Client client = new Client(6666, "127.0.0.1", e-> {

        });
        assertEquals(client.getPort(), 6666);
        assertEquals(client.getIP(), "127.0.0.1");
        assertEquals(client.getConn(), false);
        assertThrows(Exception.class, () ->{
           client.sendData("To non-existent server");
        });
    }

}