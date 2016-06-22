package com.lecturenotes.emiliyan.lecturenotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Emiliyan on 3/11/2016.
 *
 *
 */


public class DrawingView extends ImageView {
    private static final int DEFAULT_DRAWING_RESOLUTION = 512;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private boolean eraserSelected = false;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private Context context;
    public DrawingView(Context context) {
        super(context);
        setUpDrawingView(context);
    }
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDrawingView(context);
    }
    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpDrawingView(context);
    }



    private void setUpDrawingView(Context context){
        this.context = context;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(ContextCompat.getColor(context, R.color.customDarkest));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        setDrawingCacheEnabled(true);//enable saving the drawing
    }


    public void setStrokeWidth(int width){
        mPaint.setStrokeWidth(width);
    }


    public void setStrokeColor(int color){
        if(!eraserSelected) mPaint.setColor(color);

    }
    public void eraserSelected(boolean eraserSelected){
        this.eraserSelected = eraserSelected;
    }



    public Bitmap saveDrawing()
    {
        return getDrawingCache();//get drawing from cache

//        if(setResolution){
//            return ThumbnailUtils.extractThumbnail(imageBitmap, screenWidth, screenWidth);//reduce resolution of drawing
//        }
//        return ThumbnailUtils.extractThumbnail(imageBitmap, DEFAULT_DRAWING_RESOLUTION, DEFAULT_DRAWING_RESOLUTION);//reduce resolution of drawing

    }


    //methods below are copied from
    //http://stackoverflow.com/questions/16650419/draw-in-canvas-by-finger-android
    // author: Raghunandan   date accessed: 11 March 2016
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }



    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}
