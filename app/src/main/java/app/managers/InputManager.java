package app.managers;

import core.enums.Color;
import core.enums.Country;
import core.enums.Difficulty;
import core.objects.Coordinates;
import core.objects.LabWork;
import core.objects.Person;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

public class InputManager {

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Поле не может быть пустым.");
        }
    }

    public static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат, ожидалось double.");
            }
        }
    }

    public static long readLong(Scanner scanner,
                                String prompt,
                                Predicate<Long> validator,
                                String errMsg) {
        while (true) {
            System.out.print(prompt);
            try {
                long v = Long.parseLong(scanner.nextLine().trim());
                if (validator.test(v)) {
                    return v;
                }
                System.out.println(errMsg);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат, ожидалось long.");
            }
        }
    }

    public static float readFloat(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                float v = Float.parseFloat(scanner.nextLine().trim());
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат, ожидалось число с плавающей точкой.");
            }
        }
    }

    public static int readInt(Scanner scanner,
                              String prompt,
                              Predicate<Integer> validator,
                              String errMsg) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (validator.test(v)) {
                    return v;
                }
                System.out.println(errMsg);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат, ожидалось int.");
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(Scanner scanner,
                                                 Class<E> cls,
                                                 String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(cls, in);
            } catch (Exception e) {
                System.out.println("Неверное значение, допустимо: " +
                        Arrays.toString(cls.getEnumConstants()));
            }
        }
    }

    public static <E extends Enum<E>> E readOptionalEnum(Scanner scanner,
                                                         Class<E> cls,
                                                         String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = scanner.nextLine().trim();
            if (in.isEmpty()) {
                return null;
            }
            try {
                return Enum.valueOf(cls, in.toUpperCase());
            } catch (Exception e) {
                System.out.println("Неверное значение, допустимо: " +
                        Arrays.toString(cls.getEnumConstants()));
            }
        }
    }

    /**
     * Считывает новый LabWork, при этом:
     *  - creationDate генерируется автоматически,
     *  - минимальное значение вводится как Float > 0,
     *  - автор задаётся параметром ownerLogin.
     */
    public static LabWork readLabWork(Scanner scanner, String ownerLogin) {
        System.out.println("Введите данные для LabWork:");

        String name = readNonEmptyString(scanner, "  Имя работы: ");
        double x = readDouble(scanner, "  Координата X (Double): ");
        long y = readLong(scanner,
                "  Координата Y (Long, > -721): ",
                v -> v > -721,
                "  Y должен быть > -721");
        Coordinates coords = new Coordinates(x, y);

        float minimal;
        do {
            minimal = readFloat(scanner, "  Минимальное значение (Float, > 0): ");
            if (minimal <= 0) {
                System.out.println("  minimalPoint должен быть > 0");
            }
        } while (minimal <= 0);

        String desc = readNonEmptyString(scanner, "  Описание: ");
        Difficulty diff = readOptionalEnum(scanner,
                Difficulty.class,
                "  Difficulty (EASY, HARD, INSANE) или пусто: ");

        System.out.println("Данные автора будут установлены как ваш логин: " + ownerLogin);
        int weight = readInt(scanner,
                "  Вес автора (int, > 0): ",
                v -> v > 0,
                "  Вес должен быть > 0");

        Color eye = readOptionalEnum(scanner,
                Color.class,
                "  Цвет глаз (GREEN, YELLOW, BROWN, RED, BLACK, BLUE) или пусто: ");
        Color hair = readEnum(scanner,
                Color.class,
                "  Цвет волос (GREEN, YELLOW, BROWN, RED, BLACK, BLUE): ");
        Country cty = readEnum(scanner,
                Country.class,
                "  Национальность (SPAIN, CHINA, THAILAND): ");

        Person author = new Person(ownerLogin, weight, eye, hair, cty);
        return new LabWork(name, coords, minimal, desc, diff, author);
    }
}
