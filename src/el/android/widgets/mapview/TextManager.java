package el.android.widgets.mapview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import el.actor.Actor;
import el.actor.Span;
import el.actor.Text;

import java.util.concurrent.TimeUnit;

public class TextManager {
    public static final int MAX_DISPLAYED_TEXT = 5;
    public static final long TIME_TO_DISPLAY = TimeUnit.SECONDS.toNanos(10);

    private Actor actor;

    private DynamicLayout textLayout;
    private SpannableStringBuilder textBuffer;

    @SuppressWarnings("FieldCanBeLocal")//actually it can't, but intellij can't figure it out
    private long timeLeftToIncrease = TIME_TO_DISPLAY;
    private int firstToDisplayIndex = 0;
    private long lastRenderedTime = 0;

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void resize(int width) {
        if(textLayout == null && width > 0) {
            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(20);
            textBuffer = new SpannableStringBuilder();
            textLayout = new DynamicLayout(textBuffer, paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        }
    }

    public void drawText(Canvas canvas) {
        if(actor.texts.isEmpty() || textLayout == null) {
            return;
        }

        updateIndex();
        updateTextBuffer();

        textLayout.draw(canvas);
    }

    private void updateIndex() {
        long now = System.nanoTime();

        if(firstToDisplayIndex + MAX_DISPLAYED_TEXT < actor.texts.size()) {//we got many new text, and display lasts
            firstToDisplayIndex = actor.texts.size() - MAX_DISPLAYED_TEXT;
            timeLeftToIncrease = TIME_TO_DISPLAY;
        } else if(lastRenderedTime == 0) { //first time here, init some stuff
            timeLeftToIncrease = TIME_TO_DISPLAY;
        } else if(firstToDisplayIndex < actor.texts.size()){//so how much time passed in increase the index
            long elapsed = now - lastRenderedTime;
            timeLeftToIncrease = Math.max(0, timeLeftToIncrease - elapsed);
            if(timeLeftToIncrease == 0) {//than increase
                ++firstToDisplayIndex;
                timeLeftToIncrease = TIME_TO_DISPLAY;
            }
        } else {
            timeLeftToIncrease = TIME_TO_DISPLAY;
        }

        lastRenderedTime = now;
    }

    private void updateTextBuffer() {
        textBuffer.clear();
        for(int i = firstToDisplayIndex; i < actor.texts.size(); ++i) {
            appendSpan(actor.texts.get(i));
            textBuffer.append("\n");
        }
    }

    private void appendSpan(Text text) {
        for (Span span : text.spans) {
            textBuffer.append(span.text);
            textBuffer.setSpan(new ForegroundColorSpan(span.color), textBuffer.length() - span.text.length(), textBuffer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
