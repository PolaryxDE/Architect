package de.polaryx.architect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The architect takes care of building and initializing all {@link IService}s.
 */
public final class Architect {

    private final Map<Event, List<Consumer<IService>>> eventListeners;
    private final List<DependencyTree.Entry> registeredServices;
    private final List<IService> services;
    private final DependencyTree dependencyTree;

    /**
     * Creates a new architect instance.
     */
    public Architect() {
        this.eventListeners = new ConcurrentHashMap<>();
        this.registeredServices = new CopyOnWriteArrayList<>();
        this.services = new CopyOnWriteArrayList<>();
        this.dependencyTree = new DependencyTree();
    }

    /**
     * Registers a new {@link IService} in the architects system.
     *
     * @param clazz The class of the {@link IService}.
     * @param <T>   The generic type of the {@link IService}.
     * @return This architect instance.
     */
    public <T extends IService> Architect register(Class<T> clazz) {
        DependencyTree.Entry entry = new DependencyTree.Entry(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Instance.class)) {
                Class<? extends IService> type = (Class<? extends IService>) field.getType();
                entry.getDependencies().add(type);
                entry.getInstanceFields().put(type, field);
            }
        }
        this.registeredServices.add(entry);
        this.dependencyTree.add(entry);
        return this;
    }

    /**
     * Registers an event listener which is being executed when
     *
     * @param event    The event which the given listener will listen to.
     * @param listener The callback which will be called when the event is getting executed.
     * @return This architect instance.
     */
    public Architect on(Event event, Consumer<IService> listener) {
        if (!this.eventListeners.containsKey(event)) {
            this.eventListeners.put(event, new CopyOnWriteArrayList<>());
        }
        this.eventListeners.get(event).add(listener);
        return this;
    }

    /**
     * Starts the architect. The start consists of two parts. The first part will initialize all services.
     * The second one will fill all instances and will start the services.
     *
     * @return This architect instance.
     */
    public Architect start() {
        try {
            for (DependencyTree.Entry entry : this.registeredServices) {
                if (entry.getDependencies().size() > 0) {
                    for (Class<? extends IService> parent : entry.getDependencies()) {
                        this.dependencyTree.increment(entry.getHandle(), this.dependencyTree.getRise(parent));
                    }
                }
            }
            this.dependencyTree.finish();
            for (DependencyTree.Entry entry : this.dependencyTree.getEntries()) {
                IService service = entry.getHandle().newInstance();
                this.executeEvent(Event.CREATED, service);
                this.fillInstances(entry, service);
                this.services.add(service);
                service.start();
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while starting architect!", e);
        }
    }

    /**
     * Stops the architect's work which leads in the stopping of all registered {@link IService}s.
     */
    public void stop() {
        try {
            for (int i = this.services.size() - 1; i >= 0; i--) {
                IService service = this.services.get(i);
                service.stop();
                this.services.remove(service);
                this.executeEvent(Event.STOPPED, service);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while stopping architect!", e);
        }
    }

    /**
     * Fills all instances of the given service.
     *
     * @param entry   The entry owning the service.
     * @param service The service which should be filled.
     */
    private void fillInstances(DependencyTree.Entry entry, IService service) throws Exception {
        for (Class<? extends IService> type : entry.getInstanceFields().keySet()) {
            IService instance = this.services.stream().filter(x -> x.getClass().equals(type)).findFirst().orElse(null);
            if (instance == null) continue;
            Field field = entry.getInstanceFields().get(type);
            field.setAccessible(true);
            field.set(service, instance);
        }
    }

    /**
     * Executes all event listeners registered to the given event.
     *
     * @param event   The event which will be executed.
     * @param service The service for which the event gets executed.
     */
    private void executeEvent(Event event, IService service) {
        if (this.eventListeners.containsKey(event)) {
            this.eventListeners.get(event).forEach(consumer -> consumer.accept(service));
        }
    }
}
