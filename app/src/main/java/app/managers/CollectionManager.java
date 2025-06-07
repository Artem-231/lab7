//package app.managers;
//
//import core.objects.LabWork;
//
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.PriorityBlockingQueue;
//
///**
// * Менеджер коллекции LabWork.
// * Поддерживает потокобезопасный PriorityBlockingQueue и
// * обеспечивает все команды для работы с коллекцией.
// */
//public class CollectionManager {
//    /** Потокобезопасная очередь с приоритетом */
//    private final PriorityBlockingQueue<LabWork> labWorks = new PriorityBlockingQueue<>();
//    /** Дата инициализации коллекции */
//    private Date initializationDate = new Date();
//
//    /**
//     * Загрузить коллекцию из CSV-файла.
//     */
//    public void loadFromFile(String fileName) {
//        try {
//            List<LabWork> loaded = CsvFileReader.readLabWorks(fileName);
//            labWorks.clear();
//            labWorks.addAll(loaded);
//            System.out.println("Коллекция загружена из файла " + fileName);
//        } catch (Exception e) {
//            System.out.println("Ошибка при загрузке коллекции: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Сохранить коллекцию в CSV-файл.
//     */
//    public void saveToFile(String fileName) {
//        try (FileOutputStream fos = new FileOutputStream(fileName);
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
//
//            // Заголовок CSV
//            writer.write("id,name,x,y,creationDate,minimalPoint,description,difficulty,"
//                    + "authorName,authorWeight,authorEyeColor,authorHairColor,authorNationality");
//            writer.newLine();
//
//            for (LabWork lw : labWorks) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(lw.getId()).append(",");
//                sb.append(lw.getName()).append(",");
//                sb.append(lw.getCoordinates().getX()).append(",");
//                sb.append(lw.getCoordinates().getY()).append(",");
//                sb.append(lw.getCreationDate()).append(",");
//                sb.append(lw.getMinimalPoint()).append(",");
//                sb.append(lw.getDescription()).append(",");
//                sb.append(lw.getDifficulty() != null ? lw.getDifficulty() : "").append(",");
//                sb.append(lw.getAuthor().getName()).append(",");
//                sb.append(lw.getAuthor().getWeight()).append(",");
//                sb.append(lw.getAuthor().getEyeColor() != null ? lw.getAuthor().getEyeColor() : "").append(",");
//                sb.append(lw.getAuthor().getHairColor()).append(",");
//                sb.append(lw.getAuthor().getNationality());
//                writer.write(sb.toString());
//                writer.newLine();
//            }
//
//            writer.flush();
//            System.out.println("Коллекция сохранена в файл " + fileName);
//        } catch (Exception e) {
//            System.out.println("Ошибка при сохранении коллекции: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Вернуть потокобезопасную очередь.
//     * Все операции чтения/перебора можно делать через этот интерфейс.
//     */
//    public BlockingQueue<LabWork> getLabWorks() {
//        return labWorks;
//    }
//
//    /** Дата инициализации коллекции */
//    public Date getInitializationDate() {
//        return initializationDate;
//    }
//
//    /** Очистить коллекцию */
//    public void clear() {
//        labWorks.clear();
//    }
//
//    /**
//     * Удалить элемент по id.
//     * @return true, если элемент найден и удалён.
//     */
//    public boolean removeById(int id) {
//        LabWork toRemove = null;
//        for (LabWork lw : labWorks) {
//            if (lw.getId() == id) {
//                toRemove = lw;
//                break;
//            }
//        }
//        if (toRemove != null) {
//            labWorks.remove(toRemove);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Найти элемент по id.
//     * @return найденный LabWork или null.
//     */
//    public LabWork findById(int id) {
//        for (LabWork lw : labWorks) {
//            if (lw.getId() == id) {
//                return lw;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Проверить, является ли переданный элемент максимальным
//     * по сравнению с текущими в коллекции.
//     */
//    public boolean isMax(LabWork lw) {
//        for (LabWork existing : labWorks) {
//            if (lw.compareTo(existing) <= 0) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Найти элемент с минимальным id.
//     * @return минимальный LabWork или null, если коллекция пуста.
//     */
//    public LabWork getMinById() {
//        if (labWorks.isEmpty()) return null;
//        LabWork min = null;
//        for (LabWork lw : labWorks) {
//            if (min == null || lw.getId() < min.getId()) {
//                min = lw;
//            }
//        }
//        return min;
//    }
//
//    /**
//     * Вычислить и вывести в консоль среднее минимальное значение.
//     */
//    public void averageOfMinimalPoint() {
//        if (labWorks.isEmpty()) {
//            System.out.println("Коллекция пуста.");
//            return;
//        }
//        double average = labWorks.stream()
//                .mapToLong(LabWork::getMinimalPoint)
//                .average()
//                .orElse(0);
//        System.out.println("Среднее значение minimalPoint: " + average);
//    }
//}
