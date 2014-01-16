package el.android.widgets.trade;

import android.content.Context;
import android.widget.LinearLayout;
import el.actor.Actor;
import el.android.widgets.ItemBag;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TradeMinimal extends LinearLayout {
    private ItemBag mine;
    private ItemBag his;

    public TradeMinimal(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        mine = createBag();
        his = createBag();

        addView(mine);
        addView(his);
    }

    private ItemBag createBag() {
        ItemBag bag = new ItemBag(getContext());
        bag.setDimensions(4, 4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        params.weight = 1;
        bag.setLayoutParams(params);
        bag.setClickable(false);
        return bag;
    }

    public void setActor(Actor actor) {
        mine.setItems(actor.trade.myItems);
        his.setItems(actor.trade.hisItems);
    }

}
