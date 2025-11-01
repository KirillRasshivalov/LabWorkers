package algo.demo.database;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "locations")
public class LocationTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Integer x;

    @Column(name = "y", nullable = false)
    private Long y;

    @Column(name = "z")
    private float z;

    @Column(name = "name")
    private String name;

    public LocationTable() {}

    public LocationTable(Integer x, Long y, float z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }

    public Long getY() { return y; }
    public void setY(Long y) { this.y = y; }

    public float getZ() { return z; }
    public void setZ(float z) { this.z = z; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Пустая строка не может быть именем.");
        }
        this.name = name;
    }
}
