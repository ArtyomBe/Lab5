import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

public class Event
    {
        private Integer id;
        private String name;
        private java.time.ZonedDateTime date;
        private EventType eventType;

        public Event(int eventId, String eventName, LocalDateTime eventDate, EventType eventType) {
        }

        public Object getName() {
            return name;
        }

        public TemporalAccessor getDateTime() {
            return date;
        }

        public Object getType() {
            return eventType;
        }

        public Object getId() {
            return id;
        }

    }
