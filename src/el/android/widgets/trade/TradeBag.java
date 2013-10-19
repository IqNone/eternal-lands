package el.android.widgets.trade;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import el.actor.Item;
import el.android.R;
import el.android.widgets.ItemBag;

import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TradeBag extends LinearLayout {
    private static final int ROWS = 4;
    private static final int COLUMNS = 4;

    private static final Map<Integer, Integer> ACCEPT_VS_COLOR = new HashMap<Integer, Integer>(){{
        put(0, Color.RED);
        put(1, Color.YELLOW);
        put(2, Color.GREEN);
    }};

    private TextView ownerLabel;
    private ItemBag itemBag;
    private Button acceptButton;

    public TradeBag(Context context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        ownerLabel = new TextView(context);
        ownerLabel.setTextColor(0xff906A47);
        itemBag = new ItemBag(context);
        itemBag.setDimensions(ROWS, COLUMNS);
        acceptButton = new Button(context);

        addView(ownerLabel, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.topMargin = 5;
        params.bottomMargin = 5;
        params.weight = 1;
        addView(itemBag, params);
        addView(acceptButton, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        acceptButton.setText(context.getString(R.string.accept));
        acceptButton.setBackgroundResource(R.drawable.accept_background);
        acceptButton.setPadding(8, 2, 8, 2);
        acceptButton.setTextColor(ACCEPT_VS_COLOR.get(0));
    }

    public void setOwner(String owner) {
        ownerLabel.setText(owner);
        ownerLabel.invalidate();
    }

    public void setItems(Item items[]) {
        itemBag.setItems(items);
    }

    public void setAcceptStatus(int status) {
        acceptButton.setTextColor(ACCEPT_VS_COLOR.get(status));
    }

    public void setItemClickListener(ItemBag.ItemClickListener clickListener) {
        itemBag.setClickListener(clickListener);
    }

    public void setAcceptClickListener(OnClickListener acceptClickListener) {
        acceptButton.setOnClickListener(acceptClickListener);
    }
}
