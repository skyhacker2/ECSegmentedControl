package com.eleven.lib.library;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by eleven on 15/11/3.
 */
public class ECSegmentedControl extends View {

    private final static String TAG = ECSegmentedControl.class.getSimpleName();

    public interface ECSegmentedControlListener
    {
        void onSelectIndex(int index);
    }
    private Context mContext;
    private Drawable mSegmentedControlFgDrawable;
    private Drawable mSegmentedControlBgDrawable;
    private Drawable mDividerDrawable;
    private ColorStateList mTextColorState;
    private CharSequence[] mSegmentStrings;
    private int mTextSize;
    private boolean mEnableDivider;

    private Paint mTextPaint;
    private int mCount;                 // segment 数量
    private int mSelectedIndex = 0;         // 选择的下标
    private int mTouchIndex = -1;       // 点击的下标
    private int defalutHeight;
    private int minEachWidth;
    private int mHeight = -1;
    private ECSegmentedControlListener mECSegmentedControlListener;


    public ECSegmentedControl(Context context) {
        this(context, null);
    }

    public ECSegmentedControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECSegmentedControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        defalutHeight = dip2px(30);
        minEachWidth = dip2px(70);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ECSegmentedControl);
        mTextSize = a.getDimensionPixelSize(R.styleable.ECSegmentedControl_textSize, 30);
        mSegmentedControlBgDrawable = a.getDrawable(R.styleable.ECSegmentedControl_segmentedBackground);
        mSegmentedControlFgDrawable = a.getDrawable(R.styleable.ECSegmentedControl_segmentedForeground);
        mDividerDrawable = a.getDrawable(R.styleable.ECSegmentedControl_segmentedDivider);
        mTextColorState = a.getColorStateList(R.styleable.ECSegmentedControl_textColor);
        mSegmentStrings = a.getTextArray(R.styleable.ECSegmentedControl_segmentedTexts);
        mEnableDivider = a.getBoolean(R.styleable.ECSegmentedControl_segmentedEnableDivider, false);
        a.recycle();

        if (mTextColorState == null) {
            mTextColorState = getResources().getColorStateList(R.color.selector_defalut_segmented_color);
        }
        if (mSegmentedControlBgDrawable == null) {
            mSegmentedControlBgDrawable = getResources().getDrawable(R.drawable.segmented_shape_bg);
        }
        if (mSegmentedControlFgDrawable == null) {
            mSegmentedControlFgDrawable = getResources().getDrawable(R.drawable.segmented_shape_fg);
        }
        if (mDividerDrawable == null) {
            mDividerDrawable = getResources().getDrawable(R.drawable.segmented_divider);
        }


        mSelectedIndex = 0;
        if (mSegmentStrings == null) {
            mSegmentStrings = new CharSequence[]{"Segment 1", "Segment 2", "Segment 3"};
        }
        if (isInEditMode()) {
            mSegmentStrings = new CharSequence[]{"Segment 1", "Segment 2", "Segment 3"};
        }
        mCount = mSegmentStrings.length;

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            width = mSegmentedControlBgDrawable.getIntrinsicWidth();
            if (width == -1) {
                width = minEachWidth * mCount;
            }
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            height = mSegmentedControlBgDrawable.getIntrinsicHeight();
            if (height == -1) {
                height = defalutHeight;
            }
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            Log.d(TAG,"EXACTLY");
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            Log.d(TAG, "height exactly");
        }
        Log.d(TAG, "width = " + width + " " + "height = " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSegmentedControlBgDrawable.setBounds(0, 0, getWidth(), getHeight());
        mSegmentedControlBgDrawable.draw(canvas);

        canvas.save();
        int eachWidth = getWidth() / mCount;
        mSegmentedControlFgDrawable.setBounds(0, 0, getWidth(), getHeight());
        canvas.clipRect(mSelectedIndex * eachWidth, 0, mSelectedIndex * eachWidth + eachWidth, getHeight());
        mSegmentedControlFgDrawable.draw(canvas);
        canvas.restore();

        canvas.save();
        if (mTouchIndex != -1 && mTouchIndex != mSelectedIndex) {
            canvas.clipRect(mTouchIndex * eachWidth, 0, mTouchIndex * eachWidth + eachWidth, getHeight());
            mSegmentedControlFgDrawable.draw(canvas);
        }
        canvas.restore();

        // 文字
        if (mSegmentStrings != null) {
            for (int i = 0; i < mCount; i++) {
                if (i == mTouchIndex) {
                    int color = mTextColorState.getColorForState(View.PRESSED_ENABLED_STATE_SET, Color.WHITE);
                    mTextPaint.setColor(color);
                } else if (i == mSelectedIndex) {
                    int color = mTextColorState.getColorForState(View.ENABLED_SELECTED_STATE_SET, Color.WHITE);
                    mTextPaint.setColor(color);
                } else {
                    mTextPaint.setColor(mTextColorState.getDefaultColor());
                }
                float start = i * eachWidth + eachWidth / 2;
                Rect textBounds = new Rect();
                mTextPaint.getTextBounds(mSegmentStrings[i].toString(), 0, mSegmentStrings[i].length(), textBounds);
                float v = getHeight()/2 - textBounds.exactCenterY();
                canvas.save();
                canvas.clipRect(i * eachWidth, 0, i * eachWidth + eachWidth, getHeight());
                canvas.drawText(mSegmentStrings[i].toString(), start, v, mTextPaint);
                canvas.restore();
            }
        }

        // 分割线
        if (mEnableDivider) {
            for (int i = eachWidth; i < eachWidth * mCount; i += eachWidth) {
                int r = mDividerDrawable.getIntrinsicWidth() / 2;
                mDividerDrawable.setBounds(i - r, 0, i + r, getHeight());
                mDividerDrawable.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                mTouchIndex = detectTouchIndex(event.getX(), event.getY());
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                mTouchIndex = detectTouchIndex(event.getX(), event.getY());
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                mTouchIndex = detectTouchIndex(event.getX(), event.getY());
                if (mTouchIndex != -1 && mTouchIndex != mSelectedIndex) {
                    mSelectedIndex = mTouchIndex;
                    if (mECSegmentedControlListener != null) {
                        mECSegmentedControlListener.onSelectIndex(mSelectedIndex);
                    }
                }
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 检测触摸点的index
     * @param x
     * @param y
     * @return
     */
    private int detectTouchIndex(float x, float y) {
        if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
            return -1;
        }
        int eachWidth = getWidth() / mCount;
        for (int i = eachWidth * mCount, k = mCount-1; i > 0; i-=eachWidth, k--) {
            if (x < i && x > i-eachWidth) {
                return k;
            }
        }
        return -1;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public ECSegmentedControlListener getECSegmentedControlListener() {
        return mECSegmentedControlListener;
    }

    public void setECSegmentedControlListener(ECSegmentedControlListener ECSegmentedControlListener) {
        mECSegmentedControlListener = ECSegmentedControlListener;
    }

    public Drawable getSegmentedControlFgDrawable() {
        return mSegmentedControlFgDrawable;
    }

    public void setSegmentedControlFgDrawable(Drawable segmentedControlFgDrawable) {
        mSegmentedControlFgDrawable = segmentedControlFgDrawable;
    }

    public Drawable getSegmentedControlBgDrawable() {
        return mSegmentedControlBgDrawable;
    }

    public void setSegmentedControlBgDrawable(Drawable segmentedControlBgDrawable) {
        mSegmentedControlBgDrawable = segmentedControlBgDrawable;
    }

    public Drawable getDividerDrawable() {
        return mDividerDrawable;
    }

    public void setDividerDrawable(Drawable dividerDrawable) {
        mDividerDrawable = dividerDrawable;
    }

    public ColorStateList getTextColorState() {
        return mTextColorState;
    }

    public void setTextColorState(ColorStateList textColorState) {
        mTextColorState = textColorState;
    }

    public CharSequence[] getSegmentStrings() {
        return mSegmentStrings;
    }

    public void setSegmentStrings(CharSequence[] segmentStrings) {
        mSegmentStrings = segmentStrings;
        mCount = mSegmentStrings.length;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        invalidate();
    }

}
