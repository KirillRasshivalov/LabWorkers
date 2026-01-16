package algo.demo.database;

import algo.demo.enums.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@Setter
@Table(name="users")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UsersTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String name;

    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private Roles role;

    @PrePersist
    protected void onCreate() {
        if (role == null) {
            role = Roles.USER;
        }
    }

}
