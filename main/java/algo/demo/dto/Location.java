package algo.demo.dto;

import java.io.Serializable;

public class Location implements Serializable {
    private Integer x; //Поле не может быть null
    private Long y; //Поле не может быть null
    private float z;
    private String name; //Строка не может быть пустой, Поле может быть null

    public Integer getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setName(String name) {
        this.name = name;
    }
}