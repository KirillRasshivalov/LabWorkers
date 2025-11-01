package algo.demo.dto;

import java.io.Serializable;

public class Discipline implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Long lectureHours; //Поле может быть null

    public String getName() {
        return name;
    }

    public Long getLectureHours() {
        return lectureHours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLectureHours(Long lectureHours) {
        this.lectureHours = lectureHours;
    }
}