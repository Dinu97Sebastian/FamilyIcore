package com.familheey.app.CustomViews.TextViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.familheey.app.Utilities.Constants;

@SuppressLint("AppCompatCustomView")
public class BoldTextView extends TextView {

    private static Typeface customFont;

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public BoldTextView(Context context) {
        super(context);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        if (customFont == null)
            customFont = Typeface.createFromAsset(context.getAssets(), Constants.Fonts.PATH + Constants.Fonts.BOLD);
        setTypeface(customFont);
    }

}