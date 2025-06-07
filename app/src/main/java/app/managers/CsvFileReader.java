//package app.managers;
//
//import core.enums.Color;
//import core.enums.Country;
//import core.enums.Difficulty;
//import core.objects.Coordinates;
//import core.objects.LabWork;
//import core.objects.Person;
//
//import java.io.File;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Утилитарный класс для чтения объектов LabWork из CSV-файла.
// */
//public class CsvFileReader {
//    /**
//     * Считывает все объекты LabWork из указанного CSV-файла.
//     * @param fileName имя файла
//     * @return список объектов LabWork
//     * @throws Exception при ошибке чтения
//     */
//    public static List<LabWork> readLabWorks(String fileName) throws Exception {
//        File file = new File(fileName);
//        if (!file.exists()) {
//            throw new Exception("Файл " + fileName + " не найден.");
//        }
//        List<LabWork> result = new ArrayList<>();
//        try (Scanner fileScanner = new Scanner(file)) {
//            if (fileScanner.hasNextLine()) {
//                fileScanner.nextLine(); // пропускаем заголовок
//            }
//            while (fileScanner.hasNextLine()) {
//                String line = fileScanner.nextLine().trim();
//                if (line.isEmpty()) continue;
//                String[] tokens = line.split(",");
//                int id = Integer.parseInt(tokens[0]);
//                String name = tokens[1];
//                Double x = Double.parseDouble(tokens[2]);
//                Long y = Long.parseLong(tokens[3]);
//                LocalDate creationDate = LocalDate.parse(tokens[4]);
//                Long minimalPoint = Long.parseLong(tokens[5]);
//                String description = tokens[6];
//                Difficulty difficulty = tokens[7].isEmpty() ? null : Difficulty.valueOf(tokens[7].toUpperCase());
//                String authorName = tokens[8];
//                int authorWeight = Integer.parseInt(tokens[9]);
//                Color eyeColor = tokens[10].isEmpty() ? null : Color.valueOf(tokens[10].toUpperCase());
//                Color hairColor = Color.valueOf(tokens[11].toUpperCase());
//                Country nationality = Country.valueOf(tokens[12].toUpperCase());
//
//                Person author = new Person(authorName, authorWeight, eyeColor, hairColor, nationality);
//                LabWork lw = new LabWork(id, name, new Coordinates(x, y), creationDate, minimalPoint, description, difficulty, author);
//                result.add(lw);
//            }
//        }
//        return result;
//    }
//}
