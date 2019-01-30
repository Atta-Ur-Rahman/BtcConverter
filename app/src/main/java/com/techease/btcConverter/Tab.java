package com.techease.btcConverter;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hurmat on 10/10/2017.
 */

class Tab extends LinearLayout {
    public Tab(Context c, int drawable, String label) {
        super(c);

        TextView tv = new TextView(c);

        tv.setText(label);
        tv.setTextColor(getResources().getColor(R.color.colorLight));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setGravity(0x01);

        setOrientation(LinearLayout.VERTICAL);

        if (drawable != 0) {
            ImageView iv = new ImageView(c);
            iv.setImageResource(drawable);
            addView(iv);
        }
        addView(tv);
    }
}
