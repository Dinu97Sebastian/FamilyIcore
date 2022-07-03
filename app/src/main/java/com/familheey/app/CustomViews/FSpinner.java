/**
 *
 */
package com.familheey.app.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSpinner;

import com.familheey.app.R;

import java.util.Objects;


/**
 * @author Innovation
 *
 */
@SuppressLint({"DrawAllocation", "ClickableViewAccessibility"})
public class FSpinner extends AppCompatSpinner {

    private Paint boarder, trianglePaint;
    private boolean isSelect;
    //float bWidth, bHeight, paddingRight, tHeight, tWidth;


    public FSpinner(Context context) {
        super(context);
        init();
    }


    public FSpinner(Context context, int mode) {
        super(context, mode);
        init();
    }


    public FSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public FSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public FSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
        init();
    }

    private void init() {
        boarder = new Paint();
        boarder.setStyle(Paint.Style.STROKE);
        boarder.setStrokeWidth(1);

        trianglePaint = new Paint();
        trianglePaint.setStyle(Paint.Style.FILL);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.LinearLayout#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);

            /*
             * final RectF rect2 = new RectF(); Paint paint2 = new Paint();
             * paint2.setColor(Color.WHITE); paint2.setStyle(Paint.Style.FILL);
             * rect2.set(0, 0, getWidth()-1, getHeight()-1);
             * canvas.drawRoundRect(rect2, 0, 0, paint2);
             */
            Resources r = getResources();

            // draw boarder
            final RectF rect = new RectF();
            boarder.setColor(Color.parseColor("#FFFFFF"));
            boarder.setStrokeWidth(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
            rect.set(0, 0, getWidth(), getHeight());


            Bitmap imageBack = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_drop_down, null);

            trianglePaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawRoundRect(rect, 0, 0, boarder);
            // canvas.drawPath(trianglePath, trianglePaint);
            canvas.drawBitmap(imageBack, getWidth() - imageBack.getWidth(),
                    (getHeight() - imageBack.getHeight()) / 2, trianglePaint);
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelect = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isSelect = false;
                break;
            default:
                isSelect = false;
                break;
        }
        invalidate();
        return true;
    }


    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            Objects.requireNonNull(getOnItemSelectedListener()).onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            try{
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            Objects.requireNonNull(getOnItemSelectedListener()).onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
        catch (Exception e){

        }
        }

    }

}