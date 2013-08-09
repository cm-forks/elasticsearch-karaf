package org.apache.karaf.elasticsearch;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import static org.elasticsearch.common.settings.ImmutableSettings.*;
import static org.elasticsearch.node.NodeBuilder.*;

public class ClientApp {

    public Node node;
    public NodeBuilder nodeBuilder;

    public static void main(String[] args) throws IOException {

        ClientApp clientApp = new ClientApp();
        clientApp.init();

        // Get an ElasticSearch Client
        Client client = clientApp.node.client();

        // Run Scenario test
        Scenario.TestCase(client);
    }

    public void init() {
        // Configure Node Instance (= Client)
        nodeBuilder = nodeBuilder()
                .local(false)
                .data(false)
                .client(true)
                .clusterName("KARAF");

        Settings settings = settingsBuilder()
                .put("network.host", "127.0.0.1")
                .build();

        nodeBuilder.settings(settings);

        // Get the Node and start it
        node = nodeBuilder.node();
    }

}
