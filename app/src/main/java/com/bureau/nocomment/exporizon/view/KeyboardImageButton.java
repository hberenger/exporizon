package com.bureau.nocomment.exporizon.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class KeyboardImageButton extends ImageButton {
    public KeyboardImageButton(Context context) {
        super(context);
    }

    public KeyboardImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyboardImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 3 / 4;
        setMeasuredDimension(width, height); // make it square
    }
}
