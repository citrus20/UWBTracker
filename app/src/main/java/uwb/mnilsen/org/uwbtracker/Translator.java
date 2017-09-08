package uwb.mnilsen.org.uwbtracker;

/**
 * Created by mnilsen on 9/6/17.
 */

public class Translator {
    private int xOffset = 0;
    private int yOffset = 0;
    private float xScale = 0.0f;
    private float yScale = 0.0f;
    private float xRotation = 0.0f;
    private float yRotation = 0.0f;

    public Translator() {
    }

    public Translator(int xOffset, int yOffset, float xScale, float yScale) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public int getxOffset() {
        return xOffset;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public float getxScale() {
        return xScale;
    }

    public void setxScale(float xScale) {
        this.xScale = xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public void setyScale(float yScale) {
        this.yScale = yScale;
    }

    public float getxRotation() {
        return xRotation;
    }

    public void setxRotation(float xRotation) {
        this.xRotation = xRotation;
    }

    public float getyRotation() {
        return yRotation;
    }

    public void setyRotation(float yRotation) {
        this.yRotation = yRotation;
    }

    public int getTranslatedX(int x)
    {
        float f = x * this.xScale;
        return Math.round(f) + this.xOffset;
    }

    public int getTranslatedY(int y)
    {
        float f = y * this.yScale;
        return Math.round(f) + this.yOffset;
    }
}
