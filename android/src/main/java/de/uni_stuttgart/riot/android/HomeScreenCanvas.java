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
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.location.LocationScreen;
import de.uni_stuttgart.riot.android.things.CoffeeMachine;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * This class draws the different buttons in the home screen menu and handles all touch events.
 *
 * @author Florian
 *
 */
public class HomeScreenCanvas extends View {

    private HomeScreen homeScreen;

    private Paint paint = new Paint();

    private List<HomeScreenButton> buttonList = new ArrayList<HomeScreenButton>();

    private HomeScreenButton selectedButton;
    private RIOTDatabase database;

    private GestureDetectorCompat gestureDetector;

    private boolean buttonLongPressed;

    private int rotationDegree = 2; // good value for emulator = 3;
    private int refreshRate = 30; // to control the FPS

    private int textOffset = 20;

    private int fontSize = 30;

    private int alphaTouched = 50;
    private int alphaNormal = 255;

    // private List<HomeScreenButton> buttonList2 = new ArrayList<HomeScreenButton>();

    /**
     * Constructor that sets the buttons the first time or gets them from the database.
     *
     * @param context
     *            The main context of the App.
     */
    public HomeScreenCanvas(HomeScreen context) {
        super(context);

        homeScreen = context;
        database = DatabaseAccess.getDatabase();

        gestureDetector = new GestureDetectorCompat(homeScreen, new GestureListener());

        paint.setAntiAlias(true);
        paint.setTextSize(fontSize);

        buttonList = database.getHomeScreenButtonCoordinates(this);
        if (buttonList.size() == 0) {
            buttonList.add(new HomeScreenButton(this, 0, "Car", 100, 100, R.drawable.car));

            buttonList.add(new HomeScreenButton(this, 1, "House", 300, 100, R.drawable.house));

            buttonList.add(new HomeScreenButton(this, 2, "Coffee", 100, 300, R.drawable.coffee));

            buttonList.add(new HomeScreenButton(this, 3, "Calendar", 300, 300, R.drawable.calendar));

            buttonList.add(new HomeScreenButton(this, 4, "Settings", 100, 500, R.drawable.settings));

            buttonList.add(new HomeScreenButton(this, 5, "Location", 300, 500, R.drawable.location));

            buttonList.add(new HomeScreenButton(this, 6, "Logout", 10, 10, R.drawable.logout));

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
                canvas.drawText(button.getButtonDescription(), (button.getButtonX() + button.getButtonImage().getWidth() / 2 - (button.getButtonDescription().length() / 2) * 20), (button.getButtonY() + button.getButtonImage().getHeight() + textOffset), paint);
            }
        } else { // Shaking animation
            for (HomeScreenButton button : buttonList) {
                if (button == selectedButton) { // no shaking for the selected button that is dragged
                    canvas.drawBitmap(button.getImage(), button.getButtonX(), button.getButtonY(), button.getButtonPaint());
                } else { // shake all other buttons
                    canvas.drawBitmap(rotateBitmap(button.getButtonImage()), button.getButtonX(), button.getButtonY(), button.getButtonPaint());
                }

                canvas.drawText(button.getButtonDescription(), (button.getButtonX() + button.getButtonImage().getWidth() / 2 - (button.getButtonDescription().length() / 2) * 20), (button.getButtonY() + button.getButtonImage().getHeight() + textOffset), paint);
            }
            rotationDegree = -rotationDegree; // switch the rotation
            postInvalidateDelayed(refreshRate); // control the refresh rate
        }

    }

    /**
     * Buttons can't be dragged outside of the canvas.
     *
     * @param fingerX
     *            X-Coordinate of the finger.
     * @param fingerY
     *            Y-Coordinate of the finger.
     * @return Boolean if the user is still inside of the bounds of the canvas.
     */
    public boolean checkForBounds(int fingerX, int fingerY) {
        return (fingerX > selectedButton.getButtonImage().getWidth() / 2 && fingerX < this.getWidth() - selectedButton.getButtonImage().getWidth() / 2 && fingerY > selectedButton.getButtonImage().getHeight() / 2 && fingerY < this.getHeight() - selectedButton.getButtonImage().getHeight() / 2 - textOffset);
    }

    /**
     * Method that checks if a button collides with another button while dragging. Prevents the stacking of buttons.
     *
     * @param button
     *            A button from the buttonList which is checked against the selected button for collisions.
     * @return A collision was detected or not.
     */
    public boolean checkForCollision(HomeScreenButton button) {
        // System.out.println("DraggedbuttonX: " + selectedButton.getButtonX() + " DraggedbuttonY: " + selectedButton.getButtonY() + " ");
        // return !(selectedButton.getButtonX() + selectedButton.getButtonImage().getWidth() < button.getButtonX() ||
        // selectedButton.getButtonX() > button.getButtonX() + button.getButtonImage().getWidth());
        // Rect rec1 = new Rect(selectedButton.getButtonX(), selectedButton.getButtonX(), selectedButton.getButtonImage().getWidth(),
        // selectedButton.getButtonImage().getHeight());
        // Rect rec2 = new Rect(button.getButtonX(), button.getButtonX(), button.getButtonImage().getWidth(),
        // button.getButtonImage().getHeight());
        // if (rec1.intersect(rec2)) {
        // return false;
        // } else {
        // return true;
        // }
        return (((selectedButton.getButtonY() + selectedButton.getButtonImage().getHeight()) < (button.getButtonY())) || (selectedButton.getButtonY() > (button.getButtonY() + button.getButtonImage().getHeight())) || ((selectedButton.getButtonX() + selectedButton.getButtonImage().getWidth()) < button.getButtonX()) || (selectedButton.getButtonX() > (button.getButtonX() + button.getButtonImage().getWidth())));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
        case (MotionEvent.ACTION_DOWN):
            for (HomeScreenButton button : buttonList) {
                if (button.isCoordsOnButton(event.getX(), event.getY())) {
                    selectedButton = button;
                    selectedButton.getButtonPaint().setAlpha(alphaTouched);
                    // buttonList2.addAll(buttonList);
                    // buttonList2.remove(selectedButton);
                }
            }
            return true;
        case (MotionEvent.ACTION_MOVE):
            if (buttonLongPressed) {
                if (selectedButton != null) {
                    // for (HomeScreenButton button : buttonList) {
                    // System.out.println(selectedButton.getButtonDescription() + " " + button.getButtonDescription() + " " +
                    // checkForCollision(button));
                    if (/* checkForCollision(button) && */checkForBounds((int) event.getX(), (int) event.getY())) {
                        selectedButton.setButtonX((int) event.getX() - selectedButton.getButtonImage().getWidth() / 2);
                        selectedButton.setButtonY((int) event.getY() - selectedButton.getButtonImage().getHeight() / 2);
                    } else {
                        /*
                         * float dx = selectedButton.getButtonX() - button.getButtonX(); float dy = selectedButton.getButtonY() -
                         * button.getButtonY();
                         *
                         * float angle = (float) Math.atan2(dy, dx);
                         *
                         * float tempRadius = 1.5f;
                         *
                         * float targetX = (float) (selectedButton.getButtonX() + Math.cos(angle)) * tempRadius; float targetY = (float)
                         * (selectedButton.getButtonY() + Math.sin(angle)) * tempRadius;
                         *
                         * float ax = targetX - button.getButtonX(); float ay = targetY - button.getButtonY();
                         *
                         * selectedButton.setButtonX((int) (selectedButton.getButtonX() - ax)); selectedButton.setButtonY((int)
                         * (selectedButton.getButtonY() - ay)); System.out.println(selectedButton.getButtonX() + " " +
                         * selectedButton.getButtonY());
                         */
                        // }
                    }

                }
            }
            if (selectedButton != null && !selectedButton.isCoordsOnButton(event.getX(), event.getY())) {
                selectedButton.getButtonPaint().setAlpha(alphaNormal);
            }
            return true;
        case (MotionEvent.ACTION_UP):
            if (selectedButton != null && buttonLongPressed) {
                database.updateHomeScreenButtonCoordinates(selectedButton); // save Coordinates of the pressed button into the database
                selectedButton.getButtonPaint().setAlpha(alphaNormal);
                selectedButton = null;
                // buttonList2 = new ArrayList<HomeScreenButton>();
            } else if (selectedButton != null && selectedButton.isCoordsOnButton(event.getX(), event.getY()) && !buttonLongPressed) {
                if (selectedButton.getButtonDescription().equals("Calendar")) { // Special intent for the calendar
                    Intent calendarIntent = new Intent(Intent.ACTION_VIEW);
                    calendarIntent.setData(Uri.parse("content://com.android.calendar/time"));
                    homeScreen.startActivity(calendarIntent);
                } else if (selectedButton.getButtonDescription().equals("Settings")) { // Special intent for the settings screen
                    Intent settingsIntent = new Intent(homeScreen, SettingScreen.class);
                    settingsIntent.putExtra("pressedButton", homeScreen.getString(R.string.settings));
                    homeScreen.startActivity(settingsIntent);
                } else if (selectedButton.getButtonDescription().equals("Location")) { // Special intent for the location screen
                    Intent locationIntent = new Intent(homeScreen, LocationScreen.class);
                    locationIntent.putExtra("pressedButton", homeScreen.getString(R.string.location));
                    homeScreen.startActivity(locationIntent);
                } else if (selectedButton.getButtonDescription().equals("Coffee")) { // Special intent for the coffee machine
                    Intent coffeeIntent = new Intent(homeScreen, CoffeeMachine.class);
                    coffeeIntent.putExtra("pressedButton", selectedButton.getButtonDescription());
                    homeScreen.startActivity(coffeeIntent);
                } else if (selectedButton.getButtonDescription().equals("Logout")) { // Special intent for the logout
                    Intent logoutIntend = new Intent(homeScreen, LoginActivity.class);
                    //TODO should we send an logout request or just show the loginWindow?
                    //TODO if we delete the token no background syncronization is possible, therefor I just open the loginWindow and DO NOT logout the user
                    homeScreen.startActivity(logoutIntend);
                } else {
                    Intent newNotificationScreen = new Intent(homeScreen, NotificationScreen.class);
                    newNotificationScreen.putExtra("pressedButton", selectedButton.getButtonDescription());
                    homeScreen.startActivity(newNotificationScreen);
                }

                selectedButton.getButtonPaint().setAlpha(alphaNormal);
                selectedButton = null;
            }
            return true;
        default:
            return super.onTouchEvent(event);
        }

    }

    /**
     * This class is necessary to implement only a couple of GestureListener and not all of them.
     *
     */
    class GestureListener extends SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent event) {
            // System.out.println("longPress");
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

}
