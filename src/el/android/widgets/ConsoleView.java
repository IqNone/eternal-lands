package el.android.widgets;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;
import el.actor.Actor;
import el.actor.Span;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ConsoleView extends ScrollView {
    private TextView textView;
    private Actor actor;
    private int lastTextSize = 0;

    public ConsoleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        textView = new ConsoleTextView(context);
        textView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        addView(textView);
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(textNeedsUpdate()) {
            updateTextAndScroll();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean textNeedsUpdate() {
        return actor != null && isShown() && actor.texts.size() > lastTextSize;
    }

    private void updateTextAndScroll() {
        boolean scrolledToBottom = isScrolledToBottom();
        updateText();
        if (scrolledToBottom) {
            scrollToBottom();
        }
    }

    private void updateText() {
        SpannableStringBuilder buffer = new SpannableStringBuilder();
        for (int i = lastTextSize; i < actor.texts.size(); i++) {
            addSpans(buffer, actor.texts.get(i).spans);
            buffer.append("\n");
        }

        lastTextSize = actor.texts.size();
        textView.append(buffer);
    }

    private void addSpans(SpannableStringBuilder buffer, List<Span> spans) {
        for (Span span : spans) {
            buffer.append(span.text);
            buffer.setSpan(new ForegroundColorSpan(span.color), buffer.length() - span.text.length(), buffer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private boolean isScrolledToBottom() {
        return (textView.getHeight() <= getHeight()) ||
                Math.abs(textView.getBottom() - (getBottom() +  getScrollY())) < 10;
    }

    private void scrollToBottom() {
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private class ConsoleTextView extends TextView {
        private int lastTextLength = 0;

        public ConsoleTextView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if(getText().length() > lastTextLength || isLayoutRequested()){
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                lastTextLength = getText().length();
            } else {
                setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }
}
