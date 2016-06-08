package el.server;

import android.os.AsyncTask;

import java.net.Socket;

/**
 * Created by ash on 05.06.16.
 */
public class ServerConnectionTask extends AsyncTask<String, Void, Socket> {

    protected Socket doInBackground(String... args) {
        try {
            // TODO pass a server connect object
            String host = args[0];
            int port = 2000; //getInteger(args[1]);

            Socket socket =  new Socket(host, port);
            return socket;
        } catch (Exception e) {
            Exception exception = e;
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Socket socket) {
    }



}
