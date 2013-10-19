package el.android;

import el.logging.Logger;
import el.logging.LoggerFactory;
import org.acra.ACRA;

import static el.android.GameMetadata.CLIENT;

public class AcraLoggerFactory extends LoggerFactory {
    private boolean includeUsername;
    private boolean includeExtra;

    public AcraLoggerFactory(boolean includeUsername, boolean includeExtra) {
        this.includeUsername = includeUsername;
        this.includeExtra = includeExtra;
    }

    @Override
    protected Logger createLogger(Class c) {
        return new AcraLogger(c.getName());
    }

    private class AcraLogger implements Logger {
        private String name;

        public AcraLogger(String name) {
            this.name = name;
        }

        @Override
        public void info(String message) {

        }

        @Override
        public void error(String message, Exception e) {
            ACRA.getErrorReporter().putCustomData("username", getUsername());
            ACRA.getErrorReporter().putCustomData("map", getMap());
            ACRA.getErrorReporter().putCustomData("position", getPosition());
            ACRA.getErrorReporter().putCustomData("class", name);
            ACRA.getErrorReporter().putCustomData("type", "error");
            ACRA.getErrorReporter().putCustomData("message", message);
            ACRA.getErrorReporter().handleException(e, false);
        }

        @Override
        public void error(Exception e) {
            ACRA.getErrorReporter().putCustomData("username", getUsername());
            ACRA.getErrorReporter().putCustomData("map", getMap());
            ACRA.getErrorReporter().putCustomData("position", getPosition());
            ACRA.getErrorReporter().putCustomData("class", name);
            ACRA.getErrorReporter().putCustomData("type", "error");
            ACRA.getErrorReporter().putCustomData("message", e.getMessage());
            ACRA.getErrorReporter().handleException(e, false);
        }

        @Override
        public void warning(String message) {
            ACRA.getErrorReporter().putCustomData("username", getUsername());
            ACRA.getErrorReporter().putCustomData("map", getMap());
            ACRA.getErrorReporter().putCustomData("position", getPosition());
            ACRA.getErrorReporter().putCustomData("class", name);
            ACRA.getErrorReporter().putCustomData("type", "warning");
            ACRA.getErrorReporter().putCustomData("message", message);
            ACRA.getErrorReporter().handleException(new Throwable(), false);
        }

        private String getUsername() {
            return includeUsername ? CLIENT.getActor().name.get(0).text : "anonymous";
        }

        private String getMap() {
            return includeExtra ? CLIENT.getActor().mapPath : "private";
        }

        private String getPosition() {
            return includeExtra ? CLIENT.getActor().x + " " + CLIENT.getActor().y : "private";
        }
    }
}
