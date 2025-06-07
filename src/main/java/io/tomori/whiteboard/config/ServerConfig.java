

package io.tomori.whiteboard.config;

import lombok.Data;

/**
 * Config class for the whiteboard server.
 * Contain settings for hostname, ports, and admin username.
 */
@Data
public class ServerConfig {
    /**
     * Server hostname or IP address
     */
    private String host;
    /**
     * Main socket server port
     */
    private int port;
    /**
     * Model Context Protocol server port
     */
    private int mcpPort;
    /**
     * Admin username
     */
    private String username;

    /**
     * Create a server config from command line args.
     *
     * @param args Command line args for configuring the server
     */
    public ServerConfig(final String[] args) {
        host = "localhost";
        port = 8080;
        mcpPort = 8081;
        username = "defaultAdmin";
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
        if (args.length >= 4) {
            try {
                mcpPort = Integer.parseInt(args[3]);
            } catch (final NumberFormatException e) {
                System.out.println("Error: MCP Port must be a number");
                System.exit(1);
            }
        }
        System.out.println("Initialized server with: " + this);
    }
}
