# Install features/bundle

    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.elasticsearch/0.90.5_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.regexp/1.3_3
    wrap:mvn:org.apache.lucene/lucene-snowball/3.0.3
    mvn:org.apache.karaf.elasticsearch/embedded-server/1.0-SNAPSHOT
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene/4.6.0_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-analyzers-common/4.6.0_1
    wrap:mvn:org.apache.lucene/lucene-codecs/4.6.0
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-queries/4.6.0_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-sandbox/4.6.0_1
    wrap:mvn:org.apache.lucene/lucene-highlighter/4.6.0
    wrap:mvn:org.apache.lucene/lucene-join/4.6.0
    wrap:mvn:org.apache.lucene/lucene-spellchecker/3.6.2
    wrap:mvn:org.apache.lucene/lucene-spatial/4.6.0

# Error generated when an embedded server is created by Blueprint

````

2013-12-18 21:43:00,946 | ERROR | FelixStartLevel  | BlueprintContainerImpl           | container.BlueprintContainerImpl  393 | 7 - org.apache.aries.blueprint.core - 1.1.0 | Unable to start blueprint container for bundle org.apache.karaf.elasticsearch.embedded-server
org.osgi.service.blueprint.container.ComponentDefinitionException: Error when instantiating bean embedded of class class org.apache.karaf.elasticsearch.EmbeddedServer
	at org.apache.aries.blueprint.container.BeanRecipe.getInstance(BeanRecipe.java:333)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BeanRecipe.internalCreate2(BeanRecipe.java:806)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BeanRecipe.internalCreate(BeanRecipe.java:787)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.di.AbstractRecipe$1.call(AbstractRecipe.java:79)[7:org.apache.aries.blueprint.core:1.1.0]
	at java.util.concurrent.FutureTask.run(FutureTask.java:262)[:1.7.0_45]
	at org.apache.aries.blueprint.di.AbstractRecipe.create(AbstractRecipe.java:88)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintRepository.createInstances(BlueprintRepository.java:245)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintRepository.createAll(BlueprintRepository.java:183)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintContainerImpl.instantiateEagerComponents(BlueprintContainerImpl.java:668)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintContainerImpl.doRun(BlueprintContainerImpl.java:370)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintContainerImpl.run(BlueprintContainerImpl.java:261)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintExtender.createContainer(BlueprintExtender.java:259)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BlueprintExtender.modifiedBundle(BlueprintExtender.java:222)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.customizerModified(BundleHookBundleTracker.java:500)[11:org.apache.aries.util:1.1.0]
	at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.customizerModified(BundleHookBundleTracker.java:433)[11:org.apache.aries.util:1.1.0]
	at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$AbstractTracked.track(BundleHookBundleTracker.java:725)[11:org.apache.aries.util:1.1.0]
	at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.bundleChanged(BundleHookBundleTracker.java:463)[11:org.apache.aries.util:1.1.0]
	at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$BundleEventHook.event(BundleHookBundleTracker.java:422)[11:org.apache.aries.util:1.1.0]
	at org.apache.felix.framework.util.SecureAction.invokeBundleEventHook(SecureAction.java:1103)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.util.EventDispatcher.createWhitelistFromHooks(EventDispatcher.java:695)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.util.EventDispatcher.fireBundleEvent(EventDispatcher.java:483)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.Felix.fireBundleEvent(Felix.java:4244)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.Felix.startBundle(Felix.java:1923)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.Felix.setActiveStartLevel(Felix.java:1191)[org.apache.felix.framework-4.0.3.jar:]
	at org.apache.felix.framework.FrameworkStartLevelImpl.run(FrameworkStartLevelImpl.java:295)[org.apache.felix.framework-4.0.3.jar:]
	at java.lang.Thread.run(Thread.java:744)[:1.7.0_45]
Caused by: org.elasticsearch.common.settings.NoClassSettingsException: Failed to load class setting [discovery.type] with value [zen]
	at org.elasticsearch.common.settings.ImmutableSettings.loadClass(ImmutableSettings.java:349)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.elasticsearch.common.settings.ImmutableSettings.getAsClass(ImmutableSettings.java:337)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.elasticsearch.discovery.DiscoveryModule.spawnModules(DiscoveryModule.java:51)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.elasticsearch.common.inject.ModulesBuilder.add(ModulesBuilder.java:44)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.elasticsearch.node.internal.InternalNode.<init>(InternalNode.java:154)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.elasticsearch.node.NodeBuilder.build(NodeBuilder.java:159)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	at org.apache.karaf.elasticsearch.EmbeddedServer.buildNode(EmbeddedServer.java:139)[121:org.apache.karaf.elasticsearch.embedded-server:1.0.0.SNAPSHOT]
	at org.apache.karaf.elasticsearch.EmbeddedServer.init(EmbeddedServer.java:74)[121:org.apache.karaf.elasticsearch.embedded-server:1.0.0.SNAPSHOT]
	at org.apache.karaf.elasticsearch.EmbeddedServer.<init>(EmbeddedServer.java:51)[121:org.apache.karaf.elasticsearch.embedded-server:1.0.0.SNAPSHOT]
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)[:1.7.0_45]
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)[:1.7.0_45]
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)[:1.7.0_45]
	at java.lang.reflect.Constructor.newInstance(Constructor.java:526)[:1.7.0_45]
	at org.apache.aries.blueprint.utils.ReflectionUtils.newInstance(ReflectionUtils.java:329)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BeanRecipe.newInstance(BeanRecipe.java:962)[7:org.apache.aries.blueprint.core:1.1.0]
	at org.apache.aries.blueprint.container.BeanRecipe.getInstance(BeanRecipe.java:331)[7:org.apache.aries.blueprint.core:1.1.0]
	... 25 more
Caused by: java.lang.ClassNotFoundException: org.elasticsearch.discovery.zen.ZenDiscoveryModule
	at java.net.URLClassLoader$1.run(URLClassLoader.java:366)[:1.7.0_45]
	at java.net.URLClassLoader$1.run(URLClassLoader.java:355)[:1.7.0_45]
	at java.security.AccessController.doPrivileged(Native Method)[:1.7.0_45]
	at java.net.URLClassLoader.findClass(URLClassLoader.java:354)[:1.7.0_45]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:425)[:1.7.0_45]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:308)[:1.7.0_45]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)[:1.7.0_45]
	at org.elasticsearch.common.settings.ImmutableSettings.loadClass(ImmutableSettings.java:347)[117:org.apache.servicemix.bundles.elasticsearch:0.90.5.1]
	... 40 more
	
````
