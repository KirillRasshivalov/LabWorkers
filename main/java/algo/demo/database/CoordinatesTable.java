package algo.demo.database;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "coordinates")
public class CoordinatesTable implements Comparable<CoordinatesTable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private float y;

    public CoordinatesTable() {}

    public CoordinatesTable(int x, float y) {
        this.x = x;
        this.y = y;
    }

    public CoordinatesTable(Long id, int x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(CoordinatesTable other) {
        int xCompare = Double.compare(this.x, other.x);
        if (xCompare != 0) {
            return xCompare;
        }
        return Double.compare(this.y, other.y);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getX() { return x; }
    public void setX(int x) {
        if (x > 540) throw new IllegalArgumentException("X не можем быть больше 550");
        this.x = x;
    }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
}
