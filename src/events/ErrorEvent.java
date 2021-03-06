package events;

import java.util.EventObject;

public class ErrorEvent extends Event {
    String error;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ErrorEvent(Object source, String error) {
        super(source, EventType.error);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
