package algo.demo.database;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "disciplines")
public class DisciplineTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lecture_hours")
    private Long lectureHours;

    public DisciplineTable() {}

    public DisciplineTable(String name, Long lectureHours) {
        this.name = name;
        this.lectureHours = lectureHours;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        this.name = name;
    }

    public Long getLectureHours() { return lectureHours; }
    public void setLectureHours(Long lectureHours) { this.lectureHours = lectureHours; }
}