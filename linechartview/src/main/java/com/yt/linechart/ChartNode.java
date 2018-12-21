package com.yt.linechart;

/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 *
 */
public class ChartNode {
    private float x;
    private float y;
    private String text,subYear;
    private boolean maxY, minY;
    //

    public String getSubYear() {
        return subYear;
    }

    public void setSubYear(String subYear) {
        this.subYear = subYear;
    }

    private boolean flag;

    public ChartNode() {
    }

    public ChartNode(float x, float y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMaxY() {
        return maxY;
    }

    public void setMaxY(boolean maxY) {
        this.maxY = maxY;
    }

    public boolean isMinY() {
        return minY;
    }

    public void setMinY(boolean minY) {
        this.minY = minY;
    }

    public boolean isFlag() {
        return flag;
    }

    /**
     * setFlag (给当前的月份做个标记)
     *
     * @param flag
     * @return void
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
