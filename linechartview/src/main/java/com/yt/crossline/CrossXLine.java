package com.yt.crossline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

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
 */
public class CrossXLine {
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
    ArrayList<ChartNode> xPoints, mChartNodes;


    public CrossXLine(Canvas canvas, Context context, ArrayList<ChartNode> xPoints) {
        this.mCanvas = canvas;
        this.mContext = context;
        this.xPoints = xPoints;
    }

    public void drawLine(int width, int height) {
        try {
            mWidth = width;
            mHeight = height;
            startX = 0;
            startY = mHeight - Utils.dp2px(mContext, CrossLineChartView.marginBottom);
            stopX = width;
            stopY = startY;
            linetPaint.setColor(Color.GRAY);
            linetPaint.setAntiAlias(true);
            //设置线条宽度
            linetPaint.setStrokeWidth(mStrokeWidth);
            linetPaint.setStyle(Paint.Style.FILL);
            int textSize = Utils.dp2px(mContext, 11);
            linetPaint.setTextSize(textSize);

            mCanvas.drawLine(startX, startY, stopX, startY, linetPaint);

            //底部月份text
            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(Utils.dp2px(mContext, 13));
            int offsets = Utils.dp2px(mContext, 25);
            textPaint.setTextAlign(Paint.Align.CENTER);

//            //跨年上边的年标记
//            Paint yearPaint = new Paint();
//            yearPaint.setAntiAlias(true);
//            yearPaint.setTextSize(Utils.dp2px(mContext, 5));
//            int spaceOffSet = Utils.dp2px(mContext, 12);
//            int yearPffSet = Utils.dp2px(mContext, 10);
//            yearPaint.setTextAlign(Paint.Align.CENTER);
//            yearPaint.setColor(Color.parseColor("#ffffff"));
//
//            //画圆角矩形
//            Paint recPaint = new Paint();
//            recPaint.setStyle(Paint.Style.FILL);//充满
//            recPaint.setColor(Color.parseColor("#5498EE"));
//            recPaint.setAntiAlias(true);// 设置画笔的锯齿效果

            for (int i = 0; i < xPoints.size(); i++) {
                ChartNode chartNode = xPoints.get(i);
                //默认情况下颜色是灰色的的
                textPaint.setColor(Color.parseColor("#CECECE"));
                //有数据的月份颜色是深黑色
                for (ChartNode chart : mChartNodes) {
                    if (chart.getX() == chartNode.getX()) {
                        textPaint.setColor(Color.parseColor("#333333"));
                        break;
                    }
                }
                //被选中的月份颜色标蓝色
                if (xPoints.get(i).isFlag()) {
                    textPaint.setColor(Color.parseColor("#5498EE"));
                }
//                if (!TextUtils.isEmpty(chartNode.getSubYear())) {
//                    float space = xPoints.get(i + 1).getX() - chartNode.getX();
//                    // 设置个新的长方形
//                    RectF rect = new RectF(chartNode.getX() - space / 3, chartNode.getY()
//                            + spaceOffSet / 3, chartNode.getX() + space / 3, chartNode
//                            .getY() + spaceOffSet);
//                    //画圆角矩形
//                    mCanvas.drawRoundRect(rect, Utils.dp2px(mContext, 3), Utils.dp2px(mContext,
//                            2), recPaint);
//                    mCanvas.drawText(chartNode.getSubYear(), chartNode.getX(), chartNode.getY()
//                            + yearPffSet, yearPaint);
//                }

                mCanvas.drawText(chartNode.getText(), chartNode.getX(), chartNode.getY() +
                        offsets, textPaint);

            }
        } catch (Exception e) {
        }

    }

    public void setChartNodes(ArrayList<ChartNode> chartNodes) {
        mChartNodes = chartNodes;
    }

    /**
     * 跨年显示年标记
     * 全年 不显示年标记
     */
    public void drawXYearText() {
        //跨年上边的年标记
        Paint yearPaint = new Paint();
        yearPaint.setAntiAlias(true);
        yearPaint.setTextSize(Utils.dp2px(mContext, 5));
        int spaceOffSet = Utils.dp2px(mContext, 12);
        int yearPffSet = Utils.dp2px(mContext, 10);
        yearPaint.setTextAlign(Paint.Align.CENTER);
        yearPaint.setColor(Color.parseColor("#ffffff"));

        //画圆角矩形
        Paint recPaint = new Paint();
        recPaint.setStyle(Paint.Style.FILL);//充满
        recPaint.setColor(Color.parseColor("#5498EE"));
        recPaint.setAntiAlias(true);// 设置画笔的锯齿效果

        for (int i = 0; i < xPoints.size(); i++) {
            ChartNode chartNode = xPoints.get(i);
            if (!TextUtils.isEmpty(chartNode.getSubYear())) {
                float space = xPoints.get(i + 1).getX() - chartNode.getX();
                // 设置个新的长方形
                RectF rect = new RectF(chartNode.getX() - space / 3, chartNode.getY()
                        + spaceOffSet / 3, chartNode.getX() + space / 3, chartNode
                        .getY() + spaceOffSet);
                //画圆角矩形
                mCanvas.drawRoundRect(rect, Utils.dp2px(mContext, 3), Utils.dp2px(mContext, 2),
                        recPaint);
                mCanvas.drawText(chartNode.getSubYear(), chartNode.getX(), chartNode.getY()
                        + yearPffSet, yearPaint);
            }
        }
    }
}
