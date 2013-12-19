package org.apache.karaf.elasticsearch;

import org.elasticsearch.Version;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalNode;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

public class EmbeddedServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedServer.class);
    private final static String DEFAULT_DATA_DIRECTORY = "data/elasticsearch";
    private final static String NODE_NAME = "KARAF";
    private static volatile CountDownLatch keepAliveLatch;
    private static Node node;
    private Bundle bundle;

    public EmbeddedServer() {
        log.info(">> Start ES <<");
        init();
        start();
        waitThread();
        log.info(">> ES Server started <<");
    }

    public static void main(String[] args) {
        EmbeddedServer em = new EmbeddedServer();
    }

    public void init() {
        // Try to retrieve BundleContext
        bundle = FrameworkUtil.getBundle(EmbeddedServer.class);
        buildNode();
    }

    void start() {
        try {
            node.start();
        } catch (RuntimeException t) {
            shutdown();
            throw t;
        }
    }

    public void shutdown() {
        if (node != null) {
            node.close();
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

    //String settingsSource = getClass().getName().replace('.', '/') + ".yml";
    private void buildNode() {

        Settings settings = settingsBuilder()
                .put("cluster.name", "KARAF")
                .put("http.enabled", "true")
                .put("node.data", true)
                .put("path.data", DEFAULT_DATA_DIRECTORY)
                .put("name", NODE_NAME)
                .put("discovery.type", "zen")
                .put("discovery.zen.multicast.enabled", false)
                .put("discovery.zen.ping.unicast.enabled", true)
                .put("discovery.zen.unicast.hosts", "127.0.0.1")
                .put("network.host", "127.0.0.1")
                .put("gateway.type", "none")
                .put("cluster.routing.schedule", "50ms")
                .build();

        // Add plugins
        URL url = null;
        try {
            if (bundle != null) {
                url = new URL("file:///Users/chmoulli/MyProjects/elasticsearch-karaf/embedded-server/src/main/resources/plugin");
            } else {
                url = EmbeddedServer.class.getResource("/plugin/");
            }

            if (url != null) {
                File plugins = new File(url.toURI());
                settings = settingsBuilder().put(settings).put("path.plugins", plugins.getAbsolutePath()).build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Create ES Node
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.put(settings);
        builder.classLoader(EmbeddedServer.class.getClassLoader());
        node = new InternalNode(builder.build(), false);
    }
}
