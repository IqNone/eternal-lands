package el.android.widgets.trade;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import el.actor.Actor;
import el.actor.Item;
import el.android.R;
import el.android.widgets.*;
import el.android.widgets.storage.Storage;

import static android.view.ViewGroup.LayoutParams.*;
import static el.android.GameMetadata.CLIENT;

public class TradeDialog extends PopupWindow implements Invalidateable{
    private ViewFlipper flipper;

    private Inventory inventoryBig;
    private Storage storageBig;
    private Trade tradeBig;

    private QuantitySelector quantitySelector;

    private RelativeLayout bar;

    private TradeMinimal tradeSmall;
    private Inventory inventorySmall;

    private BarItem storageBarItem;
    private BarItem tradeBarItem;
    private BarItem inventoryBarITem;

    private boolean showStorage = true;

    private Actor actor;

    public TradeDialog(Context context) {
        super(context, null, R.style.CustomDialogTheme);

        setWidth(MATCH_PARENT);
        setHeight(WRAP_CONTENT);

        LinearLayout content = new LinearLayout(context);
        content.setBackgroundResource(R.drawable.popup_full_dark);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 20);
        setContentView(content);

        content.addView(getFlipper(context));
        content.addView(getQuantitySelector(context), marginLayout(0, 5, 0, 5));
        content.addView(getBar(context));

        inventoryBig.setActionCallback(new OnInventoryAction());
        inventorySmall.setCanMove(false);
    }

    private ViewGroup.LayoutParams marginLayout(int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private View getBar(Context context) {
        bar = new RelativeLayout(context);
        bar.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        inventorySmall = new Inventory(context);
        inventorySmall.setLayoutParams(new RelativeLayout.LayoutParams(100, 75));
        inventoryBarITem = new BarItem(inventorySmall, 0);
        inventoryBarITem.setLayoutParams(createMarginLayout(0, 0, 0, 0));

        tradeSmall = new TradeMinimal(context);
        tradeSmall.setLayoutParams(new ViewGroup.LayoutParams(200, 75));
        tradeBarItem = new BarItem(tradeSmall, 1);
        tradeBarItem.setLayoutParams(createMarginLayout(105, 0, 0, 0));

        ImageView storageSmall = new ImageView(context);
        storageSmall.setImageResource(R.drawable.storage_icon);
        storageSmall.setLayoutParams(new ViewGroup.LayoutParams(100, 75));
        storageBarItem = new BarItem(storageSmall, 2);
        storageBarItem.setLayoutParams(createMarginLayout(310, 0, 0, 0));

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
        tradeBig = new Trade(context);
        tradeBig.setMyItemClickListener(new RemoveFromTradeListener());
        tradeBig.setAcceptClickListener(new AcceptClickListener());
        storageBig = new Storage(context, new StorageItemClickedListener());

        flipper.addView(inventoryBig);
        flipper.addView(tradeBig);
        flipper.addView(storageBig);

        return flipper;
    }

    public void setActor(Actor actor) {
        this.actor = actor;

        inventoryBig.setItems(actor.inventory);
        inventorySmall.setItems(actor.inventory);

        tradeBig.setActor(actor);
        tradeSmall.setActor(actor);

        storageBig.setActor(actor);
    }

    public void setShowStorage(boolean value) {
        this.showStorage = value;
        flipper.setDisplayedChild(0);
        addItemsToBar();
    }

    private void addItemsToBar() {
        bar.removeAllViews();

        bar.addView(inventoryBarITem);
        bar.addView(tradeBarItem);

        if(showStorage) {
            bar.addView(storageBarItem);
        }
    }

    private ViewGroup.LayoutParams createMarginLayout(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            storageBig.invalidate();
            inventoryBig.invalidate();
            inventorySmall.invalidate();
        }
    }

    private class BarItem extends RelativeLayout {
        private int flipperIndex;

        public BarItem(View view, int flipperIndex) {
            super(view.getContext());
            this.flipperIndex = flipperIndex;

            addView(view);
            view.setClickable(false);
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    flipper.setDisplayedChild(BarItem.this.flipperIndex);
                    return true;
                }
            });
        }
    }

    private class OnInventoryAction implements Inventory.InventoryActionCallback {
        @Override
        public void onItemClicked(int itemPos) {
            CLIENT.putObjectOnTrade(itemPos, quantitySelector.getQuantity(), false);
        }

        @Override
        public void onItemMoved(int itemPos, int toPos) {

        }
    }

    private class StorageItemClickedListener implements ItemBag.ItemClickListener {
        @Override
        public void onItemClicked(Item item) {
            CLIENT.putObjectOnTrade(item.pos, quantitySelector.getQuantity(), true);
        }
    }

    private class RemoveFromTradeListener implements ItemBag.ItemClickListener {
        @Override
        public void onItemClicked(Item item) {
            CLIENT.removeObjectFromTrade(item.pos, quantitySelector.getQuantity());
        }
    }

    private class AcceptClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(actor.trade.myAccept == 2) {
                CLIENT.rejectTrade();
            } else {
                CLIENT.acceptTrade();
            }
        }
    }
}
