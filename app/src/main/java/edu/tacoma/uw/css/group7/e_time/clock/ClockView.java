package edu.tacoma.uw.css.group7.e_time.clock;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

import edu.tacoma.uw.css.group7.e_time.R;

/**
 * The ClockView is the analog clock display used for the MainFragment.  It displays the current
 * time on the device and redraws every 500 milliseconds.  Created with the help from Sylvain
 * Saurel's YouTube video tutorial on using Canvas 2D to draw an analog clock: https://www.youtube.com/watch?v=ybKgq6qqTeA
 *
 * @author Alexander Reid
 * @version 5/29/2018
 */
public class ClockView extends View {

    private int mHeight, mWidth;
    private int mPadding;
    private int mHandTruncation, mHourHandTruncation;
    private int mRadius;
    private Paint mPaint;
    private boolean mIsInit;

    //Default constructors
    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Initializes all the member variables necessary for the view.
     */
    private void initClock() {
        mHeight = getHeight();
        mWidth = getWidth();
        mPadding = 50;
        int min = Math.min(mHeight, mWidth);
        mRadius = min /2 - mPadding;
        mHandTruncation = min/ 20;
        mHourHandTruncation = min/ 7;
        mPaint = new Paint();
        mIsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas)    {
        if (!mIsInit)    {
            initClock();
        }

        drawCircle(canvas);
        drawCenter(canvas);
        drawHands(canvas);

        postInvalidateDelayed(500);
        invalidate();
    }

    /**
     * Draws one of the hands on the clock. Hand is determined from params.
     * @param canvas
     * @param loc - position for the hand
     * @param hand - 0 = Hour hand, 1 = Minute, 2 = Second
     */
    private void drawHand(Canvas canvas, double loc, int hand)    {
        double angle = Math.PI * loc/30 - Math.PI / 2;
        int handRadius;
        if (hand == 0)   {
            handRadius = mRadius - mHandTruncation - mHourHandTruncation;
            mPaint.setStrokeWidth(15);
        } else  {
            handRadius = mRadius - mHandTruncation;
            if (hand == 1)  {
                mPaint.setStrokeWidth(10);
            } else {
                mPaint.setStrokeWidth(5);
            }
        }
        canvas.drawLine(mWidth /2, mHeight /2,
                (float) (mWidth /2 + Math.cos(angle)*handRadius),
                (float) (mHeight /2 + Math.sin(angle)*handRadius),
                mPaint);
    }

    /**
     * Draws each of the three hands on the clock face.
     * @param canvas
     */
    private void drawHands(Canvas canvas)   {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;
        drawHand(canvas, ((double)c.get(Calendar.MINUTE))/12 + (c.get(Calendar.HOUR) * 5.0), 0);
        drawHand(canvas, c.get(Calendar.MINUTE), 1);
        drawHand(canvas, c.get(Calendar.SECOND), 2);
    }

    /**
     * Draws the center dot of the clock.
     * @param canvas
     */
    private void drawCenter(Canvas canvas)  {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mWidth /2, mHeight /2,12, mPaint);
    }

    /**
     * Draws the outer circle of the clock.
     * @param canvas
     */
    private void drawCircle(Canvas canvas)  {
        mPaint.reset();
        mPaint.setColor(getResources().getColor(R.color.clockPrimary));
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mWidth /2, mHeight /2, mRadius + mPadding - 10, mPaint);

    }
}
