

package io.tomori.whiteboard.config;

import lombok.Data;

/**
 * Config class for the whiteboard client.
 * Contain settings for server connection and user identification.
 */
@Data
public class ClientConfig {
    /**
     * Server hostname or IP address to connect to
     */
    private String host;
    /**
     * Server port to connect to
     */
    private int port;
    /**
     * Client username for identify
     */
    private String username;

    /**
     * Create a client config from command line args.
     *
     * @param args Command line args for configuring the client
     */
    public ClientConfig(final String[] args) {
        host = "localhost";
        port = 8080;
        username = "defaultUser";
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                System.out.println("Error: Port must be a number");
                System.exit(1);
            }
        }
        if (args.length >= 3) {
            username = args[2];
        }
        System.out.println("Initialized client with: " + this);
    }
}
