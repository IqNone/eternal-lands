package el.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import el.logging.Logger;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static el.android.GameMetadata.CLIENT;
import static el.android.GameMetadata.CONNECTION;

public abstract class GameRunner extends Activity {
    private static final Logger LOGGER = AndroidLoggerFactory.logger(GameRunner.class);

    protected abstract void updateUI();

    private final Handler handler = new Handler();

    private volatile boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LOGGER.info("enter ui activity, starting the main loop");
        isShowing = true;
        new Thread(uiMainLoop).start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LOGGER.info("exit ui activity, ending the main loop");
        isShowing = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LOGGER.info("activity killed, closing connection");

        //not sure why i get nulls here, but i get them
        if(CONNECTION != null) {
            CONNECTION.stop();
        }
        if(CLIENT != null) {
            CLIENT.stop();
        }

        CONNECTION = null;
        CLIENT = null;
    }

    private Runnable uiMainLoop = new Runnable() {
        @Override
        public void run() {
            while(isShowing && CONNECTION.isConnected()) {
                handler.post(updateUICaller);
                safeSleep(100);
            }
            LOGGER.info("main loop finished");
        }

        private void safeSleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                LOGGER.error("nasty interrupt exception while sleeping occurred", e);
            }
        }
    };

    private Runnable updateUICaller = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };
}
