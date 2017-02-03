package jp.ac.hal.blemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by pasuco on 2016/12/16.
 */

public class MyMapView extends View {

    private ScaleGestureDetector scaleGestureDetector;
    private float oldx = 0f;
    private float oldy = 0f;
    private Paint paint;
    private Canvas canvas;
    private Bitmap bmp = null;
    private Bitmap res = null;
    private Bitmap icon = null;
    Context context;
    float scale = 1;
    float xScale;
    float yScale;

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
//            @Override
//            public boolean onScale(ScaleGestureDetector detector) {
//                Log.e("dededededed",""+ detector.getScaleFactor());
//                float scaleB;
//                if(detector.getScaleFactor() > 1.0f){
//                    scaleB = (1 + (detector.getScaleFactor() -1)) * 2;
//                } else {
//                    scaleB = (1 - (1 - detector.getScaleFactor())) * 2;
//                }
//                scale = scaleB;
//                invalidate();
//                return true;
//            }
//
//            @Override
//            public boolean onScaleBegin(ScaleGestureDetector detector) {
//                return true;
//            }
//
//            @Override
//            public void onScaleEnd(ScaleGestureDetector detector) {
//
//            }
//        });
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(50);
    }

    protected void onSizeChanged(int w, int h, int oldx, int oldy) {
        super.onSizeChanged(w, h, oldx, oldy);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        res = BitmapFactory.decodeResource(getResources(), R.drawable.map001, options);
        xScale = res.getWidth();
        yScale = res.getHeight() ;
        Log.e("XXXX", yScale+ "");
        Log.e("YYYY", xScale + "");
        res = Bitmap.createScaledBitmap(res, w, h, true);

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.dd);
        icon = Bitmap.createScaledBitmap(icon, (int) (icon.getWidth() * 0.1), (int) (icon.getHeight() * 0.1), true);

        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmp);
        canvas.drawBitmap(res, 0, 0, paint);
        canvas.save();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    public void setMapPositon(float x, float y) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Bitmap res2 = Bitmap.createScaledBitmap(res, (int) (res.getWidth() * scale), (int) (res.getHeight() * scale), true);
        canvas.drawBitmap(res2, 0, 0, paint);
        Matrix matrix1 = new Matrix();
        matrix1.postTranslate(x, y);
        canvas.drawBitmap(icon, matrix1, paint);
        invalidate();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e){

//     //   scaleGestureDetector.onTouchEvent(e);
//        if(scale > 1) {
//            switch (e.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    oldx = e.getX();
//                    oldy = e.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    oldx = e.getX() - oldx;
//                    oldy = e.getY() - oldy;
//                    canvas.drawBitmap(res, oldx, oldy, paint);
//                    invalidate();
//                    break;
//                default:
//                    break;
//            }
//        }
//        return true;
//    }
    public void setBeacons(List<MyBeacon> list){

    }

}
