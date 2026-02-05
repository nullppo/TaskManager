package org.example;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {

    public static void clear() {
        try {
            if (System.console() != null) {
                System.out.print("\033[H\033[2J\033[3J");
            } else {
                for (int i = 0; i < 80; i++) System.out.println();
            }
            System.out.flush();
        } catch (Exception e) {
            for (int i = 0; i < 80; i++) System.out.println();
        }
    }

    public static void printTasks(ArrayList<Task> tasks) {
        clear();
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст\n");
            return;
        }

        for (Task task : tasks) {
            if (task.getIsComplete()) {
                System.out.println(task.getId() + ". " + task.getDescription() + "- Выполнена");
            } else {
                System.out.println(task.getId() + ". " + task.getDescription() + "- Не выполнена");
            }
        }
        System.out.println("\n");
        System.out.println("-------------------------------------");
    }

    public static void addTask(ArrayList<Task> tasks, Scanner sc) {
        clear();
        while (true) {
            System.out.println("Введите описание задачи:");
            String description = sc.nextLine();

            if (description.isEmpty()) {
                clear();
                System.out.println("Описание не может быть пустым, попробуй ещё раз");
                continue;
            }

            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setId(i + 1);
            }
            int id = tasks.size() + 1;

            while (true) {
                System.out.println("\nСтатус задачи:");
                System.out.println("1. Выполнена");
                System.out.println("2. Не выполнена");

                try {
                    int statusChoice = sc.nextInt();
                    sc.nextLine();
                    boolean isComplete = (statusChoice == 1);

                    if (statusChoice == 1 || statusChoice == 2) {
                        Task newTask = new Task(description, isComplete, id);
                        tasks.add(newTask);
                        clear();
                        System.out.println("Задача добавлена\n");
                        return;
                    } else {
                        clear();
                        System.out.println("Выбери 1 или 2");
                    }
                } catch (InputMismatchException e) {
                    clear();
                    System.out.println("Введи число");
                    sc.nextLine();
                }
            }
        }
    }

    public static void deleteTask(ArrayList<Task> tasks, Scanner sc) {
        if (tasks.isEmpty()) {

            System.out.println("Список задач пуст\n");
            return;
        }

        printTasks(tasks);

        while (true) {
            try {
                System.out.println("\nНомер задачи для удаления:");
                int id = sc.nextInt();
                sc.nextLine();

                boolean found = false;
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == id) {
                        tasks.remove(i);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    clear();
                    System.out.println("Задачи с таким номером нет");
                    printTasks(tasks);
                    continue;
                }

                for (int i = 0; i < tasks.size(); i++) {
                    tasks.get(i).setId(i + 1);
                }

                clear();
                System.out.println("Задача удалена\n");
                return;
            } catch (InputMismatchException e) {
                clear();
                System.out.println("Введи число");
                sc.nextLine();
                printTasks(tasks);
            }
        }
    }

    public static void editTask(ArrayList<Task> tasks, Scanner sc) {
        if (tasks.isEmpty()) {
            clear();
            System.out.println("Список задач пуст\n");
            return;
        }

        printTasks(tasks);

        while (true) {
            try {
                System.out.println("\nНомер задачи для редактирования:");
                int id = sc.nextInt();
                sc.nextLine();

                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == id) {
                        while (true) {
                            clear();
                            System.out.println("Что изменить?");
                            System.out.println("1. Описание");
                            System.out.println("2. Статус");

                            int choice = sc.nextInt();
                            sc.nextLine();

                            if (choice == 1) {
                                clear();
                                System.out.println("Новое описание:");
                                String newDescription = sc.nextLine();
                                tasks.get(i).setDescription(newDescription);
                                clear();
                                System.out.println("Описание обновлено\n");
                                return;
                            } else if (choice == 2) {
                                boolean current = tasks.get(i).getIsComplete();
                                tasks.get(i).setIsComplete(!current);
                                clear();
                                System.out.println("Статус изменён\n");
                                return;
                            } else {
                                clear();
                                System.out.println("Выбери 1 или 2");
                            }
                        }
                    }
                }

                clear();
                System.out.println("Задачи с таким номером нет");
                printTasks(tasks);

            } catch (InputMismatchException e) {
                clear();
                System.out.println("Введи число");
                sc.nextLine();
                printTasks(tasks);
            }
        }
    }

    public static void saveTask(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            clear();
            System.out.println("Список задач пуст\n");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Task task : tasks) {
            sb.append(task.getId()).append(". ")
                    .append(task.getDescription())
                    .append(" ---> ")
                    .append(task.getIsComplete() ? "Выполнена" : "Не выполнена")
                    .append("\n");
        }

        try {
            Files.writeString(Path.of("tasks.txt"), sb.toString());
            clear();
            System.out.println("Задачи сохранены в tasks.txt\n");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения файла");
        }
    }

    public static void saveInJson(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            clear();
            System.out.println("Список задач пуст\n");
            return;
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(tasks);

        try {
            Files.writeString(Path.of("tasks.json"), json);
            clear();
            System.out.println("Задачи сохранены в tasks.json\n");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения файла");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            System.out.println("Даров, че делаем?");
            System.out.println("1. Посмотреть задачи");
            System.out.println("2. Добавить задачу");
            System.out.println("3. Удалить задачу");
            System.out.println("4. Редактировать задачу");
            System.out.println("5. Сохранить в txt");
            System.out.println("6. Сохранить в json");
            System.out.println("0. Выход\n");

            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        printTasks(tasks);
                        break;
                    case 2:
                        addTask(tasks, sc);
                        break;
                    case 3:
                        deleteTask(tasks, sc);
                        break;
                    case 4:
                        editTask(tasks, sc);
                        break;
                    case 5:
                        saveTask(tasks);
                        break;
                    case 6:
                        saveInJson(tasks);
                        break;
                    case 0:
                        clear();
                        System.out.println("Пока!");
                        return;
                    default:
                        clear();
                        System.out.println("Выбери число из списка\n");
                }
            } catch (InputMismatchException e) {
                clear();
                System.out.println("Введи число\n");
                sc.nextLine();
            }
        }
    }
}