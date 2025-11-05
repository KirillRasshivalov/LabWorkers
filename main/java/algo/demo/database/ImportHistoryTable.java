package algo.demo.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "import_history")
public class ImportHistoryTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String status;

    @Column(name = "owner")
    private String user;

    @Column(name = "add_nums")
    private Long numberOfAdds;
}
