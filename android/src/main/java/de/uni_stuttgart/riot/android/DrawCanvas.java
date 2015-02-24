package de.uni_stuttgart.riot.android;

import android.content.Context;
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

    private Canvas canvas;
    Paint paint = new Paint();

    public DrawCanvas(Context context) {
        super(context);
        this.setOnTouchListener(this);

        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);

        canvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(Color.RED);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car, new BitmapFactory.Options()), 100, 100, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        canvas.drawColor(Color.RED);
        canvas.drawColor(Color.GREEN);
        // canvas.drawCircle(event.getX(), event.getY(), 500, paint);
        System.out.println(event.getX());
        // invalidate();
        return true;
    }
}
