package algo.demo.repository;

import algo.demo.EnvLoader;
import algo.demo.database.*;
import algo.demo.dto.LabWork;
import algo.demo.dto.Person;
import algo.demo.enums.Color;
import algo.demo.enums.Difficulty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;


@ApplicationScoped
public class LabWorkRepository {

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

    private void setIsolationLevel() {
        em.unwrap(org.hibernate.Session.class)
                .doWork(connection -> {
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                });
    }

    public LabWorkTable addLbWork(LabWork labWork) {
        em.getTransaction().begin();
        setIsolationLevel();

        CoordinatesTable coordinatesTable = new CoordinatesTable(
                labWork.getCoordinates().getX(),
                labWork.getCoordinates().getY()
        );
        em.persist(coordinatesTable);

        LocationTable locationTable = new LocationTable(
                labWork.getAuthor().getLocation().getX(),
                labWork.getAuthor().getLocation().getY(),
                labWork.getAuthor().getLocation().getZ(),
                labWork.getAuthor().getLocation().getName()
        );
        em.persist(locationTable);

        Person person = labWork.getAuthor();
        PersonTable personTable = new PersonTable(
                person.getName(),
                Color.valueOf(person.getEyeColor().toString()),
                Color.valueOf(person.getHairColor().toString()),
                locationTable,
                person.getBirthday(),
                person.getWeight()
        );
        em.persist(personTable);

        DisciplineTable disciplineTable = new DisciplineTable(
                labWork.getDiscipline().getName(),
                labWork.getDiscipline().getLectureHours()
        );
        em.persist(disciplineTable);

        LabWorkTable labWorkTable = new LabWorkTable(
                labWork.getName(),
                coordinatesTable,
                labWork.getDescription(),
                Difficulty.valueOf(labWork.getDifficulty().toString()),
                disciplineTable,
                labWork.getMinimalPoint(),
                personTable
        );
        em.persist(labWorkTable);
        em.getTransaction().commit();
        return labWorkTable;
    }

    public LabWorkTable updateLabWorkById(Long id, LabWork labWork) {
        em.getTransaction().begin();
        setIsolationLevel();

        LabWorkTable labWorkExisting = em.find(LabWorkTable.class, id);

        CoordinatesTable existingCoordinates = labWorkExisting.getCoordinates();
        if (existingCoordinates != null) {
            existingCoordinates.setX(labWork.getCoordinates().getX());
            existingCoordinates.setY(labWork.getCoordinates().getY());
            em.merge(existingCoordinates);
        } else {
            CoordinatesTable newCoordinates = new CoordinatesTable(
                    labWork.getCoordinates().getX(),
                    labWork.getCoordinates().getY()
            );
            em.persist(newCoordinates);
            labWorkExisting.setCoordinates(newCoordinates);
        }

        DisciplineTable existingDiscipline = labWorkExisting.getDiscipline();
        if (existingDiscipline != null) {
            existingDiscipline.setName(labWork.getDiscipline().getName());
            existingDiscipline.setLectureHours(labWork.getDiscipline().getLectureHours());
            em.merge(existingDiscipline);
        } else {
            DisciplineTable newDiscipline = new DisciplineTable(
                    labWork.getDiscipline().getName(),
                    labWork.getDiscipline().getLectureHours()
            );
            em.persist(newDiscipline);
            labWorkExisting.setDiscipline(newDiscipline);
        }

        PersonTable existingAuthor = labWorkExisting.getAuthor();
        if (existingAuthor != null) {
            existingAuthor.setName(labWork.getAuthor().getName());
            existingAuthor.setEyeColor(labWork.getAuthor().getEyeColor());
            existingAuthor.setHairColor(labWork.getAuthor().getHairColor());
            existingAuthor.setBirthday(labWork.getAuthor().getBirthday());
            existingAuthor.setWeight(labWork.getAuthor().getWeight());
            em.merge(existingAuthor);

            LocationTable existingLocation = existingAuthor.getLocation();
            if (existingLocation != null) {
                existingLocation.setX(labWork.getAuthor().getLocation().getX());
                existingLocation.setY(labWork.getAuthor().getLocation().getY());
                existingLocation.setZ(labWork.getAuthor().getLocation().getZ());
                existingLocation.setName(labWork.getAuthor().getLocation().getName());
                em.merge(existingLocation);
            } else {
                LocationTable newLocation = new LocationTable(
                        labWork.getAuthor().getLocation().getX(),
                        labWork.getAuthor().getLocation().getY(),
                        labWork.getAuthor().getLocation().getZ(),
                        labWork.getAuthor().getLocation().getName()
                );
                em.persist(newLocation);
                existingAuthor.setLocation(newLocation);
            }
        } else {
            LocationTable newLocation = new LocationTable(
                    labWork.getAuthor().getLocation().getX(),
                    labWork.getAuthor().getLocation().getY(),
                    labWork.getAuthor().getLocation().getZ(),
                    labWork.getAuthor().getLocation().getName()
            );
            em.persist(newLocation);

            PersonTable newAuthor = new PersonTable(
                    labWork.getAuthor().getName(),
                    labWork.getAuthor().getEyeColor(),
                    labWork.getAuthor().getHairColor(),
                    newLocation,
                    labWork.getAuthor().getBirthday(),
                    labWork.getAuthor().getWeight()
            );
            em.persist(newAuthor);
            labWorkExisting.setAuthor(newAuthor);
        }

        labWorkExisting.setName(labWork.getName());
        labWorkExisting.setDescription(labWork.getDescription());
        labWorkExisting.setDifficulty(labWork.getDifficulty());
        labWorkExisting.setMinimalPoint(labWork.getMinimalPoint());
        LabWorkTable merged = em.merge(labWorkExisting);
        em.getTransaction().commit();
        return merged;
    }

