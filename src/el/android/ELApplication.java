package el.android;

import android.app.Application;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "",
        mailTo = "el.android.report@gmail.com",
        mode = ReportingInteractionMode.SILENT)
public class ELApplication extends Application {
}
