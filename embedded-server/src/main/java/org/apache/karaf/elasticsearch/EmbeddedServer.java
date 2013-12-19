package org.apache.karaf.elasticsearch;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.elasticsearch.Version;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalNode;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.collect.Maps.*;
import static org.elasticsearch.common.settings.ImmutableSettings.*;

public class EmbeddedServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedServer.class);

    private final static String DEFAULT_DATA_DIRECTORY = "data/elasticsearch";
    private final static String NODE_NAME = "KARAF";
    private static volatile CountDownLatch keepAliveLatch;
    private static Node node;

    private String id;
    private BundleContext bundleContext;

    public static void main(String[] args) {
        EmbeddedServer em = new EmbeddedServer("LOCAL");
    }

    public EmbeddedServer(String id) {
        log.info(">> Start ES <<");
        try {
            init(id);
            start();
            waitThread();
            log.info(">> ES Server started <<");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public EmbeddedServer() {
        this("LOCAL");
    }

    public void init(String id) throws URISyntaxException {
        buildNode(id);
    }

    public void shutdown() {
        node.close();
    }

    void start() {
        try {
            node.start();
        } catch (RuntimeException t) {
            shutdown();
            throw t;
        }
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

    private void buildNode(String id) throws URISyntaxException {

        //File pluginDir = new File(EmbeddedServer.class.getResource("/plugin").toURI());
        //String settingsSource = getClass().getName().replace('.', '/') + ".yml";

        Settings settings = settingsBuilder()
                .put("cluster.name", "KARAF")
                .put("http.enabled", "true")
                .put("node.data", true)
                .put("path.data", DEFAULT_DATA_DIRECTORY)
                .put("name", id)
                .put("discovery.type", "zen")
                .put("discovery.zen.multicast.enabled", false)
                .put("discovery.zen.ping.unicast.enabled", true)
                .put("discovery.zen.unicast.hosts", "127.0.0.1")
                .put("network.host", "127.0.0.1")
                .put("gateway.type", "none")
                .put("cluster.routing.schedule", "50ms")
                .build();

        // Add plugins
        if (bundleContext == null) {
            URL pluginDir = EmbeddedServer.class.getResource("/plugin/");
            String plugins = new File(pluginDir.toURI()).getAbsolutePath();
            settings = settingsBuilder().put("path.plugins", plugins).build();
        } else {
            System.out.print(">> Karaf, here we are !!!!");
        }

        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.put(settings);
        builder.classLoader(EmbeddedServer.class.getClassLoader());
        node = new InternalNode(builder.build(), false);
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


}
