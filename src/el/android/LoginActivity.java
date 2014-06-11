package el.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import el.android.release.ReleaseNotesService;
import el.logging.LoggerFactory;
import org.acra.ACRA;
import org.acra.sender.EmailIntentSender;

import static android.view.View.OnClickListener;
import static el.android.GameMetadata.authenticateClient;
import static el.android.GameMetadata.startUpServerConnection;
import static el.android.SharedSettings.PREDEFINED_PASSWORD;
import static el.android.SharedSettings.PREDEFINED_USERNAME;

public class LoginActivity extends Activity {
    private EditText usernameText;
    private EditText passwordText;

    private ImageView loginButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameText = getEditText(R.id.username);
        passwordText = getEditText(R.id.password);

        loginButton = (ImageView) findViewById(R.id.login);
        loginButton.setOnClickListener(ON_LOG_IN_CLICK);

        findViewById(R.id.settings).setOnClickListener(ON_SETTINGS_CLICK);

        ReleaseNotesService releaseNotesService = new ReleaseNotesService(this);

        getTextView(R.id.releaseName).setText(releaseNotesService.getReleaseName());
        getTextView(R.id.releaseNumber).setText(releaseNotesService.getReleaseNumber());
        getTextView(R.id.releaseDate).setText(releaseNotesService.getReleaseDate());

        getTextView(R.id.releaseContentLink).setOnClickListener(ON_READ_NOTES_CLICK);
    }

    private EditText getEditText(int resource) {
        return (EditText)findViewById(resource);
    }

    private TextView getTextView(int resource) {
        return (TextView)findViewById(resource);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        usernameText.setText(settings.getString(PREDEFINED_USERNAME, ""));
        passwordText.setText(settings.getString(PREDEFINED_PASSWORD, ""));

        initLogger(settings);
    }

    private void initLogger(SharedPreferences settings) {
        if(settings.getBoolean(SharedSettings.ENABLE_REPORTS, false)) {
            LoggerFactory.setImplementation(new AcraLoggerFactory(includeUsername(settings), includeExtra(settings)));
            ACRA.init(getApplication());
            ACRA.getErrorReporter().addReportSender(new EmailIntentSender(this));
        } else {
            LoggerFactory.setImplementation(new AndroidLoggerFactory());
        }
    }

    private boolean includeUsername(SharedPreferences settings) {
        return settings.getBoolean(SharedSettings.REPORTS_INCLUDE_USERNAME, false);
    }

    private boolean includeExtra(SharedPreferences settings) {
        return settings.getBoolean(SharedSettings.REPORTS_INCLUDE_EXTRA, false);
    }

    private OnClickListener ON_LOG_IN_CLICK = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

            if(username.length() > 0 && password.length() > 0) {
                loginButton.setEnabled(false);
                processLogin(username, password);
            } else {
                addCredentialsErrorMessages(username, password);
            }
        }

        private void processLogin(String username, String password) {
            loginButton.setEnabled(true);

            if(!startUpServerConnection()){
                ((TextView) findViewById(R.id.loginError)).setText(getString(R.string.connect_to_server_error));
                return;
            }

            if(!authenticateClient(username, password)) {
                ((TextView) findViewById(R.id.loginError)).setText(getString(R.string.authenticate_error));
                return;
            }

            Intent intent = new Intent(LoginActivity.this, Game.class);
            startActivity(intent);
        }

        private void addCredentialsErrorMessages(String username, String password) {
            if(username.length() == 0) {
                usernameText.setError(getString(R.string.username_empty_error));
            }
            if(password.length() == 0) {
                passwordText.setError(getString(R.string.password_empty_error));
            }
        }
    };

    private OnClickListener ON_SETTINGS_CLICK = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
        }
    };

    private OnClickListener ON_READ_NOTES_CLICK = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, ReleaseNotesActivity.class));
        }
    };
}
