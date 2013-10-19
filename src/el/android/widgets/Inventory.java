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

public class Inventory extends View {
    public static interface InventoryActionCallback {
        public void onItemClicked(int itemPos);
        public void onItemMoved(int itemPos, int toPos);
    }

    private Paint itemPaint;
    private Paint cooldownPaint;
    private Paint equipmentPaint;
    private Paint textPaint;
    private Paint selectedPaint;
    private Paint moveOkPaint;
    private Paint moveBadPaint;

    private float cellWidth;
    private float cellHeight;

    private Item[] items;
    private int startPos;
    private int endPos;

    private InventoryActionCallback actionCallback;

    private boolean canMove = true;

    public Inventory(Context context) {
        super(context);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);

        itemPaint = createGridPaint(0xff906A47);
        equipmentPaint = createGridPaint(0xff6A7D5A);
        selectedPaint = createGridPaint(0xFFFFD800);
        moveOkPaint = createGridPaint(0xFF4CFF00);
        moveBadPaint = createGridPaint(0xFFFF0000);

        cooldownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cooldownPaint.setStyle(Paint.Style.FILL);
        cooldownPaint.setColor(0xB4001A60);

        setOnTouchListener(new InventoryTouchListener());
    }

    private Paint createGridPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        return paint;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void setActionCallback(InventoryActionCallback actionCallback) {
        this.actionCallback = actionCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 3 / 4;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cellHeight = getHeight() / 6;
        cellWidth = getWidth() / 8;

        drawItemsImages(canvas);
        drawItemsGrid(canvas);
        drawEquipmentGrid(canvas);
        drawSelections(canvas);
    }

    private void drawItemsGrid(Canvas canvas) {
        for(int i = 0; i < 7; ++i) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, getHeight() - 2, itemPaint);
            canvas.drawLine(0, i * cellHeight, 6 * cellWidth, i * cellHeight, itemPaint);
        }
    }

    private void drawEquipmentGrid(Canvas canvas) {
        //draw vertical lines
        canvas.drawLine(cellWidth * 7, cellHeight, cellWidth * 7, cellHeight * 5, equipmentPaint);
        canvas.drawLine(cellWidth * 8, cellHeight, cellWidth * 8, cellHeight * 5, equipmentPaint);

        //draw horizontal lines
        for(int i = 0; i < 5; ++i) {
            canvas.drawLine(cellWidth * 6 + 1, (i + 1) * cellHeight, cellWidth * 8, (i + 1) * cellHeight, equipmentPaint);
        }
    }

    private void drawItemsImages(Canvas canvas) {
        if(items == null) {
            return;
        }

        for(int i = 0; i < 36; ++i) {
            if(items[i] != null && items[i].quantity > 0) {
                drawItemImage(items[i], i % 6 * cellWidth, i / 6 * cellHeight, canvas);
            }
        }

        for(int i = 0; i < 8; ++i) {
            if(items[36 + i] != null && items[36 + i].quantity > 0) {
                drawItemImage(items[36 + i], (i % 2 + 6) * cellWidth, (i / 2 + 1) * cellHeight, canvas);
            }
        }
    }

    private Rect src = new Rect();
    private Rect dst = new Rect();

    private void drawItemImage(Item item, float left, float top, Canvas canvas) {
        Assets.IconBitmap image = Assets.getItemImage(item.imageId);

        src.set(image.x, image.y, image.x + image.size, image.y + image.size);
        dst.set((int) left, (int) top, (int)(left + cellWidth), (int)(top + cellHeight));

        canvas.drawBitmap(image.bitmap, src, dst, null);

        if(item.cooldownLeft > 0 && item.cooldownMax > 0) {
            double d = ((double) item.cooldownLeft) / item.cooldownMax;
            dst.set(dst.left, (int) (top + cellHeight * (1 - d)), dst.right, dst.bottom);
            canvas.drawRect(dst, cooldownPaint);
        }

        if(cellHeight / 2 > textPaint.getTextSize()) {
            canvas.drawText(String.valueOf(item.quantity), left + 2, top + 2 + textPaint.getTextSize(), textPaint);
        }
    }

    private void drawSelections(Canvas canvas) {
        if(!canMove || startPos == -1 || endPos == -1 || startPos == endPos) {
            return;
        }

        drawGridCell(startPos, selectedPaint, canvas);
        drawGridCell(endPos, items[endPos] == null || items[endPos].quantity == 0 ? moveOkPaint : moveBadPaint, canvas);
    }

    private void drawGridCell(int pos, Paint paint, Canvas canvas) {
        float left = pos < 36 ? pos % 6 * cellWidth : ((pos - 36) % 2 + 6)* cellWidth;
        float top = pos < 36 ? pos / 6 * cellHeight : ((pos - 36) / 2 + 1) * cellHeight;

        canvas.drawRect(left, top, left + cellWidth, top + cellHeight, paint);
    }

    private class InventoryTouchListener implements OnTouchListener {
        private boolean dragging = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    startPos = getInventoryPos(event);
                } break;
                case MotionEvent.ACTION_MOVE: {
                    if(canMove && startPos != -1 && items[startPos] != null) {
                        dragging = true;
                        endPos = getInventoryPos(event);
                        invalidate();
                    }
                } break;
                case MotionEvent.ACTION_UP :{
                    endPos = getInventoryPos(event);
                    sendActionEvents(startPos, endPos, dragging);
                    dragging = false;
                    startPos = -1;
                    endPos = -1;
                    invalidate();
                }
            }

            return true;
        }

        private int getInventoryPos(MotionEvent event) {
            int column = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);

            if(row >=0 && row < 6 && column >= 0 && column < 6) {
                return row * 6 + column;
            }

            if(column >= 6 && column <= 7 && row >= 1 && row <= 4) {
                return 36 + (row - 1) * 2 + column - 6;
            }

            return -1;
        }

        private void sendActionEvents(int startPos, int endPos, boolean dragging) {
            if(actionCallback == null || endPos == -1) {
                return;
            }

            if(dragging && endPos != startPos) {
                if(items[endPos] == null || items[endPos].quantity == 0) {
                    actionCallback.onItemMoved(startPos, endPos);
                }
            } else if(items[endPos] != null && items[endPos].quantity > 0){
                actionCallback.onItemClicked(endPos);
            }
        }
    }
}
