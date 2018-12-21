package com.yt.crossline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;

import com.yt.linechart.ChartNode;
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
public class CrossYLine {
    /**
     * View 的宽和高
     */
    private int mWidth, mHeight;
    private Canvas mCanvas;
    private Context mContext;

    private Paint linetPaint = new Paint();

    private Paint mPaint = new Paint();
    private float mStrokeWidth = 1.0f;

    /**
     * x 轴上面的坐标
     */
    private ArrayList<ChartNode> xPoints;


    /**
     * 固定的首，中，尾坐标
     */
    private ArrayList<ChartNode> mfixPoints = new ArrayList<ChartNode>();

    private final String TAG = CrossYLine.class.getSimpleName();


    public CrossYLine(Canvas canvas, Context context, ArrayList<ChartNode> xPoints) {
        this.mCanvas = canvas;
        this.mContext = context;
        this.xPoints = xPoints;
    }

    public void drawLine(int width, int height) {
        mWidth = width;
        mHeight = height;
        //虚线颜色
        linetPaint.setColor(Color.parseColor("#CCCCCC"));
        linetPaint.setAntiAlias(true);
        //设置线条宽度
        linetPaint.setStrokeWidth(mStrokeWidth);
        linetPaint.setStyle(Paint.Style.STROKE);
        //画虚线，需要关闭硬件加速 在xml文件中 android:layerType="software"
        PathEffect effects = new DashPathEffect(new float[]{10,5}, 1);
        linetPaint.setPathEffect(effects);

        //实现颜色
        mPaint.setColor(Color.parseColor("#CCCCCC"));
        mPaint.setAntiAlias(true);
        //设置线条宽度
        mPaint.setStrokeWidth(mStrokeWidth * 4);
        mPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < xPoints.size(); i++) {
            float startX = xPoints.get(i).getX();
            int startY = Utils.dp2px(mContext, 45);

            float stopX = startX;
            int stopY = mHeight - Utils.dp2px(mContext, CrossLineChartView.marginBottom);
            if (xPoints.get(i).isFlag()) {
                mCanvas.drawLine(startX, startY, stopX, stopY, mPaint);
            } else {
                mCanvas.drawLine(startX, startY, stopX, stopY, linetPaint);

            }
        }

    }


}
