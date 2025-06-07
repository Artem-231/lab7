//package app.managers;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Класс App содержит основной цикл работы приложения.
// */
//public class App {
//    private final CollectionManager collectionManager;
//    private final CommandManager commandManager;
//    private final List<String> commandHistory = new LinkedList<>();
//    private boolean running = true;
//
//    public App(CollectionManager collectionManager, CommandManager commandManager) {
//        this.collectionManager = collectionManager;
//        this.commandManager = commandManager;
//    }
//
//    /**
//     * Запускает интерактивный режим.
//     */
//    public void startInteractiveMode() {
//        Scanner scanner = new Scanner(System.in);
//        while (running) {
//            System.out.print("Введите команду: ");
//            String line = scanner.nextLine().trim();
//            if (line.isEmpty()) continue;
//            addToHistory(line.split("\\s+")[0]);
//            //commandManager.executeCommand(line, scanner, this);
//        }
//        System.out.println("Программа завершена.");
//    }
//
//    public void stop() {
//        running = false;
//    }
//
//    public List<String> getCommandHistory() {
//        return commandHistory;
//    }
//
//    private void addToHistory(String cmd) {
//        if (commandHistory.size() >= 10) {
//            commandHistory.remove(0);
//        }
//        commandHistory.add(cmd);
//    }
//}
