

package io.tomori.whiteboard.exception;

/**
 * Exception for parsing error, such as JSON or HTTP request
 */
public class ParseException extends RuntimeException {
    /**
     * Constructor for ParseException
     *
     * @param message error message
     */
    public ParseException(final String message) {
        super(message);
    }
}
