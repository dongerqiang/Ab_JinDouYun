package com.qdigo.jindouyun.utils;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by jpj on 2017-08-01.
 */

public class RollingTextView extends android.support.v7.widget.AppCompatTextView {
    public RollingTextView(Context context) {
        super(context);
    }

    public RollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RollingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
