package com.familheey.app.CustomViews.Buttons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.familheey.app.Utilities.Constants;

@SuppressLint("AppCompatCustomView")
public class RegularButton extends Button {

    public RegularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public RegularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public RegularButton(Context context) {
        super(context);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), Constants.Fonts.PATH + Constants.Fonts.REGULAR);
        setTypeface(customFont);
    }

}
