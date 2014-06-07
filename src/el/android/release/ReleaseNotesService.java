package el.android.release;

import android.content.Context;
import el.android.R;
import el.utils.IOUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReleaseNotesService {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d LLLL yyyy");

    private Context context;

    private String number;
    private String name;
    private Date date;

    public ReleaseNotesService(Context context) {
        this.context = context;

        BufferedReader reader = openReader();

        try {
            String numberAndName[] = reader.readLine().split(":");
            number = numberAndName[0];
            name = numberAndName[1];
            date = DATE_FORMAT.parse(reader.readLine());
        } catch (IOException e) {
            number = null;
            name = null;
            date = null;
        } catch (ParseException e) {
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

    public Date getReleaseDate () {
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
