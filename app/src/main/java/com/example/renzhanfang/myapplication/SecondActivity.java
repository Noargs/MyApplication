package com.example.renzhanfang.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by renzhanfang on 2015/4/24.
 */
public class SecondActivity extends ActionBarActivity {

    private FXIndexListView mFXIndexListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mFXIndexListView = (FXIndexListView) findViewById(R.id.list);
        mFXIndexListView.setOnIndexViewFocusChangeListener(new FXIndexListView.OnIndexViewFocusChangeListener() {
           @Override
           public void onChange(View getFocusView, View lostFocusView) {
               getFocusView.setBackgroundColor(Color.WHITE);
               if (null != lostFocusView) {
                   lostFocusView.setBackgroundColor(Color.TRANSPARENT);
               }
               Log.e("TOPBOTTOM", getFocusView.getPaddingTop()+getFocusView.getPaddingBottom()+"");
           }
        });
        mFXIndexListView.setIndexViewSelected(26);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}
