package com.yt.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.yt.utils.Utils;

/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 *
 */
public class XLine {
    /**
     * View 的宽和高
     */
    private int mWidth, mHeight;

    int scale = 50;

    int startX = 10;//起始端点的X坐标。

    int startY = mHeight - scale;//起始端点的Y坐标。

    int stopX;//终止端点的X坐标。

    int stopY;//终止端点的Y坐标。

    private Canvas mCanvas;
    private Context mContext;
    Paint linetPaint = new Paint();
    private float mStrokeWidth = 1.0f;


    public XLine(Canvas canvas, Context context) {
        this.mCanvas = canvas;
        this.mContext = context;
    }

    public void drawLine(int width, int height) {
        mWidth = width;
        mHeight = height;
        startX = 0;
        startY = mHeight - Utils.dp2px(mContext,  LineChartView.marginBottom);
        stopX = width;
        stopY = startY;
        linetPaint.setColor(Color.GRAY);
        linetPaint.setAntiAlias(true);
        //设置线条宽度
        linetPaint.setStrokeWidth(mStrokeWidth);
        linetPaint.setStyle(Paint.Style.FILL);

        mCanvas.drawLine(startX, startY, stopX, startY, linetPaint);

    }
}
