package edu.tacoma.uw.css.group7.e_time.clock;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

import edu.tacoma.uw.css.group7.e_time.R;

public class ClockView extends View {

    private int height, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private Rect rect = new Rect();
    private String timeText = "";
    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40,
                getResources().getDisplayMetrics());

        int min = Math.min(height, width);
        radius = min /2 - padding;
        handTruncation = min/ 20;
        hourHandTruncation = min/ 7;
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas)    {
        if (!isInit)    {
            initClock();
        }

        drawCircle(canvas);
        drawCenter(canvas);
        drawHands(canvas);
        //drawTime(canvas);

        postInvalidateDelayed(500);
        invalidate();
    }

    private void drawHand(Canvas canvas, double loc, int hand)    {
        double angle = Math.PI * loc/30 - Math.PI / 2;
        int handRadius;
        if (hand == 0)   {
            handRadius = radius - handTruncation - hourHandTruncation;
            paint.setStrokeWidth(15);
        } else  {
            handRadius = radius - handTruncation;
            if (hand == 1)  {
                paint.setStrokeWidth(10);
            } else {
                paint.setStrokeWidth(5);
            }
        }
        //int handRadius = isHour ? radius - handTruncation - hourHandTruncation: radius - handTruncation;
        canvas.drawLine(width/2, height/2,
                (float) (width/2 + Math.cos(angle)*handRadius),
                (float) (height/2 + Math.sin(angle)*handRadius),
                paint);
    }

    private void drawHands(Canvas canvas)   {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;
        drawHand(canvas, ((double)c.get(Calendar.MINUTE))/12 + (c.get(Calendar.HOUR) * 5.0), 0);
        drawHand(canvas, c.get(Calendar.MINUTE), 1);
        drawHand(canvas, c.get(Calendar.SECOND), 2);
    }

    private void drawTime(Canvas canvas)    {
        Calendar c = Calendar.getInstance();
        timeText = String.format("%2d:%2d", c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));

        paint.setTextSize(fontSize);
        paint.setColor(getResources().getColor(android.R.color.black));
        int x = (int) (width/2) - 100;
        int y = (int) (height/2) + 20;
        canvas.drawText(timeText, x, y, paint);
    }

    private void drawCenter(Canvas canvas)  {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width/2, height/2,12,paint);


    }
    private void drawCircle(Canvas canvas)  {
        paint.reset();
        paint.setColor(getResources().getColor(R.color.clockPrimary));
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width/2, height/2, radius + padding - 10, paint);

    }
}
