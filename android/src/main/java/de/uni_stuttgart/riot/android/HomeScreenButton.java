package de.uni_stuttgart.riot.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * Class for a single button viewed on the HomeScreen.
 *
 */
public class HomeScreenButton {

    private int id;

    private int buttonX;
    private int buttonY;

    private String buttonDescription;

    private Bitmap buttonImage;
    private int imageID;

    private Paint buttonPaint;

    private float scaleFactor = 1.5f;

    /**
     * Constructor of the HomeScreenButton.
     * 
     * @param canvas
     * @param id
     * @param buttonDescription
     * @param buttonX
     * @param buttonY
     * @param imageID
     */
    public HomeScreenButton(DrawCanvas canvas, int id, String buttonDescription, int buttonX, int buttonY, int imageID) {
        this.id = id;
        this.buttonDescription = buttonDescription;
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        this.imageID = imageID;

        buttonImage = BitmapFactory.decodeResource(canvas.getResources(), this.imageID, new BitmapFactory.Options());
        buttonImage = Bitmap.createScaledBitmap(buttonImage, (int) (buttonImage.getWidth() * scaleFactor), (int) (buttonImage.getHeight() * scaleFactor), true);

        buttonPaint = new Paint();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void startIntent() {

    }

    public String getButtonDescription() {
        return buttonDescription;
    }

    public void setButtonDescription(String buttonDescription) {
        this.buttonDescription = buttonDescription;
    }

    public Paint getButtonPaint() {
        return buttonPaint;
    }

    public void setButtonPaint(Paint buttonPaint) {
        this.buttonPaint = buttonPaint;
    }

    public Bitmap getImage() {
        return buttonImage;
    }

    public int getButtonX() {
        return buttonX;
    }

    public void setButtonX(int buttonX) {
        this.buttonX = buttonX;
    }

    public int getButtonY() {
        return buttonY;
    }

    public void setButtonY(int buttonY) {
        this.buttonY = buttonY;
    }

    public Bitmap getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(Bitmap buttonImage) {
        this.buttonImage = buttonImage;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

}
