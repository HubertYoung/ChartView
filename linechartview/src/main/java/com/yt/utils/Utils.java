package com.yt.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.yt.linechart.ChartNode;

import java.math.BigDecimal;

/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 */
public class Utils {
    public static final String TAG = Utils.class.getSimpleName();
    private static String NUM_ONE = "1";
    private static String NUM_ZERO = "0";

    public static boolean isEmpty(String src) {
        return src == null || src.trim().length() == 0;
    }

    public static int dp2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context
                .getResources().getDisplayMetrics());
    }

    public ChartNode convertNode(ChartNode chartNode, int height, Context context) {
        chartNode.setY(height - chartNode.getY());
        return chartNode;
    }

    /**
     * convertNode (这里用一句话描述这个方法的作用)
     *
     * @param minY         最小的y坐标点
     * @param maxY         最大的y坐标点
     * @param currentX     当前x坐标
     * @param currentY     当前的y坐标
     * @param text         当前文本内容
     * @param height       控件的高度
     * @param heightOffset 高度的偏移量
     */
    public static ChartNode convertNode(String minY, String maxY, float currentX, String
            currentY, String text, int height, int heightOffset) {

        minY = minY.replace(",", "");
        maxY = maxY.replace(",", "");
        currentY = currentY.replace(",", "");
        //大数据新算法
        LogUtils.eTag(TAG, "height:" + height);
        String subtract1 = subtractNumberFormat(currentY, minY);
        String subtract2 = "";
        //最大值和最小值相等时 返回1,防subtract2作为除数为0的情况
        if (maxY == minY) {
            subtract2 = NUM_ONE;
        } else {
            subtract2 = subtractNumberFormat(maxY, minY);
        }
        String s1 = null;
        try {
            s1 = divideNumberFormat(subtract1, subtract2);
        } catch (Exception e) {
            //当subtract2作为除数为0时，s1为0
            s1 = NUM_ZERO;
        }
        String s2 = multiplyNumberFormat(s1, String.valueOf(height * 6 / 15));
        String s3 = subtractNumberFormat(String.valueOf(height - heightOffset), s2);
        if (s3.contains("-")) {
//            CMBCLog.i("", "====-convertY:" + s3);

        }
        LogUtils.eTag(TAG, "s3===" + s3 + ",s2:==" + s2 + ",s1:==" + s1);

        ChartNode chartNode = new ChartNode(currentX, Float.valueOf(s3), text);


        return chartNode;
    }

    /**
     * 除法运算
     *
     * @param bigStr1
     * @param bigStr2
     * @return
     */
    public static String divideNumberFormat(String bigStr1, String bigStr2) {
        BigDecimal a = new BigDecimal(bigStr1);
        BigDecimal b = new BigDecimal(bigStr2);
        BigDecimal divide = a.divide(b, 5, BigDecimal.ROUND_HALF_UP);
        return divide.toString();
    }

    /**
     * // 乘法运算
     *
     * @param bigStr1
     * @param bigStr2
     * @return
     */
    public static String multiplyNumberFormat(String bigStr1, String bigStr2) {
        BigDecimal a = new BigDecimal(bigStr1);
        BigDecimal b = new BigDecimal(bigStr2);
        BigDecimal multiply = a.multiply(b);
        return multiply.toString();
    }

    /**
     * 大数减法运算
     *
     * @param bigStr1
     * @param bigStr2
     * @return
     */
    public static String subtractNumberFormat(String bigStr1, String bigStr2) {
        BigDecimal a = new BigDecimal(bigStr1);
        BigDecimal b = new BigDecimal(bigStr2);
        BigDecimal subtract = a.subtract(b);
        return subtract.toString();
    }

    /**
     * 大数加法运算
     *
     * @param bigStr1
     * @param bigStr2
     * @return
     */
    public static String addNumberFormat(String bigStr1, String bigStr2) {
        BigDecimal a = new BigDecimal(bigStr1);
        BigDecimal b = new BigDecimal(bigStr2);
        BigDecimal add = a.add(b);
        return add.toString();
    }


    /**
     * 获取屏幕的宽度和高度
     *
     * @param mAct
     * @return int @exception
     */
    public static int[] getScreenSize(Activity mAct) {

        DisplayMetrics dm = new DisplayMetrics();
        mAct.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int[] screenSize = new int[2];
        screenSize[0] = screenWidth;
        screenSize[1] = screenHeight;
        return screenSize;
    }
}
