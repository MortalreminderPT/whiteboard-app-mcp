

package io.tomori.whiteboard.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Util class for JSON serialization and deserialization.
 * This class uses Gson library to serialize and deserialize objects to and from JSON.
 */
public class JsonUtil {
    private static final Gson gson = new Gson();

    /**
     * Serialize an object to JSON.
     *
     * @param obj Object to serialize
     * @return JSON string
     */
    public static String toJson(final Object obj) {
        try {
            return gson.toJson(obj);
        } catch (final Exception e) {
            System.out.println("Failed to serialize object to JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deserialize a JSON string to an object.
     *
     * @param json JSON string
     * @param type Object type to deserialize
     * @param <T>  Object generic type
     * @return Deserialized object
     */
    public static <T> T fromJson(final String json, final Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (final Exception e) {
            System.out.println("Failed to deserialize JSON to object: " + e.getMessage());
            return null;
        }
    }
}
