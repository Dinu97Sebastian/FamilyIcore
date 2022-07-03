package com.familheey.app.CustomViews.TextViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.familheey.app.Utilities.Constants;

@SuppressLint("AppCompatCustomView")
public class SemiBoldTextView extends TextView {

    public SemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public SemiBoldTextView(Context context) {
        super(context);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), Constants.Fonts.PATH + Constants.Fonts.SEMIBOLD);
        setTypeface(customFont);
    }

}