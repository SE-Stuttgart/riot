package de.uni_stuttgart.riot.android;

import android.content.Intent;
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

    private HomeScreen homeScreen;

    private Bitmap backgroundBitmap;
    private Canvas canvas;
    private boolean isInitialized;
    private Paint paint = new Paint();

    private HomeScreenButton carButton;
    private HomeScreenButton houseButton;
    private HomeScreenButton coffeeMachineButton;
    private HomeScreenButton calendarButton;
    private HomeScreenButton settingsButton;

    public DrawCanvas(HomeScreen context) {
        super(context);

        homeScreen = context;

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);

        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);

    }

    private void initalizeCanvas() {
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

        canvas = new Canvas();
        canvas.setBitmap(backgroundBitmap);
        canvas.drawColor(Color.WHITE);

        carButton = new HomeScreenButton(100, 100, BitmapFactory.decodeResource(getResources(), R.drawable.car, new BitmapFactory.Options()));
        houseButton = new HomeScreenButton(300, 100, BitmapFactory.decodeResource(getResources(), R.drawable.house, new BitmapFactory.Options()));
        coffeeMachineButton = new HomeScreenButton(100, 300, BitmapFactory.decodeResource(getResources(), R.drawable.coffee, new BitmapFactory.Options()));
        calendarButton = new HomeScreenButton(300, 300, BitmapFactory.decodeResource(getResources(), R.drawable.calendar, new BitmapFactory.Options()));
        settingsButton = new HomeScreenButton(300, 600, BitmapFactory.decodeResource(getResources(), R.drawable.settings, new BitmapFactory.Options()));

        isInitialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            initalizeCanvas();

        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        canvas.drawBitmap(carButton.getImage(), carButton.getButtonX(), carButton.getButtonY(), carButton.getButtonPaint());
        canvas.drawBitmap(houseButton.getImage(), houseButton.getButtonX(), houseButton.getButtonY(), houseButton.getButtonPaint());
        canvas.drawBitmap(coffeeMachineButton.getImage(), coffeeMachineButton.getButtonX(), coffeeMachineButton.getButtonY(), coffeeMachineButton.getButtonPaint());
        canvas.drawBitmap(calendarButton.getImage(), calendarButton.getButtonX(), calendarButton.getButtonY(), calendarButton.getButtonPaint());
        canvas.drawBitmap(settingsButton.getImage(), settingsButton.getButtonX(), settingsButton.getButtonY(), settingsButton.getButtonPaint());
    }

    public boolean onTouch(View view, MotionEvent event) {

        if (isCoordsOnButton(event.getX(), event.getY(), carButton)) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                carButton.getButtonPaint().setAlpha(50);
                this.invalidate();
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                Intent carScreen = new Intent(homeScreen, NotificationScreen.class);
                carScreen.putExtra("pressedButton", "car");
                homeScreen.startActivity(carScreen);
            }
        }

        return true;
    }

    private static boolean isCoordsOnButton(float fingerX, float fingerY, HomeScreenButton button) {
        System.out.println("FingerX: " + fingerX + " FingerY: " + fingerY + " ButtonX " + button.getButtonX() + " ButtonY " + button.getButtonY());
        return (fingerX >= button.getButtonX() && fingerX <= (button.getButtonX() + button.getButtonImage().getWidth()) && fingerY >= button.getButtonY() && fingerY <= (button.getButtonY() + button.getButtonImage().getHeight()));
    }
}
