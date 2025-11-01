package algo.demo.dto;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private int x; //Максимальное значение поля: 540
    private float y;


    public int getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
