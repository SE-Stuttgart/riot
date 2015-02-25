package de.uni_stuttgart.riot.android;

import android.graphics.Bitmap;
import android.graphics.Paint;

public class HomeScreenButton {

    private int buttonX;
    private int buttonY;

    private String buttonDescription;

    private Bitmap buttonImage;
    private Paint buttonPaint;

    public HomeScreenButton(String desc, int x, int y, Bitmap image) {
        buttonDescription = desc;
        buttonX = x;
        buttonY = y;
        buttonImage = image;
        buttonPaint = new Paint();
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

}
