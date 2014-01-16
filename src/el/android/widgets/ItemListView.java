package el.android.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ItemListView extends ScrollView{
    public static interface OnItemClickListener{
        public void onItemClicked(String item, int index);
    }

    private LinearLayout list;
    private List<String> items;

    private OnItemClickListener clickListener;

    public ItemListView(Context context) {
        super(context);

        list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        addView(list);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItems(List<String> items) {
        if(this.items == null || this.items.size() != items.size()) {
            this.items = items;
            createItems();
            requestLayout();
            invalidate();
        }
    }

    private void createItems() {
        list.removeAllViews();

        for (int i = 0; i < items.size(); i++) {
            list.addView(createItemView(items.get(i), i));
        }
    }

    private View createItemView(String item, int index) {
        TextView textView = new TextView(getContext());
        textView.setText(item);
        textView.setOnClickListener(new TextViewClickListener(item, index));
        textView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        return textView;
    }

    private class TextViewClickListener implements OnClickListener {
        private String item;
        private int index;

        public TextViewClickListener(String item, int index) {
            this.item = item;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null) {
                clickListener.onItemClicked(item, index);
            }
        }
    }
}
