package el.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import el.logging.Logger;
import el.logging.LoggerFactory;
import org.acra.ACRA;
import org.acra.sender.EmailIntentSender;

import static el.android.GameMetadata.CLIENT;
import static el.android.GameMetadata.CONNECTION;

public abstract class GameRunner extends Activity {
    private static final Logger LOGGER = AndroidLoggerFactory.logger(GameRunner.class);

    protected abstract void updateUI();

    private final Handler handler = new Handler();
    private PowerManager.WakeLock wakeLock;

    private volatile boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Eternal Lands");
    }

    @Override
    protected void onStart() {
        super.onStart();

        LOGGER.info("enter ui activity, starting the main loop");
        isShowing = true;
        new Thread(uiMainLoop).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
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
