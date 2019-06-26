package com.zxj.zlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Wheel extends View implements View.OnTouchListener {


    int centerX;    //方向盘X轴中心
    int centerY;    //方向盘Y轴中心
    int xPosition;  //点击圆形按钮的x坐标
    int yPosition;  //点击圆形按钮的y坐标

    int mainRadius; // 相对于方向盘圆心的 点击圆形按钮圆心范围
    int secondRadius;   // 点击的圆形按钮的半径
    boolean isClicked;  // 用于判断方向盘是否被点击
    int offset = 100;
    OnWheelMoveListener wheelMoveListener;

    public Wheel(Context context) {
        super(context);
    }

    public Wheel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Wheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isClicked=false;//初始化为未点击状态
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width =MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(heightMeasureSpec);
        if(width>height){
            width=height;
        }else{
            height=width;
        }
        setMeasuredDimension(width, height);

        this.secondRadius=width/6;

        this.centerX=width/2;
        this.centerY=height/2;
        this.xPosition=centerX;
        this.yPosition=centerY;

        this.mainRadius=width-offset-secondRadius-centerX;
    }

    @Override
    protected void onDraw(Canvas canvas){
        Bitmap bm;
        Paint BackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BackgroundPaint.setFilterBitmap(true);
        BackgroundPaint.setDither(true);

        bm = ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.wheel0, null)).getBitmap();

        Rect mSrcRect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        Rect mDestRect = new Rect(offset, offset,getWidth()-offset, getHeight()-offset);
        canvas.drawBitmap(bm, mSrcRect, mDestRect, BackgroundPaint);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#52c1bd"));
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(this.xPosition, this.yPosition, secondRadius, circlePaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        isClicked=true;
        this.xPosition = (int) event.getX();
        this.yPosition = (int) event.getY();
        double nn = Math.sqrt((this.xPosition-this.centerX)*(this.xPosition-this.centerX)+(this.yPosition-this.centerY)*(this.yPosition-this.centerY));
        if(Math.sqrt((this.xPosition-this.centerX)*(this.xPosition-this.centerX)+(this.yPosition-this.centerY)*(this.yPosition-this.centerY))>mainRadius){
            double Yrate=(this.yPosition-this.centerY)/Math.sqrt((this.xPosition-this.centerX)*(this.xPosition-this.centerX)+(this.yPosition-this.centerY)*(this.yPosition-this.centerY));
            double Xrate=(this.xPosition-this.centerX)/Math.sqrt((this.xPosition-this.centerX)*(this.xPosition-this.centerX)+(this.yPosition-this.centerY)*(this.yPosition-this.centerY));
            this.yPosition=(int)(mainRadius*Yrate)+this.centerY;
            this.xPosition=(int)(mainRadius*Xrate)+this.centerX;
        }

        if(this.wheelMoveListener!=null){
            this.wheelMoveListener.onValueChanged((this.xPosition-this.centerX)/(mainRadius*1.0f),(this.centerY-this.yPosition)/(mainRadius*1.0f));
        }
        invalidate();

        if(event.getAction()==1){
            isClicked=false;
            this.yPosition=this.centerY;
            this.xPosition=this.centerX;
            if(this.wheelMoveListener!=null){
                this.wheelMoveListener.onValueChanged(this.xPosition-this.centerX,this.yPosition-this.centerY);
            }
            invalidate();
        }
        return true;
    }

    public void setOnWheelMoveListener(OnWheelMoveListener listener){
        this.wheelMoveListener=listener;
    }

    public static abstract interface OnWheelMoveListener {
        public abstract void onValueChanged(float xDistance, float yDistance);
    }
}
