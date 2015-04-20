package com.example.like.tibetan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by like on 15-4-13.
 */
public class keyBoardView extends ViewGroup {

    public keyBoardView(Context context) {
        super(context);
    }

    public keyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         *
         * */
         int widthMode=MeasureSpec.getMode(widthMeasureSpec);
         int heightMode=MeasureSpec.getMode(heightMeasureSpec);
         int sizeWidth=MeasureSpec.getSize(widthMeasureSpec);
         int sizeHeight=MeasureSpec.getSize(heightMeasureSpec);
         //计算出所有childView的宽和高


        
    }
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs){
        return new MarginLayoutParams(getContext(),attrs);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
