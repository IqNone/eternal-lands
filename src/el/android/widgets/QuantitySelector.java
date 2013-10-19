package el.android.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import el.android.R;

public class QuantitySelector extends ViewGroup {
    public static final int[] QUANTITIES = {1, 10, 100, 1000};

    private QuantityTextInput numberInput;

    private Paint strokePaint;
    private Paint textPaint;

    private int textHeight;

    public QuantitySelector(Context context) {
        super(context);

        numberInput = createNumberInput();
        addView(numberInput);

        strokePaint = new Paint();
        strokePaint.setStrokeWidth(1);
        strokePaint.setColor(0xFF365CBC);
        strokePaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint(numberInput.getPaint());
        textPaint.setColor(0xFF365CBC);

        Rect bounds = new Rect();
        textPaint.getTextBounds("a", 0, 1, bounds);
        textHeight = bounds.height();

        for (int quantity : QUANTITIES) {
            addView(new QuantityButton(getContext(), quantity));
        }
    }

    public int getQuantity() {
        return numberInput.getQuantity();
    }

    public void setQuantity(int quantity) {
        numberInput.setQuantity(quantity);
    }

    private QuantityTextInput createNumberInput() {
        return new QuantityTextInput(getContext());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        numberInput.layout(0, 0, width / 4, 2 * textHeight + 10);
        int buttonWidth = width * 3 / 16;
        for (int i = 1; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(width / 4 + (i - 1) * buttonWidth - 1, 0, width / 4 + i * buttonWidth, 2 * textHeight + 10);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 2* textHeight + 10;

        setMeasuredDimension(width, height);
    }

    private class QuantityTextInput extends EditText {
        private int quantity;

        public QuantityTextInput(Context context) {
            super(context);

            setBackgroundResource(R.drawable.quantity_selector);

            setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            setInputType(InputType.TYPE_CLASS_NUMBER);
            setRawInputType(Configuration.KEYBOARD_12KEY);

            setPadding(8, 0, 0, 0);
            MarginLayoutParams params = new MarginLayoutParams(100, 50);
            params.setMargins(0, 0, 0, 0);
            setLayoutParams(params);

            setTextColor(0xFF00912C);

            setQuantity(QUANTITIES[0]);
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            setText(String.valueOf(this.quantity));
        }

        @Override
        public void onEditorAction(int actionCode) {
            super.onEditorAction(actionCode);

            String newStr = getText().toString();
            if(newStr == null || newStr.length() == 0) {
                setQuantity(quantity);
            } else {
                quantity = Integer.valueOf(newStr);
            }
        }
    }

    private class QuantityButton extends View {
        private int quantity;

        public QuantityButton(Context context, int quantity) {
            super(context);
            this.quantity = quantity;

            setOnClickListener(new QuantityClickListener(quantity));
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, strokePaint);
            canvas.drawText(String.valueOf(quantity), 2, getHeight() - 10, textPaint);
        }
    }

    private class QuantityClickListener implements OnClickListener {
        private int quantity;

        public QuantityClickListener(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public void onClick(View v) {
            setQuantity(quantity);
        }
    }
}
