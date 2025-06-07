package app.service;

import core.dao.LabWorkDao;
import core.objects.LabWork;
import core.enums.Difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис бизнес-логики для операций с объектами LabWork.
 */
public class LabWorkService {
    private final LabWorkDao dao = new storage.postgres.PostgresLabWorkDao();

    /**
     * Возвращает все элементы коллекции, отсортированные по id.
     */
    public List<LabWork> fetchAll(String login) {
        return dao.fetchAll().stream()
                .sorted(Comparator.comparingInt(LabWork::getId))
                .collect(Collectors.toList());
    }

    /**
     * Добавляет новый элемент, присваивая ему владельца (login).
     */
    public boolean add(LabWork lw, String login) {
        lw.setOwnerLogin(login);
        return dao.insert(lw).isPresent();
    }

    /**
     * Обновляет элемент по id, только если текущий пользователь является владельцем.
     */
    public boolean update(int id, LabWork lw, String login) {
        lw.setId(id);
        lw.setOwnerLogin(login);
        return dao.update(lw);
    }

    /**
     * Удаляет элемент по id, только если текущий пользователь является владельцем.
     */
    public boolean remove(long id, String login) {
        return dao.delete(id, login);
    }

    /**
     * Удаляет все элементы текущего пользователя.
     */
    public boolean clear(String login) {
        boolean anyRemoved = false;
        for (LabWork lw : dao.fetchAll()) {
            if (login.equals(lw.getOwnerLogin())) {
                if (dao.delete(lw.getId(), login)) {
                    anyRemoved = true;
                }
            }
        }
        return anyRemoved;
    }

    /**
     * Среднее minimalPoint по всем элементам коллекции.
     */
    public double averageOfMinimalPoint(String login) {
        return dao.fetchAll().stream()
                .mapToDouble(LabWork::getMinimalPoint)
                .average()
                .orElse(0.0);
    }

    /**
     * Количество элементов, difficulty которых меньше threshold, по всей коллекции.
     */
    public long countLessThanDifficulty(Difficulty threshold, String login) {
        return dao.fetchAll().stream()
                .filter(lw -> lw.getDifficulty() != null
                        && lw.getDifficulty().ordinal() < threshold.ordinal())
                .count();
    }

    /**
     * Добавляет новый элемент, если он больше наибольшего элемента во всей коллекции.
     */
    public boolean addIfMax(LabWork lw, String login) {
        Optional<LabWork> max = dao.fetchAll().stream().max(Comparator.naturalOrder());
        if (max.isPresent() && lw.compareTo(max.get()) <= 0) {
            return false;
        }
        return add(lw, login);
    }

    /**
     * Элемент с минимальным id среди всех элементов коллекции.
     */
    public LabWork minById(String login) {
        return dao.fetchAll().stream()
                .min(Comparator.comparingInt(LabWork::getId))
                .orElse(null);
    }

    /**
     * Информация о коллекции: тип DAO и общее количество элементов.
     */
    public String info(String login) {
        long total = dao.fetchAll().size();
        return String.format("Тип: %s, элементов: %d",
                dao.getClass().getSimpleName(), total);
    }
}
