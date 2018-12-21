package com.yt.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;


import com.yt.utils.LogUtils;

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
public class LineChartView extends RelativeLayout {
    /**
     * View 的宽和高
     */
    private int mWidth, mHeight;
    private Context mContext;
    public static final int marginBottom = 35;
    public static final String TAG = LineChartView.class.getSimpleName();
    private int mTextSize = 3;

    private YLine yLine;
    private XLine xLine;
    private Coordinate coordinate;


    private ArrayList<ChartNode> xPoints;
    /**
     * 底部固定的三个日期text
     */
    private Coordinate.BottomTextBean mBottomTextBean;

    private ArrayList<ChartNode> mNodes = new ArrayList<>();

    /**
     * 底部固定的三个日期text
     */
    private Coordinate.ColorBean mColorBean;


    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        ViewTreeObserver obserrve = getViewTreeObserver();
        obserrve.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                LogUtils.i(TAG, "onGlobalLayout view绘制前");
                if (0 != getMeasuredWidth()) {
                    LogUtils.i(TAG, "onGlobalLayout view开始绘制");

                    if (mInvalidataChart != null) {
                        mInvalidataChart.invalidate();
                    }
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

            }
        });


    }

    public void setInvalidataChart(InvalidataChart invalidataChart) {
        mInvalidataChart = invalidataChart;
    }

    private InvalidataChart mInvalidataChart;

    public interface InvalidataChart {
        void invalidate();
    }

    public int getLineChartHeight() {
        return this.mHeight;
    }

    public void setxPoints(ArrayList<ChartNode> xPoints) {
        this.xPoints = xPoints;
    }

    public Coordinate.BottomTextBean getBottomTextBean() {
        return mBottomTextBean;
    }

    public void setBottomTextBean(Coordinate.BottomTextBean bottomTextBean) {
        mBottomTextBean = bottomTextBean;
    }

    public ArrayList<ChartNode> getNodes() {
        return mNodes;
    }

    public void setNodes(ArrayList<ChartNode> nodes) {
        mNodes = nodes;
    }

    public Coordinate.ColorBean getColorBean() {
        return mColorBean;
    }

    public void setColorBean(Coordinate.ColorBean colorBean) {
        mColorBean = colorBean;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.i(TAG, "onDraw");
        //画x轴
        xLine = new XLine(canvas, mContext);
        //画Y轴
//        yLine = new YLine(canvas, mContext, xPoints);
        //可以根据自己的需要来
        if (xPoints != null && xPoints.size() > 0 && mNodes != null && mNodes.size() > 0 &&
                mBottomTextBean != null && mColorBean != null) {
            xLine.drawLine(mWidth, mHeight);
//            yLine.drawLine(mWidth, mHeight);
            //画点
            coordinate = new Coordinate(mWidth, mHeight, canvas, LineChartView.this, mContext);
            //设置颜色
            coordinate.setColorBean(mColorBean);
            //设置所有x轴上面的坐标
            coordinate.setxPoints(xPoints);
            //view上面显示个点
            coordinate.setNodes(mNodes);
            //底部固定的三个日期
            coordinate.setDateList(mBottomTextBean);
            coordinate.drawNode();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.i(TAG, "onLayout");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
//            throw new IllegalArgumentException("width must be EXACTLY,you should set like
//                    android:width =\"200dp\"");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("height must be EXACTLY,you should set like " +
                    "android:height=\"200dp\"");
        }

        setMeasuredDimension(mWidth, mHeight);
    }
}
