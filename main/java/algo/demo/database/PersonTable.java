package algo.demo.database;

import algo.demo.enums.Color;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "persons")
public class PersonTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    private Color hairColor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private LocationTable location;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    public PersonTable() {}

    public PersonTable(String name, Color eyeColor, Color hairColor, LocationTable location,
                  LocalDateTime birthday, Integer weight) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.birthday = birthday;
        this.weight = weight;
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

    public Color getEyeColor() { return eyeColor; }
    public void setEyeColor(Color eyeColor) {
        if (eyeColor == null) throw new IllegalArgumentException("Глаза должны иметь цвет.");
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() { return hairColor; }
    public void setHairColor(Color hairColor) { this.hairColor = hairColor; }

    public LocationTable getLocation() { return location; }
    public void setLocation(LocationTable location) { this.location = location; }

    public LocalDateTime getBirthday() { return birthday; }
    public void setBirthday(LocalDateTime birthday) { this.birthday = birthday; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Вес должен быть больше 0.");
        }
        this.weight = weight;
    }
}
