package com.yt.crossline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
public class CrossLineCoordinate {
    /**
     * View 的宽和高
     */
    private int mWidth, mHeight;

    private Canvas mCanvas;
    private Context mContext;

    private ArrayList<ChartNode> mNodes = new ArrayList<>();
    private ArrayList<ChartNode> xPoints;

    /**
     * 底部固定的三个日期text
     */
    private ColorBean mColorBean;
    /**
     * 点的半径
     */
    private float mPointRadius;
    private float mPointBigRadius;

    public ChartNode mCurrentChartNode;


    public CrossLineCoordinate(int width, int height, Canvas canvas, Context context) {
        mWidth = width;
        mHeight = height;
        mCanvas = canvas;
        mContext = context;
    }

    public void setxPoints(ArrayList<ChartNode> xPoints) {
        this.xPoints = xPoints;
    }

    public void setNodes(ArrayList<ChartNode> nodes) {
        mNodes = nodes;
    }

    public void drawNode() {

        pointLinePaint();

    }

    private void pointLinePaint() {

        int lineColor = 0;
        try {
            lineColor = Color.parseColor(mColorBean.lineColor);
        } catch (Exception e) {
            e.printStackTrace();
            lineColor = Color.BLACK;
        }
        //画点
        Paint pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(lineColor);
        //设置线条宽度
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(Utils.dp2px(mContext, 1));
        mPointRadius = Utils.dp2px(mContext, 2);
        mPointBigRadius = Utils.dp2px(mContext, 4);


        //
        //折线
        Paint chartLinePaint = new Paint();
        chartLinePaint.setAntiAlias(true);
        //设置线条宽度
        chartLinePaint.setStyle(Paint.Style.FILL);
        chartLinePaint.setColor(lineColor);
        chartLinePaint.setStrokeWidth(3);

        int size = mNodes.size();
        //遍历需要当前选中的坐标
        float bigX = 0.0f;
        for (ChartNode chartNode : xPoints) {
            if (chartNode.isFlag()) {
                bigX = chartNode.getX();
            }
        }
        //画点
        for (int s = 0; s < mNodes.size(); s++) {
            if (!(s == (size - 1))) {
                mCanvas.drawLine(mNodes.get(s).getX(), mNodes.get(s).getY(), mNodes.get(s + 1).getX(), mNodes.get(s + 1).getY(), chartLinePaint);
            }
            float radius = 0.0f;
            if (bigX == mNodes.get(s).getX()) {
                radius = mPointBigRadius;
                mCurrentChartNode = mNodes.get(s);
            } else {
                radius = mPointRadius;
            }
            mCanvas.drawCircle(mNodes.get(s).getX(), mNodes.get(s).getY(), radius, pointPaint);

        }
    }

    public void setColorBean(ColorBean colorBean) {
        mColorBean = colorBean;
    }

    public static class ColorBean {
        public String areaColor;
        public String textColor;
        public String lineColor;
        public String minPellucid;
        public String maxPellucid;

        public ColorBean(String areaColor, String textColor, String lineColor, String minPellucid, String maxPellucid) {
            this.areaColor = areaColor;
            this.textColor = textColor;
            this.lineColor = lineColor;
            this.minPellucid = minPellucid;
            this.maxPellucid = maxPellucid;
        }

        public String getTextColor() {
            return textColor;
        }

        public void setTextColor(String textColor) {
            this.textColor = textColor;
        }

    }
}
