package el.android.widgets.storage;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import el.actor.Actor;
import el.actor.Item;
import el.android.R;
import el.android.widgets.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static el.android.GameMetadata.CLIENT;

public class StorageDialog extends Dialog implements Invalidateable, ItemBag.ItemClickListener {
    private Inventory inventorySmall;
    private Inventory inventoryBig;
    private Storage storageBig;
    private QuantitySelector quantitySelector;

    private ViewFlipper flipper;

    private Item items[];


    public StorageDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        content.setPadding(3, 3, 3, 3);
        setContentView(content);

        content.addView(getFlipper(context));
        content.addView(new PaddingView(getContext()));
        content.addView(getQuantitySelector(context));
        content.addView(new PaddingView(getContext()));
        content.addView(getBar(context));

        inventoryBig.setActionCallback(new OnInventoryAction());
        inventorySmall.setCanMove(false);
    }

    private View getBar(Context context) {
        RelativeLayout bar = new RelativeLayout(context);
        bar.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        inventorySmall = new Inventory(context);
        inventorySmall.setLayoutParams(new RelativeLayout.LayoutParams(120, 90));
        BarItem item1 = new BarItem(inventorySmall, 0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(120, 90);
        params.topMargin = 0;
        params.leftMargin = 0;
        item1.setLayoutParams(params);

        ImageView storageSmall = new ImageView(context);
        storageSmall.setImageResource(R.drawable.storage_icon);
        storageSmall.setLayoutParams(new ViewGroup.LayoutParams(120, 90));
        BarItem item2 = new BarItem(storageSmall, 1);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(120, 90);
        params1.topMargin = 0;
        params1.leftMargin = 125;
        item2.setLayoutParams(params1);

        bar.addView(item1);
        bar.addView(item2);

        return bar;
    }

    private View getQuantitySelector(Context context) {
        quantitySelector = new QuantitySelector(context);
        return quantitySelector;
    }

    private ViewFlipper getFlipper(Context context) {
        flipper = new ViewFlipperFixed(context);
        flipper.setInAnimation(context, android.R.anim.slide_in_left);
        flipper.setOutAnimation(context, android.R.anim.slide_out_right);

        inventoryBig = new Inventory(context);
        storageBig = new Storage(context, this);

        flipper.addView(inventoryBig);
        flipper.addView(storageBig);
        return flipper;
    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            storageBig.invalidate();
            inventoryBig.invalidate();
            inventorySmall.invalidate();
        }
    }

    @Override
    public void onItemClicked(Item item) {
        CLIENT.withdraw(item.pos, quantitySelector.getQuantity());
    }

    public void setActor(Actor actor) {
        items = actor.inventory;

        inventorySmall.setItems(actor.inventory);
        inventoryBig.setItems(actor.inventory);
        storageBig.setActor(actor);
    }

    private class OnInventoryAction implements Inventory.InventoryActionCallback {
        @Override
        public void onItemClicked(int itemPos) {
            Item item = items[itemPos];
            CLIENT.deposit(item.pos, quantitySelector.getQuantity());
        }

        @Override
        public void onItemMoved(int itemPos, int toPos) {
            CLIENT.moveItemInInventory(itemPos, toPos);
        }
    }

    private class BarItem extends RelativeLayout {
        private final View view;

        private final int flipperIndex;
        public BarItem(View view, int flipperIndex) {
            super(view.getContext());
            this.view = view;
            this.flipperIndex = flipperIndex;

            addView(this.view);
            view.setClickable(false);
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    flipper.setDisplayedChild(BarItem.this.flipperIndex);
                    return true;
                }
            });
        }

    }

    private class PaddingView extends View {

        public PaddingView(Context context) {
            super(context);
        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 5;

            setMeasuredDimension(width, height);
        }

    }
}
