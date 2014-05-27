# Turn on your Apache Karaf Container into a Big data Node

ElasticSearch project ...

# Pre-requisite

- Apache Karaf container must be installed on your machine.
- Download it from this location : http://karaf.apache.org/index/community/download.html#Karaf2.3.5
- Extract the content, move to the directory bin in a Unix/Dos Terminal and start Karaf

    ./karaf

# Install bundle

- Next, we will deploy the bundles required to install and run an ElasticSearch node on Apache Karaf

    install -s mvn:org.apache.aries.spifly/org.apache.aries.spifly.dynamic.bundle/1.0.0
    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.regexp/1.3_3
    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.elasticsearch/0.90.5_2
    install -s mvn:org.apache.karaf.elasticsearch/embedded-server/1.0-SNAPSHOT

- When the bundles have been deployed, a ElasticSearch server will be started
- You can verify/validate this by looking to your karaf log (log:display)

    2014-05-27 19:10:37,943 | INFO  | l Console Thread | EmbeddedServer                   | raf.elasticsearch.EmbeddedServer   31 | 57 - org.apache.karaf.elasticsearch.embedded-server - 1.0.0.SNAPSHOT | >> Start ES <<
    2014-05-27 19:10:37,955 | INFO  | l Console Thread | EmbeddedServer                   | raf.elasticsearch.EmbeddedServer  113 | 57 - org.apache.karaf.elasticsearch.embedded-server - 1.0.0.SNAPSHOT | >> Location of ES Plugins : file:/Users/chmoulli/MyApplications/apache-karaf-2.3.5/plugins
    2014-05-27 19:10:37,988 | INFO  | l Console Thread | node                             | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] version[0.90.5], pid[9301], build[NA/NA]
    2014-05-27 19:10:37,988 | INFO  | l Console Thread | node                             | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] initializing ...
    2014-05-27 19:10:37,991 | INFO  | l Console Thread | plugins                          | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] loaded [], sites []
    2014-05-27 19:10:38,901 | INFO  | l Console Thread | node                             | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] initialized
    2014-05-27 19:10:38,901 | INFO  | l Console Thread | node                             | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] starting ...
    2014-05-27 19:10:38,949 | INFO  | l Console Thread | transport                        | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] bound_address {inet[/127.0.0.1:9300]}, publish_address {inet[/127.0.0.1:9300]}
    2014-05-27 19:10:41,969 | INFO  | updateTask][T#1] | service                          | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] new_master [KARAF][xyWB8VIqT5KYgZpAkQhehQ][inet[/127.0.0.1:9300]], reason: zen-disco-join (elected_as_master)
    2014-05-27 19:10:41,988 | INFO  | l Console Thread | discovery                        | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] KARAF/xyWB8VIqT5KYgZpAkQhehQ
    2014-05-27 19:10:41,993 | INFO  | l Console Thread | http                             | mmon.logging.slf4j.Slf4jESLogger  100 | 58 - org.apache.servicemix.bundles.elasticsearch - 0.90.5.2 | [KARAF] bound_address {inet[/127.0.0.1:9200]}, publish_address {inet[/127.0.0.1:9200]}


# Add Data

Here is a nice article providing basic JSON queries to create an index and fetch data
[elasticsearch-in-5-minutes](http://www.elasticsearchtutorial.com/elasticsearch-in-5-minutes.html)

## Command to be used to 

### Get Info

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
- STDIN to Redis

    Terminal 1
    cd /Users/chmoulli/MyApplications/redis-2.8.3/bin
    ./redis-server

    Terminal 2
    cd /Users/chmoulli/MyApplications/logstash

    java -jar logstash-1.3.2-flatjar.jar agent -f indexer.conf

    ```
    input {
      stdin { type => "stdin-type" }
    }

    output {
      stdout {
        debug => true
        debug_format => "json"
      }
      redis {
        host => "localhost"
        data_type => "list"
        key => "logstash"
      }
    }
    ```

- REDIS to STDOUT / ES

    Terminal 3


    ```
    input {
      redis {
        host           => '127.0.0.1'
        data_type      => 'list'
        key            => 'logstash'
        type           => 'logstash'
        message_format => "json_event"
      }
    }

    output {
      stdout { codec => json }
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

    java -jar logstash-1.3.2-flatjar.jar agent -f logstash-redis.conf

OR

- Create a logstash conf file (`logstash-karaf.conf`) to collect log files of Karaf
Remarks :
 * ${karaf.home} must be changed to point to your Apache Karaf Home installation directory
 * Elasticsearch is not embedded as we will use the instance running in Karaf

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

    java -jar logstash-1.3.2-flatjar.jar agent -f logstash-redis-karaf.conf


