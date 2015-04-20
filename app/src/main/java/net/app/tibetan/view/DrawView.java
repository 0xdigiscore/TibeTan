package net.app.tibetan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import net.app.tibetan.input.SoftKeyBoard;

import java.util.Timer;
import java.util.TimerTask;
public class DrawView extends View{
    public int WIDTH = 256;
    public int HEIGHT = 256;
    public int MaxStrokes = 30;
    public int MaxPoints = 1000;
    public int MaxPointsxy = 2000;
    private int view_width = 0;
    private int view_height = 0;
    private int width=0;
    private int height=0;
    private int preX;
    private int preY;//ÆðÊŒµãµÄy×ø±êÖµ
    private Path path;	//Â·Ÿ¶
    public Paint paint = null;	//»­±Ê
    Bitmap cacheBitmap = null;
    Canvas cacheCanvas = null;// ¶šÒåcacheBitmapÉÏµÄCanvas¶ÔÏó
    public Point [][] word = new Point[MaxStrokes][MaxPoints];
    public int[][] array = new int[MaxStrokes][];
    public int stroke_num = 0;//±Ê»®Êý
    private int i = -1;//±Ê»­Êý³õÊŒ»¯
    private int j = 0;//Ã¿žö±Ê»­µãÊý³õÊŒ»¯
    public int[] points_stroke = new int[MaxPoints];//Ã¿±ÊÊµŒÊµãÊý
    private boolean hasMeasured=false;
    private int[] result;
    private boolean isGoOn=true;
    private Timer timer;
    boolean readflag=false;
    boolean threadflag=false;
    public DrawView draw = null;
    double end;
    public int CLASSCOUNT = 128;
    public int[][] resultData_P =new int[MaxStrokes][MaxPointsxy];
    public int[] resultData_R = new int[CLASSCOUNT];
    public String[] resultData = new String[CLASSCOUNT];
    public int[] result_show = new int[10];
    public int flag = 0;
    public int tag = 0;
    long finish=0;
    Bitmap[] bm = new Bitmap[CLASSCOUNT];
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        view_width = context.getResources().getDisplayMetrics().widthPixels;
        view_height = context.getResources().getDisplayMetrics().heightPixels;
        ViewTreeObserver vto = getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            public boolean onPreDraw()
            {

                if (hasMeasured == false)
                {

                    height = getMeasuredHeight();
                    width = getMeasuredWidth();
                    System.out.println("draw width->"+width);
                    System.out.println("draw heiht->"+height);
                    hasMeasured = true;

                }
                return true;
            }
        });
        //int canvas_h = height*2/3;
        cacheBitmap = Bitmap.createBitmap(view_width, view_height,Config.ARGB_8888);
        cacheCanvas = new Canvas();
        path = new Path();
        cacheCanvas.setBitmap(cacheBitmap);
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setDither(true);
        for(int m=0;m<MaxStrokes;m++){
            for(int n = 0; n<MaxPoints; n++){
                word[m][n] =new Point(-1,-1);
            }

        }
        timer = new Timer(true);


    }
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    Log.i("222222222222","2222222222222");
                    if(word[0][0].x!=-1){

                        Message msg1=new Message();
                        msg1.what=98;
                        int a[]={1,2,2,3,4,5,6,0};
                        msg1.obj=a;
                        SoftKeyBoard.Writehandler.sendMessage(msg1);

                    }
            }


        }

    };
    Handler pause=new Handler(){
        public void handleMessage(Message msg) {

        }
    };

    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what=1;
            handler.sendMessage(message);
        }
    };
    private void touch_start() {
        if(task != null)
            task.cancel();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what=1;
                handler.sendMessage(message);

            }
        };
    }
    private void touch_move() {
        if(task != null)
            task.cancel();
        task = new TimerTask() {

            @Override
            public void run() {

                Message message = new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        };
    }
    private void touch_up() {

        if (timer!=null) {
            if (task!=null) {
                task.cancel();
                task = new TimerTask() {
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                };
                timer.schedule(task, 500);
            }
        }else {

            timer = new Timer(true);
            timer.schedule(task, 500);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        Paint bmpPaint = new Paint();
        canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint);
        canvas.drawPath(path, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }
    @SuppressLint("NewApi") @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int x = (int)event.getX();
        int y = (int)event.getY();
        long start = 0;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                ++i;
                j = 0;
                path.moveTo(x, y);
                preX = x;
                preY = y;
                Point point_start =new Point((int) preX,(int) preY);
                if(i<30&&j<=1000){
                    word[i][j] = point_start;
                }
                touch_start();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(x - preX);
                int dy = Math.abs(y - preY);
                if (dx > 0 || dy > 0) {
                    ++j;
                    path.quadTo(preX, preY, x, y);
                    preX = x;
                    preY = y;
                    Point point_move = new Point((int)preX,(int)preY);
                    try{
                        if(i<=30&&j<1000){
                            word[i][j] = point_move;
                        }
                    }catch(ArrayIndexOutOfBoundsException e){

                        e.printStackTrace();
                    }
                }
                stroke_num = i + 1 ;
                touch_move();
                break;
            case MotionEvent.ACTION_UP:
                threadflag=true;
                cacheCanvas.drawPath(path, paint); //»æÖÆÂ·Ÿ¶
                path.reset();
                ++j;
                if(i<30&&j<1000&&i>0){
                    points_stroke[i] = j;
                }
                touch_up();

                break;
        }
        invalidate();
        return true;
    }
    public void show_points(){
        for(int m=0; m < MaxStrokes  ;m++){
            if(word[m][0].x == -1)
                break;
            for(int n=0; n <= MaxPoints;n++)
                if(word[m][n].x == -1)
                    break;
                else
                    System.out.println("" + word[m][n]);
        }
    }
    public void clearnPointCach()
    {
        stroke_num = 0;
        for(int m=0;m<30;m++){
            for(int n=0;n<1000;n++){
                word[m][n].set(-1, -1);
            }
            points_stroke[m]=0;
        }
        isGoOn=true;
        i=-1;
        j=0;
    }
    public void clearnDraw()
    {
        Paint paint= new Paint();
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
        cacheCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC));
        invalidate();
        path.reset();
        clearnPointCach();
    }
    public void cleanD(){
        Paint paint= new Paint();
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
        cacheCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC));
        invalidate();
        path.reset();
    }
    public int[][] pointtoarray(Point[][] word ,int[][] array){
        for(int m = 0; m < MaxStrokes; m++)
            for(int n = 0; n <  MaxPoints ; n++){
                array[m][n*2] = word[m][n].x;
                array[m][n*2+1] = word[m][n].y;
            }
        return array;
    }
}