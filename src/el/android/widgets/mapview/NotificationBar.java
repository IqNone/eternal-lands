package el.android.widgets.mapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import el.actor.Actor;
import el.android.GameMetadata;
import el.android.assets.Assets;

import java.util.Date;

public class NotificationBar {
    private static final int ICON_SIZE_PX = 32;
    private static final long CAN_MOVE_MILLIS = 1500;

    private static final int PADDING = 20;

    private Actor actor;
    private int width = 0;

    private int iconSizeDP = ICON_SIZE_PX;
    private long lastUpdate = 0;

    private int height = 0;
    private boolean canWalk = false;

    private Paint paint;

    private Bitmap harvesting;
    private Bitmap walkingOn;
    private Bitmap walkingOff;
    private Bitmap disconnected;

    private long movingNotificationRemaining = 0;

    public NotificationBar(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        final float scale = context.getResources().getDisplayMetrics().density;
        iconSizeDP = (int) (ICON_SIZE_PX * scale + 0.5f);
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void showCanWalkNotification(boolean canMove) {
        this.canWalk = canMove;
        this.movingNotificationRemaining = CAN_MOVE_MILLIS;
    }

    public void draw(Canvas canvas) {
        if(actor == null || width == 0 || height == 0) {
            return;
        }

        long now = new Date().getTime();
        long elapsed = lastUpdate == 0 ? 0 : now - lastUpdate;
        lastUpdate = now;

        int left = PADDING;

        if(actor.harvesting) {
            drawNotification(getHarvesting(), left, canvas);
            left += PADDING + iconSizeDP;
        }

        if(movingNotificationRemaining > 0) {
            movingNotificationRemaining -= elapsed;
        }

        if(movingNotificationRemaining > 0 && (movingNotificationRemaining / 300) % 2 == 0) {
            drawNotification(getWalking(), left, canvas);
            left += PADDING + iconSizeDP;
        }

        if(!GameMetadata.CONNECTION.isConnected()) {
            drawNotification(getDisconnected(), left, canvas);
            left += PADDING + iconSizeDP;
        }
    }

    private void drawNotification(Bitmap img, int left, Canvas canvas) {
        canvas.drawBitmap(
                img,
                src(0, 0, img.getWidth(), img.getHeight()),
                dst(left, height - PADDING - iconSizeDP, left + iconSizeDP, height - PADDING),
                paint);
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

    private Bitmap getHarvesting() {
        if(harvesting == null) {
            harvesting = Assets.loadBitmap("icons/harvesting.png");
        }
        return harvesting;
    }

    private Bitmap getWalking() {
        return canWalk ? getWalkingOn() : getWalkingOff();
    }

    public Bitmap getWalkingOn() {
        if(walkingOn == null) {
            walkingOn = Assets.loadBitmap("icons/walking_on.png");
        }
        return walkingOn;
    }

    public Bitmap getWalkingOff() {
        if(walkingOff == null) {
            walkingOff = Assets.loadBitmap("icons/walking_off.png");
        }
        return walkingOff;
    }

    public Bitmap getDisconnected() {
        if(disconnected == null) {
            disconnected = Assets.loadBitmap("icons/disconnected.png");
        }
        return disconnected;
    }
}
