package com.rh.heji.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Date: 2021/3/2
 * @author: 锅得铁
 * # 圆 点
 */
public class CircleView extends View {
    private int b = Color.RED;

    private Paint paint = null;

    public CircleView(Context paramContext) {
        super(paramContext);
    }

    public CircleView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public CircleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public CircleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (this.paint == null) {
            this.paint = new Paint(1);
            this.paint.setStyle(Paint.Style.FILL);
        }
        this.paint.setColor(this.b);
        paramCanvas.drawCircle((getWidth() / 2), (getHeight() / 2), (Math.min(getWidth(), getHeight()) / 2), this.paint);
    }

    public void setBackground(Drawable paramDrawable) {
        super.setBackground(null);
    }

    public void setColor(int color) {
        this.b = color;
        invalidate();
    }
}
