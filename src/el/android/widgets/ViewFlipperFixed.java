package el.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class ViewFlipperFixed extends ViewFlipper {
    public ViewFlipperFixed(Context context) {
        super(context);
    }

    public ViewFlipperFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try{
            super.onDetachedFromWindow();
        }catch(Exception e) {
            stopFlipping();
        }
    }
}
