package com.example.renzhanfang.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by renzhanfang on 2015/4/29.
 */
public class FXIndexListView extends LinearLayout{

    public static String TAG = "FXIndexListView";
    private float minIndexTextSize = 13f;   //dp
    private Context mContext;
    private ListView mListView;
    private View mTopView;
    private LinearLayout mIndexView;
    private ScrollView mIndexViewParent;
    // 字母索引数组
    private String[] mIndexTexts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    // 保存显示索引的View对象
    private ArrayList<TextView> mIndexViewList = new ArrayList<>(27);
    private View mLastClickedView;
    private OnIndexViewClickChangeListener mOnIndexViewClickChangeListener;
    private OnIndexViewFocusChangeListener mOnIndexViewFocusChangeListener;

    public interface OnIndexViewClickChangeListener {
        public void onClick(View view);
    }

    public interface OnIndexViewFocusChangeListener {
        /**
         * 由于点索引而导致索引“焦点”发生变化，此处“焦点”并不完全等同于Android中View体系中的焦点
         * @param getFocusView  获得“焦点”的View
         * @param lostFocusView 失去“焦点”的View，可能为null
         */
        public void onChange(View getFocusView, View lostFocusView);
    }

    public FXIndexListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setOrientation(HORIZONTAL);
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.FXIndexListView);
        int topLayoutId = typedArray.getResourceId(R.styleable.FXIndexListView_layoutTopOfList, 0);
        LinearLayout leftLayout = new LinearLayout(mContext);
        leftLayout.setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        leftLayout.setLayoutParams(lp);
        addView(leftLayout);
        if (0 == topLayoutId) { // 没有顶部View

        } else {
            mTopView = LayoutInflater.from(mContext).inflate(topLayoutId, null);
            leftLayout.addView(mTopView);
        }
        int listViewId = typedArray.getResourceId(R.styleable.FXIndexListView_layoutOfList, 0);
        if (0 == listViewId) {
            mListView = new FXInfiniteListView(mContext);
            mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            mListView = (ListView)LayoutInflater.from(mContext).inflate(listViewId, null);
        }
        leftLayout.addView(mListView);
        mIndexView = new LinearLayout(mContext);
        mIndexView.setOrientation(VERTICAL);
        mIndexView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mIndexViewParent = new ScrollView(mContext);
        LayoutParams scrollViewLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        scrollViewLp.weight = 0;
        mIndexViewParent.setLayoutParams(scrollViewLp);
        mIndexViewParent.addView(mIndexView);
        addView(mIndexViewParent);
        initIndexView();
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int scrollHeight = b-t;
            int height = scrollHeight - mIndexViewParent.getPaddingTop() - mIndexViewParent.getPaddingBottom();
            int perHeight = (height-mIndexView.getPaddingBottom()-mIndexView.getPaddingTop()) / mIndexTexts.length;
            int offcut = (height-mIndexView.getPaddingBottom()-mIndexView.getPaddingTop()) - perHeight * mIndexTexts.length; // 处理上面不能整除情况
            if (offcut > 0) {
                int remainder = offcut % 2;
                mIndexViewParent.setPadding(0, offcut/2+remainder, 0, offcut/2);
            }
            Log.e(TAG, (height-mIndexView.getPaddingBottom()-mIndexView.getPaddingTop())+"");
            float textSize = getSuggestedTextSize(height);
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            int minPerHeight = (int)((minIndexTextSize + 4)*dm.density+0.5f);
            if (perHeight < minPerHeight) {
                perHeight = minPerHeight;
            }
            Log.e("FXIndexListView", "textSize:"+textSize);
            Log.e(TAG, "perHeight:" + perHeight);
            for (int i = 0; i < mIndexView.getChildCount(); i++) {
                TextView child = (TextView)mIndexView.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.width = perHeight;
                lp.height = perHeight;
                child.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }

        super.onLayout(changed, l, t, r, b);
        Log.e("FXIndexListView", "onLayout");
    }

    private void initIndexView() {
        for (int i = 0; i < mIndexTexts.length; i++) {
            TextView tv = new TextView(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            tv.setLayoutParams(lp);
            tv.setText(mIndexTexts[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnIndexViewClickChangeListener) {
                        mOnIndexViewClickChangeListener.onClick(v);
                    }
                    if (v != mLastClickedView) {
                        if (null != mOnIndexViewFocusChangeListener) {
                           mOnIndexViewFocusChangeListener.onChange(v, mLastClickedView);
                        }
                    }
                    mLastClickedView = v;
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, ((TextView)v).getText()+"");
                    }
                }
            });
            mIndexView.addView(tv);
            mIndexViewList.add(tv);
        }
    }

    private float getSuggestedTextSize(int height) {
        float size = 0f;
        int perHeight = (height-mIndexView.getPaddingBottom()-mIndexView.getPaddingTop()) / mIndexTexts.length;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        size = perHeight - 4*dm.density+0.5f;

        if (size < minIndexTextSize*dm.density+0.5f) {
            size = minIndexTextSize*dm.density+0.5f;
        }
        return size;
    }

    public ListView getListView() {
        return mListView;
    }

    public View getTopViewOfList() {
        return mTopView;
    }

    public OnIndexViewClickChangeListener getOnIndexViewClickChangeListener() {
        return mOnIndexViewClickChangeListener;
    }

    public void setOnIndexViewFocusChangeListener(OnIndexViewClickChangeListener mOnIndexViewClickChangeListener) {
        this.mOnIndexViewClickChangeListener = mOnIndexViewClickChangeListener;
    }

    public OnIndexViewFocusChangeListener getOnIndexViewFocusChangeListener() {
        return mOnIndexViewFocusChangeListener;
    }

    public void setOnIndexViewFocusChangeListener(OnIndexViewFocusChangeListener mOnIndexViewFocusChangeListener) {
        this.mOnIndexViewFocusChangeListener = mOnIndexViewFocusChangeListener;
    }

    public void setIndexViewSelected(int index) {
        if (index < 0 || index > mIndexViewList.size()) {
            throw new IllegalArgumentException();
        }
        if (null != mIndexViewList) {
            TextView tv = mIndexViewList.get(index);
            if (null != mOnIndexViewFocusChangeListener) {
                mOnIndexViewFocusChangeListener.onChange(tv, mLastClickedView);
            }
            mLastClickedView = tv;
        }
    }
}
