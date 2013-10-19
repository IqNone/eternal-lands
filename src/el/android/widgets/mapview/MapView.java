package el.android.widgets.mapview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import el.actor.Actor;
import el.actor.BaseActor;
import el.actor.Span;
import el.android.SharedSettings;
import el.android.assets.Assets;
import el.android.expansions.ExternalStorageUtil;
import el.android.widgets.Commander;
import el.logging.Logger;
import el.logging.LoggerFactory;
import el.map.MapObject;
import us.gorges.android.GestureImageView;

import static el.client.Colors.*;

public class MapView extends GestureImageView implements View.OnTouchListener, Commander {
    private static final Logger LOGGER = LoggerFactory.logger(MapView.class);

    public static final int ENTRABLE_SIZE = 5;
    public static final int HARVESTABLE_SIZE = 1;

    public static final int ACTOR_RADIUS_PX = 10;

    private float scale = 1;

    private Paint positionPaint;
    private Paint movingToPaint;

    private Paint[] actorsPaint;
    private Paint namePaint;

    private Bitmap bitmap;
    private int mapWidth;
    private int mapHeight;


    private Actor actor;
    private String lastBitmapPath;

    private TextManager textManager = new TextManager();
    private TouchListener touchListener = new TouchListener();
    private NotificationBar notificationBar;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        positionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        positionPaint.setStyle(Paint.Style.FILL);
        positionPaint.setColor(Color.BLUE);

        movingToPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        movingToPaint.setStyle(Paint.Style.FILL);
        movingToPaint.setColor(Color.RED);

        actorsPaint = new Paint[7];
        actorsPaint[0] = createActorPaint(GREEN1);
        actorsPaint[1] = createActorPaint(COLORS[GREY1]);
        actorsPaint[2] = createActorPaint(COLORS[BLUE2]);
        actorsPaint[3] = createActorPaint(COLORS[GREY1]);
        actorsPaint[4] = createActorPaint(COLORS[RED3]);
        actorsPaint[5] = createActorPaint(COLORS[RED3]);
        actorsPaint[6] = createActorPaint(0xFFDAD900);

        namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setTextSize(15);

        setPadding(1, 1, 1, 1);

        setOnTouchListener(this);

