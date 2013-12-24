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

