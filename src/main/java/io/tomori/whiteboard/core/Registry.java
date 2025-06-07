

package io.tomori.whiteboard.core;

import io.tomori.whiteboard.annotations.Command;
import io.tomori.whiteboard.model.SocketMessage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for controllers and command-annotated methods.
 * Provide a mechanism for routing socket messages to their appropriate handlers.
 */
public class Registry {
    private static Registry instance;
    private final Map<String, Object> controllerMap = new HashMap<>();
    private final Map<String, Method> methodMap = new HashMap<>();

    /**
     * Return the singleton instance of Registry.
     *
     * @return Registry instance
     */
    public static synchronized Registry getInstance() {
        if (instance == null) {
            instance = new Registry();
        }
        return instance;
    }

    /**
     * Register a controller and its command-annotated methods.
     *
     * @param controller controller to register
     */
    public void registerController(final Object controller) {
        for (final Method m : controller.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                final Command command = m.getAnnotation(Command.class);
                final String key = command.value().toString();
                methodMap.put(key, m);
                controllerMap.put(key, controller);
            }
        }
    }

    /**
     * Process a socket message by invoking the appropriate controller method.
     *
     * @param update socket message to process
     */
    public void process(final SocketMessage update) {
        final String key = update.getType().toString();
        System.out.println("key: " + key);
        final Object controller = controllerMap.get(key);
        final Method method = methodMap.get(key);
        if (controller == null || method == null) {
            System.out.println("Controller or method not found for key: " + key);
            return;
        }
        try {
            method.invoke(controller, update);
        } catch (final Exception e) {
            System.out.println("Error invoking method: " + e.getMessage());
        }
    }
}