        notificationBar = new NotificationBar(context);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        touchListener.enableLongPress(settings.getBoolean(SharedSettings.ENABLE_LONG_PRESS, true));
    }

    private Paint createActorPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        textManager.setActor(actor);
        touchListener.setActor(actor);
        notificationBar.setActor(actor);

        if(!actor.mapPath.equals(lastBitmapPath)) {
            reloadBitmap();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return touchListener.onTouch(event, getMapX(event.getX()), getMapY(event.getY()));
    }

    @Override
    public void setCommandListener(CommandListener listener) {
        touchListener.setCommandListener(listener);
    }


    public void setCanMovNotifier(boolean canMove) {
        notificationBar.showCanWalkNotification(canMove);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        textManager.resize(getWidth());
        notificationBar.setDimensions(getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!isShown() || actor == null || actor.mapPath == null || bitmap == null) {
            return;
        }

        scale = getScaledWidth() / (float)mapWidth;

        if(scale > 2) {
            drawEntrables(canvas);
            drawHarvestables(canvas);
        }

        drawActors(canvas);
        drawActor(canvas, getScreenX(actor.x), getScreenY(actor.y), positionPaint);

        if(actor.moveToX >= 0 && actor.moveToY >= 0) {
            drawActor(canvas, getScreenX(actor.moveToX), getScreenY(actor.moveToY), movingToPaint);
        }


//        Paint line = new Paint();
//        line.setColor(Color.WHITE);
//
//        for(int i = 1; i < mapWidth; ++i) {
//            canvas.drawLine(getScreenX(i), getScreenY(0), getScreenX(i), getScreenY(mapHeight), line);
//            canvas.drawLine(getScreenX(0), getScreenY(i), getScreenX(mapWidth), getScreenY(i), line);
//        }

        textManager.drawText(canvas);
        notificationBar.draw(canvas);
    }

    private void drawEntrables(Canvas canvas) {
//        float r = Math.min(40, ENTRABLE_SIZE * scale);
        float r = ENTRABLE_SIZE * scale;

        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2);
        border.setColor(Color.BLACK);

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(Color.LTGRAY);

        for (MapObject entrable : actor.map.entrables) {
            float x = getScreenX(entrable.x);
            float y = getScreenY(entrable.y);

            if(onScreen(x, y)) {
                canvas.drawCircle(x + scale / 2, y - scale / 2, r / 2, fill);
                canvas.drawCircle(x + scale / 2, y - scale / 2, r / 2, border);
            }
        }
    }

    private void drawHarvestables(Canvas canvas) {
        float s = HARVESTABLE_SIZE * scale;

        for(MapObject harvestable : actor.map.harvestables) {
            float x = getScreenX(harvestable.x);
            float y = getScreenY(harvestable.y);

            if(onScreen(x, y)) {
                if(harvestable.imgId != 0){
                    Assets.IconBitmap image = Assets.getItemImage(harvestable.imgId);
                    canvas.drawBitmap(image.bitmap,
                            src(image.x, image.y, image.x + image.size, image.y + image.size),
                            dst((int)x, (int)(y - s), (int)(x + s), (int)y),
                            null);
                }
            }
        }
    }

    private void drawActors(Canvas canvas) {
        for (BaseActor baseActor : actor.actors.values()) {
            Paint paint = baseActor.id == actor.id ? actorsPaint[0] : actorsPaint[baseActor.nameColor];
            drawActor(canvas, getScreenX(baseActor.x), getScreenY(baseActor.y), paint);
            if(scale > 4) {
                drawName(canvas, baseActor);
            }
        }
    }

    private void drawName(Canvas canvas, BaseActor baseActor) {
        float x = getScreenX(baseActor.x);
        float y = getScreenY(baseActor.y);
        int size = 0;
        for (Span span : baseActor.name) {
            size += namePaint.measureText(span.text);
        }
        float pos = x + scale / 2 - size / 2;
        for (Span span : baseActor.name) {
            namePaint.setColor(span.color == -1 ? actorsPaint[baseActor.nameColor].getColor() : span.color);
            canvas.drawText(span.text, pos, y - scale / 2 - 20, namePaint);
            pos += namePaint.measureText(span.text);
        }
    }

    private boolean onScreen(float x, float y) {
        return x >= 0 && y >= 0 && x <= getWidth() && y <=  getHeight();
    }
    //use only 2 instances
    private Rect src = new Rect();
    private Rect dst = new Rect();

    private Rect src(int left, int top, int right, int bottom) {
        src.set(left, top, right, bottom);
        return src;
    }

    private Rect dst(int left, int top, int right, int bottom) {
        dst.set(left, top, right, bottom);
        return dst;
    }

    private void reloadBitmap() {
        if(bitmap != null) {
            bitmap.recycle();
        }

        lastBitmapPath = actor.mapPath;
        mapWidth = actor.map.width;
        mapHeight = actor.map.height;
        bitmap  = loadBitmap(createBitmapFilename(actor.mapPath));

        setImageBitmap(bitmap);
        setMaxScale(4 * mapWidth);
    }

    private Bitmap loadBitmap(String filename) {
        Bitmap bitmap = ExternalStorageUtil.loadMap(getContext(), filename);
        if(bitmap == null) {
            bitmap = tryLoadBitmapFromAssets(filename);
        }

        return bitmap;
    }

    private Bitmap tryLoadBitmapFromAssets(String fileName) {
        try {
            return Assets.loadBitmap(fileName);
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }
        return null;
    }

    private String createBitmapFilename(String mapPath) {
        return mapPath.substring(1).replaceAll("elma", "png");
    }

    private void drawActor(Canvas canvas, float x, float y, Paint paint) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return;
        }

        canvas.drawCircle(x + scale / 2, y - scale / 2, ACTOR_RADIUS_PX, paint);
    }

    private float getScreenX(int x) {
        return x * scale - getScaledWidth() / 2 + getImageX();
    }

    private float getScreenY(int y) {
        return (mapHeight - y) * scale - getScaledHeight() / 2  + getImageY();
    }

    private int getMapX(float screenX) {
        return (int) ((screenX + getScaledWidth() / 2 - getImageX()) / scale);
    }

    private int getMapY(float screenY) {
        return mapHeight - (int) ((screenY + getScaledHeight() / 2 - getImageY()) / scale) - 1;
    }
}
