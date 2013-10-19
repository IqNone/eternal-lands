package el.android.widgets.storage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import el.actor.Actor;
import el.android.GameMetadata;
import el.android.widgets.ItemBag;
import el.android.widgets.ItemListView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static el.android.widgets.ItemBag.ItemClickListener;

public class Storage extends RelativeLayout implements AdapterView.OnItemClickListener {
    public static final int ROWS = 10;
    public static final int COLUMNS = 6;

    private final Paint strokePaint;

    private final ItemListView listView;
    private final ScrollView scrollView;
    private final ItemBag grid;

    private int categorySize = 0;

    public Storage(Context context, ItemClickListener clickListener) {
        super(context);

        setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        listView = new ItemListView(context);
        listView.setClickListener(new onCategoryClicked());

        addView(listView, 0);

        scrollView = new ScrollView(getContext());
        grid = new ItemBag(getContext());
        grid.setClickListener(clickListener);
        grid.setDimensions(ROWS, COLUMNS);
        scrollView.addView(grid);

        addView(scrollView, 1);

        strokePaint = new Paint();
        strokePaint.setStrokeWidth(1);
        strokePaint.setColor(0xff906A47);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public void setActor(Actor actor) {
        if(categorySize != actor.storage.categories.size()) {
            listView.setItems(actor.storage.categories);
            categorySize = actor.storage.categories.size();
        }
        if(grid.getItems() == null) {
            grid.setItems(actor.storage.items);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = w * 3 / 4;
        listView.layout(1, 1, w / 4 - 1, h - 1);
        scrollView.layout(w / 4, 1, r - 1, h - 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 3 / 4;

        setMeasuredDimension(width, height);

        listView.measure(width /4, heightMeasureSpec);
        grid.measure(width * 3 / 4, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, strokePaint);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameMetadata.CLIENT.getStorageCategory(position + 1);
    }

    private class onCategoryClicked implements ItemListView.OnItemClickListener {
        @Override
        public void onItemClicked(String item, int index) {
            GameMetadata.CLIENT.getStorageCategory(index + 1);
        }
    }
}
