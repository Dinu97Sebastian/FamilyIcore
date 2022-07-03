package com.familheey.app.Utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.familheey.app.R;

/**
 * An extension of the default Android EditText widget that includes a clear button on the right
 * side. This button only displays when there is text in the field and disappears when the field
 * is blank. Can also set a listener to be notified when the widgets clear button is pressed.
 */
public class DeletableEditText extends AppCompatEditText {

    public static int BUTTON_SIZE_SMALL = 3;
    public static int BUTTON_SIZE_NORMAL = 2;
    public static int BUTTON_SIZE_BIG = 1;
    private boolean clearButtonIsShowing;
    private OnTextClearedListener textClearedListener;
    private int buttonSize;

    public DeletableEditText(Context context) {
        super(context);
        init(context, DeletableEditText.BUTTON_SIZE_NORMAL);
    }

    public DeletableEditText(Context context, int buttonSize) {
        super(context);
        init(context, buttonSize);
    }

    public DeletableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, DeletableEditText.BUTTON_SIZE_NORMAL);
    }

    public DeletableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, DeletableEditText.BUTTON_SIZE_NORMAL);
    }

    /**
     * Set a listener to be notified when the field is cleared using the clear button;
     */
    public void setOnTextClearedListener(OnTextClearedListener textClearedListener) {
        this.textClearedListener = textClearedListener;
    }

    /**
     * Use the BUTTON_SIZE_X constants when setting this.
     */
    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
        refreshClearButtonView();
    }

    private void refreshClearButtonView() {
        Drawable clearButton = getResources().getDrawable(R.drawable.search_clear_black);
        clearButton.setBounds(0, 0, clearButton.getIntrinsicWidth() / buttonSize, clearButton.getIntrinsicHeight() / buttonSize);

        if (clearButtonIsShowing) {
            DeletableEditText.this.setCompoundDrawables(null, null, clearButton, null);
        } else {
            DeletableEditText.this.setCompoundDrawables(null, null, null, null);
        }
    }

    private void init(final Context context, int startingButtonSize) {
        clearButtonIsShowing = false;
        buttonSize = startingButtonSize;

        //create Drawable X icon and place it
        final Drawable clearButton = getResources().getDrawable(R.drawable.search_clear_black);
        clearButton.setBounds(0, 0, clearButton.getIntrinsicWidth() / buttonSize, clearButton.getIntrinsicHeight() / buttonSize);

        //set up touch listeners to the text field will clear when the X is clicked
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (clearButtonIsShowing) {
                    Drawable co = DeletableEditText.this.getCompoundDrawables()[2];

                    if (motionEvent.getX() > view.getMeasuredWidth() - view.getPaddingRight() - co.getIntrinsicWidth()) {
                        if (null != textClearedListener) {
                            textClearedListener.beforeTextCleared(DeletableEditText.this.getText().toString());
                        }

                        DeletableEditText.this.setText("");

                        if (null != textClearedListener) {
                            textClearedListener.afterTextCleared();
                        }

                        return true;
                    }
                }

                return false;
            }
        });

        //set up listener to hide the X when the text field is empty
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                clearButtonIsShowing = DeletableEditText.this.getText().length() > 0;
                refreshClearButtonView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
    }

    /**
     * Listener for a DeletableEditText that notifies when the clear button has been pressed.
     */
    interface OnTextClearedListener {

        /**
         * Called after the clear button is pressed but right before the text field is cleared
         */
        void beforeTextCleared(String textInEditText);

        /**
         * Called immediately after the text has been cleared as a result of a clear button press
         */
        void afterTextCleared();
    }
}