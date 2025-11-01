package algo.demo.repository;

import algo.demo.EnvLoader;
import algo.demo.database.CoordinatesTable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CoordinatesRepository {

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

    public CoordinatesTable minValueOfCoordinates() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CoordinatesTable> cq = cb.createQuery(CoordinatesTable.class);
        Root<CoordinatesTable> root = cq.from(CoordinatesTable.class);
        cq.select(root);
        List<CoordinatesTable> list = em.createQuery(cq).getResultList().stream().sorted().collect(Collectors.toList());
        return list.get(0);
    }
}
