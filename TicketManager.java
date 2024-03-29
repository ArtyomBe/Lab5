import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TicketManager {
    private LinkedHashMap<Integer, Ticket> ticketCollection = new LinkedHashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private Deque<String> commandHistory = new LinkedList<>();

    public TicketManager(String fileName) {
        loadCollectionFromFile(fileName);
    }
    private void printeHelp() {
        System.out.println("\033[31;1mПРАВИЛА (МИНИ ГАЙД):\033[0m");
        System.out.println("\033[31;1m1. При вводе данных недопустимо использование знаков препинания, за исключением тех что входят в название функции\033[0m");
        System.out.println("\033[31;1m2. Для просмотра всех доступных функций (команд) введите команду help\033[0m");
        System.out.println("");
        System.out.println("\033[36;1m╱▔▔▔▔▔▔▔╲╱▔▔▔▔▔╲\033[0m");
        System.out.println("\033[36;1m▏╮╭┈┈╮╭┈╮▏╭╮┈╭╮▕\033[0m");
        System.out.println("\033[36;1m▏┊╱▔▉┊╱▔▉▏▊┃▕▋┃▕\033[0m");
        System.out.println("\033[36;1m▏╯╲▂╱┊╲▂╱▏▔▅┈▔▔▕\033[0m ARTYOM BETEKHTIN AND STEPAN VOROBYEV FROM INFOCHEMISTRY LAB 5" );
        System.out.println("\033[36;1m╲╭┳┳╮▕▋╭╱╲┳┳┳┫▂╱\033[0m");
        System.out.println("\033[36;1m┈▔▏┣┳┳┳┳▏▕┻┻┻╯▏┈\033[0m");
        System.out.println("\033[36;1m┈┈▏╰┻┻┻┻▏▕▂▂▂╱┈┈\033[0m");
        System.out.println("\033[36;1m┈┈╲▂▂▂▂▂▏┈┈┈┈┈┈┈ \033[0m");
        System.out.println("");
    }

    public void run() {
        printeHelp();
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
                        printFileCSV("output.csv");
                        break;
                    default:
                        System.out.println("\033[35;1mНеверная команда. Введите 'help' для списка команд.\033[0m");
                        break;
                }
            }
        }
    }

    private String inputStringValue(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String userInput = scanner.nextLine().trim();

            if (!hasInvalidCharacters(userInput)) {
                return userInput;
            }

            System.out.println("\033[35;1mОшибка: недопустимые символы. Попробуйте еще раз.\033[0m");
        }
    }

    private int inputIntegerValue(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String userInput = scanner.nextLine().trim();

            if (hasInvalidCharacters(userInput)) {
                System.out.println("\033[35;1mОшибка: недопустимые символы. Попробуйте еще раз.\033[0m");
                continue;
            }

            try {
                return Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("\033[35;1mОшибка: неверный формат числа. Попробуйте еще раз.\033[0m");
            }
        }
    }

    private double inputDoubleValue(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String userInput = scanner.nextLine().trim();

            if (hasInvalidCharacters(userInput)) {
                System.out.println("\033[35;1mОшибка: недопустимые символы. Попробуйте еще раз.\033[0m");
                continue;
            }

            try {
                return Double.parseDouble(userInput);
            } catch (NumberFormatException e) {
                System.out.println("\033[35;1mОшибка: неверный формат числа. Попробуйте еще раз.\033[0m");
            }
        }
    }

    private boolean hasInvalidCharacters(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isAlphabetic(c)) {
                return c == ',' || c == '.' || c == ';' || c == '!' || c == '?' || c == '/';
            }
        }
        return false;
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
        System.out.println("print_csv : вывести элементы коллекции в файл в формате CSV");
        System.out.println("print_unique_event_names : вывести уникальные названия событий в коллекции");
    }

    private void printInfo() {
        System.out.println("Информация о коллекции:");
        System.out.println("Тип коллекции: Hashtable<Integer, Ticket>");
        System.out.println("Дата инициализации: " + LocalDateTime.now());
        System.out.println("Количество элементов: " + ticketCollection.size());
    }

    private void showTickets() {
        for (Ticket ticket : ticketCollection.values()) {
            System.out.println(ticket);
        }
    }

    private void insertTicket() {
        boolean insertSuccess = false;

        while (!insertSuccess) {
            try {
                int id = generateUniqueTicketId();
                Ticket newTicket = inputTicketInfo();
                ticketCollection.put(id, newTicket);

                System.out.println("Элемент успешно создан с идентификатором " + id + " и записан в коллекцию.");
                insertSuccess = true;

            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("\033[35;1mОшибка при вводе данных: \033[0m" + e.getMessage());
            }
        }
    }

    private Ticket inputTicketInfo() {
        System.out.println("Введите данные для нового элемента:");
        String name = inputStringValue("Введите название билета без символов препинания");
        int x = inputIntegerValue("Введите координату x без символов препинания");
        double y = inputDoubleValue("Введите координату y без символов препинания");
        Coordinates coordinates = new Coordinates(x, y);
        Event event = inputEventInfo();
        LocalDateTime creationDate = LocalDateTime.now();
        double price = inputDoubleValue("Введите цену билета");

        return new Ticket(generateUniqueTicketId(), name, coordinates, creationDate, price, true, TicketType.USUAL, event);
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
            throw new IllegalArgumentException("\033[35;1mОшибка: введен некорректный тип билета.\033[0m");
        }
    }

    private Event inputEventInfo() {
        //System.out.print("Введите название события: ");
        String eventName = inputStringValue("Введите название события без запятых или точек");
        //System.out.print("Введите ID события: ");
        int eventId = inputIntegerValue("Введите ID события без символов препинания");
        System.out.print("Введите дату события в формате 'ГГГГ-ММ-ДД HH:MM': ");
        String eventDateStr = scanner.nextLine();
        LocalDateTime eventDate = LocalDateTime.parse(eventDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.print("Введите тип события (E_SPORTS, BASEBALL, BASKETBALL): ");
        String eventTypeStr = scanner.nextLine().toUpperCase();
        EventType eventType;
        try {
            eventType = EventType.valueOf(eventTypeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("\033[35;1mОшибка: введен некорректный тип события.\033[0m");
        }
        return new Event(eventId, eventName, eventDate, eventType);
    }

    private Event inputEvent() {
        return inputEventInfo();
    }

    private Event inputUpdatedEventInfo() {
        System.out.print("Введите новое название события: ");
        String eventName = inputStringValue("Введите новое название события без запятых или точек");

        System.out.print("Введите новый ID события: ");
        int eventId = inputIntegerValue("Введите новый ID события без символов препинания");

        System.out.print("Введите новую дату события в формате 'ГГГГ-ММ-ДД HH:MM': ");
        String eventDateStr = scanner.nextLine();
        LocalDateTime eventDate = LocalDateTime.parse(eventDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm"));

        System.out.print("Введите новый тип события (E_SPORTS, BASEBALL, BASKETBALL): ");
        String eventTypeStr = scanner.nextLine().toUpperCase();
        EventType eventType;
        try {
            eventType = EventType.valueOf(eventTypeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("\033[35;1mОшибка: введен некорректный тип события.\033[0m");
        }

        return new Event(eventId, eventName, eventDate, eventType);
    }

    private void updateEvent(Ticket ticket) {
        System.out.println("Введите новые данные для события:");

        Event updatedEvent = inputUpdatedEventInfo();

        ticket.setEvent(updatedEvent);
        ticketCollection.put(ticket.getId(), ticket);

        System.out.println("Событие успешно обновлено.");
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
            String name = inputStringValue("Введите имя без запятых или точек");
            if (!name.isEmpty()) {
                currentTicket.setName(name);
            }
            if (!inputStringValue("Введите новое название события (оставить пустым, чтобы не обновлять)").isEmpty()) {
                updateEvent(currentTicket);
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
            System.out.println("\033[35;1mОшибка при парсинге числового значения: \033[0m" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\033[35;1mОшибка при парсинге: \033[0m" + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("\033[35;1mОшибка при парсинге строки: некорректные данные.\033[0m");
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
            System.out.println("\033[35;1mОшибка: введено некорректное значение для ключа.\033[0m");
        }
    }

    private void clearTickets() {
        ticketCollection.clear();
        System.out.println("Коллекция успешно очищена.");
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.csv", false))){
            for (Ticket ticket : ticketCollection.values()) {
                writer.println(ticket.toCSVString());
            }
            System.out.println("Коллекция успешно сохранена в файл.");
        } catch (IOException e) {
            System.out.println("\033[35;1mОшибка при сохранении коллекции в файл: \033[0m" + e.getMessage());
        }
    }

    private void executeScript(String[] tokens) {
        try {
            if (tokens.length < 2) {
                System.out.println("\033[35;1mОшибка: не указано имя файла для выполнения скрипта.\033[0m");
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
            System.out.println("\033[35;1mОшибка: файл скрипта не найден.\033[0m");
        }
    }

    private void executeInsertScript(String[] scriptTokens) {
        if (scriptTokens.length < 3) {
            System.out.println("\033[35;1mОшибка: недостаточно аргументов для команды insert.\033[0m");
            return;
        }

        try {
            String keyStr = scriptTokens[1];
            int key = Integer.parseInt(keyStr);

            if (ticketCollection.containsKey(key)) {
                System.out.println("\033[35;1mОшибка: элемент с указанным ключом уже существует.\033[0m");
                return;
            }

            String ticketData = scriptTokens[2];
            Ticket newTicket = Ticket.parseTicket(ticketData);

            if (newTicket != null) {
                ticketCollection.put(key, newTicket);
                System.out.println("Элемент успешно добавлен из скрипта.");
            } else {
                System.out.println("\033[35;1mОшибка при парсинге данных для команды insert из скрипта.\033[0m");
            }
        } catch (NumberFormatException e) {
            System.out.println("\033[35;1mОшибка: некорректное значение ключа для команды insert из скрипта.\033[0m");
        }
    }

    private void executeUpdateScript(String[] scriptTokens) {
        if (scriptTokens.length < 3) {
            System.out.println("\033[35;1mОшибка: недостаточно аргументов для команды update.\033[0m");
            return;
        }

        try {
            String keyStr = scriptTokens[1];
            int key = Integer.parseInt(keyStr);

            if (!ticketCollection.containsKey(key)) {
                System.out.println("\033[35;1mОшибка: элемент с указанным ключом не найден.\033[0m");
                return;
            }

            String ticketData = scriptTokens[2];
            Ticket updatedTicket = Ticket.parseTicket(ticketData);

            if (updatedTicket != null) {
                ticketCollection.put(key, updatedTicket);
                System.out.println("Элемент успешно обновлен из скрипта.");
            } else {
                System.out.println("\033[35;1mОшибка при парсинге данных для команды update из скрипта.\033[0m");
            }
        } catch (NumberFormatException e) {
            System.out.println("\033[35;1mОшибка: некорректное значение ключа для команды update из скрипта.\033[0m");
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
            System.out.println("\033[35;1mОшибка при парсинге числового значения: \033[0m" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\033[35;1mОшибка при парсинге: \033[0m" + e.getMessage());
        }
    }

    private void removeGreaterByKey() {
        try {
            System.out.print("Введите ключ, по которому нужно удалить элементы: ");
            int key = Integer.parseInt(scanner.nextLine());

            ticketCollection.entrySet().removeIf(entry -> entry.getKey() > key);

            System.out.println("Элементы успешно удалены.");
        } catch (NumberFormatException e) {
            System.out.println("\033[35;1mОшибка: введено некорректное значение для ключа.\033[0m");
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
            System.out.println("\033[35;1mОшибка: не указано значение цены для фильтрации.\033[0m");
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
            System.out.println("\033[35;1mОшибка при парсинге числового значения цены: \033[0m" + e.getMessage());
        }
    }

    private int generateUniqueTicketId() {
        Set<Integer> generatedIds = new HashSet<>();
        Random random = new Random();
        int id;
        do {
            id = random.nextInt(1000) + 1;
        } while (generatedIds.contains(id));
        generatedIds.add(id);
        return id;
    }

    private void printFileCSV(String fileName) {
        String cvsSplitBy = ",";

        System.out.println("\033[0;34m" +
                String.format("%-4s | %-15s | %-7s | %-10s | %-35s | %-8s | %-12s | %-12s | %-12s | %-12s | %-12s",
                        "ID",
                        "Ticket name",
                        "X",
                        "Y",
                        "Creation date",
                        "Price",
                        "Refundable",
                        "Ticket type",
                        "Event ID",
                        "Event name",
                        "Event date") +
                "\033[0m");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                Event event = new Event(Integer.parseInt(data[8]), data[9], ZonedDateTime.parse(data[10], DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime(), EventType.valueOf(data[11]));

                Ticket ticket = new Ticket(Integer.parseInt(data[0]), data[1], new Coordinates(Integer.parseInt(data[2]), Double.parseDouble(data[3])), ZonedDateTime.parse(data[4], DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime(), Double.parseDouble(data[5]), Boolean.parseBoolean(data[6]), TicketType.valueOf(data[7]), event);

                System.out.println("\033[0;34m" +
                        String.format("%-4s | %-15s | %-7s | %-10s | %-35s | %-8s | %-12s | %-12s | %-12s | %-12s | %-12s",
                                ticket.getId(),
                                ticket.getName(),
                                String.valueOf(ticket.getCoordinates().getX()),
                                String.valueOf(ticket.getCoordinates().getY()),
                                ticket.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME),
                                String.valueOf(ticket.getPrice()),
                                String.valueOf(ticket.isRefundable()),
                                ticket.getType().name(),
                                String.valueOf(event.getId()),
                                event.getName(),
                                event.getEventDate().format(DateTimeFormatter.ISO_DATE_TIME)) +
                        "\033[0m");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File dataFile = new File("data.csv");
        TicketManager ticketManager = new TicketManager(dataFile.getPath());
        ticketManager.saveToFile();
        ticketManager.run();
    }
}