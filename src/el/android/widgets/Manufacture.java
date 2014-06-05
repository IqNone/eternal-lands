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

public class Manufacture extends View {
    public static interface ManufactureActionCallback {
        public void onItemClicked(int itemPos);
        public void onItemLongClicked(int itemPos);
        public void onManufactureItemClicked(int itemPos);
    }

    private Paint itemPaint;
    private Paint cooldownPaint;
    private Paint equipmentPaint;
    private Paint textPaint;

    private float cellWidth;
    private float cellHeight;

    private Item[] items;
    private int pos;

    private boolean recipe_ok = false;

    public Item[] manufacture_items =  new Item[6];

    private ManufactureActionCallback actionCallback;

    public Manufacture(Context context) {
        super(context);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);

        itemPaint = createGridPaint(0xff906A47);
        equipmentPaint = createGridPaint(0xff6A7D5A);

        cooldownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cooldownPaint.setStyle(Paint.Style.FILL);
        cooldownPaint.setColor(0xB4001A60);

        setOnTouchListener(new ManufactureTouchListener());
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

    public void addItem(int itemPos){
        int existing_item_pos = -1;
        int first_empty_cell = -1;

        // If the recipe items are not provided and a new item is clicked, first
        // clean the recipe and build the new one
        if( !getRecipe_ok() ) {
            removeAll();
        }


        // Check if the clicked item is already in the manufacture grid
        for(int i = 0; i < manufacture_items.length; i++){
            if(manufacture_items[i] != null && manufacture_items[i].imageId == items[itemPos].imageId) {
                manufacture_items[i].pos = items[itemPos].pos;
                if(items[itemPos].quantity > manufacture_items[i].quantity) {
                    manufacture_items[i].quantity = manufacture_items[i].quantity + 1;
                }
                existing_item_pos = i;
                break;
            }
        }

        if (existing_item_pos == -1) {
            // Not an existing item, so add it to the manufacture item list in the first empty box
            // Find first empty cell in manufacture window if the item is not existing
            for(int i = 0; i < manufacture_items.length; i++) {
                if(manufacture_items[i] == null || manufacture_items[i].quantity == 0) {
                    first_empty_cell = i;
                    break;
                }
            }

            // If the item is null create it first, check if there is space for a new item
            // if not it means all 6 boxes are filled for manufacturing
            if(first_empty_cell != -1 &&  manufacture_items[first_empty_cell] == null) {
                Item item = new Item();
                item.imageId = items[itemPos].imageId;
                item.pos = items[itemPos].pos;
                item.quantity = 1;
                manufacture_items[first_empty_cell] = item;
            }
        }
    }

    public void remove_manufacture_item(int itemPos){

        if(manufacture_items[itemPos] != null && manufacture_items[itemPos].quantity > 0) {
            manufacture_items[itemPos].quantity = manufacture_items[itemPos].quantity - 1;
        }

        if(manufacture_items[itemPos].quantity == 0) {
            manufacture_items[itemPos] = null;
        }
    }

    // Clear manufacture pipe
    public void removeAll() {
        for(int i = 0; i < manufacture_items.length; i++){
            if(manufacture_items[i] != null) {
                manufacture_items[i] = null;
            }
        }
    }


    public void check_recipe(){
        // Implementing this whole thing might be easier with arraylist of items?
        int itemPos;
        int num_items = 0;
        setRecipe_ok(true);

        // If there is no item in recipe, don't bother checking items
        for(int i = 0; i < manufacture_items.length; i++) {
            if (manufacture_items[i] != null) {
                num_items++;
            }
        }

        if(num_items == 0) {
            setRecipe_ok(false);
            return;
        }

        boolean possible = true;

        // Desktop client code adapted. (Should be easier)
        for(int i = 0; possible && i< num_items; i++) {
            if(manufacture_items[i] != null && manufacture_items[i].quantity > 0) {
                boolean not_found = true;
                for(int j = 0; possible && j < items.length; j++) {
                    if(  (items[j] != null && items[j].quantity > 0) &&
                            (manufacture_items[i].imageId == items[j].imageId) ){
                        // We need to set position of item in the inventory position in case
                        // item is moved in inventory.
                        manufacture_items[i].pos = j;

                        // Original code also checks for item id's. They are not currently use in Android.
                        if( manufacture_items[i].quantity > items[j].quantity) {
                            possible = false;
                        }
                        not_found = false;
                        break;
                    }
                }

                if(not_found) {
                    possible = false;
                }

            }
        }

        setRecipe_ok(possible);
    }


