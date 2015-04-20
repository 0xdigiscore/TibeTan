package com.example.like.tibetan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by like on 15-4-14.
 */
public class HdWriteDrawView extends View {
    private Paint paint;
    public HdWriteDrawView(Context context) {
        super(context);
    }
    public HdWriteDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();
    }
    @Override
    public void onDraw(Canvas canvas){
        paint.setColor(Color.BLACK);
        float width=getMeasuredWidth();

        float height=getMeasuredHeight();
        canvas.drawLine(width/3,width,height/3,height/3,paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(110,150,60,paint);
    }
}
