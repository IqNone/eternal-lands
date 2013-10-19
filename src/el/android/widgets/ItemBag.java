package el.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import el.actor.Item;
import el.android.assets.Assets;

public class ItemBag extends View implements View.OnTouchListener {
    public static interface ItemClickListener{
        public void onItemClicked(Item item);
    }

    protected Item items[];

    protected int rows = 6;
    protected int columns = 6;

    protected double cellSize;

    protected Paint strokePaint;
    protected Paint textPaint;

    private ItemClickListener clickListener;

    public ItemBag(Context context) {
        super(context);

        strokePaint = new Paint();
        strokePaint.setStrokeWidth(1);
        strokePaint.setColor(0xff906A47);
        strokePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);

        setOnTouchListener(this);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDimensions(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        invalidate();
    }

    public void setItems(Item items[]) {
        this.items = items;
        invalidate();
    }

    public Item[] getItems() {
        return items;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = width / columns;
        int height = (int) (cellSize * rows);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawItemsImages(canvas);
        drawGrid(canvas);
    }

    private void drawGrid(Canvas canvas) {
        for(int i = 0; i < rows; ++i){
            canvas.drawLine(0, (float)(i * cellSize), getWidth() - 1, (float) (i * cellSize), strokePaint);
        }
        canvas.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1, strokePaint);

        for(int i = 0; i < columns; ++i){
            canvas.drawLine((float)(i * cellSize), 0, (float) (i * cellSize), getHeight() - 1, strokePaint);
        }
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1, strokePaint);
    }

    private void drawItemsImages(Canvas canvas) {
        if(items == null) {
            return;
        }

        for(int i = 0; i < items.length; ++i) {
            if(items[i] != null && items[i].quantity > 0) {
                drawItemImage(items[i], (float)(i % columns * cellSize), (float) (i / columns * cellSize), canvas);
            }
        }
    }

    private Rect src = new Rect();
    private Rect dst = new Rect();

    private void drawItemImage(Item item, float left, float top, Canvas canvas) {
        Assets.IconBitmap image = Assets.getItemImage(item.imageId);

        src.set(image.x, image.y, image.x + image.size, image.y + image.size);
        dst.set((int) left, (int) top, (int)(left + cellSize), (int)(top + cellSize));

        canvas.drawBitmap(image.bitmap, src, dst, null);

        canvas.drawText(String.valueOf(item.quantity), left + 2, top + 2 + textPaint.getTextSize(), textPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(clickListener == null) {
            return false;
        }

        if(items != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) (event.getX() / cellSize);
            int y = (int) (event.getY() / cellSize);

            int pos = y * columns + x;
            if(pos < 0 || pos >= items.length) {
                return true;
            }

            Item item = items[pos];

            if(item != null && item.quantity > 0) {
                clickListener.onItemClicked(item);
            }
        }

        return true;
    }
}
