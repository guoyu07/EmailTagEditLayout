package com.ma.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ma.emailtageditlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局的容器
 * Created by mapengtang on 2016/10/27.
 */

public class EditFlowLayout extends ViewGroup implements OnFlowViewChangeListener {

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;

    private GenerateTagEditText mGenerateEditText;//该行存在其他组件
    private int mTextSize;
    private boolean hasAdded = false;

    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    protected List<Integer> mLineWidth = new ArrayList<Integer>();
    private int mGravity;
    private List<View> lineViews = new ArrayList<>();

    public EditFlowLayout(Context context) {
        this(context, null);
    }

    public EditFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getResValues(context, attrs);
        setWillNotDraw(false);
    }

    private void getResValues(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mGravity = typedArray.getInt(R.styleable.FlowLayout_gravity, LEFT);
        typedArray.recycle();
    }

    @Override
    public void onAddView(View childView) {
        addView(childView);
    }

    @Override
    public void onRemoveView(View childView) {
        removeAllViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        createEditView();
    }

    private void createEditView() {
        //TODO 待修改为字体的大小
        mTextSize = getResources().getDimensionPixelSize(R.dimen.x20);
        mGenerateEditText = new GenerateTagEditText(getContext());
        mGenerateEditText.setListener(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //wrap_content
        int width = 0;
        int height = 0;
        //行高以及行宽（每行的宽度不相同）
        int lineWidth = 0;
        int lineHeight = 0;
        int cCount = getChildCount();
        for (int i = 0; i < cCount; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            //测量子组件的大小
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //设置EditText的边距
            if (mGenerateEditText == null) {
                mGenerateEditText = new GenerateTagEditText(getContext());
            }
            mGenerateEditText.setMargin(lp.leftMargin, lp.rightMargin);
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                //该行若不能装下组件则换行
                width = Math.max(width, lineWidth);
                //下一行的值
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //最大宽度的值
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        if (!hasAdded) {
            addEditTextView(width, lineWidth, lineHeight);
            hasAdded = true;
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(), modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
    }

    private void addEditTextView(int width, int lineWidth, int lineHeight) {
        mGenerateEditText.setContainerWidth(width);
        if (width - lineWidth > mTextSize * 10) {
            mGenerateEditText.generate(width - lineWidth, 0, lineHeight, mTextSize);
            for (EditText editText : mGenerateEditText.getEditTextList()) {
                addView(editText);
            }
            mGenerateEditText = null;
        } else if (width - lineHeight > mTextSize * 3) {
            mGenerateEditText.generate(width - lineWidth, width, lineHeight, mTextSize);
            for (EditText editText : mGenerateEditText.getEditTextList()) {
                addView(editText);
            }
            mGenerateEditText = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //清空所有的数据
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        //width中不包含margin的值
        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        int cCount = getChildCount();
        for (int i = 0; i < cCount; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                //该行不能装下组件，进行换行操作
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(lineWidth);
                lineWidth = 0;
                lineHeight = childHeight + getPaddingTop() + getPaddingBottom();
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; ++i) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);
            //set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    left = width - currentLineWidth + getPaddingLeft();
                    break;
            }
            for (int j = 0; j < lineViews.size(); ++j) {
                View child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int rc = lc + child.getMeasuredWidth() + lp.rightMargin;
                int tc = top + lp.topMargin;
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);
                left += lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin;
            }
            top += lineHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        int cCount = mLineHeight.size();
        int bottom = 0;
        for (int i = 0; i < cCount; ++i) {
            bottom += mLineHeight.get(i);
            canvas.drawLine(0, bottom, getMeasuredWidth(), bottom, paint);
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
