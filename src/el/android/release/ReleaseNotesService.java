package el.android.release;

import android.content.Context;
import el.android.R;
import el.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReleaseNotesService {
    private Context context;

    private String number;
    private String name;
    private String date;

    public ReleaseNotesService(Context context) {
        this.context = context;

        BufferedReader reader = openReader();

        try {
            String numberAndName[] = reader.readLine().split(":");
            number = numberAndName[0];
            name = numberAndName[1];
            date = reader.readLine();
        } catch (IOException e) {
            number = null;
            name = null;
            date = null;
        } finally {
            IOUtils.closeQuite(reader);
        }
    }

    public String getReleaseNumber() {
        return number;
    }

    public String getReleaseName() {
        return name;
    }

    public String getReleaseDate () {
        return date;
    }

    public String getReleaseNotes() {
        BufferedReader reader = openReader();

        try {
            return tryReadContent(reader);
        } catch (IOException e) {
            return "";
        } finally {
            IOUtils.closeQuite(reader);
        }
    }

    private String tryReadContent(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    private BufferedReader openReader() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.release_notes);
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
