package core.objects;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double x;
    private long y;

    /** Для сериализации и для LabWorkDialog */
    public Coordinates() {}

    public Coordinates(double x, long y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public long getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(long y) {
        this.y = y;
    }
}
