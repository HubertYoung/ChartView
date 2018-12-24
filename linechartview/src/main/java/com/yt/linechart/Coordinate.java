package com.yt.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.TextUtils;
import android.widget.TextView;


import com.yt.utils.LogUtils;
import com.yt.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 *
 */
public class Coordinate {
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
    private float mStrokeWidth = 3.0f;


    private YLine mYLine;

    /**
     * 底部固定的三个日期text
     */
    private BottomTextBean mBottomTextBean;

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
    private float mInnerPointRadius;
    private LineChartView mParentView;
    private final String TAG = Coordinate.class.getSimpleName();

    public Coordinate(int width, int height, Canvas canvas, LineChartView view, Context context) {
        mWidth = width;
        mHeight = height;
        mCanvas = canvas;
        mContext = context;
        this.mParentView = view;
    }

    public ArrayList<ChartNode> getxPoints() {
        return xPoints;
    }

    public void setxPoints(ArrayList<ChartNode> xPoints) {
        this.xPoints = xPoints;
    }

    public ArrayList<ChartNode> getNodes() {
        return mNodes;
    }

    public void setNodes(ArrayList<ChartNode> nodes) {
        mNodes = nodes;
    }

    public void setDateList(BottomTextBean dateList) {
        this.mBottomTextBean = dateList;
    }

    public void drawNode() {
        setLinePath();

        pointPaint();


        if (mBottomTextBean.isChange){
            addBottomText();
        }else {
            addBottomFixText();
        }


    }

