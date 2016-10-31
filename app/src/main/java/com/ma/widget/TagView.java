package com.ma.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ma.emailtageditlayout.R;

/**
 * 流式布局的childView
 * Created by mapengtang on 2016/10/27.
 */

public class TagView extends View {

    private String mTxt = "";
    private Paint mPaint;
    private int mBgColor = Color.CYAN;
    private int mBoundRaius = 10;//圆角的弧度
    private int mTxtColor = Color.BLACK;
    private float mTxtSize = 0;

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayoutChildView);
        mTxt = typedArray.getString(R.styleable.FlowLayoutChildView_txt);
        mBoundRaius = typedArray.getDimensionPixelSize(R.styleable.FlowLayoutChildView_round_raius, 10);
        mBgColor = typedArray.getColor(R.styleable.FlowLayoutChildView_bg_color, Color.CYAN);
        mTxtColor = typedArray.getColor(R.styleable.FlowLayoutChildView_txt_color, Color.BLACK);
        mTxtSize = typedArray.getDimensionPixelSize(R.styleable.FlowLayoutChildView_txt_size, 0);
        mTxtSize = getTextSize(mTxtSize);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        //计算组件的大小
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            height = heightSize;
        } else {
            if (mPaint != null) {
                mPaint.reset();
            }
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Rect rectTxtBound = new Rect();
            mPaint.setTextSize(mTxtSize);
            mPaint.getTextBounds(mTxt, 0, mTxt.length(), rectTxtBound);
            width = rectTxtBound.width() + getPaddingLeft() + getPaddingRight();
            height = rectTxtBound.height() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    //设置文本
    public void setText(String mTxt) {
        this.mTxt = mTxt;
        requestLayout();
    }

    public String getText() {
        return mTxt;
    }

    public float getTextSize(float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, r.getDisplayMetrics());
    }

    //仅支持居中的显示
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //不让其设置大小，直接绘制
        if (mTxtSize == 0) {
            return;
        }
        mPaint.reset();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //画出背景图
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);
        Path pathOval = new Path();
        pathOval.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), new float[]{mBoundRaius, mBoundRaius, mBoundRaius, mBoundRaius, mBoundRaius, mBoundRaius, mBoundRaius, mBoundRaius}, Path.Direction.CCW);
        canvas.drawPath(pathOval, mPaint);
        mPaint.reset();
        //画出字体
        mPaint.setColor(mTxtColor);
        mPaint.setTextSize(mTxtSize);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int baseLine = (int) (getMeasuredHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mTxt, getMeasuredWidth() / 2f, baseLine, mPaint);
    }
}
