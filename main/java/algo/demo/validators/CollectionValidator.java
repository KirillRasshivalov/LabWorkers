package algo.demo.validators;

import algo.demo.dto.*;
import algo.demo.exceptions.ParsingException;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class CollectionValidator implements CollectionsValidator {

    @Override
    public void validateLabWork(LabWork labWork) throws ParsingException {
        if (labWork.getName() == null || labWork.getName().trim().isEmpty()) {
            throw new ParsingException("Поле 'name' не может быть null или пустым");
        }

        if (labWork.getCoordinates() == null) {
            throw new ParsingException("Поле 'coordinates' не может быть null");
        }

        if (labWork.getDescription() != null && labWork.getDescription().length() > 2956) {
            throw new ParsingException("Длина поля 'description' не должна превышать 2956 символов");
        }

        if (labWork.getDifficulty() == null) {
            throw new ParsingException("Поле 'difficulty' не может быть null");
        }

        if (labWork.getDiscipline() == null) {
            throw new ParsingException("Поле 'discipline' не может быть null");
        }

        if (labWork.getMinimalPoint() == null) {
            throw new ParsingException("Поле 'minimalPoint' не может быть null");
        }

        if (labWork.getMinimalPoint() <= 0) {
            throw new ParsingException("Поле 'minimalPoint' должно быть больше 0");
        }

        if (labWork.getAuthor() == null) {
            throw new ParsingException("Поле 'author' не может быть null");
        }

        validateCoordinates(labWork.getCoordinates());

        validatePerson(labWork.getAuthor());

        validateDiscipline(labWork.getDiscipline());
    }

    private void validateCoordinates(Coordinates coordinates) throws ParsingException {
        if (coordinates.getX() > 540) {
            throw new ParsingException("Максимальное значение поля 'coordinates.x' равно 540");
        }
    }

    private void validatePerson(Person person) throws ParsingException {
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            throw new ParsingException("Поле 'author.name' не может быть null или пустым");
        }

        if (person.getEyeColor() == null) {
            throw new ParsingException("Поле 'author.eyeColor' не может быть null");
        }

        if (person.getWeight() == null) {
            throw new ParsingException("Поле 'author.weight' не может быть null");
        }

        if (person.getWeight() <= 0) {
            throw new ParsingException("Поле 'author.weight' должно быть больше 0");
        }

        if (person.getLocation() != null) {
            validateLocation(person.getLocation());
        }

        if (person.getBirthday() != null && person.getBirthday().isAfter(LocalDateTime.now())) {
            throw new ParsingException("Поле 'author.birthday' не может быть в будущем");
        }
    }

    private void validateLocation(Location location) throws ParsingException {
        if (location.getX() == null) {
            throw new ParsingException("Поле 'author.location.x' не может быть null");
        }

        if (location.getY() == null) {
            throw new ParsingException("Поле 'author.location.y' не может быть null");
        }

        if (location.getName() != null && location.getName().trim().isEmpty()) {
            throw new ParsingException("Поле 'author.location.name' не может быть пустой строкой");
        }
    }

    private void validateDiscipline(Discipline discipline) throws ParsingException {
        if (discipline.getName() == null || discipline.getName().trim().isEmpty()) {
            throw new ParsingException("Поле 'discipline.name' не может быть null или пустым");
        }

        if (discipline.getLectureHours() != null && discipline.getLectureHours() < 0) {
            throw new ParsingException("Поле 'discipline.lectureHours' не может быть отрицательным");
        }
    }
}
