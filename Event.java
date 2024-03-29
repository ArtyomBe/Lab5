import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

public class Event
{
    private Integer id;
    private String name;
    private ZonedDateTime date;
    private EventType eventType;

    public Event(int eventId, String eventName, LocalDateTime eventDate, EventType eventType) {
        this.id = eventId;
        this.name = eventName;
        this.date = ZonedDateTime.of(eventDate, ZoneId.systemDefault());
        this.eventType = eventType;
    }

    public LocalDateTime getEventDate() {
        return LocalDateTime.from(date);
    }

    public String getName() {
        return name;
    }

    public TemporalAccessor getDateTime() {
        return date;
    }

    public EventType getType() {
        return eventType;
    }

    public Integer getId() {
        return id;
    }

}