package el.android.expansions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import el.logging.Logger;
import el.logging.LoggerFactory;

import java.io.InputStream;

public class ExternalStorageUtil {
    private static final Logger LOGGER = LoggerFactory.logger(ExternalStorageUtil.class);

    public static Bitmap loadMap(Context context, String filename) {
        InputStream in = getInputStream(context, filename);
        return in == null ? null : BitmapFactory.decodeStream(in);
    }

    public static InputStream getInputStream(Context context, String filename) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        try {
//            Karolus is special:)
//            ZipResourceFile expansionFile = APKExpansionSupport.getResourceZipFile(new String[] {"/storage/external_SD/Android/obb/el.android/main.1.el.android.obb"});
            ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, 1, 0);
            return expansionFile.getInputStream(filename);
        } catch (Exception e) {
//            happens all the time and ends up sending emails all the time
//            LOGGER.error(e);
            return null;
        }
    }
}