    /**
     * x轴显示全部日期
     */
    private void addBottomText() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置线条宽度
        paint.setStyle(Paint.Style.FILL);
        //画笔颜色可以改成动态的
        paint.setColor(Color.parseColor("#666666"));
        paint.setTextSize(Utils.dp2px(mContext, 8));
        int offsets = Utils.dp2px(mContext, 10);
        LogUtils.eTag(TAG,"xPoints.size():"+xPoints.size() );
        for (int i = 0; i < xPoints.size(); i++) {
            String text = mBottomTextBean.dateList.get(i);
            mCanvas.drawText(text, xPoints.get(i).getX(), xPoints.get(i).getY() + offsets, paint);

        }
    }


    /**
     * (画点)
     * * @return
     */

    private void pointPaint() {

        //画点
        Paint pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        //设置线条宽度
        pointPaint.setStyle(Paint.Style.FILL);
        mPointRadius = Utils.dp2px(mContext, 4);
        mInnerPointRadius = Utils.dp2px(mContext, 3);

        //
        //折线
        Paint chartLinePaint = new Paint();
        chartLinePaint.setAntiAlias(true);
        //设置线条宽度
        chartLinePaint.setStyle(Paint.Style.FILL);
        String lineColor = mColorBean.lineColor;
        if (!TextUtils.isEmpty(lineColor)) {
            try {
                chartLinePaint.setColor(Color.parseColor(lineColor));
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage(), e);
                chartLinePaint.setColor(Color.parseColor("#FD8E4A"));
            }
        } else {
            chartLinePaint.setColor(Color.parseColor("#FD8E4A"));

        }
        chartLinePaint.setStrokeWidth(3);

        int size = mNodes.size();
        //画点
        for (int s = 0; s < mNodes.size(); s++) {
            if (!(s == (size - 1))) {
                chartLinePaint.setColor(Color.parseColor("#FD8E4A"));
                mCanvas.drawLine(mNodes.get(s).getX(), mNodes.get(s).getY(), mNodes.get(s + 1)
                        .getX(), mNodes.get(s + 1).getY(), chartLinePaint);
            }
            //第一个坐标和最后一个不画点
//            if (!(s == 0) && !(s == (size - 1))) {
            if (mNodes.get(s).isMaxY() || mNodes.get(s).isMinY()) {
                if (!TextUtils.isEmpty(lineColor)) {
                    try {
                        pointPaint.setColor(Color.parseColor(lineColor));
                    } catch (Exception e) {
                        LogUtils.e(TAG, e.getMessage(), e);
                        pointPaint.setColor(Color.parseColor("#FD8E4A"));
                    }
                } else {
                    pointPaint.setColor(Color.parseColor("#FD8E4A"));
                }
                mCanvas.drawCircle(mNodes.get(s).getX(), mNodes.get(s).getY(), mPointRadius,
                        pointPaint);
                pointPaint.setColor(Color.WHITE);
                mCanvas.drawCircle(mNodes.get(s).getX(), mNodes.get(s).getY(), mInnerPointRadius,
                        pointPaint);
                //添加资产波动文字
//                    addNodeText(mNodes.get(s));

            }

//            }
        }
    }


    /**
     * (绘制渐变色)
     * * @return
     */

    private void setLinePath() {
        //画线
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        //设置线条宽度
        linePaint.setStyle(Paint.Style.FILL);


        ArrayList<ChartNode> lines = new ArrayList<>();
        if (mNodes != null && mNodes.size() > 0) {
            lines.addAll(mNodes);
        }
        if (lines == null || lines.size() <= 0) {
            return;
        }
        int length = mNodes.size();
        ChartNode chartNode1 = new ChartNode(mNodes.get(0).getX(), xPoints.get(0).getY(), "");
        ChartNode chartNode2 = new ChartNode(mNodes.get(length - 1).getX(), xPoints.get(0).getY()
                , "");

        lines.add(0, chartNode1);
        lines.add(chartNode2);

        //定义一个Path对象，封闭一个多边形
        Path path = new Path();
        for (int t = 0; t < lines.size(); t++) {
            if (t == 0) {
                path.moveTo(lines.get(t).getX(), lines.get(t).getY());
            } else {
                path.lineTo(lines.get(t).getX(), lines.get(t).getY());
            }
        }
        int deepColor, shallColor;
        try {
            deepColor = Color.parseColor(mColorBean.deepColor);
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage(), e);
            deepColor = Color.parseColor("#FEDFC6");
        }
        float y0 = 0.0f, y1 = 0.0f;

        try {
            y1 = xPoints.get(0).getY();
            for (ChartNode chartNode : mNodes) {
                if (chartNode.isMaxY()) {
                    y0 = chartNode.getY() * 7 / 9;
                }

            }
            shallColor = Color.parseColor(mColorBean.shallColor);
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage(), e);
            shallColor = Color.parseColor("#FFF1E7");
            y1 = 900;

        }


        //渐变的是一个颜色序列(#faf84d,#003449,#808080,#cc423c)
        LinearGradient mShader = new LinearGradient(0, y0, 0, y1, new int[]{deepColor,
                shallColor}, new float[]{0.3f, 1.0f}, Shader.TileMode.MIRROR);
        linePaint.setShader(mShader);

        path.close();
        //根据Path进行绘制，绘制五角星
        mCanvas.drawPath(path, linePaint);
    }


    /**
     * (添加点上面的文本)
     */
    private void addNodeText(ChartNode chartNode) {

        TextView textView = new TextView(mContext);
        mParentView.addView(textView);
        textView.setBackgroundColor(Color.WHITE);
        String text;
        if (chartNode.isMaxY()) {
            text = "资产最大波动" + "\n" + "¥" + chartNode.getText();
        } else if (chartNode.isMinY()) {
            text = "资产最小波动" + "\n" + "¥" + chartNode.getText();
        } else {
            return;
        }
        textView.setTextColor(Color.parseColor("#FD8E4A"));
        textView.setText(text);
        float x = chartNode.getX();
        float y = chartNode.getY();

        int offsetsUp = Utils.dp2px(mContext, 25);
        int offsetsDown = Utils.dp2px(mContext, 5);

//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        //设置线条宽度
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.GREEN);
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setTextSize(40);

        textView.setTextSize(Utils.dp2px(mContext, 3));
        textView.setX(x);
        if (chartNode.isMinY()) {
            textView.setY(y + offsetsDown);
        } else {
            textView.setY(y - offsetsUp);
        }

