package de.uni_stuttgart.riot.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;

/**
 * 
 * Draws the HomeScreen of the application.
 *
 */
public class DrawCanvas extends View implements OnTouchListener {

    private static final int ALPHA_DOWN = 50;
    private static final int ALPHA_UP = 255;
    private static final double NANO_TO_S = 1000000000.0;
    private static final double LIMIT_TO_MOVE = 1.5; //

    private double elapseTime = 0.0;
    private long startTime = 0;

    private HomeScreen homeScreen;

    private Bitmap backgroundBitmap;
    private Canvas canvas;
    private boolean isInitialized;
    // private Paint paint = new Paint();

    private HomeScreenButton carButton;
    private HomeScreenButton houseButton;
    private HomeScreenButton coffeeMachineButton;
    private HomeScreenButton calendarButton;
    private HomeScreenButton settingsButton;

    private List<HomeScreenButton> listButton = new ArrayList<HomeScreenButton>();

    private HomeScreenButton selectedButton;
    private RIOTDatabase database;

    public DrawCanvas(HomeScreen context) {
        super(context);

        homeScreen = context;
        database = DatabaseAccess.getDatabase();

        // setFocusable(true);
        // setFocusableInTouchMode(true);
        this.setOnTouchListener(this);

        // paint.setColor(Color.RED);
        // paint.setAntiAlias(true);
        // paint.setStyle(Style.FILL_AND_STROKE);

        List<HomeScreenButton> buttonList = database.getHomeScreenButtonCoordinates();

        // TODO: Hässlichen Code hier einfacher aufbaun
        if (buttonList.size() == 0) {
            carButton = new HomeScreenButton(0, "Car", 100, 100, BitmapFactory.decodeResource(getResources(), R.drawable.car, new BitmapFactory.Options()));
            listButton.add(carButton);

            houseButton = new HomeScreenButton(1, "House", 300, 100, BitmapFactory.decodeResource(getResources(), R.drawable.house, new BitmapFactory.Options()));
            listButton.add(houseButton);

            coffeeMachineButton = new HomeScreenButton(2, "CoffeeMachine", 100, 300, BitmapFactory.decodeResource(getResources(), R.drawable.coffee, new BitmapFactory.Options()));
            listButton.add(coffeeMachineButton);

            calendarButton = new HomeScreenButton(3, "Calendar", 300, 300, BitmapFactory.decodeResource(getResources(), R.drawable.calendar, new BitmapFactory.Options()));
            listButton.add(calendarButton);

            settingsButton = new HomeScreenButton(4, "Settings", 300, 600, BitmapFactory.decodeResource(getResources(), R.drawable.settings, new BitmapFactory.Options()));
            listButton.add(settingsButton);
        } else {
            carButton = new HomeScreenButton(0, "Car", buttonList.get(0).getButtonX(), buttonList.get(0).getButtonY(), BitmapFactory.decodeResource(getResources(), R.drawable.car, new BitmapFactory.Options()));
            listButton.add(carButton);

            houseButton = new HomeScreenButton(1, "House", buttonList.get(1).getButtonX(), buttonList.get(1).getButtonY(), BitmapFactory.decodeResource(getResources(), R.drawable.house, new BitmapFactory.Options()));
            listButton.add(houseButton);

            coffeeMachineButton = new HomeScreenButton(2, "CoffeeMachine", buttonList.get(2).getButtonX(), buttonList.get(2).getButtonY(), BitmapFactory.decodeResource(getResources(), R.drawable.coffee, new BitmapFactory.Options()));
            listButton.add(coffeeMachineButton);

            calendarButton = new HomeScreenButton(3, "Calendar", buttonList.get(3).getButtonX(), buttonList.get(3).getButtonY(), BitmapFactory.decodeResource(getResources(), R.drawable.calendar, new BitmapFactory.Options()));
            listButton.add(calendarButton);

            settingsButton = new HomeScreenButton(4, "Settings", buttonList.get(4).getButtonX(), buttonList.get(4).getButtonY(), BitmapFactory.decodeResource(getResources(), R.drawable.settings, new BitmapFactory.Options()));
            listButton.add(settingsButton);
        }
    }

    private void initalizeCanvas() {
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);

        canvas = new Canvas();
        canvas.setBitmap(backgroundBitmap);
        canvas.drawColor(Color.WHITE);

        isInitialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized) {
            initalizeCanvas();
        }

        canvas.drawBitmap(backgroundBitmap, 0, 0, null);

        canvas.drawBitmap(houseButton.getImage(), houseButton.getButtonX(), houseButton.getButtonY(), houseButton.getButtonPaint());
        canvas.drawBitmap(carButton.getImage(), carButton.getButtonX(), carButton.getButtonY(), carButton.getButtonPaint());
        canvas.drawBitmap(coffeeMachineButton.getImage(), coffeeMachineButton.getButtonX(), coffeeMachineButton.getButtonY(), coffeeMachineButton.getButtonPaint());
        canvas.drawBitmap(calendarButton.getImage(), calendarButton.getButtonX(), calendarButton.getButtonY(), calendarButton.getButtonPaint());
        canvas.drawBitmap(settingsButton.getImage(), settingsButton.getButtonX(), settingsButton.getButtonY(), settingsButton.getButtonPaint());
        // int xOffset = (int) (13.33f * homeScreen.getResources().getDisplayMetrics().density + 0.5f);
        // int yOffset = (int) (14.67f * homeScreen.getResources().getDisplayMetrics().density + 0.5f);
        // int xVal = carButton.getButtonX() + 10; // Point curScreenCoords
        // int yVal = carButton.getButtonY() + carButton.getButtonY() + 5; // Point curScreenCoords
        // canvas.drawText(carButton.getButtonDescription(), xVal + xOffset, yVal + yOffset, paint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTime = System.nanoTime();
            for (HomeScreenButton button : listButton) {
                if (isCoordsOnButton(event.getX(), event.getY(), button)) {
                    selectedButton = button;
                    selectedButton.getButtonPaint().setAlpha(ALPHA_DOWN);
                }
            }

        }

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (selectedButton != null) {
                // save Coordinates of the pressed button into the database
                database.updateHomeScreenButtonCoordinates(selectedButton);

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

                selectedButton.getButtonPaint().setAlpha(ALPHA_UP);
            }

        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            elapseTime = (double) ((System.nanoTime() - startTime) / NANO_TO_S);
            if (selectedButton != null && elapseTime > LIMIT_TO_MOVE) {
                selectedButton.setButtonX((int) event.getX());
                selectedButton.setButtonY((int) event.getY());
            }
        }

        invalidate();
        return true;
    }

    /**
     * 
     * Proof if a button was tipped or not.
     * 
     * @param fingerX
     *            Represents the x coordinate of the user input.
     * @param fingerY
     *            Represents the y coordinate of the user input.
     * @param button
     *            Button object.
     * @return if there is a button (true) or not (false)
     */
    private static boolean isCoordsOnButton(float fingerX, float fingerY, HomeScreenButton button) {
        // System.out.println("FingerX: " + fingerX + " FingerY: " + fingerY + " ButtonX " + button.getButtonX() + " ButtonY " +
        // button.getButtonY());
        return (fingerX >= button.getButtonX() && fingerX <= (button.getButtonX() + button.getButtonImage().getWidth()) && fingerY >= button.getButtonY() && fingerY <= (button.getButtonY() + button.getButtonImage().getHeight()));
    }
}
