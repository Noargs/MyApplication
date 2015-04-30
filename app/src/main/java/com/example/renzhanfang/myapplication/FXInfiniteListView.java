package com.example.renzhanfang.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by renzhanfang on 2015/4/29.
 */
public class FXInfiniteListView extends ListView{

    public FXInfiniteListView(Context context) {
        super(context);
    }

    public FXInfiniteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }
}
