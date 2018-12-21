package com.example.yt.chartview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/9/21.
 */

public class TVBanner extends LinearLayout {

    private  Context mContext;
    private int mImageWidth;
    private int mitemHeight;
    private int mitemWidth;
    private int mTime;
    private LayoutParams bigViewParams;
    private View bigView;
    private LinearLayout linearLayout;
    private ScrollView scrollView;

    public TVBanner(Context context) {
        super(context);
        this.mContext = context;
        initView(context, null);
    }

    public TVBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context, attrs);
    }

    public TVBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.setOrientation(HORIZONTAL);
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TVBanner);
//        mImageWidth = ta.getDimensionPixelSize(R.styleable.TVBanner_imageWidth, 0);
//        mitemHeight = ta.getDimensionPixelSize(R.styleable.TVBanner_itemHeight, 0);
//        mitemWidth = ta.getDimensionPixelSize(R.styleable.TVBanner_itemWidth, 0);
//        mTime = ta.getDimensionPixelSize(R.styleable.TVBanner_time, 0);

        bigViewParams = new LayoutParams(mImageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams listParams = new LayoutParams(mitemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView = new ScrollView(context);
        LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.setLayoutParams(listParams);
//        scrollView.addView(linearLayout);

    }


    public TVBanner addListView(List<View> views) {
        TextView textView = new TextView(mContext);
        textView.getParent();
        textView.setHeight(30);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setBackgroundColor(Color.BLUE);
//        if (null == views || views.size() == 0) return this;
        for (int i = 0; i < 5; i++) {
            linearLayout.addView(textView);
        }
        scrollView.addView(linearLayout);
        addView(scrollView);
        return this;
    }


    public TVBanner addBigView(View view) {
        this.bigView = view;
        addView(view);
        view.setLayoutParams(bigViewParams);
        return this;
    }





}
