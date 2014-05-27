## TODO

### Create log4j-json pattern

Follow instructions here

    https://ops4j1.jira.com/browse/PAXLOGGING-114

Modify pom file of `jsonevent-layout`

        <profile>
          <id>bundle</id>
          <build>
            <plugins>
              <plugin>
                  <groupId>org.apache.felix</groupId>
                  <artifactId>maven-bundle-plugin</artifactId>
                  <version>2.3.7</version>
                  <extensions>true</extensions>
                  <configuration>
                      <instructions>
                        <Bundle-Name>${project.groupId}.${project.artifactId}</Bundle-Name>
                        <Bundle-SymbolicName>${project.groupId}.${project.artifactId}</Bundle-SymbolicName>
                        <Import-Package>!*</Import-Package>
                        <Fragment-Host>org.ops4j.pax.logging.pax-logging-service;bundle-version="[1.6,1.7)"</Fragment-Host>
                        <Embed-Dependency>*;scope=compile|runtime;inline=true</Embed-Dependency>
                        <Implementation-Version>${project.version}</Implementation-Version>
                      </instructions>
                  </configuration>
              </plugin>
            </plugins>
          </build>
        </profile>

Build json format

    mvn clean install -Pbundle

Copy bundle created to system directory

    mkdir -p ${karaf.home}/system/net/logstash/log4j/jsonevent-layout/1.6-SNAPSHOT/
    cp target/jsonevent-layout-1.6-SNAPSHOT.jar ${karaf.home}/system/net/logstash/log4j/jsonevent-layout/1.6-SNAPSHOT/

Add this line to etc/startup.properties before Pax Logging and start karaf after doing a cleanup

    net/logstash/log4j/jsonevent-layout/1.6-SNAPSHOT/jsonevent-layout-1.6-SNAPSHOT.jar=8


    log4j:ERROR Could not instantiate class [net.logstash.log4j.JSONEventLayout].
    java.lang.ClassNotFoundException: net.logstash.log4j.JSONEventLayout not found by org.ops4j.pax.logging.pax-logging-service [4]
    	at org.apache.felix.framework.BundleWiringImpl.findClassOrResourceByDelegation(BundleWiringImpl.java:1460)
    	at org.apache.felix.framework.BundleWiringImpl.access$400(BundleWiringImpl.java:72)
    	at org.apache.felix.framework.BundleWiringImpl$BundleClassLoader.loadClass(BundleWiringImpl.java:1843)
    	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
    	at java.lang.Class.forName0(Native Method)
    	at java.lang.Class.forName(Class.java:190)
    	at org.apache.log4j.helpers.Loader.loadClass(Loader.java:198)
    	at org.apache.log4j.helpers.OptionConverter.instantiateByClassName(OptionConverter.java:326)
    	at org.apache.log4j.helpers.OptionConverter.instantiateByKey(OptionConverter.java:123)
    	at org.apache.log4j.PaxLoggingConfigurator.parseAppender(PaxLoggingConfigurator.java:129)
    	at org.apache.log4j.PropertyConfigurator.parseCategory(PropertyConfigurator.java:735)
    	at org.apache.log4j.PropertyConfigurator.configureRootCategory(PropertyConfigurator.java:615)
    	at org.apache.log4j.PropertyConfigurator.doConfigure(PropertyConfigurator.java:502)
    	at org.apache.log4j.PaxLoggingConfigurator.doConfigure(PaxLoggingConfigurator.java:72)
    	at org.ops4j.pax.logging.service.internal.PaxLoggingServiceImpl.updated(PaxLoggingServiceImpl.java:214)
    	at org.ops4j.pax.logging.service.internal.PaxLoggingServiceImpl$1ManagedPaxLoggingService.updated(PaxLoggingServiceImpl.java:362)
    	
 
Modify the etc/org.ops4j.pax.logging.cfg to point to your logback.xml file 

# 
# TO USE LogBack in order to generate for each log message a JSON message
org.ops4j.pax.logging.logback.config.file=${karaf.base}/etc/logback.xml
#

Change the etc/startup.properties file to load Pax logging & Logback

#
# Startup core services like logging
#
org/ops4j/pax/url/pax-url-mvn/1.3.6/pax-url-mvn-1.3.6.jar=5
org/ops4j/pax/url/pax-url-wrap/1.3.6/pax-url-wrap-1.3.6.jar=5
org/apache/servicemix/bundles/org.apache.servicemix.bundles.antlr/2.7.7_5/org.apache.servicemix.bundles.antlr-2.7.7_5.jar=8
org/apache/servicemix/bundles/org.apache.servicemix.bundles.asm/2.2.3_5/org.apache.servicemix.bundles.asm-2.2.3_5.jar=8
org/codehaus/janino/com.springsource.org.codehaus.commons.compiler/2.6.1/com.springsource.org.codehaus.commons.compiler-2.6.1.jar=8
org/codehaus/groovy/groovy/2.2.1/groovy-2.2.1.jar=8
com/fasterxml/jackson/core/jackson-core/2.1.0/jackson-core-2.1.0.jar=8
com/fasterxml/jackson/core/jackson-annotations/2.1.0/jackson-annotations-2.1.0.jar=8
com/fasterxml/jackson/core/jackson-databind/2.1.0/jackson-databind-2.1.0.jar=8
#org/ops4j/pax/logging/pax-logging-logback/1.7.1/pax-logging-logback-1.7.1.jar=8
org/ops4j/pax/logging/pax-logging-logback/1.7.2-SNAPSHOT/pax-logging-logback-1.7.2-SNAPSHOT.jar=8
org/ops4j/pax/logging/pax-logging-api/1.7.1/pax-logging-api-1.7.1.jar=8
org/ops4j/pax/logging/pax-logging-service/1.7.1/pax-logging-service-1.7.1.jar=8
org/apache/felix/org.apache.felix.configadmin/1.6.0/org.apache.felix.configadmin-1.6.0.jar=10
org/apache/felix/org.apache.felix.fileinstall/3.2.6/org.apache.felix.fileinstall-3.2.6.jar=11

Edit your logback xml file 

cat ../etc/logback.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder>
      <!--<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>-->
      <pattern>%d{ISO8601} | %-5.5p | %-16.16t | %-32.32c{1} | %-32.32C %4L | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n</pattern>
    </encoder>
  </appender>

  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${karaf.base}/data/log/karaf.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <!-- daily rollover -->
      <fileNamePattern>${karaf.base}/data/log/karaf.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
      <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
        <!-- or whenever the file size reaches 100MB -->
        <maxFileSize>100MB</maxFileSize>
      </timeBasedFileNamingAndTriggeringPolicy>
      <!-- keep 30 days worth of history -->
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <append>true</append>
    <!-- encoders are assigned the type
ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="ch.qos.logback.contrib.json.classic.JsonLayout">
        <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
          <!-- prettyPrint is probably ok in dev, but usually not
ideal in production: -->
          <prettyPrint>true</prettyPrint>
        </jsonFormatter>
        <context>api</context>
        <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</timestampFormat>
        <timestampFormatTimezoneId>UTC</timestampFormatTimezoneId>
        <appendLineSeparator>true</appendLineSeparator>
      </layout>
      <!-- <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern> -->
    </encoder>
  </appender>
  <root level="info">
    <appender-ref ref="FILE"/>
    <appender-ref ref="STDOUT"/>
  </root>
</configuration>

## Test with Logback

https://github.com/logstash/logstash-logback-encoder

    java -cp .,logstash-1.3.2-flatjar.jar,logstash-logback-encoder-1.3.jar -jar logstash-1.3.2-flatjar.jar agent -v -f logstash-karaf.conf


