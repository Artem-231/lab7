package core.objects;

import core.enums.Color;
import core.enums.Country;

import java.io.Serializable;

/**
 * Класс Person описывает автора LabWork и
 * хранит имя, вес, цвет глаз/волос и национальность.
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int weight;                // > 0
    private Color eyeColor;            // может быть null
    private Color hairColor;           // не null
    private Country nationality;       // не null

    /**
     * Упрощённый конструктор только с именем.
     * Остальные поля можно будет заполнить позже или оставить дефолтными.
     */
    public Person(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name не может быть пустым");
        this.name = name;
        this.weight = 0;              // или любой дефолт >0
        this.eyeColor = null;         // можно оставить null
        this.hairColor = null;        // можно оставить null
        this.nationality = null;      // можно оставить null
    }

    /**
     * Полный конструктор для загрузки из БД/файла.
     */
    public Person(String name,
                  int weight,
                  Color eyeColor,
                  Color hairColor,
                  Country nationality) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name не может быть пустым");
        if (weight <= 0)
            throw new IllegalArgumentException("weight должен быть > 0");
        if (hairColor == null)
            throw new IllegalArgumentException("hairColor не может быть null");
        if (nationality == null)
            throw new IllegalArgumentException("nationality не может быть null");

        this.name = name;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    // Геттеры и сеттеры

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name не может быть пустым");
        this.name = name;
    }

    public void setWeight(int weight) {
        if (weight <= 0)
            throw new IllegalArgumentException("weight должен быть > 0");
        this.weight = weight;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setHairColor(Color hairColor) {
        if (hairColor == null)
            throw new IllegalArgumentException("hairColor не может быть null");
        this.hairColor = hairColor;
    }

    public void setNationality(Country nationality) {
        if (nationality == null)
            throw new IllegalArgumentException("nationality не может быть null");
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                '}';
    }
}
