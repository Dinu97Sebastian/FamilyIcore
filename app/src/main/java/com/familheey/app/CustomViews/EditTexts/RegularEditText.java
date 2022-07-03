package com.familheey.app.CustomViews.EditTexts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.familheey.app.Utilities.Constants;

@SuppressLint("AppCompatCustomView")
public class RegularEditText extends EditText {
    private static Typeface customFont;

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public RegularEditText(Context context) {
        super(context);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        if (customFont == null)
            customFont = Typeface.createFromAsset(context.getAssets(), Constants.Fonts.PATH + Constants.Fonts.REGULAR);
        setTypeface(customFont);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        if (customFont == null)
            customFont = Typeface.createFromAsset(getContext().getAssets(), Constants.Fonts.PATH + Constants.Fonts.REGULAR);
        super.setTypeface(tf);
    }
}