    public void deleteLabWorkById(Long id) {
        em.getTransaction().begin();
        setIsolationLevel();
        em.remove(em.find(LabWorkTable.class, id));
        em.getTransaction().commit();
    }

    public List<LabWorkTable> getAllLabWorkTables() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LabWorkTable> cq = cb.createQuery(LabWorkTable.class);
        Root<LabWorkTable> root = cq.from(LabWorkTable.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    public LabWorkTable getLabWorkById(Long id) {
        return em.find(LabWorkTable.class, id);
    }

    public Double getMiddleValue() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LabWorkTable> cq = cb.createQuery(LabWorkTable.class);
        Root<LabWorkTable> root = cq.from(LabWorkTable.class);
        cq.select(root);
        List<LabWorkTable> list = em.createQuery(cq).getResultList();
        double answer = 0;
        for (LabWorkTable labWorkTable : list) {
            answer += Double.valueOf(labWorkTable.getMinimalPoint());
        }
        return answer / list.size();
    }

    public LabWorkTable getLabWorkByMinCoordinate() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LabWorkTable> cq = cb.createQuery(LabWorkTable.class);
        Root<LabWorkTable> root = cq.from(LabWorkTable.class);
        cq.select(root);
        List<LabWorkTable> list = em.createQuery(cq).getResultList();
        List<LabWorkTable> sortedByCoordinates = list.stream()
                .sorted((l1, l2) -> l1.compareByCoordinates(l2))
                .collect(Collectors.toList());
        return sortedByCoordinates.get(0);
    }

    public List<Integer> getUnicMinimalPoints() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LabWorkTable> cq = cb.createQuery(LabWorkTable.class);
        Root<LabWorkTable> root = cq.from(LabWorkTable.class);
        cq.select(root);
        List<LabWorkTable> list = em.createQuery(cq).getResultList();
        Map<Integer, Integer> map = new HashMap<>();
        List<Integer> ans = new ArrayList<>();
        for (LabWorkTable labWorkTable : list) {
            if (map.get(labWorkTable.getMinimalPoint()) == null) {
                ans.add(labWorkTable.getMinimalPoint());
            }
            map.put(labWorkTable.getMinimalPoint(), map.getOrDefault(labWorkTable.getMinimalPoint(), 0));
        }
        return ans;
    }

    public void decreaseDifficultyInLabWork(Long id, Integer numToReduce) {
        LabWorkTable labWorkTable = em.find(LabWorkTable.class, id);
        List<Difficulty> listOfDifficult = Arrays.asList(Difficulty.values());
        labWorkTable.setDifficulty(listOfDifficult.get(Math.max(0, listOfDifficult.indexOf(labWorkTable.getDifficulty()) - numToReduce)));
        em.merge(labWorkTable);
    }
}
