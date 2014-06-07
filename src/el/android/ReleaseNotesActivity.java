package el.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import el.android.release.ReleaseNotesService;

public class ReleaseNotesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReleaseNotesService releaseNotesService = new ReleaseNotesService(this);

        TextView tv = new TextView(this);
        tv.setText(releaseNotesService.getReleaseNotes());
        setContentView(tv);
    }
}
