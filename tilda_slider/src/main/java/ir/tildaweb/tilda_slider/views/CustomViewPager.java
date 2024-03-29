package ir.tildaweb.tilda_slider.views;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private final String TAG = this.getClass().getName();
    private boolean mustWrapContent=true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, boolean mustWrapContent) {
        super(context);
        this.mustWrapContent=mustWrapContent;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mustWrapContent) {
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) height = h;
                Log.d(TAG, "onMeasure: " + h);
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
