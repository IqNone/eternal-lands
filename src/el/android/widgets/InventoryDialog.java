package el.android.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import el.actor.Attributes;
import el.actor.Item;
import el.android.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.LayoutParams;
import static el.android.GameMetadata.CLIENT;

public class InventoryDialog extends Dialog implements Invalidateable{
    private Inventory inventory;
    private LoadInfo capacityInfo;

    public InventoryDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        buildContent(context);
    }

    private void buildContent(Context context) {
        inventory = new Inventory(context);
        capacityInfo = new LoadInfo(context);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        layout.setPadding(2, 2, 2, 2);

        layout.addView(inventory);
        layout.addView(capacityInfo);

        inventory.setActionCallback(new OnInventoryAction());

        setContentView(layout);
    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            inventory.invalidate();
            capacityInfo.updateText();
        }
    }

    public void setItems(Item[] items) {
        inventory.setItems(items);
    }

    public void setCapacity(Attributes.Attribute capacity) {
        capacityInfo.setCapacity(capacity);
    }

    private class OnInventoryAction implements Inventory.InventoryActionCallback {
        @Override
        public void onItemClicked(int itemPos) {
            CLIENT.useItem(itemPos);
        }

        @Override
        public void onItemMoved(int itemPos, int toPos) {
            CLIENT.moveItemInInventory(itemPos, toPos);
        }
    }

    private static class LoadInfo extends TextView {
        private Attributes.Attribute capacity;

        public LoadInfo(Context context) {
            super(context);
        }

        public void setCapacity(Attributes.Attribute capacity) {
            this.capacity = capacity;
        }

        public void updateText() {
            setText(getContext().getString(R.string.inv_capacity, capacity.current, capacity.base));
        }
    }
}
