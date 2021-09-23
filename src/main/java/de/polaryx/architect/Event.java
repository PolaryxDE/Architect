package de.polaryx.architect;

import java.util.function.Consumer;

/**
 * All events available for the event bus of an {@link Architect#on(Event, Consumer)}.
 */
public enum Event {
    /**
     * Gets called when the {@link IService} was being created.
     */
    CREATED,
    /**
     * Gets called when the {@link IService} was being stopped.
     */
    STOPPED
}
