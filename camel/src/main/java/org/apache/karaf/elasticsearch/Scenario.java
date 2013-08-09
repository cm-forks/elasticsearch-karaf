package org.apache.karaf.elasticsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class Scenario {

    private static final Logger log = LoggerFactory.getLogger(Scenario.class);

    public static void TestCase(Client client) throws IOException {

        DeleteIndexRequest deleteIndexRequest;
        IndicesExistsRequest indicesExistsRequest;
        GetRequest request;
        GetResponse r;
        SearchResponse response;

        client.prepareIndex("aaa","bbb", "1")
                .setSource("foo", "bar")
                .setRefresh(true).execute().actionGet();

        request = new GetRequest("aaa","bbb", "1");
        r = client.get(request).actionGet();

        Map<String, GetField> fields = r.getFields();
        Collection<GetField> getFields = fields.values();

        for(GetField getField : getFields) {
            System.out.println("getField  : " + getField);
        }

        // Add indice ip and make a search
        indicesExistsRequest = new IndicesExistsRequest("ip");
        Boolean indiceExist = client.admin().indices().exists(indicesExistsRequest).actionGet().isExists();

        if(indiceExist) {
          deleteIndexRequest = new DeleteIndexRequest("ip");
          client.admin().indices().delete(deleteIndexRequest).actionGet();
        }

        client.admin().indices().prepareCreate("ip").setSettings(ImmutableSettings.settingsBuilder().put("index.number_of_shards", 1)).execute().actionGet();

        client.admin().indices().preparePutMapping("ip").setType("type1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("type1")
                        .startObject("properties")
                        .startObject("from")
                        .field("type", "ip")
                        .endObject()
                        .startObject("to")
                        .field("type", "ip")
                        .endObject()
                        .endObject()
                        .endObject()
                        .endObject())
                .execute().actionGet();

        client.prepareIndex("ip", "type1", "1")
                .setSource("from", "192.168.0.5", "to", "192.168.0.10")
                .setRefresh(true).execute().actionGet();

        SearchRequestBuilder requestBuilder = client.prepareSearch("ip")
                .setQuery(boolQuery().must(rangeQuery("from").lt("192.168.0.7")).must(rangeQuery("to").gt("192.168.0.7")));

        response = requestBuilder.execute().actionGet();

        System.out.println(">> Search request : " + requestBuilder.toString());
        System.out.println(">> Search result : " + response.toString());

        assertThat(response.getHits().totalHits(), equalTo(1l));


        // TimeStamp
        indicesExistsRequest = new IndicesExistsRequest("timestamp-id");
        indiceExist = client.admin().indices().exists(indicesExistsRequest).actionGet().isExists();

        if(indiceExist) {
            deleteIndexRequest = new DeleteIndexRequest("timestamp-id");
            client.admin().indices().delete(deleteIndexRequest).actionGet();
        }

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("time-type")
                .startObject("_timestamp")
                .field("enabled", true)
                .field("store", "yes")
                .endObject()
                .endObject()
                .endObject();

        client.admin().indices().prepareCreate("timestamp-id")
                .addMapping("time-type", mapping)
                .execute().actionGet();

        // client.admin().cluster().prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet();

        client.prepareIndex("timestamp-id", "time-type", "1")
                .setSource("field1", "value1")
                .setTimestamp("2013-01-01T00:00:00")
                .setRefresh(true).execute().actionGet();

        requestBuilder = client.prepareSearch("timestamp-id")
                .setQuery(QueryBuilders.rangeQuery("_timestamp").gte("2012-01-01").lte("2014-01-01"));

        response = requestBuilder.execute().actionGet();

        System.out.println(">> Search request 1 : " + requestBuilder.toString());
        System.out.println(">> Search result 1 : " + response.toString());

        assertThat(response.getHits().totalHits(), equalTo(1l));

        requestBuilder = client.prepareSearch("timestamp-id")
                .setQuery(QueryBuilders.rangeQuery("_timestamp").gte("2013-07-01").lte("2014-01-01"));

        response = requestBuilder.execute().actionGet();

        System.out.println(">> Search request 2 : " + requestBuilder.toString());
        System.out.println(">> Search result 2 : " + response.toString());

        assertThat(response.getHits().totalHits(), equalTo(0L));

    }

    private void runLocalTest(Client client) throws IOException, InterruptedException {

        Date aDate = new Date();
        String INDEX_NAME = "karaf";

        IndexResponse indexResponse = client.prepareIndex(INDEX_NAME, "type1", "1")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("committer", "true")
                        .field("user", "charlesm")
                        .field("message", "Trying out Elastic Search")
                        .field("postDate", aDate )
                                //.startObject("_timestamp")
                                //.field("enabled", true)
                                //.field("store", "yes")
                        .endObject()
                )
                .execute().actionGet();

        // Check Data created
        assertEquals("type1", indexResponse.getType());
        assertEquals("1", indexResponse.getId());
        log.trace("Index created - Type1 - Id - 1");
        log.trace("Insert field : committer = true");
        log.trace("Insert field : user = charlesm");
        log.trace("Insert field : message = Trying out Elastic Search");

        client.admin().indices().prepareRefresh().execute().actionGet();

        // Search
        SearchResponse searchResponse = client.prepareSearch(INDEX_NAME)
                .setQuery(matchAllQuery()).addField("user")
                .execute().actionGet();

        // Validate result
        assertEquals(1, searchResponse.getHits().totalHits());
        assertEquals(1, searchResponse.getHits().hits().length);
        assertEquals(1, searchResponse.getHits().getAt(0).fields().size());
        assertEquals("charlesm", searchResponse.getHits().getAt(0).fields().get("user").value().toString());
        // assertEquals("2013", searchResponse.getHits().getAt(0).fields().get("postDate").value().toString());

        client.admin().indices().prepareDelete().execute().actionGet();

        // Bulk request
        int counter = 0;
        long i = 1;
        Random random = new Random(1L);
        BulkRequestBuilder request = client.prepareBulk();
        for (; i <= 100; i++) {
            counter++;
            XContentBuilder source = jsonBuilder().startObject()
                    .field("id", Integer.valueOf(counter))
                    .field("committer", "true")
                    .field("user", "user" + Integer.valueOf(counter))
                    .field("message", "Trying out Elastic Search" + Integer.valueOf(counter))
                    .field("postDate", new Date(System.currentTimeMillis() + (random.nextInt() * counter)))
                    .endObject();
            request.add(Requests.indexRequest("committer").type("type1").id(Integer.toString(counter))
                    .source(source));
        }
        BulkResponse response = request.execute().actionGet();

        client.admin().indices().prepareCreate("test")
                .addMapping("type2", XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("type2")
                        .startObject("_timestamp")
                        .field("enabled", true)
                        .field("store", "yes")
                        .endObject()
                        .endObject()
                        .endObject())
                .execute().actionGet();


        // client.admin().cluster().prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet();

        log.info("--> check with automatic _timestamp");
        long now1 = System.currentTimeMillis();
        client.prepareIndex("test", "type2", "1")
                .setSource("field1", "value1")
                .setRefresh(true).execute().actionGet();
        long now2 = System.currentTimeMillis();

        // we check both realtime get and non realtime get
        GetResponse getResponse = client.prepareGet("test", "type2", "1").setFields("_timestamp").setRealtime(true).execute().actionGet();
        long _timestamp = ((Number) getResponse.getField("_timestamp").getValue()).longValue();
        assertThat(_timestamp, greaterThanOrEqualTo(now1));
        assertThat(_timestamp, lessThanOrEqualTo(now2));

        // verify its the same _timestamp when going the replica
        getResponse = client.prepareGet("test", "type2", "1").setFields("_timestamp").setRealtime(true).execute().actionGet();
        assertThat(((Number) getResponse.getField("_timestamp").getValue()).longValue(), equalTo(_timestamp));

        log.info("--> check with custom _timestamp (string)");
        client.prepareIndex("test", "type2", "1")
                .setSource("field1", "value1")
                .setTimestamp("2013-08-06T22:15:00.020")
                .setRefresh(true).execute().actionGet();

        client.prepareIndex("test", "type2", "1")
                .setSource("field2", "value2")
                .setTimestamp("2013-08-06T22:10:00.020")
                .setRefresh(true).execute().actionGet();

        client.prepareIndex("test", "type2", "1")
                .setSource("field3", "value3")
                .setTimestamp("2013-08-06T22:05:00.020")
                .setRefresh(true).execute().actionGet();

       /* getResponse = client.prepareGet("test", "type2", "1").setFields("_timestamp").setRealtime(false).execute().actionGet();
        _timestamp = ((Number) getResponse.getField("_timestamp").getValue()).longValue();
        assertThat(_timestamp, equalTo(20l));
        // verify its the same _timestamp when going the replica
        getResponse = client.prepareGet("test", "type2", "1").setFields("_timestamp").setRealtime(false).execute().actionGet();
        assertThat(((Number) getResponse.getField("_timestamp").getValue()).longValue(), equalTo(_timestamp));  */

        // TimeStamp
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest("timestamp-id");
        Boolean indiceExist = client.admin().indices().exists(indicesExistsRequest).actionGet().isExists();

        if(indiceExist) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("timestamp-id");
            client.admin().indices().delete(deleteIndexRequest).actionGet();
        }

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("time-type")
                .startObject("_timestamp")
                .field("enabled", true)
                .field("store", "yes")
                .endObject()
                .endObject()
                .endObject();

        client.admin().indices().prepareCreate("timestamp-id")
                .addMapping("time-type", mapping)
                .execute().actionGet();

        // client.admin().cluster().prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet();

        client.prepareIndex("timestamp-id", "time-type", "1")
                .setSource("field1", "value1")
                .setTimestamp("2013-01-01T00:00:00")
                .setRefresh(true).execute().actionGet();

        SearchRequestBuilder requestBuilder = client.prepareSearch("timestamp-id")
                .setQuery(QueryBuilders.rangeQuery("_timestamp").gte("2012-01-01").lte("2014-01-01"));

        SearchResponse r = requestBuilder.execute().actionGet();

        System.out.println(">> Search request 1 : " + requestBuilder.toString());
        System.out.println(">> Search result 1 : " + r.toString());

        assertThat(r.getHits().totalHits(), equalTo(1l));

        requestBuilder = client.prepareSearch("timestamp-id")
                .setQuery(QueryBuilders.rangeQuery("_timestamp").gte("2013-07-01").lte("2014-01-01"));

        r = requestBuilder.execute().actionGet();

        System.out.println(">> Search request 2 : " + requestBuilder.toString());
        System.out.println(">> Search result 2 : " + r.toString());

        assertThat(r.getHits().totalHits(), equalTo(0L));

    }


}
