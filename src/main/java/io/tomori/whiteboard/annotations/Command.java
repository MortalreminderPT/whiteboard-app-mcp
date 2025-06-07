

package io.tomori.whiteboard.annotations;

import io.tomori.whiteboard.constant.CommandType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods that handle specific message types.
 * Used by the Registry to route income socket message to the appropriate handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * type of this command, for finding the right handler
     *
     * @return type of this command
     */
    CommandType value();
}
