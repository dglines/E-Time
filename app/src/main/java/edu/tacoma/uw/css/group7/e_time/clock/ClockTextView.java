package edu.tacoma.uw.css.group7.e_time.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

/**
 * The ClockTextView is the digital clock display used for the MainFragment.  It displays the current
 * time on the device and redraws every 500 milliseconds.  Created with the help from Sylvain
 * Saurel's YouTube video tutorial on using Canvas 2D to draw an analog clock: https://www.youtube.com/watch?v=ybKgq6qqTeA
 *
 * @author Alexander Reid
 * @version 5/29/2018
 */
public class ClockTextView extends View {

    private int mHeight, mWidth;
    private int mFontSize = 0;
    private Paint mPaint;
    private boolean mIsInit;
    private String mTimeText = "";
    
    //Default constructors
    public ClockTextView(Context context) {
        super(context);
    }

    public ClockTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Initializes all the member variables necessary for the view.
     */
    private void initClock() {
        mHeight = getHeight();
        mWidth = getWidth();
        mFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 68,
                getResources().getDisplayMetrics());

        int min = Math.min(mHeight, mWidth);
        mPaint = new Paint();
        mIsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas)    {
        if (!mIsInit)    {
            initClock();
        }
        drawTime(canvas);

        postInvalidateDelayed(500);
        invalidate();
    }

    /**
     * Draws the current time.
     * @param canvas
     */
    private void drawTime(Canvas canvas)    {
        mPaint.reset();
        Calendar c = Calendar.getInstance();
        //formatting the time to use two digits for the hours and minutes.
        mTimeText = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));

        mPaint.setTextSize(mFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.create("Courier",Typeface.NORMAL));
        mPaint.setColor(getResources().getColor(android.R.color.white));

        canvas.drawText(mTimeText, mWidth /2, mHeight, mPaint);
    }

}
