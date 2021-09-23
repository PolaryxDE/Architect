package de.polaryx.architect;

/**
 * The service entity is the main entity which will be used by the {@link Architect}. The architect allows
 * registering classes which implement this service. Other services can require these services.
 * The service instances will than be shared automatically.
 */
public interface IService {

    /**
     * Starts the service. By default it won't be implemented but can be overwritten. The start method is being called
     * when the architect finishes configuration and now starts all services at once.
     */
    default void start() {}

    /**
     * Stops the service. By default it won't be implemented but can be overwritten. The stop method is being called
     * when the architect's stop method {@link Architect#stop()} is being called.
     */
    default void stop() {}
}
