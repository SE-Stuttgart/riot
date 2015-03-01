package de.uni_stuttgart.riot.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;

public class DrawCanvas extends View {

    private HomeScreen homeScreen;

    private Paint paint = new Paint();

    private List<HomeScreenButton> buttonList = new ArrayList<HomeScreenButton>();

    private HomeScreenButton selectedButton;
    private RIOTDatabase database;

    private GestureDetectorCompat gestureDetector;

    private boolean buttonLongPressed;

    private int rotationDegree = 2; // good value for emulator = 3;
    private int refreshRate = 20;

    public DrawCanvas(HomeScreen context) {
        super(context);

        homeScreen = context;
        database = DatabaseAccess.getDatabase();

        gestureDetector = new GestureDetectorCompat(homeScreen, new GestureListener());

        paint.setAntiAlias(true);
        paint.setTextSize(30);

        buttonList = database.getHomeScreenButtonCoordinates(this);
        if (buttonList.size() == 0) {
            buttonList.add(new HomeScreenButton(this, 0, "Car", 100, 100, R.drawable.car));

            buttonList.add(new HomeScreenButton(this, 1, "House", 300, 100, R.drawable.house));

            buttonList.add(new HomeScreenButton(this, 2, "Coffee", 100, 300, R.drawable.coffee));

            buttonList.add(new HomeScreenButton(this, 3, "Calendar", 300, 300, R.drawable.calendar));

            buttonList.add(new HomeScreenButton(this, 4, "Settings", 100, 500, R.drawable.settings));

            buttonList.add(new HomeScreenButton(this, 5, "Location", 300, 500, R.drawable.location));

            buttonList.add(new HomeScreenButton(this, 6, "Logout", 100, 700, R.drawable.logout));

            for (HomeScreenButton button : buttonList) {
                database.updateHomeScreenButtonCoordinates(button);
            }
        }

    }

    /**
     * Rotate a given bitmap with a defined degree value.
     * 
     * @param bitmap
     *            The bitmap that will be rotated
     * @return The new rotated bitmap
     */
    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();

        matrix.setRotate(rotationDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE); // background color

        if (!buttonLongPressed) {
            for (HomeScreenButton button : buttonList) {
                canvas.drawBitmap(button.getImage(), button.getButtonX(), button.getButtonY(), button.getButtonPaint());
                canvas.drawText(button.getButtonDescription(), (button.getButtonX() + button.getButtonImage().getWidth() / 2 - (button.getButtonDescription().length() / 2) * 20), (button.getButtonY() + button.getButtonImage().getHeight() + 10), paint);
            }
        } else { // Shaking animation
            for (HomeScreenButton button : buttonList) {
                if (button == selectedButton) { // no shaking for the selected button that is dragged
                    canvas.drawBitmap(button.getImage(), button.getButtonX(), button.getButtonY(), button.getButtonPaint());
                } else { // shake all other buttons
                    canvas.drawBitmap(rotateBitmap(button.getButtonImage()), button.getButtonX(), button.getButtonY(), button.getButtonPaint());
                }

                canvas.drawText(button.getButtonDescription(), (button.getButtonX() + button.getButtonImage().getWidth() / 2 - (button.getButtonDescription().length() / 2) * 20), (button.getButtonY() + button.getButtonImage().getHeight() + 35), paint);
            }
            rotationDegree = -rotationDegree; // switch the rotation
            postInvalidateDelayed(refreshRate); // control the refresh rate
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
        case (MotionEvent.ACTION_DOWN):
            for (HomeScreenButton button : buttonList) {
                if (isCoordsOnButton(event.getX(), event.getY(), button)) {
                    selectedButton = button;
                    selectedButton.getButtonPaint().setAlpha(50);
                }
            }
            return true;
        case (MotionEvent.ACTION_MOVE):
            if (buttonLongPressed) {
                if (selectedButton != null) {
                    selectedButton.setButtonX((int) event.getX() - selectedButton.getButtonImage().getWidth() / 2);
                    selectedButton.setButtonY((int) event.getY() - selectedButton.getButtonImage().getHeight() / 2);
                }
            }
            if (selectedButton != null && !isCoordsOnButton(event.getX(), event.getY(), selectedButton)) {
                selectedButton.getButtonPaint().setAlpha(255);
            }
            return true;
        case (MotionEvent.ACTION_UP):
            if (selectedButton != null && buttonLongPressed) {
                database.updateHomeScreenButtonCoordinates(selectedButton); // save Coordinates of the pressed button into the database
                selectedButton.getButtonPaint().setAlpha(255);
                selectedButton = null;
            } else if (selectedButton != null && isCoordsOnButton(event.getX(), event.getY(), selectedButton) && !buttonLongPressed) {
                Intent newNotificationScreen = new Intent(homeScreen, NotificationScreen.class);

                if (selectedButton.getButtonDescription().equals("Calendar")) { // Special intent for the calendar
                    Intent calendarIntent = new Intent(Intent.ACTION_VIEW);
                    calendarIntent.setData(Uri.parse("content://com.android.calendar/time"));
                    homeScreen.startActivity(calendarIntent);
                } else if (selectedButton.getButtonDescription().equals("Settings")) { // Special intent for the settings screen
                    Intent settingsIntent = new Intent(homeScreen, SettingScreen.class);
                    settingsIntent.putExtra("pressedButton", homeScreen.getString(R.string.settings));
                    homeScreen.startActivity(settingsIntent);
                } else {
                    newNotificationScreen.putExtra("pressedButton", selectedButton.getButtonDescription());
                    homeScreen.startActivity(newNotificationScreen);
                }

                selectedButton.getButtonPaint().setAlpha(255);
                selectedButton = null;
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * This class is necessary to implement only a couple of GestureListener and not all of them.
     * 
     */
    class GestureListener extends SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent event) {
            System.out.println("longPress");
            if (selectedButton != null) {
                buttonLongPressed = true;
                invalidate();
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            buttonLongPressed = false;
            return false;
        }

    }

    /**
     * This method detects if a finger pressed a button or not.
     * 
     * @param fingerX
     *            The X-Position of the finger on the screen
     * @param fingerY
     *            The Y-Position of the finger on the screen
     * @param button
     *            The button that is checked against the finger position *
     */
    private static boolean isCoordsOnButton(float fingerX, float fingerY, HomeScreenButton button) {
        // System.out.println("FingerX: " + fingerX + " FingerY: " + fingerY + " ButtonX " + button.getButtonX() + " ButtonY " +
        // button.getButtonY());
        return (fingerX >= button.getButtonX() && fingerX <= (button.getButtonX() + button.getButtonImage().getWidth()) && fingerY >= button.getButtonY() && fingerY <= (button.getButtonY() + button.getButtonImage().getHeight()));
    }

}
