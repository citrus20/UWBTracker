package uwb.mnilsen.org.uwbtracker;

/**
 * Created by mnilsen on 9/6/17.
 */

public class Translator {
    private float zoneXOffset = 0;  //  in pixels
    private float zoneYOffset = 0;  //  in pixels
    private float tagXOffset = 0;  //  in mm
    private float tagYOffset = 0;  //  in mm
    private float scale = 1.0f;
    private float xRotation = 0.0f;
    private float yRotation = 0.0f;

    public Translator() {
    }

    public Translator(int xOffset, int yOffset, float xScale) {
        this.zoneXOffset = xOffset;
        this.zoneYOffset = yOffset;
        this.scale = xScale;
    }

    public float getZoneXOffset() {
        return zoneXOffset;
    }

    public void setZoneXOffset(float zoneXOffset) {
        this.zoneXOffset = zoneXOffset;
    }

    public float getZoneYOffset() {
        return zoneYOffset;
    }

    public void setZoneYOffset(float zoneYOffset) {
        this.zoneYOffset = zoneYOffset;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
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

    public float getTranslatedX(float x)
    {
        float f = (x  * this.scale) + this.zoneXOffset;
        return f;
    }

    public float getTranslatedY(float y)
    {
        float f = (y * this.scale)  + this.zoneYOffset;
        return f;
    }

    public float getTranslatedTagX(float x)
    {
        return this.getTranslatedX(x + this.tagXOffset);
    }

    public float getTranslatedTagY(float y)
    {
        return this.getTranslatedY(y + this.tagYOffset);
    }

    public static void main(String[] args)
    {
        Translator t = new Translator(0,0,0.5f);
        System.out.println(String.format("X=%s, f(x)=%s",10,t.getTranslatedX(10)));
        System.out.println(String.format("X=%s, f(x)=%s",100,t.getTranslatedX(100)));
        System.out.println(String.format("X=%s, f(x)=%s",1000,t.getTranslatedX(1000)));
        System.out.println(String.format("Y=%s, f(y)=%s",10,t.getTranslatedY(10)));
        System.out.println(String.format("Y=%s, f(y)=%s",100,t.getTranslatedY(100)));
        System.out.println(String.format("Y=%s, f(y)=%s",1000,t.getTranslatedY(1000)));
    }

    @Override
    public String toString() {
        return "Translator{" +
                "zoneXOffset=" + zoneXOffset +
                ", zoneYOffset=" + zoneYOffset +
                ", scale=" + scale +
                ", xRotation=" + xRotation +
                ", yRotation=" + yRotation +
                '}';
    }
}
