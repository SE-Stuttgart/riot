package de.uni_stuttgart.riot.android.things;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CoffeeMachineButton {

    private boolean isPressed;

    private Bitmap normalTexture;
    private Bitmap pressedTexture;

    private int xPosition;
    private int yPosition;

    public CoffeeMachineButton(CoffeeMachine coffeeMachine, int x, int y, int normalID, int pressedID) {
        xPosition = x;
        yPosition = y;

        normalTexture = BitmapFactory.decodeResource(coffeeMachine.getResources(), normalID, new BitmapFactory.Options());
        pressedTexture = BitmapFactory.decodeResource(coffeeMachine.getResources(), pressedID, new BitmapFactory.Options());
    }

    public boolean isCoordsOnButton(float fingerX, float fingerY) {

        return (fingerX >= xPosition && fingerX <= (xPosition + normalTexture.getWidth()) && fingerY >= yPosition && fingerY <= (yPosition + normalTexture.getHeight()));
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public Bitmap getNormalTexture() {
        return normalTexture;
    }

    public void setNormalTexture(Bitmap normalTexture) {
        this.normalTexture = normalTexture;
    }

    public Bitmap getPressedTexture() {
        return pressedTexture;
    }

    public void setPressedTexture(Bitmap pressedTexture) {
        this.pressedTexture = pressedTexture;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

}
