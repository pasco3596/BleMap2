package jp.ac.hal.blemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pasuco on 2016/12/16.
 */

public class MyMapView extends View {

    private float oldx = 0f;
    private float oldy = 0f;
    private Paint paint;
    private Canvas canvas;
    private Bitmap bmp = null;
    private Bitmap res = null;
    private Bitmap icon = null;
    int w;
    int h;
    float xScale;
    float yScale;
    float halfX;
    float halfY;
    List<MyBeacon> list;
    List<Item> items;
    Bitmap icon2;
    Context context;

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(1);
        list = new ArrayList<>();
        items = new ArrayList<>();

        icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.beacon);
        icon2 = Bitmap.createScaledBitmap(icon2, (int) (icon2.getWidth() * 0.4), (int) (icon2.getHeight() * 0.4), true);
    }

    protected void onSizeChanged(int w, int h, int oldx, int oldy) {
        super.onSizeChanged(w, h, oldx, oldy);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        res = BitmapFactory.decodeResource(getResources(), R.drawable.map001, options);
        res = Bitmap.createScaledBitmap(res, w, h, true);

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.dd);
        icon = Bitmap.createScaledBitmap(icon, (int) (icon.getWidth() * 0.1), (int) (icon.getHeight() * 0.1), true);

        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bmp);
       // canvas.drawBitmap(res, 0, 0, paint);
        this.w = w;
        this.h = h;
        setLine();

        xScale = res.getWidth() / 5;
        yScale = res.getHeight() / 5;
        halfX = w / 2;
        halfY = h / 2;

        setBeacons();
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    public void setMapPosition(float x, float y) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
       // canvas.drawBitmap(res, 0, 0, paint);
        Matrix matrix1 = new Matrix();
        float xp = (-x * xScale);
        float yp = (y * yScale);
        matrix1.postTranslate(xp, yp);
        canvas.drawBitmap(icon, matrix1, paint);


        Paint paint1 = new Paint();
        paint1.setColor(Color.BLUE);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        paint1.setStrokeJoin(Paint.Join.ROUND);
        paint1.setTextSize(80);
        paint1.setStrokeWidth(2);
        canvas.drawText("X   " + xp + "   Y   " + yp, 0, h -100, paint1);


        setBeacons();
        setLine();
        invalidate();
        Log.e("XXX", x + "");
        Log.e("YYY", y + "");
    }

    public void setBeacons() {
        this.list = MainActivity.allBeacons;
        for (MyBeacon myBeacon : list) {
            Matrix matrix = new Matrix();
            float x =  myBeacon.getX() * xScale;
            float y =  myBeacon.getY() * yScale;
            matrix.postTranslate(x, y);

            canvas.drawBitmap(icon2, matrix, paint);
        }
    }

    public void setLine() {
        for (int i = 0; i < w; ) {
            canvas.drawLine(i, 0, i, h, paint);
            i += w / 5;
        }
        for (int i = 0; i < h; ) {
            canvas.drawLine(0, i, w, i, paint);
            i += h / 5;
        }
    }

    public void mapTarget(String name) {
        items = MainActivity.items;
        for (Item item : items) {
            Log.e("item", item.getName());
            if (item.getName().equals(name)) {
                Matrix matrix = new Matrix();
                matrix.postTranslate((item.getX() * xScale), (item.getY() * yScale));
                Bitmap icon3 = Bitmap.createScaledBitmap(icon2, icon2.getWidth() * 2, icon2.getHeight() * 2, true);
                canvas.drawBitmap(icon3, matrix, paint);
                Log.e("LOGLOG", name);
            }
            invalidate();
        }
    }
}