package com.ravibasil.thebigdream.barcon.barcode.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class Pointer extends View
{
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float x;
    private float y;
    private int r;


    public Pointer(Context context, float xp, float yp, int r) {
        super(context);
        // TODO Auto-generated constructor stub
        mPaint.setColor(0xFFFFFFFF);
        this.x = xp;
        this.y = yp;
        this.r = r;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, r, mPaint);
    }



}
