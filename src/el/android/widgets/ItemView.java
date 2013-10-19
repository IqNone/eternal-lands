package el.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import el.actor.Item;
import el.android.GameMetadata;
import el.android.assets.Assets;

import static java.lang.String.valueOf;

public class ItemView extends View implements View.OnClickListener {
    private Paint textPaint;
    private Paint borderPaint;
    private Paint cooldownPaint;

    private Assets.IconBitmap iconBitmap;
    private Item item;

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initPaints();
        initActions();
    }

    private void initPaints() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);

        borderPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(0xFF916B48);

        cooldownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cooldownPaint.setStyle(Paint.Style.FILL);
        cooldownPaint.setColor(0xB4001A60);
    }

    private void initActions() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (item == null || item.quantity == 0) {
            return;
        }

        GameMetadata.CLIENT.useItem(item.pos);
    }

    public void setItem(Item item) {
        //need to update the image
        if(item != null && this.item != null && item.imageId == this.item.imageId && this.item.quantity > 0 && item.quantity > 0 && iconBitmap != null) {
            return;
        }

        this.item = item;
        if(item != null && item.quantity > 0) {
            iconBitmap = Assets.getItemImage(item.imageId);
        } else {
            iconBitmap = null;
        }
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(iconBitmap != null) {
            drawItemImage(canvas);
            drawItemCooldown(canvas);
            drawItemQuantity(canvas);
        }
        canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, borderPaint);
    }

    private void drawItemImage(Canvas canvas) {
        canvas.drawBitmap(
                iconBitmap.bitmap,
                new Rect(iconBitmap.x, iconBitmap.y, iconBitmap.x + iconBitmap.size - 1, iconBitmap.y + iconBitmap.size - 1),
                new Rect(1, 1, getWidth() - 1, getHeight() - 1),
                null);
    }

    private void drawItemCooldown(Canvas canvas) {
        if(item.cooldownLeft == 0 || item.cooldownMax == 0) {
            return;
        }

        double d = ((double) item.cooldownLeft) / item.cooldownMax;
        int h = getHeight() - 2; //1 for left and 1 for right

        canvas.drawRect(1, (int) (1 + h * (1 - d)), getWidth() - 1, getHeight() - 1, cooldownPaint);
    }

    private void drawItemQuantity(Canvas canvas) {
        canvas.drawText(valueOf(item.quantity), 2, 2 + textPaint.getTextSize(), textPaint);
    }
}
