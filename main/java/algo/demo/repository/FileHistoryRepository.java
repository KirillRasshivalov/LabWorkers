package algo.demo.repository;

import algo.demo.EnvLoader;
import algo.demo.database.ImportHistoryTable;
import algo.demo.dto.FileHistory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class FileHistoryRepository {

    private EntityManagerFactory emf;
    private EntityManager em;


    @PostConstruct
    public void init() {
        EnvLoader.loadEnv();
        this.emf = Persistence.createEntityManagerFactory("labwork_pu");
        this.em = emf.createEntityManager();
    }

    @PreDestroy
    public void cleanup() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public void save(FileHistory fileHistory) {
        em.getTransaction().begin();
        ImportHistoryTable importHistoryTable = new ImportHistoryTable();
        importHistoryTable.setStatus(fileHistory.status());
        importHistoryTable.setUser(fileHistory.user());
        importHistoryTable.setNumberOfAdds(fileHistory.numOfAdds());
        em.persist(importHistoryTable);
        em.getTransaction().commit();
    }
}
