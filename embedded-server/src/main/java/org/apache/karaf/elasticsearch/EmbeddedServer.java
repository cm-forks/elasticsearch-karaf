package org.apache.karaf.elasticsearch;

import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.elasticsearch.Version;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.collect.Maps.*;
import static org.elasticsearch.common.settings.ImmutableSettings.*;

public class EmbeddedServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedServer.class);

    private final static String DEFAULT_DATA_DIRECTORY = "data/elasticsearch";
    private final static String NODE_NAME = "KARAF";
    private static volatile CountDownLatch keepAliveLatch;
    private final Map<String, Node> nodes = newHashMap();
    private final Map<String, Client> clients = newHashMap();

    public EmbeddedServer(String id) {

        log.info(">> Start ES Server <<");

        try {
            init(id);
            start(id);
            waitThread();

            log.info(">> Server started <<");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public EmbeddedServer() {

        log.info(">> Start ES Server <<");
        String id = NODE_NAME;

        try {
            init(id);
            start(id);
            waitThread();

            log.info(">> Server started <<");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void init() throws URISyntaxException {
        init("KARAF");
    }

    public void init(String id) throws URISyntaxException {
        // Build ElasticSearch with one Node
        buildNode(id, settingsBuilder().build());
    }

    void start(String id) {
        nodes.get(id).start();
    }

    void waitThread() {
        keepAliveLatch = new CountDownLatch(1);
        // keep this thread alive (non daemon thread) until we shutdown - ctrl c
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                keepAliveLatch.countDown();
            }
        });

        Thread keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    keepAliveLatch.await();
                } catch (InterruptedException e) {
                    // bail out
                }
            }
        }, "elasticsearch[keepAlive/" + Version.CURRENT + "]");

        keepAliveThread.setDaemon(false);
        keepAliveThread.start();
    }

    private void buildNode(String id, Settings settings) throws URISyntaxException {

        //File pluginDir = new File(EmbeddedServer.class.getResource("/plugin").toURI());

        //String settingsSource = getClass().getName().replace('.', '/') + ".yml";
        Settings finalSettings = settingsBuilder()
                //.loadFromClasspath(settingsSource)
                //.put(defaultSettings)
                //.put(settings)
                //.put("cluster.name", "test-cluster-" + NetworkUtils.getLocalAddress().getHostName())
                .put("cluster.name","KARAF")
                .put("http.enabled", "true")
                .put("node.data", true)
                .put("path.data", DEFAULT_DATA_DIRECTORY)
                //.put("path.plugins",pluginDir.getAbsolutePath())
                .put("name", id)
                .put("discovery.type", "zen")
                .put("discovery.zen.multicast.enabled", false)
                .put("discovery.zen.ping.unicast.enabled", true)
                .put("discovery.zen.unicast.hosts", "127.0.0.1")
                .put("network.host","127.0.0.1")
                .build();

        if (finalSettings.get("gateway.type") == null) {
            // default to non gateway
            finalSettings = settingsBuilder().put(finalSettings).put("gateway.type", "none").build();
        }

        if (finalSettings.get("cluster.routing.schedule") != null) {
            // decrease the routing schedule so new nodes will be added quickly
            finalSettings = settingsBuilder().put(finalSettings).put("cluster.routing.schedule", "50ms").build();
        }

        /*Node node = nodeBuilder()
                .settings(finalSettings)
                .local(false)
                .build();*/

        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.put(finalSettings);
        builder.classLoader(EmbeddedServer.class.getClassLoader());
        Node node = new InternalNode(builder.build(),false);
        nodes.put(id, node);
        clients.put(id, node.client());
    }

/*    public void stop() {
        Client client = clients.remove(id);
        if (client != null) {
            client.close();
        }
        Node node = nodes.remove(id);
        if (node != null) {
            node.close();
        }
    }

    Client getClient(String id) {
        return clients.get(id);
    }*/

}
