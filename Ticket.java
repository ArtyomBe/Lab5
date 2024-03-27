import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class Ticket {
    private int id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate; // Здесь изменено на ZonedDateTime
    private double price;
    private boolean refundable;
    private TicketType type;
    private Event event;

    public Ticket(int id, String name, Coordinates coordinates, LocalDateTime creationDate, double price, boolean refundable, TicketType type, Event event) {
        this.id = generateUniqueId();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.from(creationDate); // Здесь изменено на ZonedDateTime
        this.price = price;
        this.refundable = refundable;
        this.type = type;
        this.event = event;
    }
    private int generateUniqueId() {
        return (int) (System.currentTimeMillis() + idCounter++);
    }
    private static int idCounter = 1;
    @Override
    public int hashCode() {
        return id;
    }

    public static Ticket parseTicket(String input) {
        String[] tokens = input.split(",");
        if (tokens.length < 8) {
            System.out.println("Ошибка при парсинге строки: недостаточно данных.");
            return null;
        }

        try {
            int id = Integer.parseInt(tokens[0].trim());
            String name = tokens[1].trim();

            int x = Integer.parseInt(tokens[2].trim());
            double y = Double.parseDouble(tokens[3].trim());
            Coordinates coordinates = new Coordinates(x, y);

            LocalDateTime creationDate = LocalDateTime.parse(tokens[4].trim());

            double price = Double.parseDouble(tokens[5].trim());
            boolean refundable = Boolean.parseBoolean(tokens[6].trim());

            TicketType type = TicketType.valueOf(tokens[7].trim());

            Event event = null;
            if (tokens.length > 8) {
                int eventId = Integer.parseInt(tokens[8].trim());
                String eventName = tokens[9].trim();
                LocalDateTime eventDate = LocalDateTime.parse(tokens[10].trim());
                EventType eventType = EventType.valueOf(tokens[11].trim());
                event = new Event(eventId, eventName, eventDate, eventType);
            }

            return new Ticket(id, name, coordinates, creationDate, price, refundable, type, event);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка при парсинге числового значения: " + e.getMessage());
            return null;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Ошибка при парсинге строки: некорректные данные.");
            return null;
        }
    }
    public String toCSVString() {
        return String.format("%d,%s,%d,%.1f,%s,%.1f,%b,%s,%d,%s,%s,%s",
                id, name, coordinates.getX(), coordinates.getY(),
                creationDate, price, refundable, type, event.getId(),
                event.getName(), event.getDateTime(), event.getType());
    }

    public static Ticket parseTicketFromCSV(String csvString) {
        String[] tokens = csvString.split(",");
        if (tokens.length < 8) {
            System.out.println("Ошибка при парсинге строки: недостаточно данных.");
            return null;
        }

        try {
            int id = Integer.parseInt(tokens[0].trim());
            String name = tokens[1].trim();
            int x = Integer.parseInt(tokens[2].trim());
            double y = Double.parseDouble(tokens[3].trim());
            Coordinates coordinates = new Coordinates(x, y);
            LocalDateTime creationDate = LocalDateTime.parse(tokens[4].trim());
            double price = Double.parseDouble(tokens[5].trim());
            boolean refundable = Boolean.parseBoolean(tokens[6].trim());
            TicketType type = TicketType.valueOf(tokens[7].trim());

            // Создаем объект Ticket с полученными значениями
            Ticket ticket = new Ticket(id, name, coordinates, creationDate, price, refundable, type, null);

            // Если есть информация о событии (больше 8 значений), парсим ее и добавляем к объекту Ticket
            if (tokens.length > 8) {
                int eventId = Integer.parseInt(tokens[8].trim());
                String eventName = tokens[9].trim();
                LocalDateTime eventDate = LocalDateTime.parse(tokens[10].trim());
                EventType eventType = EventType.valueOf(tokens[11].trim());
                Event event = new Event(eventId, eventName, eventDate, eventType);
                ticket.setEvent(event);
            }

            return ticket;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Ошибка при парсинге строки: некорректные данные.");
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public TicketType getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
