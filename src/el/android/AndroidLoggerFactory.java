package el.android;

import android.util.Log;
import el.logging.Logger;
import el.logging.LoggerFactory;

public class AndroidLoggerFactory extends LoggerFactory{
    @Override
    protected Logger createLogger(Class c) {
        return new AndroidLogger(c.getName());
    }

    private class AndroidLogger implements Logger {
        private String tag;

        public AndroidLogger(String tag) {
            this.tag = tag;
        }

        @Override
        public void info(String message) {
            Log.i(tag, message);
        }

        @Override
        public void error(String message, Exception e) {
            Log.e(tag, message, e);
        }

        @Override
        public void error(Exception e) {
            Log.e(tag, e.getMessage(), e);
        }

        @Override
        public void warning(String message) {
            Log.w(tag, message);
        }
    }
}
