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
    private static final int ITEM_SIZE = 51;

    private static AssetManager assetManager;
    private static ConcurrentHashMap<Integer, Bitmap> itemsFiles = new ConcurrentHashMap<>(27);

    public static void setAssetManager(AssetManager am) {
        assetManager = am;
    }

    public static IconBitmap getItemImage(int itemId) {
        int fileId = itemId / ITEMS_IN_FILE;
        int itemInFile =  itemId % ITEMS_IN_FILE;

        int row = itemInFile / ITEMS_ON_ROW;
        int col = itemInFile % ITEMS_ON_ROW;

        Bitmap bitmap = getItemBitmapFromFileId(fileId);
        return new IconBitmap(bitmap, col * ITEM_SIZE, row * ITEM_SIZE);
    }

    private static Bitmap getItemBitmapFromFileId(int fileId) {
        Bitmap bitmap = itemsFiles.get(fileId);
        if(bitmap == null) {
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

    public static class IconBitmap{
        public IconBitmap(Bitmap bitmap, int x, int y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
        }

        public Bitmap bitmap;
        public int x;
        public int y;
        public int size = ITEM_SIZE;
    }
}