    public void setActionCallback(ManufactureActionCallback actionCallback) {
        this.actionCallback = actionCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 4 / 3;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cellHeight = getHeight() / 8;
        cellWidth = getWidth() / 6;

        drawItemsImages(canvas);
        drawItemsGrid(canvas);
        drawManufactureGrid(canvas);
        //drawSelections(canvas);
    }

    private void drawItemsGrid(Canvas canvas) {
        for(int i = 0; i < 7; ++i) {
            // Vertical
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, 6 * cellHeight, itemPaint);
            // Horizontal
            canvas.drawLine(0, i * cellHeight, 6 * cellWidth, i * cellHeight, itemPaint);
        }
    }

    private void drawManufactureGrid(Canvas canvas) {
        // Manufacture grid will be 6 cells wide below inventory

        for(int i = 0; i < 7; ++i) {
            // Vertical
            canvas.drawLine(i * cellWidth, 6 * cellHeight, i * cellWidth, 7 * cellHeight, equipmentPaint);
        }
        // Horizontal
        canvas.drawLine(0, 7 * cellHeight, 6 * cellWidth, 7 * cellHeight, equipmentPaint);
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

        // If the all the items of recipe is ok, than draw images
        if(getRecipe_ok()) {
            for (int i = 0; i < 6; ++i) {
                if (manufacture_items[i] != null && manufacture_items[i].quantity > 0) {
                    drawItemImage(manufacture_items[i], i * cellWidth, 6 * cellHeight, canvas);
                }
            }
        }
        invalidate();
        requestLayout();

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


    public boolean getRecipe_ok() {
        return recipe_ok;
    }

    public void setRecipe_ok(boolean recipe_ok) {
        this.recipe_ok = recipe_ok;
    }



    private class ManufactureTouchListener implements OnTouchListener {
        private int startPos=-1;

        long event_start = 0;
        long event_time = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    event_start = event.getEventTime();
                    startPos = getInventoryPos(event);
                } break;
                case MotionEvent.ACTION_UP: {
                    event_time = event.getEventTime() - event_start;
                    pos = getInventoryPos(event);
                    // If the user starts clicking one item, and then drags the finger invalidate that action
                    // If down and up positions are at the same item, them send the action.
                    if(startPos == pos) {
                        sendActionEvents(pos, event_time);
                    }
                } break;
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
            // If the clicked item is in the manufacturing boxes, send it with 44+column, so
            // that it does nothing with the inventory and equipment boxes;
            // Later subtract 44 from everything related to manufacture boxes
            if(row >=6) {
                return 44 + column;
            }

            return -1;
        }

        private void sendActionEvents(int pos, long event_time) {
            if(actionCallback == null || pos == -1) {
                return;
            }

            if(pos >= 44) {
                // Removing item from manufacture pipe
                if( manufacture_items[pos - 44] != null && manufacture_items[pos - 44].quantity > 0) {
                    // Subtract 44 so that it sends the right position in the manufacture item list
                    actionCallback.onManufactureItemClicked(pos - 44);
                }
            }else if(items[pos] != null && items[pos].quantity > 0){
                // Adding item to manufacture pipe or using item
                // If the item in inventory part of the manufacture window is clicked more than 1 second
                // then send client the useitem message.
                if(event_time < 1000) {
                    actionCallback.onItemClicked(pos);
                }else {
                    actionCallback.onItemLongClicked(pos);
                }
            }
        }
    }
}
