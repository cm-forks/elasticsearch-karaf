package org.apache.karaf.elasticsearch;

public class ServerApp {

    private final static String NODE_NAME = "LOCAL";
    private static String id;

    public static void main(String[] args) throws Exception {

        ServerApp serverApp = new ServerApp();

        if (args.length > 0) {
            serverApp.id = args[0];
        } else {
            serverApp.id = NODE_NAME;
        }

        // Start Server Node
        EmbeddedServer embeddedServer = new EmbeddedServer(id);

    }

}
