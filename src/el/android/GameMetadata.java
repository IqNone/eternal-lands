package el.android;

import el.client.Client;
import el.server.ServerConnection;

public class GameMetadata {
    private static final String HOST = "game.eternal-lands.com";
    private static final int PORT = 2000;

    public static ServerConnection CONNECTION;
    public static Client CLIENT;

    public static boolean startUpServerConnection() {
        if(CONNECTION == null) {
            CONNECTION = new ServerConnection(HOST, PORT);
        }

        if(!CONNECTION.isConnected()) {
            if(!CONNECTION.connect()) {
                return false;
            }
        }

        if(!CONNECTION.isRunning()) {
            CONNECTION.start();
        }

        return true;
    }

    public static boolean authenticateClient(String username, String password) {
        CLIENT = new Client(CONNECTION);
        return CLIENT.authenticate(username, password);
    }
}
