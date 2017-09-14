package uwb.mnilsen.org.uwbtracker;

import java.util.Arrays;

/**
 * Created by mnilsen on 9/14/17.
 */

public class AnchorConfig {
    private float[] anchor0Position;
    private float[] anchor1Position;
    private float[] anchor2Position;

    private float a0a1Distance;
    private float a0a2Distance;
    private float a1a2Distance;

    private float[] circleCenter;
    private float circleRadius;

    public AnchorConfig() {
    }

    public AnchorConfig(float[] anchor0Position, float[] anchor1Position, float[] anchor2Position, float a0a1Distance, float a0a2Distance, float a1a2Distance, float[] circleCenter, float circleRadius) {
        this.anchor0Position = anchor0Position;
        this.anchor1Position = anchor1Position;
        this.anchor2Position = anchor2Position;
        this.a0a1Distance = a0a1Distance;
        this.a0a2Distance = a0a2Distance;
        this.a1a2Distance = a1a2Distance;
        this.circleCenter = circleCenter;
        this.circleRadius = circleRadius;
    }

    public float[] getAnchor0Position() {
        return anchor0Position;
    }

    public void setAnchor0Position(float[] anchor0Position) {
        this.anchor0Position = anchor0Position;
    }

    public float[] getAnchor1Position() {
        return anchor1Position;
    }

    public void setAnchor1Position(float[] anchor1Position) {
        this.anchor1Position = anchor1Position;
    }

    public float[] getAnchor2Position() {
        return anchor2Position;
    }

    public void setAnchor2Position(float[] anchor2Position) {
        this.anchor2Position = anchor2Position;
    }

    public float getA0a1Distance() {
        return a0a1Distance;
    }

    public void setA0a1Distance(float a0a1Distance) {
        this.a0a1Distance = a0a1Distance;
    }

    public float getA0a2Distance() {
        return a0a2Distance;
    }

    public void setA0a2Distance(float a0a2Distance) {
        this.a0a2Distance = a0a2Distance;
    }

    public float getA1a2Distance() {
        return a1a2Distance;
    }

    public void setA1a2Distance(float a1a2Distance) {
        this.a1a2Distance = a1a2Distance;
    }

    public float[] getCircleCenter() {
        return circleCenter;
    }

    public void setCircleCenter(float[] circleCenter) {
        this.circleCenter = circleCenter;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    @Override
    public String toString() {
        return "AnchorConfig{" +
                "anchor0Position=" + Arrays.toString(anchor0Position) +
                ", anchor1Position=" + Arrays.toString(anchor1Position) +
                ", anchor2Position=" + Arrays.toString(anchor2Position) +
                ", a0a1Distance=" + a0a1Distance +
                ", a0a2Distance=" + a0a2Distance +
                ", a1a2Distance=" + a1a2Distance +
                ", circleCenter=" + Arrays.toString(circleCenter) +
                ", circleRadius=" + circleRadius +
                '}';
    }
}
