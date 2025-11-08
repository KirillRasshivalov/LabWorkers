package algo.demo.repository;

import algo.demo.EnvLoader;
import algo.demo.PasswordHasher;
import algo.demo.database.UsersTable;
import algo.demo.enums.Roles;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;


@ApplicationScoped
public class UserRepository {

    private final PasswordHasher passwordHasher = new PasswordHasher();

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

    public Boolean isNameInUse(String name) {
        Long count = em.createQuery("SELECT COUNT(u) FROM UsersTable u WHERE u.name = :name",
                        Long.class)
                        .setParameter("name", name)
                        .getSingleResult();
        return count > 0;
    }

    public Boolean isUserExist(String name, String password) {
        try {
            UsersTable userTable = em.createQuery("SELECT u FROM UsersTable u WHERE u.name = :name",
                    UsersTable.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return passwordHasher.verify(password, userTable.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    public void registerUser(String name, String password) {
        em.getTransaction().begin();
        UsersTable userTable = new UsersTable();
        userTable.setName(name);
        userTable.setPassword(password);
        em.persist(userTable);
        em.getTransaction().commit();
    }

    public void setRole(String name, Roles role) {
        em.getTransaction().begin();
        UsersTable userTable = em.createQuery("SELECT u FROM UsersTable u WHERE u.name = :name",
                UsersTable.class)
                .setParameter("name", name)
                .getSingleResult();
        userTable.setRole(role);
        em.merge(userTable);
        em.getTransaction().commit();
    }

    public Roles getRole(String name) {
        Roles role = em.createQuery("SELECT u.role FROM UsersTable u WHERE u.name = :name",
                Roles.class)
                .setParameter("name", name)
                .getSingleResult();
        return role == null ? Roles.USER : role;
    }

    public List<UsersTable> getUsersOfSystem() {
        List<UsersTable> usersTables = em.createQuery("SELECT u FROM UsersTable u WHERE u.role = :roles",
                UsersTable.class)
                .setParameter("roles", Roles.USER)
                .getResultList();
        return usersTables;
    }
}
