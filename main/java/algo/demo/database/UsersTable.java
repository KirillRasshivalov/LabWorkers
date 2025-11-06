package algo.demo.database;

import algo.demo.enums.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="users")
public class UsersTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String name;

    @Column(name="password")
    private String password;

    @Column
    private Roles role;

}
