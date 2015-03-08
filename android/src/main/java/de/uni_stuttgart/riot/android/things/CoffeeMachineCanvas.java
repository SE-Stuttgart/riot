package de.uni_stuttgart.riot.android.things;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import de.uni_stuttgart.riot.android.R;

//CHECKSTYLE:OFF TODO 
public class CoffeeMachineCanvas extends View {

    private Paint paint = new Paint();

    private CoffeeMachine coffeeMachine;

    private CoffeeMachineButton onOff;

    private GestureDetectorCompat gestureDetector;

    public CoffeeMachineCanvas(CoffeeMachine context) {
        super(context);
        coffeeMachine = context;

        paint.setAntiAlias(true);
        // gestureDetector = new GestureDetectorCompat(this, context);

        onOff = new CoffeeMachineButton(coffeeMachine, 200, 200, R.drawable.off_button, R.drawable.on_button);

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (onOff.isPressed()) {
            canvas.drawBitmap(onOff.getPressedTexture(), onOff.getXPosition(), onOff.getYPosition(), paint);
        } else {
            canvas.drawBitmap(onOff.getNormalTexture(), onOff.getXPosition(), onOff.getYPosition(), paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (onOff.isCoordsOnButton(x, y)) {
                onOff.setPressed(!onOff.isPressed());
            }
        }

        invalidate();

        return super.onTouchEvent(event);
    }

}
