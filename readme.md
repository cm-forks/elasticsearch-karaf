# Turn on your OSGI Container into a Big data Node

ElasticSearch project ...

# Pre-requisite

Apache Karaf container must be installed on your machine. Download it from this location : http://karaf.apache.org/index/community/download.html#Karaf2.3.3
Extract the content, move to the directory bin in a Terminal and start Karaf

    ./karaf

# Install bundle

Next, we will install the bundles required to install and run an ElasticSearch node on Apache Karaf

    install -s mvn:org.apache.aries.spifly/org.apache.aries.spifly.dynamic.bundle/1.0.0
    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.regexp/1.3_3
    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.elasticsearch/0.90.5_2-SNAPSHOT
    install -s mvn:org.apache.karaf.elasticsearch/embedded-server/1.0-SNAPSHOT

# Add Data

Here is a nice article providing basic JSON queries to create an index and fetch data
[elasticsearch-in-5-minutes](http://www.elasticsearchtutorial.com/elasticsearch-in-5-minutes.html)

## Info

    curl -XGET 'http://localhost:9200//_search?pretty' -d ''

## Create index & post a message

### Twitter index

    curl -XPUT "http://localhost:9200/twitter/tweet/1" -d'
    {
        "user" : "kimchy",
        "post_date" : "2009-11-15T14:12:12",
        "message" : "trying out Elastic Search"
    }'

    curl -XPUT "http://localhost:9200/twitter/tweet/1" -d'
    {
        "user" : "charles",
        "post_date" : "2013-12-23T18:10:10",
        "message" : "trying out Elastic Search 1"
    }'

### Blog index

    curl -XPUT 'http://localhost:9200/blog/user/dilbert' -d '{ "name" : "Dilbert Brown" }'

    curl -XPUT 'http://localhost:9200/blog/post/1' -d '
    {
        "user": "dilbert",
        "postDate": "2011-12-15",
        "body": "Search is hard. Search should be easy." ,
        "title": "On search"
    }'

    curl -XPUT 'http://localhost:9200/blog/post/2' -d '
    {
        "user": "dilbert",
        "postDate": "2011-12-12",
        "body": "Distribution is hard. Distribution should be easy." ,
        "title": "On distributed search"
    }'

    curl -XPUT 'http://localhost:9200/blog/post/3' -d '
    {
        "user": "dilbert",
        "postDate": "2011-12-10",
        "body": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat" ,
        "title": "Lorem ipsum"
    }'

## Find messages

    curl -XGET 'http://localhost:9200/blog/user/dilbert?pretty=true'
    curl -XGET 'http://localhost:9200/blog/post/1?pretty=true'
    curl -XGET 'http://localhost:9200/blog/post/2?pretty=true'
    curl -XGET 'http://localhost:9200/blog/post/3?pretty=true'

## Search

### Find all blog posts by Dilbert:

    curl 'http://localhost:9200/blog/post/_search?q=user:dilbert&pretty=true'

### All posts which don't contain the term search:

    curl 'http://localhost:9200/blog/post/_search?q=-title:search&pretty=true'

### Retrieve the title of all posts which contain search and not distributed:

    curl 'http://localhost:9200/blog/post/_search?q=+title:search%20-title:distributed&pretty=true&fields=title'

### A range search on postDate:

    curl -XGET 'http://localhost:9200/blog/_search?pretty=true' -d '
    {
        "query" : {
            "range" : {
                "postDate" : { "from" : "2011-12-10", "to" : "2011-12-12" }
            }
        }
    }'

## Bulk Insert

    curl -XDELETE 'http://localhost:9200/test/?pretty=1'

    curl -XPUT 'http://localhost:9200/test/?pretty=1' -d '{ index : { number_of_shards : 3, number_of_replicas : 0 }}'

    curl -XPUT 'http://localhost:9200/_bulk' -d '
    { "create" : {"_index":"test","_type":"one","_id":"1"} }
    {"name":"01","category":"01","subcategory":"01" }
    { "create" : {"_index":"test","_type":"one","_id":"2"} }
    {"name":"02","category":"01","subcategory":"02" }
    { "create" : {"_index":"test","_type":"one","_id":"3"} }
    {"name":"03","category":"01","subcategory":"02" }
    { "create" : {"_index":"test","_type":"one","_id":"4"} }
    {"name":"04","category":"02","subcategory":"01" }
    { "create" : {"_index":"test","_type":"one","_id":"5"} }
    {"name":"05","category":"02","subcategory":"01" }
    { "create" : {"_index":"test","_type":"one","_id":"6"} }
    {"name":"06","category":"01","subcategory":"02" } '

## Using LogStash

- Download and install logstash
- Create a logstash conf file (`logstash-karaf.conf`) to collect log files of Karaf
Remarks :
 * ${karaf.home} must be changed to point to your Apache Karaf Home installation directory
 * Elasticsearch is not emebedded as we will use the instance running in Karaf

```
    input {
      stdin {
        type => "stdin-type"
      }
      file {
        type  => "karaf"
        codec => 'plain'
        path  => ["/Users/chmoulli/Temp/apache-karaf-2.3.3/data/log/*.log"]
      }
    }

    output {
      stdout { codec => rubydebug }
      elasticsearch {
        embedded   => "false"
        host       => "127.0.0.1"
        port       => 9300
        node_name  => "KARAF"
        cluster    => "KARAF"
        bind_host  => "localhost"
      }
    }
```

- Start `logstash`

    java -jar logstash-1.3.2-flatjar.jar agent -v -f logstash-karaf.conf

## TODO

### Create log4j-son pattern

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

Add this line to etc/startup.properties before PAx Logging and start karaf after doing a cleanup

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

## Test with Logback

https://github.com/logstash/logstash-logback-encoder

    java -cp .,logstash-1.3.2-flatjar.jar,logstash-logback-encoder-1.3.jar -jar logstash-1.3.2-flatjar.jar agent -v -f logstash-karaf.conf



