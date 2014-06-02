package el.android.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import el.android.R;

import static java.lang.Math.min;

public class ELProgressBar extends View {
    private boolean showText;
    private boolean remaining;
    private int color;

    private int total = 100;
    private int current = 0;

    private Paint textPaint;
    private Paint borderPaint;
    private Paint fillPaint;

    public ELProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ELProgressBar, 0, 0);
        try {
            showText = a.getBoolean(R.styleable.ELProgressBar_showText, false);
            remaining = a.getBoolean(R.styleable.ELProgressBar_showText, false);
            color = a.getColor(R.styleable.ELProgressBar_color, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(10);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(0xFF916B48);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);

        setOnTouchListener(new OnTouchListener());
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
        invalidate();
        requestLayout();
    }


    public boolean isRemaining() {
        return remaining;
    }

    public void setRemaining(boolean remaining) {
        this.remaining = remaining;
        invalidate();
        requestLayout();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        fillPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        invalidate();
        requestLayout();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawStroke(canvas);
        drawFill(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int current_for_text;
        float y;

        if (remaining) {
            current_for_text = total - current;
        }
        else {
            current_for_text = current;
        }

        if(showText) {
            if (current_for_text < 1000) {
                textPaint.setTextSize(getHeight() - 3);
            }
            else {
                textPaint.setTextSize(getHeight()/2 - 3);
            }

            y = textPaint.getTextSize() - 2;

            canvas.drawText(String.valueOf(current_for_text), 2, y, textPaint);
        }
    }

    private void drawStroke(Canvas canvas) {
        //int left = showText ? 30 : 0;
        int left = showText ? 0 : 0;
        canvas.drawRect(left + 1, 1, getWidth() - 1, getHeight() - 1, borderPaint);
    }

    private void drawFill(Canvas canvas) {
        //int left = showText ? 30 : 0;
        int left = showText ? 0 : 0;
        int right = left + (int)(min(1, (double)current / total) * (getWidth() - left));

        if(right > left + 4) {
            canvas.drawRect(left + 2, 2, right - 2, getHeight() - 2, fillPaint);
        }
    }

    private class OnTouchListener implements View.OnTouchListener {
        private TextView label;
        private AlertDialog dialog;

        public OnTouchListener() {
            label = new TextView(getContext());
            label.setGravity(Gravity.CENTER);
            label.setText(current + "/" + total);

            if(!ELProgressBar.this.isInEditMode()) {
                dialog = new AlertDialog
                    .Builder(getContext())
                    .setView(label)
                    .create();
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !dialog.isShowing()) {
                prepareToDisplayDialog();
                dialog.show();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                dialog.dismiss();
            }
            return true;
        }

        private void prepareToDisplayDialog() {
            label.setText(current + "/" + total);

            Rect rect = new Rect(0, 0, 0, 0);
            getGlobalVisibleRect(rect);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = rect.left + (getWidth() - label.getWidth()) / 2;
            params.y = rect.top - 140;
            dialog.getWindow().setAttributes(params);
        }
    }
}
