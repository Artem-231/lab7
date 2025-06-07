package core.objects;

import core.enums.Difficulty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс LabWork представляет лабораторную работу.
 * Serializable для передачи по сети.
 */
public class LabWork implements Comparable<LabWork>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger nextId = new AtomicInteger(1);

    private int id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Float minimalPoint;
    private String description;
    private Difficulty difficulty;
    private Person author;

    public LabWork() {
        this.id = nextId.getAndIncrement();
        this.creationDate = LocalDateTime.now();
        this.coordinates = new Coordinates();
    }

    public LabWork(LabWork other) {
        this.id = other.id;
        this.name = other.name;
        this.coordinates = new Coordinates(
                other.coordinates.getX(), other.coordinates.getY()
        );
        this.creationDate = other.creationDate;
        this.minimalPoint = other.minimalPoint;
        this.description = other.description;
        this.difficulty = other.difficulty;
        this.author = other.author;
        // гарантируем, что следующий id будет больше существующего
        while (nextId.get() <= this.id) {
            nextId.incrementAndGet();
        }
    }

    /**
     * Конструктор для создания нового LabWork.
     * Генерирует id и creationDate.
     */
    public LabWork(String name,
                   Coordinates coordinates,
                   Float minimalPoint,
                   String description,
                   Difficulty difficulty,
                   Person author) {
        setName(name);
        setCoordinates(coordinates);
        setMinimalPoint(minimalPoint);
        setDescription(description);
        this.difficulty = difficulty;
        setAuthor(author);

        this.id = nextId.getAndIncrement();
        this.creationDate = LocalDateTime.now();
    }

    /**
     * Полный конструктор для загрузки из БД/файла
     * с заранее известным id и creationDate.
     */
    public LabWork(int id,
                   String name,
                   Coordinates coordinates,
                   LocalDateTime creationDate,
                   Float minimalPoint,
                   String description,
                   Difficulty difficulty,
                   Person author) {
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть > 0");
        }
        setName(name);
        setCoordinates(coordinates);
        if (creationDate == null) {
            throw new IllegalArgumentException("creationDate не может быть null");
        }
        this.creationDate = creationDate;
        setMinimalPoint(minimalPoint);
        setDescription(description);
        this.difficulty = difficulty;
        setAuthor(author);

        this.id = id;
        while (nextId.get() <= id) {
            nextId.incrementAndGet();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    /** Для отображения в таблице: кто владелец (имя автора). */
    public String getOwnerLogin() {
        return author.getName();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть пустым");
        }
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("coordinates не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public void setMinimalPoint(Float minimalPoint) {
        if (minimalPoint == null || minimalPoint <= 0) {
            throw new IllegalArgumentException("minimalPoint должен быть > 0");
        }
        this.minimalPoint = minimalPoint;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("description не может быть пустым");
        }
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setAuthor(Person author) {
        if (author == null) {
            throw new IllegalArgumentException("author не может быть null");
        }
        this.author = author;
    }

    public void setOwnerLogin(String ownerLogin) {
        if (author == null) {
            throw new IllegalStateException("Author is null, cannot set owner");
        }
        author.setName(ownerLogin);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(LabWork other) {
        int cmp = this.minimalPoint.compareTo(other.minimalPoint);
        if (cmp == 0) {
            cmp = Integer.compare(this.id, other.id);
        }
        return cmp;
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", description='" + description + '\'' +
                ", difficulty=" + difficulty +
                ", author=" + author +
                '}';
    }
}
