package el.android.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import el.actor.Attributes;
import el.actor.Item;
import el.actor.Span;
import el.android.R;
import el.client.Colors;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.LayoutParams;
import static el.android.GameMetadata.CLIENT;

public class ManufactureDialog extends Dialog implements Invalidateable{
    private Manufacture manufacture;
    private LoadInfo capacityInfo;
    private ManufactureInfo manufactureResult;
    private Button clearButton;
    private Button mixButton;
    private Button mixAllButton;

    public ManufactureDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        buildContent(context);
    }

    private void buildContent(Context context) {
        manufacture = new Manufacture(context);
        capacityInfo = new LoadInfo(context);
        manufactureResult = new ManufactureInfo(context);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        layout.setPadding(2, 2, 2, 2);

        layout.addView(manufacture);
        layout.addView(manufactureResult);
        layout.addView(capacityInfo);


        LinearLayout button_holders = new LinearLayout(getContext());
        button_holders.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        layout.setPadding(2, 2, 2, 2);


        LinearLayout clear_button = new LinearLayout(getContext());
        clear_button.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        clear_button.setGravity(Gravity.BOTTOM|Gravity.LEFT);
        clear_button.setOrientation(LinearLayout.HORIZONTAL);
        clear_button.setPadding(2,2,2,2);

        LinearLayout mix_buttons = new LinearLayout(getContext());
        mix_buttons.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mix_buttons.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        mix_buttons.setOrientation(LinearLayout.HORIZONTAL);
        mix_buttons.setPadding(2,2,2,2);

        clearButton = new Button(getContext());
        clearButton.setText("Clear");
        clearButton.setTextSize(12);
        clearButton.setTextColor(0xFFFF0000);
        clearButton.setGravity(Gravity.BOTTOM| Gravity.LEFT);
        clearButton.setOnClickListener(new OnClearButtonClickListener());

        mixButton = new Button(getContext());
        mixButton.setText("Mix");
        mixButton.setTextSize(12);
        mixButton.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        mixButton.setOnClickListener(new OnMixButtonClickListener());

        mixAllButton = new Button(getContext());
        mixAllButton.setText("Mix All");
        //mixButton.setLayoutParams();
        mixAllButton.setTextSize(12);
        mixAllButton.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        mixAllButton.setOnClickListener(new OnMixAllButtonClickListener());


        clear_button.addView(clearButton);
        mix_buttons.addView(mixButton);
        mix_buttons.addView(mixAllButton);

        button_holders.addView(clear_button);
        button_holders.addView(mix_buttons);

        layout.addView(button_holders);

        manufacture.setActionCallback(new OnManufactureAction());

        setContentView(layout);
    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            // Should I check the recipe everytime hmm?
            manufacture.check_recipe();
            if(!manufacture.getRecipe_ok()) {
                mixButton.setEnabled(false);
                mixAllButton.setEnabled(false);
            }
            else{
                mixButton.setEnabled(true);
                mixAllButton.setEnabled(true);
            }
            manufacture.invalidate();
            capacityInfo.updateText();
            manufactureResult.updateText();

        }
    }

    public void setItems(Item[] items) {
        manufacture.setItems(items);
    }

    public void setCapacity(Attributes.Attribute capacity) {
        capacityInfo.setCapacity(capacity);
    }

    public void setInventoryText(Span inventory_item_text) {
        manufactureResult.setInventoryText(inventory_item_text);
    }

    private class OnManufactureAction implements Manufacture.ManufactureActionCallback {
        @Override
        public void onItemClicked(int itemPos) {
            // When an item is clicked in inventory, add one to the manufacture grid
            // in to the first empty position.
            //CLIENT.useItem(itemPos);
            manufacture.addItem(itemPos);
        }

        public void onItemLongClicked(int itemPos) {
            // If an item is long clicked on inventory part of the manufacture dialog, send client
            // to use the item (to be able to manufacture and use items on the same window
            CLIENT.useItem(itemPos);
        }


        public void onManufactureItemClicked(int itemPos) {
            // When an item from manufacturing boxes is clicked remove the item
            if( itemPos < 6) {
                manufacture.remove_manufacture_item(itemPos);
            }
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


    private static class ManufactureInfo extends TextView {
        private Span ManufactureText;

        public ManufactureInfo(Context context) { super(context); }

        public void setInventoryText(Span ManufactureItemText) {this.ManufactureText = ManufactureItemText;}

        public void updateText() {
            setTextColor(Colors.COLORS[ManufactureText.color]);
            setText(String.valueOf(ManufactureText.text));
        }
    }

    private class OnClearButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            manufacture.removeAll();
        }

    }


    private class OnMixButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CLIENT.manufacture_item(manufacture.manufacture_items, 1);
        }

    }


    private class OnMixAllButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CLIENT.manufacture_item(manufacture.manufacture_items, 255);
        }

    }

}
