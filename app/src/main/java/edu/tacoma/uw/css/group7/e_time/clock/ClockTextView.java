package edu.tacoma.uw.css.group7.e_time.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

import edu.tacoma.uw.css.group7.e_time.R;

public class ClockTextView extends View {

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
    public ClockTextView(Context context) {
        super(context);
    }

    public ClockTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 68,
                getResources().getDisplayMetrics());

        int min = Math.min(height, width);
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas)    {
        if (!isInit)    {
            initClock();
        }
        drawTime(canvas);

        postInvalidateDelayed(500);
        invalidate();
    }

    private void drawTime(Canvas canvas)    {
        paint.reset();
        Calendar c = Calendar.getInstance();
        timeText = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));

        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create("Courier",Typeface.NORMAL));
        paint.setColor(getResources().getColor(android.R.color.white));

        canvas.drawText(timeText, width/2, height, paint);
    }

}
