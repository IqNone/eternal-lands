package el.android.assets;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import static el.utils.IOUtils.closeQuite;

public class Assets {
    private static final int ITEMS_ON_ROW = 5;
    private static final int ITEMS_IN_FILE = 25;
    private static final int ITEM_SIZE = 50; // The item images are 255 by 255, but a single item is only 50 by 50

    private static AssetManager assetManager;
    private static ConcurrentHashMap<Integer, Bitmap> itemsFiles = new ConcurrentHashMap<Integer, Bitmap>(27);

    public static void setAssetManager(AssetManager am) {
        assetManager = am;
    }

    // TODO what if itemID is negative or outsite the range
    public static IconBitmap getItemImage(int itemId) {
        int fileId = itemId / ITEMS_IN_FILE;
        int itemInFile = itemId % ITEMS_IN_FILE;

        int row = itemInFile / ITEMS_ON_ROW;
        int col = itemInFile % ITEMS_ON_ROW;

        try {
            return new IconBitmap(getItemBitmapFromFileId(fileId), col * ITEM_SIZE, row * ITEM_SIZE);
        } catch (RuntimeException e) {
            // If no corresponding bitmap if found
            // return the missing item logo, found
            // on the last item map.
            // TODO make it select itself the smallest
            return new IconBitmap(getItemBitmapFromFileId(26), (ITEMS_IN_FILE - 1) / ITEMS_ON_ROW * ITEM_SIZE, (ITEMS_ON_ROW - 1) * ITEM_SIZE);
        }
    }

    public static Bitmap getItemBitmapFromFileId(int fileId) {
        Bitmap bitmap = itemsFiles.get(fileId);
        if (bitmap == null) {
            bitmap = loadBitmap("items/items" + (fileId + 1) + ".png");
            itemsFiles.put(fileId, bitmap);
        }
        return bitmap;
    }

    public static Bitmap loadBitmap(String fileName) {
        InputStream in = null;
        Bitmap bitmap = null;

        try {
            in = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null) {
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        } finally {
            closeQuite(in);
        }

        return bitmap;
    }

    public static class IconBitmap {
        public Bitmap bitmap;
        public int x;
        public int y;
        public int size = ITEM_SIZE;

        public IconBitmap(Bitmap bitmap, int x, int y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
        }

        public boolean sameAs(IconBitmap iconBitmap) {
            if (iconBitmap == null) {
                return false;
            }

            if (this.bitmap == null && iconBitmap.bitmap != null) {
                return false;
            } else if (!this.bitmap.sameAs(iconBitmap.bitmap)) {
                // sameAs is too strict!!
                return false;
            }
            return this.x == iconBitmap.x && this.y == iconBitmap.y;
        }
    }
}
