package algo.demo.database;

import algo.demo.enums.Difficulty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "lab_works")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LabWorkTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private CoordinatesTable coordinates;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "description", length = 2956)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "discipline_id", nullable = false)
    private DisciplineTable discipline;

    @Column(name = "minimal_point", nullable = false)
    private Integer minimalPoint;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private PersonTable author;

    public LabWorkTable() {
        this.creationDate = LocalDateTime.now();
    }

    public LabWorkTable(String name, CoordinatesTable coordinates, String description,
                   Difficulty difficulty, DisciplineTable discipline, Integer minimalPoint, PersonTable author) {
        this();
        this.name = name;
        this.coordinates = coordinates;
        this.description = description;
        this.difficulty = difficulty;
        this.discipline = discipline;
        this.minimalPoint = minimalPoint;
        this.author = author;
    }

    public int compareByCoordinates(LabWorkTable other) {
        if (this.coordinates == null && other.coordinates == null) {
            return 0;
        }
        if (this.coordinates == null) {
            return -1;
        }
        if (other.coordinates == null) {
            return 1;
        }

        int xCompare = Integer.compare(this.coordinates.getX(), other.coordinates.getX());
        if (xCompare != 0) {
            return xCompare;
        }
        return Float.compare(this.coordinates.getY(), other.coordinates.getY());
    }

    public Long getId() { return id; }
    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID cannot be null and must be greater than 0");
        }
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public CoordinatesTable getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesTable coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null");
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) throw new IllegalArgumentException("Creation date cannot be null");
        this.creationDate = creationDate;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description != null && description.length() > 2956) {
            throw new IllegalArgumentException("Description length cannot exceed 2956 characters");
        }
        this.description = description;
    }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) throw new IllegalArgumentException("Difficulty cannot be null");
        this.difficulty = difficulty;
    }

    public DisciplineTable getDiscipline() { return discipline; }
    public void setDiscipline(DisciplineTable discipline) {
        if (discipline == null) throw new IllegalArgumentException("Discipline cannot be null");
        this.discipline = discipline;
    }

    public Integer getMinimalPoint() { return minimalPoint; }
    public void setMinimalPoint(Integer minimalPoint) {
        if (minimalPoint == null || minimalPoint <= 0) {
            throw new IllegalArgumentException("Minimal point cannot be null and must be greater than 0");
        }
        this.minimalPoint = minimalPoint;
    }

    public PersonTable getAuthor() { return author; }
    public void setAuthor(PersonTable author) {
        if (author == null) throw new IllegalArgumentException("Author cannot be null");
        this.author = author;
    }
}
