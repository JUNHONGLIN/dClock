package com.dclock.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.dclock.R;
import com.dclock.utils.UIHelper;

public class FontableTextView extends TextView {

        public FontableTextView(Context context)
        {
            super(context);
        }

        public FontableTextView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            UIHelper.setCustomFont(
                    this,
                    context,
                    attrs,
                    R.styleable.DigitalClock_Views_FontableTextView,
                    R.styleable.DigitalClock_Views_FontableTextView_font);
        }

        public FontableTextView(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
            UIHelper.setCustomFont(
                    this,
                    context,
                    attrs,
                    R.styleable.DigitalClock_Views_FontableTextView,
                    R.styleable.DigitalClock_Views_FontableTextView_font);
        }
}
