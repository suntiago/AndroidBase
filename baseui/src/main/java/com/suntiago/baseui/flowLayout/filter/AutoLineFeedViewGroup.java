package com.suntiago.baseui.flowLayout.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by yu.zai on 2015/12/30.
 * child view will auto line feed
 */
public class AutoLineFeedViewGroup extends ViewGroup {
    private int GAP_VER = 24;
    private int GAP_HORI = 24;

    private boolean gravityCentre = true;

    /**
     * @param context
     */
    public AutoLineFeedViewGroup(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoLineFeedViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * @param context
     * @param attrs
     */
    public AutoLineFeedViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGAPVER(int ver) {
        GAP_VER = ver;
    }

    public void setGAPHORI(int hori) {
        GAP_HORI = hori;
    }

    public void setGravityCentre(boolean gravityCentre) {
        this.gravityCentre = gravityCentre;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = 0;// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 1;
        int errorRange = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width;
            if (x > autualWidth) {
                x = width;//+SIDE_MARGIN;
                rows++;
            }
            int gap = 0;
            Integer co = mErrorRange.get(rows);
            if (gravityCentre && co != null) {
                errorRange = co;
                gap = errorRange / 2;
            }

            y = rows * (height + GAP_VER) - GAP_VER;
            if (i == 0) {
                view.layout(gap + x - width, y - height, gap + x, y);
            } else {
                view.layout(gap + x - width, y - height, gap + x, y);
            }
            x += GAP_HORI;
        }
    }

    HashMap<Integer, Integer> countItem = new HashMap<>();
    HashMap<Integer, Integer> mErrorRange = new HashMap<>();
    int mActualWidth = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - GAP_HORI * 2;//实际宽度
        mActualWidth = actualWidth;
        int childCount = getChildCount();

        //Note down the number of a line
        int countLine = 0;
        int lastBlank = 0;
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lastBlank = actualWidth - x - GAP_HORI;
            //最后的空白不足以放下
            if (lastBlank < width) {//换行
                //记录每行child个数
                if (countItem.containsKey(rows)) {
                    countItem.remove(rows);
                }
                countItem.put(rows, countLine);

                //记录每行多余的空白
                if (mErrorRange.containsKey(rows)) {
                    mErrorRange.remove(rows);
                }
                mErrorRange.put(rows, lastBlank);

                rows++;
                x = width;
                lastBlank = 0;
                countLine = 1;
            } else {
                countLine++;
                x += GAP_HORI;
                x += width;
                lastBlank = lastBlank - width;
            }
            y = rows * (height + GAP_VER);
        }
        if (lastBlank != 0) {
            //记录每行child个数
            if (countItem.containsKey(rows)) {
                countItem.remove(rows);
            }
            countItem.put(rows, countLine);

            //记录每行多余的空白
            if (mErrorRange.containsKey(rows)) {
                mErrorRange.remove(rows);
            }
            mErrorRange.put(rows, lastBlank);
        }

        setMeasuredDimension(actualWidth, y - GAP_VER);
    }

}