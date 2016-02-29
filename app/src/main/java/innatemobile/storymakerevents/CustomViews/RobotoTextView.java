package innatemobile.storymakerevents.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import innatemobile.storymakerevents.R;

public class RobotoTextView extends TextView {

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);

    }

    public RobotoTextView(Context context) {
        super(context);
        init(null, null);
    }

    private void init(AttributeSet attrs, Context context) {
        if (attrs!=null && context!=null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
            String fontName = a.getString(R.styleable.RobotoTextView_fontName);
            if (fontName!=null) {
                Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}