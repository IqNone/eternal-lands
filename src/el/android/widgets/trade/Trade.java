package el.android.widgets.trade;

import android.content.Context;
import android.widget.LinearLayout;
import el.actor.Actor;
import el.android.widgets.ItemBag;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Trade extends LinearLayout{
    private TradeBag mine;
    private TradeBag his;

    public Trade(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        mine = createBag("You");
        his = createBag("");

        addView(mine);
        addView(his);
    }

    private TradeBag createBag(String owner) {
        TradeBag bag = new TradeBag(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        params.weight = 1;
        bag.setLayoutParams(params);
        bag.setOwner(owner);
        return bag;
    }

    public void setActor(Actor actor) {
        mine.setItems(actor.trade.myItems);
        mine.setAcceptStatus(actor.trade.myAccept);
        his.setItems(actor.trade.hisItems);
        his.setOwner(actor.trade.partnersName);
        his.setAcceptStatus(actor.trade.hisAccept);
    }

    public void setMyItemClickListener(ItemBag.ItemClickListener clickListener){
        mine.setItemClickListener(clickListener);
    }

    public void setAcceptClickListener(OnClickListener acceptClickListener) {
        mine.setAcceptClickListener(acceptClickListener);
    }
}
