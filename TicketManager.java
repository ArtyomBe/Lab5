import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TicketManager {
    private Hashtable<Integer, Ticket> ticketCollection = new Hashtable<>();
    private Scanner scanner = new Scanner(System.in);
    private Deque<String> commandHistory = new LinkedList<>();

    public TicketManager(String fileName) {
        loadCollectionFromFile(fileName);
    }

    public void run() {
        while (true) {
            System.out.print("Введите команду (help для списка команд): ");
            String input = scanner.nextLine().trim();
            commandHistory.add(input);

            String[] tokens = input.split("\\s+");

            if (tokens.length > 0) {
                String command = tokens[0].toLowerCase();
                switch (command) {
                    case "help":
                        printHelp();
                        break;
                    case "info":
                        printInfo();
                        break;
                    case "show":
                        showTickets();
                        break;
                    case "insert":
                        insertTicket();
                        break;
                    case "update":
                        updateTicket();
                        break;
                    case "remove_key":
                        removeTicketByKey();
                        break;
                    case "clear":
                        clearTickets();
                        break;
                    case "save":
                        saveToFile();
                        break;
                    case "execute_script":
                        executeScript(tokens);
                        break;
                    case "exit":
                        System.out.println("Программа завершена.");
                        System.exit(0);
                        break;
                    case "history":
                        printHistory();
                        break;
                    case "replace_if_greater":
                        replaceIfGreater();
                        break;
                    case "remove_greater_key":
                        removeGreaterByKey();
                        break;
                    case "average_of_price":
                        calculateAveragePrice();
                        break;
                    case "min_by_price":
                        findMinByPrice();
                        break;
                    case "filter_by_price":
                        filterByPrice(tokens);
                        break;
                    case "print_csv":
                        printCSV();
                        break;
                    default:
                        System.out.println("Неверная команда. Введите 'help' для списка команд.");
                        break;
                }
            }
        }
    }

    private void loadCollectionFromFile(String fileName) {
        try (Scanner fileScanner = new Scanner(new FileReader(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                Ticket ticket = Ticket.parseTicketFromCSV(line);
                if (ticket != null) {
                    ticketCollection.put(ticket.getId(), ticket);
                }
            }
            System.out.println("Коллекция успешно загружена из файла.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Создана новая коллекция.");
        }
    }

    private void printHelp() {
        System.out.println("Список команд:");
        System.out.println("help : вывести справку по доступным командам");
        System.out.println("info : вывести информацию о коллекции");
        System.out.println("show : вывести все элементы коллекции");
        System.out.println("insert null {element} : добавить новый элемент с заданным ключом");
        System.out.println("update id {element} : обновить значение элемента коллекции, id которого равен заданному");
        System.out.println("remove_key null : удалить элемент из коллекции по его ключу");
        System.out.println("clear : очистить коллекцию");
        System.out.println("save : сохранить коллекцию в файл");
        System.out.println("execute_script file_name : считать и исполнить скрипт из указанного файла");
        System.out.println("exit : завершить программу (без сохранения в файл)");
        System.out.println("history : вывести последние 6 команд (без их аргументов)");
        System.out.println("replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого");
        System.out.println("remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный");
        System.out.println("average_of_price : вывести среднее значение поля price для всех элементов коллекции");
        System.out.println("min_by_price : вывести любой объект из коллекции, значение поля price которого является минимальным");
        System.out.println("filter_by_price price : вывести элементы, значение поля price которых равно заданному");
    }

    private void printInfo() {
        System.out.println("Информация о коллекции:");
        System.out.println("Тип коллекции: Hashtable<Integer, Ticket>");
        System.out.println("Дата инициализации: " + LocalDateTime.now());
        System.out.println("Количество элементов: " + ticketCollection.size());
    }

    private void showTickets() {
        System.out.println("Элементы коллекции:");
        for (Ticket ticket : ticketCollection.values()) {
            System.out.println(ticket);
        }
    }

    private void insertTicket() {
        boolean insertSuccess = false;

        while (!insertSuccess) {
            try {
                int id = generateUniqueId();

                System.out.println("Введите данные для нового элемента:");

                System.out.print("Введите имя: ");
                String name = scanner.nextLine();

                int x = 0;
                double y = 0;

                // Input x
                while (true) {
                    System.out.print("Введите координату x: ");
                    String xInput = scanner.nextLine();

                    try {
                        x = Integer.parseInt(xInput);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка при вводе координаты x. Попробуйте еще раз!");
                    }
                }

                // Input y
                while (true) {
                    System.out.print("Введите координату y: ");
                    String yInput = scanner.nextLine();

                    try {
                        y = Double.parseDouble(yInput);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка при вводе координаты y. Попробуйте еще раз!");
                    }
                }

                Coordinates coordinates = new Coordinates(x, y);

                // Замените этот блок кода, чтобы получить текущую дату и время
                ZonedDateTime creationDate = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

                System.out.print("Введите цену билета: ");
                double price = scanner.nextDouble();
                scanner.nextLine();

                System.out.print("Введите значение refundable (true/false): ");
                String refundableInput = scanner.nextLine();
                boolean refundable = Boolean.parseBoolean(refundableInput);

                TicketType type = inputTicketType();

                Event event = inputEvent();

                Ticket newTicket = new Ticket(id, name, coordinates, creationDate.toLocalDateTime(), price, refundable, type, event);
                ticketCollection.put(id, newTicket);

                System.out.println("Элемент успешно создан с идентификатором " + id + " и записан в коллекцию.");

                insertSuccess = true;

            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Ошибка при вводе данных: " + e.getMessage());
            }
        }
    }

    private Coordinates inputCoordinates() {
        System.out.println("Введите координаты:");
        System.out.print("Введите x: ");
        int x = Integer.parseInt(scanner.nextLine());

        System.out.print("Введите y: ");
        double y = Double.parseDouble(scanner.nextLine());

        return new Coordinates(x, y);
    }

    private TicketType inputTicketType() {
        System.out.println("Введите тип билета (USUAL, BUDGETARY, CHEAP): ");
        String typeStr = scanner.nextLine().toUpperCase();

        try {
            return TicketType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка: введен некорректный тип билета.");
        }
    }

    private Event inputEvent() {
        System.out.println("Введите данные для события (если есть):");

        System.out.print("Введите ID события: ");
        int eventId = Integer.parseInt(scanner.nextLine());

        System.out.print("Введите название события: ");
        String eventName = scanner.nextLine();

        System.out.print("Введите тип события (E_SPORTS, BASEBALL, BASKETBALL): ");
        String eventTypeStr = scanner.nextLine().toUpperCase();

        EventType eventType;
        try {
            eventType = EventType.valueOf(eventTypeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка: введен некорректный тип события.");
        }

        System.out.print("Введите дату события в формате 'ГГГГ-ММ-ДДTHH:MM': ");
        String eventDateStr = scanner.nextLine();
        LocalDateTime eventDate = LocalDateTime.parse(eventDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        return new Event(eventId, eventName, eventDate, eventType);
    }


    private void updateTicket() {
        try {
            System.out.print("Введите ID элемента для обновления: ");
            int id = Integer.parseInt(scanner.nextLine());

            if (!ticketCollection.containsKey(id)) {
                System.out.println("Элемент с указанным ID не найден.");
                return;
            }

            Ticket currentTicket = ticketCollection.get(id);
            System.out.println("Текущее значение:");
            System.out.println(currentTicket);

            System.out.println("Введите новые данные для обновления (оставьте пустым для сохранения текущего значения):");

            System.out.print("Введите имя: ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                currentTicket.setName(name);
            }

            Coordinates coordinates = inputCoordinates();
            currentTicket.setCoordinates(coordinates);

            System.out.print("Введите цену: ");
            String priceStr = scanner.nextLine();
            if (!priceStr.isEmpty()) {
                double price = Double.parseDouble(priceStr);
                currentTicket.setPrice(price);
            }

            System.out.print("Является ли билет возвращаемым (true/false): ");
            String refundableStr = scanner.nextLine();
            if (!refundableStr.isEmpty()) {
                boolean refundable = Boolean.parseBoolean(refundableStr);
                currentTicket.setRefundable(refundable);
            }

            System.out.print("Введите тип билета (USUAL, BUDGETARY, CHEAP): ");
            String typeStr = scanner.nextLine().toUpperCase();
            if (!typeStr.isEmpty()) {
                TicketType type = TicketType.valueOf(typeStr);
                currentTicket.setType(type);
            }

            Event event = inputEvent();
            currentTicket.setEvent(event);

            ticketCollection.put(id, currentTicket);
            System.out.println("Элемент успешно обновлен.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при парсинге числового значения: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при парсинге: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Ошибка при парсинге строки: некорректные данные.");
        }
    }

    private void removeTicketByKey() {
        try {
            System.out.print("Введите ключ элемента для удаления: ");
            int key = Integer.parseInt(scanner.nextLine());

            if (!ticketCollection.containsKey(key)) {
                System.out.println("Элемент с указанным ключом не найден.");
                return;
            }

            ticketCollection.remove(key);
            System.out.println("Элемент успешно удален.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введено некорректное значение для ключа.");
        }
    }

    private void clearTickets() {
        ticketCollection.clear();
        System.out.println("Коллекция успешно очищена.");
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv"))) {
            for (Ticket ticket : ticketCollection.values()) {
                writer.write(ticket.toCSVString());
                writer.newLine();
            }
            System.out.println("Коллекция успешно сохранена в файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении коллекции в файл.");
        }
    }


    private void executeScript(String[] tokens) {
        try {
            if (tokens.length < 2) {
                System.out.println("Ошибка: не указано имя файла для выполнения скрипта.");
                return;
            }

            String fileName = tokens[1];
            Scanner fileScanner = new Scanner(new FileReader(fileName));

            while (fileScanner.hasNextLine()) {
                String scriptCommand = fileScanner.nextLine().trim();
                commandHistory.add(scriptCommand);

                String[] scriptTokens = scriptCommand.split("\\s+");

                if (scriptTokens.length > 0) {
                    String scriptCommandName = scriptTokens[0].toLowerCase();

                    switch (scriptCommandName) {
                        case "insert":
                            executeInsertScript(scriptTokens);
                            break;
                        case "update":
                            executeUpdateScript(scriptTokens);
                            break;

                        default:
                            System.out.println("Неверная команда в скрипте: " + scriptCommand);
                            break;
                    }
                }
            }

            fileScanner.close();
            System.out.println("Скрипт успешно выполнен.");
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл скрипта не найден.");
        }
    }

    private void executeInsertScript(String[] scriptTokens) {
        if (scriptTokens.length < 3) {
            System.out.println("Ошибка: недостаточно аргументов для команды insert.");
            return;
        }

        try {
            String keyStr = scriptTokens[1];
            int key = Integer.parseInt(keyStr);

            if (ticketCollection.containsKey(key)) {
                System.out.println("Ошибка: элемент с указанным ключом уже существует.");
                return;
            }

            String ticketData = scriptTokens[2];
            Ticket newTicket = Ticket.parseTicket(ticketData);

            if (newTicket != null) {
                ticketCollection.put(key, newTicket);
                System.out.println("Элемент успешно добавлен из скрипта.");
            } else {
                System.out.println("Ошибка при парсинге данных для команды insert из скрипта.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: некорректное значение ключа для команды insert из скрипта.");
        }
    }


    private void executeUpdateScript(String[] scriptTokens) {
        if (scriptTokens.length < 3) {
            System.out.println("Ошибка: недостаточно аргументов для команды update.");
            return;
        }

        try {
            String keyStr = scriptTokens[1];
            int key = Integer.parseInt(keyStr);

            if (!ticketCollection.containsKey(key)) {
                System.out.println("Ошибка: элемент с указанным ключом не найден.");
                return;
            }

            String ticketData = scriptTokens[2];
            Ticket updatedTicket = Ticket.parseTicket(ticketData);

            if (updatedTicket != null) {
                ticketCollection.put(key, updatedTicket);
                System.out.println("Элемент успешно обновлен из скрипта.");
            } else {
                System.out.println("Ошибка при парсинге данных для команды update из скрипта.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: некорректное значение ключа для команды update из скрипта.");
        }
    }


    private void printHistory() {
        System.out.println("История последних команд:");
        for (String command : commandHistory) {
            System.out.println(command);
        }
    }

    private void replaceIfGreater() {
        try {
            System.out.print("Введите ключ элемента: ");
            int key = Integer.parseInt(scanner.nextLine());

            if (!ticketCollection.containsKey(key)) {
                System.out.println("Элемент с указанным ключом не найден.");
                return;
            }

            Ticket currentTicket = ticketCollection.get(key);
            System.out.println("Текущее значение:");
            System.out.println(currentTicket);

            System.out.println("Введите новое значение для замены (оставьте пустым для сохранения текущего значения):");

            System.out.print("Введите цену: ");
            String newPriceStr = scanner.nextLine();
            if (!newPriceStr.isEmpty()) {
                double newPrice = Double.parseDouble(newPriceStr);
                if (newPrice > currentTicket.getPrice()) {
                    currentTicket.setPrice(newPrice);
                    System.out.println("Значение успешно заменено.");
                } else {
                    System.out.println("Новое значение не больше старого.");
                }
            } else {
                System.out.println("Значение не введено, текущее значение сохранено.");
            }

            ticketCollection.put(key, currentTicket);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при парсинге числового значения: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при парсинге: " + e.getMessage());
        }
    }


    private void removeGreaterByKey() {
        try {
            System.out.print("Введите ключ, по которому нужно удалить элементы: ");
            int key = Integer.parseInt(scanner.nextLine());

            ticketCollection.entrySet().removeIf(entry -> entry.getKey() > key);

            System.out.println("Элементы успешно удалены.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введено некорректное значение для ключа.");
        }
    }


    private void calculateAveragePrice() {
        if (ticketCollection.isEmpty()) {
            System.out.println("Коллекция пуста. Среднее значение невозможно вычислить.");
            return;
        }

        double totalSum = 0;
        for (Ticket ticket : ticketCollection.values()) {
            totalSum += ticket.getPrice();
        }

        double averagePrice = totalSum / ticketCollection.size();
        System.out.println("Среднее значение цены билетов: " + averagePrice);
    }


    private void findMinByPrice() {
        if (ticketCollection.isEmpty()) {
            System.out.println("Коллекция пуста. Невозможно найти билет с минимальной ценой.");
            return;
        }

        Ticket minTicket = null;
        double minPrice = Double.MAX_VALUE;

        for (Ticket ticket : ticketCollection.values()) {
            if (ticket.getPrice() < minPrice) {
                minPrice = ticket.getPrice();
                minTicket = ticket;
            }
        }

        System.out.println("Билет с минимальной ценой:");
        System.out.println(minTicket);
    }


    private void filterByPrice(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Ошибка: не указано значение цены для фильтрации.");
            return;
        }

        try {
            double targetPrice = Double.parseDouble(tokens[1].trim());

            System.out.println("Билеты с ценой, равной " + targetPrice + ":");
            boolean found = false;

            for (Ticket ticket : ticketCollection.values()) {
                if (ticket.getPrice() == targetPrice) {
                    System.out.println(ticket);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("Билеты с указанной ценой не найдены.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка при парсинге числового значения цены: " + e.getMessage());
        }
    }


    private int generateUniqueId() {
        Set<Integer> generatedIds = new HashSet<>();
        Random random = new Random();
        int id;
        do {
            id = random.nextInt(1_000);
        } while (generatedIds.contains(id));
        generatedIds.add(id);

        return id;
    }

    private void printCSV() {
        String csvFile = "output.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] data = line.split(cvsSplitBy);
                    for (int i = 0; i < data.length; i++) {
                        System.out.print(String.format("%-" + data[i].length() + "s", data[i]));
                        if (i < data.length - 1) {
                            System.out.print(" | ");
                        }
                    }
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File dataFile = new File("data.csv");
        String fileName = System.getenv("FILENAME");
//        if (fileName == null) {
//            System.err.println("Не указано имя файла в переменной окружения FILENAME.");
//            System.exit(1);
//        }

        TicketManager ticketManager = new TicketManager(dataFile.getPath());
        ticketManager.run();
    }
}
