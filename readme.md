# Install features/bundle

    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.elasticsearch/0.90.5_1
    install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.regexp/1.3_3
    install -s wrap:mvn:org.apache.lucene/lucene-snowball/3.0.3
    install -s wrap:mvn:org.apache.lucene/lucene-core/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-analyzers-common/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-codecs/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-sandbox/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-queryparser/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-queries/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-memory/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-highlighter/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-misc/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-suggest/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-grouping/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-join/4.4.0
    install -s wrap:mvn:org.apache.lucene/lucene-spellchecker/3.6.2
    install -s wrap:mvn:com.spatial4j/spatial4j/0.3
    install -s wrap:mvn:org.apache.lucene/lucene-spatial/4.4.0

    install -s mvn:org.apache.karaf.elasticsearch/embedded-server/1.0-SNAPSHOT

    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene/4.6.0_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-analyzers-common/4.6.0_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-queries/4.6.0_1
    mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.lucene-sandbox/4.6.0_1

# Add Data

## Info

curl -XGET 'http://localhost:9200//_search?pretty' -d ''

## Create index & post a message

curl -XPUT "http://localhost:9200/twitter/tweet/1" -d'
{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elastic Search"
}'
