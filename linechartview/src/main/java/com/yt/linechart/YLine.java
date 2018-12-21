package com.yt.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;

import com.yt.utils.Utils;

import java.util.ArrayList;

/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 *
 */
public class YLine {
    /**
     * View 的宽和高
     */
    private int mWidth, mHeight;
    private Canvas mCanvas;
    private Context mContext;

    Paint linetPaint = new Paint();
    private float mStrokeWidth = 1.0f;

//    public static final int marginBottom = 35;

    /**
     * x 轴上面的坐标
     */
    private ArrayList<ChartNode> xPoints;


    /**
     * 固定的首，中，尾坐标
     */
    private ArrayList<ChartNode> mfixPoints = new ArrayList<ChartNode>();

    private final String TAG = YLine.class.getSimpleName();


    public YLine(Canvas canvas, Context context, ArrayList<ChartNode> xPoints) {
        this.mCanvas = canvas;
        this.mContext = context;
        this.xPoints = xPoints;
    }

    public void drawLine(int width, int height) {
        mWidth = width;
        mHeight = height;

        linetPaint.setColor(Color.BLACK);
        linetPaint.setAntiAlias(true);
        //设置线条宽度
        linetPaint.setStrokeWidth(mStrokeWidth);
        linetPaint.setStyle(Paint.Style.STROKE);

        PathEffect effects = new DashPathEffect(new float[]{15, 2, 15, 2}, 1);
        linetPaint.setPathEffect(effects);


        for (int i = 0; i < xPoints.size(); i++) {
            float startX = xPoints.get(i).getX();
            int startY = Utils.dp2px(mContext, 25);

            float stopX = startX;
            int stopY = mHeight - Utils.dp2px(mContext, LineChartView.marginBottom);
            mCanvas.drawLine(startX, startY, stopX, stopY, linetPaint);
        }

    }

}
