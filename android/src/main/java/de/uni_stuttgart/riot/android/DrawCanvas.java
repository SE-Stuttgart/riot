package de.uni_stuttgart.riot.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import de.enpro.android.riot.R;

//http://www.milestone-blog.de/android-development/einfaches-zeichnen-canvas/

public class DrawCanvas extends View implements OnTouchListener {
    Bitmap bitmap;
    Canvas bitmapCanvas;
    boolean isInitialized;
    Paint paint = new Paint();

    public DrawCanvas(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);

        isInitialized = false;
    }

    private void init() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

        bitmapCanvas = new Canvas();
        bitmapCanvas.setBitmap(bitmap);
        bitmapCanvas.drawColor(Color.BLACK);

        isInitialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            init();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car, new BitmapFactory.Options()), 100, 100, null);
    }

    public boolean onTouch(View view, MotionEvent event) {
        bitmapCanvas.drawCircle(event.getX(), event.getY(), 5, paint);

        invalidate();
        return true;
    }

}