//        mCanvas.drawText(text, x, y - offsets, paint);
    }

    /**
     * (x轴上三等分固定text文字)
     */

    private void addBottomFixText() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置线条宽度
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#666666"));
        paint.setTextSize(Utils.dp2px(mContext, 11));
        int offsets = Utils.dp2px(mContext, 20);


        ArrayList<ChartNode> fixNodes = new ArrayList<>();
        if (xPoints.size() >= 2) {
            //x轴上面的坐标是固定的三个
            int size = xPoints.size();

            fixNodes.add(0, xPoints.get(0));
            fixNodes.add(1, new ChartNode(mWidth / 2, xPoints.get(0).getY(), ""));
            fixNodes.add(2, xPoints.get(size - 1));
        }

        for (int i = 0; i < fixNodes.size(); i++) {
            String text = mBottomTextBean.startDate;
            if (i == 0) {
                text = mBottomTextBean.startDate;
                paint.setTextAlign(Paint.Align.LEFT);
            } else if (i == 1) {
                text = mBottomTextBean.midDate;
                paint.setTextAlign(Paint.Align.CENTER);
            } else if (i == 2) {
                text = mBottomTextBean.endDate;
                paint.setTextAlign(Paint.Align.RIGHT);
            }
            mCanvas.drawText(text, fixNodes.get(i).getX(), fixNodes.get(i).getY() + offsets, paint);

        }


    }

    public ColorBean getColorBean() {
        return mColorBean;
    }

    public void setColorBean(ColorBean colorBean) {
        mColorBean = colorBean;
    }

    public static class ColorBean {
        public String areaColor;
        public String textColor;
        public String lineColor;
        //最小透明度
        public String minPellucid;
        //最大透明度
        public String maxPellucid;
        /**
         * 深色
         */
        public String deepColor;
        /**
         * 浅色
         */
        public String shallColor;

        public ColorBean(String areaColor, String textColor, String lineColor, String
                minPellucid, String maxPellucid) {
            this.areaColor = areaColor;
            this.textColor = textColor;
            this.lineColor = lineColor;
            this.minPellucid = minPellucid;
            this.maxPellucid = maxPellucid;
        }

        public String getAreaColor() {
            return areaColor;
        }

        public void setAreaColor(String areaColor) {
            this.areaColor = areaColor;
        }

        public String getTextColor() {
            return textColor;
        }

        public void setTextColor(String textColor) {
            this.textColor = textColor;
        }

        public String getLineColor() {
            return lineColor;
        }

        public void setLineColor(String lineColor) {
            this.lineColor = lineColor;
        }

        public String getMinPellucid() {
            return minPellucid;
        }

        public void setMinPellucid(String minPellucid) {
            this.minPellucid = minPellucid;
        }

        public String getMaxPellucid() {
            return maxPellucid;
        }

        public void setMaxPellucid(String maxPellucid) {
            this.maxPellucid = maxPellucid;
        }

        public String getDeepColor() {
            return deepColor;
        }

        public void setDeepColor(String deepColor) {
            this.deepColor = deepColor;
        }

        public String getShallColor() {
            return shallColor;
        }

        public void setShallColor(String shallColor) {
            this.shallColor = shallColor;
        }
    }

    public static class BottomTextBean {
        public boolean isChange() {
            return isChange;
        }

        public void setChange(boolean change) {
            isChange = change;
        }

        //x轴是否固定文本显示，显示全部日期，还是只显示三个
        public boolean isChange;

        public BottomTextBean(String startDate, String endDate, String midDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.midDate = midDate;
        }

        public String startDate;
        public String endDate;
        public String midDate;
        public List<String> dateList;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getMidDate() {
            return midDate;
        }

        public void setMidDate(String midDate) {
            this.midDate = midDate;
        }

        public void setDateList( ArrayList<String> dateList) {
            this.dateList= dateList;
        }

        public List<String> getDateList() {
            return dateList;
        }
    }


}
